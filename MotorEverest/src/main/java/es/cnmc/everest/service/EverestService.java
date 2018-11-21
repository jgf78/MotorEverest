package es.cnmc.everest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.cnmc.component.model.*;
import es.cnmc.component.service.exception.StorageException;
import es.cnmc.everest.dao.ItemFileDAO;
import es.cnmc.everest.dao.PropertyDAO;
import es.cnmc.everest.exception.ErrorCode;
import es.cnmc.everest.exception.EverestRuntimeException;
import es.cnmc.everest.model.EmpresaElectrica;
import es.cnmc.everest.model.ItemFile;
import es.cnmc.everest.model.TipoValidacion;
import es.cnmc.everest.model.helper.OneJSONHelper;
import es.cnmc.everest.monitoring.MailSender;
import es.cnmc.everest.processor.BusinessProcessorFactory;
import es.cnmc.everest.processor.EverestProcessorContainer;
import es.cnmc.everest.processor.model.FormatFileLine;
import es.cnmc.everest.processor.validator.BusinessValidation;
import es.cnmc.everest.sede.EventService;
import es.cnmc.everest.util.CSVReader;
import es.cnmc.everest.util.ErrorsUtil;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.i18n.SuperCsvMessages;

import javax.jms.JMSException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class EverestService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Logger loggerSolicitudes = LoggerFactory.getLogger("solicitudes");

    private static final Locale ES = new Locale("es");

    @Autowired
    private ValidatorService validatorService;

    @Autowired
    private OneJSONHelper oneJSONHelper;

    @Autowired
    private EventService eventService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ItemFileDAO itemFileDAO;

    @Autowired
    private CSVReader csvReader;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private PropertyDAO propertyDAO;

    @Autowired
    private ErrorsUtil errorsUtil;

    /**
     * Importante: La sede no contempla validación del censo por "ejercicio de referencia" por tanto no habrá más de un ejercicio de referencia abierto en la Sede (formularios 5,6 y 7)
     *             El motor permitirá el ejercicio que esté en el patrón del nombre del ZIP
     * @param oneJSON
     * @throws IOException
     * @throws StorageException
     * @throws ConfigurationException
     * @throws JMSException
     */
    public void process(OneJSON oneJSON) throws IOException, StorageException, ConfigurationException, JMSException
    {
        List<ItemFile> expectedFiles = null;
        ZipInputStream zis = null;
        Fichero ficheroProcesar = oneJSONHelper.getFichero(oneJSON);
        EmpresaElectrica empresa = oneJSONHelper.getEmpresa(oneJSON);
        TipoValidacion tipoValidacion = validatorService.getTipoValidacion(oneJSON);


        List<ErrorFichero> allErrors = new ArrayList<>();
        ErrorFichero errorFicheroZIP = new ErrorFichero(ficheroProcesar.getNombreFichero(), oneJSONHelper.getFichero(oneJSON).getUuid());
        allErrors.add(errorFicheroZIP);

        SuperCsvMessages.setDefaultLocale(ES);
        try {
            logger.info(String.format("Procesando '%s'....", ficheroProcesar.getNombreFichero()));

            if (validatorService.isValidateCenso(oneJSON)) {
                // Valida si la empresa puede entregar este tipo de fichero
                validatorService.validateCenso(oneJSON, errorFicheroZIP);
                logger.info(String.format("El presentador '%s' tiene permiso", empresa.getNif()));
            } else {
                logger.info(String.format("No se comprueba si el presentador '%s' tiene permiso", empresa.getNif()));
            }

            // Valida que esté activo el año de referencia (que exista y que el aporte haya sido entregado en tiempo)
            validatorService.validateAnioReferencia(oneJSON, errorFicheroZIP);
            logger.info("Ejercicio Referencia vigente");

            // Valida el nombre del ZIP
            validatorService.validateZipFilename(oneJSON, errorFicheroZIP);
            logger.info("Nombre correcto del fichero ZIP");

            // Valida que el ejercicio de referencia coincida con el del nombre del fichero
            validatorService.validateMatchAnioReferencia(oneJSON, errorFicheroZIP);
            logger.info("Ejercicio Referencia coincide con nombre del fichero ZIP");

            logger.info("Tipo de validación: " + tipoValidacion);
            switch (tipoValidacion) {
                case APLAZADA:
                    eventService.sendEventToAplazadas(oneJSON);
                    logger.info("Validaciones aplazadas para: " + oneJSON.getUuidEntrada());
                    return;
                case NINGUNA:
                    logger.info("NO se valida el contenido del fichero");
                    break;
                case TODAS:
                    if (empresa.getCodigoR() != null) {
                        // Valida código distruidora interesado con el del nombre del fichero
                        validatorService.validateCodDis(oneJSON, errorFicheroZIP);
                        logger.info("Coincide el código de distribuidora");
                    } else {
                        logger.info("No viene R1 en oneJSON. No se valida código!");
                    }

                    // Envía mensaje a la sede con inicio de procesamiento
                    eventService.sendEventToSede(oneJSON, EventoFichero.ESTADO_INICIO, "Inicio procesamiento", 0);

                    // Consigue los ficheros que debe contener el ZIP según el tipo de fichero
                    expectedFiles = itemFileDAO.findByTipoFichero(ficheroProcesar.getIdTipoFichero(), empresa.getCodigoR());

                    // Descarga el ZIP del S3
                    InputStream is = repositoryService.download(oneJSON);
                    logger.info("Preparado inputStream para el fichero ZIP del S3...");

                    // Valida el contenido de los ficheros del ZIP
                    ArchiveStreamFactory ass = new ArchiveStreamFactory();
                    ass.setEntryEncoding("CP858");

                    ArchiveInputStream ais = ass.createArchiveInputStream(ArchiveStreamFactory.ZIP, is);
                    ZipArchiveEntry entry;
                    try {
                        while ((entry = (ZipArchiveEntry) ais.getNextEntry()) != null) {
                            ErrorFichero fileError = new ErrorFichero(entry.getName(), "");
                            allErrors.add(fileError);
                            validateZIPEntry(oneJSON, entry, ais, expectedFiles, fileError);
                        }
                    } catch (IllegalArgumentException e) {
                        String msg = e.getMessage() + ". " + SuperCsvMessages.getMessage("cnmc.validation.file.InvalidEncondeFileName.message");
                        ErrorLinea errorLinea = new ErrorLinea(null, null, null, ErrorCode.ERR100.getErrCode(), null, msg);
                        errorFicheroZIP.getErrores().add(errorLinea);
                    }

                    // Comprueba los ficheros obligatorios aportados
                    boolean auditoriaFound = checkAuditoriaFound(expectedFiles);
                    for (ItemFile itemFile : expectedFiles) {
                        if (itemFile.isRequired() && !itemFile.isFound()) {
                            if (itemFile.isDeclResponsable() && auditoriaFound) continue;
                            String msg = SuperCsvMessages.getMessage("cnmc.validation.filezip.Incomplete.message", itemFile.getTypeFile().equals(ItemFile.TypeFile.FILE) ? "fichero" : "directorio", itemFile.getName());
                            ErrorLinea errorLinea = new ErrorLinea(null, null, null, ErrorCode.ERR101.getErrCode(), null, msg);
                            errorFicheroZIP.getErrores().add(errorLinea);
                        }
                    }
                    break;
                default:
                    throw new EverestRuntimeException("Tipo de validations no contemplada:" + tipoValidacion);
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);

            if (!(e instanceof javax.validation.ValidationException)) {
                ErrorLinea errorLinea = new ErrorLinea(null, null, null, ErrorCode.ERR999.getErrCode(), null, "Error interno del servidor. Por favor, vuelva a intentarlo más tarde.");
                errorFicheroZIP.getErrores().add(errorLinea);

                mailSender.sendEmailError(oneJSON, e);
            } else if (errorsUtil.getNumberOfErrors(allErrors) == 0) { // Pueden llegar excepciones de validación pero con allErrors.getErrores sin elementos
                ErrorLinea errorLinea = new ErrorLinea(null, null, null, ErrorCode.ERR000.getErrCode(), null, e.getMessage());
                errorFicheroZIP.getErrores().add(errorLinea);
            }
        }
        finally
        {
            if (zis != null) {
                zis.close();
            }
            long errorNum = errorsUtil.getNumberOfErrors(allErrors); logger.info("Número de errores: " + errorNum);

            // Si hay errores sube un fichero .err al S3. Si no hay errores guarda el ZIP en disco para que lo use EVEREST,
            String codEstado = (errorNum == 0)? EventoFichero.ESTADO_FIN_OK:EventoFichero.ESTADO_FIN_KO;
            if (codEstado.equals(EventoFichero.ESTADO_FIN_KO)) {
                errorsUtil.compactErrors(allErrors);
                uploadErrorsFile(oneJSON, errorNum, allErrors);
            } else if (!tipoValidacion.equals(TipoValidacion.APLAZADA)) {
                downloadAndCopyFiles(oneJSON);
                mailSender.sendEmailCargaOk(oneJSON);
            }

            // Se notifica a la sede en uno de los casos siguientes:
            // - Las validaciones no están aplazadas
            // - Las validaciones están aplazadas pero hay un error (este error sólo puede ser de censo o nombre fichero)
            boolean notificarSede = !tipoValidacion.equals(TipoValidacion.APLAZADA) || (tipoValidacion.equals(TipoValidacion.APLAZADA) && codEstado.equals(EventoFichero.ESTADO_FIN_KO));
            if (notificarSede) {
                // JMS a la Sede y ésta envía email+notificación sólo si la validación ha sido fallida
                eventService.sendEventToSede(oneJSON, codEstado, "Fin procesamiento", errorNum);
            }

            logger.info(String.format("Proceso finalizado '%s'. Resultado: '%s'", ficheroProcesar.getNombreFichero(), codEstado));
            loggerSolicitudes.info(String.format("%s # %s # %s # %s # %s # %s # %s",String.valueOf(ficheroProcesar.getIdTipoFichero()),empresa.getCodigoR()!=null?empresa.getCodigoR():empresa.getNif(),empresa.getNombre(),oneJSON.getUuidEntrada(),ficheroProcesar.getNombreFichero(), codEstado, tipoValidacion));
        }
    }


    private void downloadAndCopyFiles(OneJSON oneJSON) throws ConfigurationException, IOException, StorageException {
        // Compone el directorio de destino y lo crea si no existe
        String folder = propertyDAO.getPropertyValue(oneJSON,"file.dir.zip");
        String numeroRegistroGeneral = oneJSON.getCarga().getNumeroRegistroGeneral();

        StringBuilder baseFolder = new StringBuilder();
        baseFolder.append(folder).append(File.separator).append(numeroRegistroGeneral);

        File directory = new File(baseFolder.toString());
        directory.mkdir();

        // Descarga el fichero ZIP y lo copia en el punto de montaje
        String zipFileName = new StringBuilder(baseFolder.toString()).append(File.separator).append(oneJSONHelper.getFichero(oneJSON).getNombreFichero()).toString();
        InputStream isZIP = repositoryService.download(oneJSON);
        FileUtils.copyInputStreamToFile(isZIP, new File(zipFileName));
        logger.info("Fichero ZIP copiado en: " + zipFileName);

        // Descarga el mensaje y lo copia en el punto de montaje
        String jsonFile =  zipFileName + ".json";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(jsonFile), oneJSON);
        logger.info("Fichero JSON copiado en: " + jsonFile);

        // Descarga el justificante y lo copia en el punto de montaje
        InputStream isJustificante = repositoryService.download(oneJSON.getUrlJustificante());
        String justificanteFileName = new StringBuilder(baseFolder.toString()).append(File.separator).append("justificante_").append(numeroRegistroGeneral).append(".pdf").toString();
        FileUtils.copyInputStreamToFile(isJustificante, new File(justificanteFileName));
        logger.info("Justificante copiado en: " + justificanteFileName);
    }

     /**
     * Valida un fichero del fichero zip
     */
    public void validateZIPEntry(OneJSON oneJSON, ZipEntry zipEntry, InputStream zis, List<ItemFile> expectedFiles, ErrorFichero fileError) throws ConfigurationException, IOException, javax.validation.ValidationException, ReflectiveOperationException {
        String zipEntryName = zipEntry.getName();

        // Valida el nombre del directorio
        if (zipEntry.isDirectory() || zipEntryName.contains("/")){
            zipEntryName = validatorService.validateDirectory(oneJSON, zipEntry, expectedFiles, fileError);
            // TODO: poder validar ficheros dentro de directorios
            if (zipEntryName==null) return; // no hay que validar el fichero
        }

        // Valida el nombre del fichero
        ItemFile itemFile = validatorService.validateFilename(oneJSON, zipEntryName, expectedFiles, fileError);

        // Valida el contenido del fichero si está marcado como "se debe validar"
        if (itemFile!=null) {
            boolean tamanioOk = validatorService.validateMinSize(oneJSON, itemFile, zipEntry, fileError);

            if (tamanioOk && itemFile.isValidate()) {
                String year = validatorService.getYearFromZipFilename(oneJSON);
                String formatFilename = propertyDAO.getPathFormatFile(oneJSON, year, itemFile.getId());
                String validationFilename = propertyDAO.getPathValidateionFile(oneJSON, year, itemFile.getId());

                List<Object> sintaxValidations = csvReader.readFileCSV(formatFilename, FormatFileLine.getSyntaxTypeHeaders(), fileError, FormatFileLine.class.getName());
                List<BusinessValidation.Validation> businessValidations = BusinessProcessorFactory.getBusinessValidations(validationFilename);

                EverestProcessorContainer everestProcessorContainer = new EverestProcessorContainer();
                everestProcessorContainer.setSyntaxValidations(sintaxValidations);
                everestProcessorContainer.setBusinessValidations(businessValidations);
                everestProcessorContainer.initializeProcessors();
                csvReader.validateFileCSV(zis, everestProcessorContainer, fileError);
            }
        }
    }


    /**
     * Sube fichero de errores al S3. uuidFicheroZIP.err
     */
    private void uploadErrorsFile(OneJSON oneJSON, long errorNum, List<ErrorFichero> errors) throws IOException, StorageException {
        if (errorNum == 0) {
            throw new EverestRuntimeException("errorNum tiene que ser mayor que 0 para subir un fichero .err");
        };

        // Lista de errores a json en un string
        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors);

        // String a imputstream
        long length = str.getBytes().length;
        InputStream is = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));

        // Sube el fichero
        Fichero fichero = oneJSONHelper.getFichero(oneJSON);
        String fileName = fichero.getNombreFichero() + ".err";
        String pathAlmacenamiento = fichero.getPathAlmacenamiento() + ".err";
        repositoryService.upload(oneJSON.getProcedimientoEntrada().getIdAlmacenamientoBase(), pathAlmacenamiento, fileName, length, is);

        logger.info(String.format("Fichero de errores subido a S3. '%s' - '%s'", oneJSON.getProcedimientoEntrada().getIdAlmacenamientoBase(), pathAlmacenamiento));
    }


    /**
     * Indica si se han aportado todos los ficheros de auditoría
     */
    private boolean checkAuditoriaFound(List<ItemFile> expectedFiles) {
        boolean allAuditoriaFound = false;
        for (ItemFile itemFile : expectedFiles) {
            if (!itemFile.isAuditoria()) continue;

            if (!itemFile.isFound()) {
                return false;
            } else {
                allAuditoriaFound = true;
            }
        }
        return allAuditoriaFound;
    }


}
