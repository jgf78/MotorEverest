package es.cnmc.everest.service;

import es.cnmc.component.model.*;
import es.cnmc.everest.dao.PropertyDAO;
import es.cnmc.everest.exception.ErrorCode;
import es.cnmc.everest.exception.EverestRuntimeException;
import es.cnmc.everest.model.ItemFile;
import es.cnmc.everest.model.TipoFicheroConst;
import es.cnmc.everest.model.TipoValidacion;
import es.cnmc.everest.model.helper.OneJSONHelper;
import es.cnmc.everest.sede.Seguridad;
import es.cnmc.everest.util.DateUtil;
import es.cnmc.everest.util.ERValidator;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.i18n.SuperCsvMessages;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

/**
 * Created by dromera on 10/05/2016.
 */
@Service
public class ValidatorService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Seguridad seguridad;

    @Autowired
    private PropertyDAO propertyDAO;

    @Autowired
    private OneJSONHelper oneJSONHelper;

    @Autowired
    private TipoFicheroConst tipoFicheroConst;

    @Autowired
    private DateUtil dateUtil;

    /**
     * Valida el nombre del fichero zip de entrada recuperando la mascara de un fichero de propiedades.
     */
    public void validateZipFilename(OneJSON oneJSON, ErrorFichero errorFichero) throws ConfigurationException {
        String zipFileName = oneJSONHelper.getFichero(oneJSON).getNombreFichero();
        String pattern = propertyDAO.getPropertyValue(oneJSON, "file.zip.pattern");

        boolean isValid = validateFileNameComplete(zipFileName, pattern, errorFichero) ;
        if (!isValid) {
            throw new javax.validation.ValidationException(errorFichero.getErrores().get(errorFichero.getErrores().size()-1).getMensaje());
        }
    }


    /**
     * Valida que el ejercicio de referencia del JSON coincida con el del nombre del fichero
     * @param oneJSON
     * @param errorFichero
     * @throws ConfigurationException
     */
    public void validateMatchAnioReferencia(OneJSON oneJSON, ErrorFichero errorFichero) throws ConfigurationException {
        String zipFileName = oneJSONHelper.getFichero(oneJSON).getNombreFichero();
        String anioReferenciaJson = oneJSONHelper.getAnioReferencia(oneJSON);
        String anioReferenciaZIP = getYearFromZipFilename(oneJSON);

        boolean isValid = anioReferenciaJson.equals(anioReferenciaZIP);
        if (!isValid) {
            String msg = SuperCsvMessages.getMessage("cnmc.validation.filezip.InvalidRefYear.message", zipFileName, anioReferenciaJson);
            ErrorLinea errorLinea = new ErrorLinea(null, null, null, ErrorCode.ERR100.getErrCode(), null, msg);
            errorFichero.getErrores().add(errorLinea);

            logger.info(msg);

            throw new javax.validation.ValidationException(msg);
        }
    }


    /**
     * Valida que el ejercico de referencia esté vigente
     * @param oneJSON
     * @param errorFichero
     * @throws ConfigurationException
     */
    public void validateAnioReferencia(OneJSON oneJSON, ErrorFichero errorFichero) throws ConfigurationException {
        boolean isValid = false;
        DateUtil.DateRange rangoEjercicioActivo = null;
        String anioReferenciaJson = oneJSONHelper.getAnioReferencia(oneJSON);

        String anioReferenciaPermitido = propertyDAO.getPropertyValue(oneJSON,"aniosReferencia"); // de momento sólo devuelve-permito un ejercicio activo

        if (!anioReferenciaPermitido.trim().equalsIgnoreCase("ninguno") && anioReferenciaJson.equals(anioReferenciaPermitido)) {
            rangoEjercicioActivo = getVigenciaAnioReferencia(oneJSON, anioReferenciaJson);
            Date fechaEntrada = oneJSON.getFechaEntrada();

            isValid = dateUtil.isInRange(fechaEntrada, rangoEjercicioActivo);
        }
        if (!isValid) {
            String msg = SuperCsvMessages.getMessage("cnmc.validation.ejercicioReferencia.notFound.message", anioReferenciaJson);
            ErrorLinea errorLinea = new ErrorLinea(null, null, null, ErrorCode.ERR100.getErrCode(), null, msg);
            errorFichero.getErrores().add(errorLinea);

            logger.info(msg + ":" + rangoEjercicioActivo);

            throw new javax.validation.ValidationException(msg);
        }
    }




    public void validateCodDis(OneJSON oneJSON, ErrorFichero errorFiheroZip) {
        String zipFileName = oneJSONHelper.getFichero(oneJSON).getNombreFichero();
        int posR1 = zipFileName.toUpperCase().lastIndexOf("R1-");
        String codDisInFileName = zipFileName.toUpperCase().substring(posR1, posR1+6);
        String codDistribuidora = oneJSONHelper.getEmpresa(oneJSON).getCodigoR();

        boolean isValid = codDistribuidora.equals(codDisInFileName);
        if (!isValid){
            String msg = SuperCsvMessages.getMessage("cnmc.validation.filezip.CodDisNoMatch.message", codDistribuidora, codDisInFileName);
            ErrorLinea errorLinea = new ErrorLinea(null, null, null, ErrorCode.ERR100.getErrCode(), null, msg);
            errorFiheroZip.getErrores().add(errorLinea);

            logger.info(msg);
            throw new javax.validation.ValidationException(msg);
        }
    }


    /**
     * Valida si una empresa interesada puede entregar este tipo de fichero
     */
    public void validateCenso(OneJSON oneJSON, ErrorFichero error) throws ConfigurationException, IOException {
        Integer idRolTipoFichero = Integer.valueOf(propertyDAO.getPropertyValue(oneJSON, "role.id"));

        List<RolEmpresa> rolEmpresaList = oneJSONHelper.roles(oneJSON);
        for (RolEmpresa rolEmpresa : rolEmpresaList) {
            if (rolEmpresa.getRol().getIdRol().equals(idRolTipoFichero)) return;
        }

        String codDistribuidora = oneJSONHelper.getEmpresa(oneJSON).getCodigoR();
        String msg = SuperCsvMessages.getMessage("cnmc.validation.censo.notFound.message", codDistribuidora);
        ErrorLinea errorLinea = new ErrorLinea(null, null, null, ErrorCode.ERR000.getErrCode(), null, msg);
        error.getErrores().add(errorLinea);

        throw new javax.validation.ValidationException(msg);
    }



    /**
     * Valida si una empresa interesada puede entregar este tipo de fichero
     * Ya no se usa!
     */
    private void validateCensoFechaActual(OneJSON oneJSON, ErrorFichero error) throws ConfigurationException, IOException {
        Integer idRolTipoFichero = Integer.valueOf(propertyDAO.getPropertyValue(oneJSON, "role.id"));

        Rol[] rolList = seguridad.getRoles(oneJSONHelper.getEmpresa(oneJSON).getNif());
        for (Rol rol : rolList) {
            if (rol.getIdRol().equals(idRolTipoFichero)) return;
        }

        String codDistribuidora = oneJSONHelper.getEmpresa(oneJSON).getCodigoR();
        String msg = SuperCsvMessages.getMessage("cnmc.validation.censo.notFound.message", codDistribuidora);
        ErrorLinea errorLinea = new ErrorLinea(null, null, null, ErrorCode.ERR000.getErrCode(), null, msg);
        error.getErrores().add(errorLinea);

        throw new javax.validation.ValidationException(msg);
    }


    /**
     * Valida si el tipo de fichero puede tener o no directorios
     */
    public String validateDirectory(OneJSON oneJSON, ZipEntry zipEntry,  List<ItemFile> expectedFiles, ErrorFichero error) {
        String fileName = null;
        Integer idTipoFichero = oneJSONHelper.getFichero(oneJSON).getTipoFichero().getIdTipoFichero();
        if (idTipoFichero.equals(tipoFicheroConst.DISTRIBUCION_INVENTARIO)
                || idTipoFichero.equals(tipoFicheroConst.DISTRIBUCION_AUDITORIA)
                || idTipoFichero.equals(tipoFicheroConst.DISTRIBUCION_PLANINVERSION)
                || idTipoFichero.equals(tipoFicheroConst.TRANSPORTE_INVENTARIO)
                || idTipoFichero.equals(tipoFicheroConst.TRANSPORTE_AUDITORIA)
                || idTipoFichero.equals(tipoFicheroConst.TRANSPORTE_PLANINVERSION))
        {
            String msg = SuperCsvMessages.getMessage("cmnc.validation.filezip.ForbiddenFolder.message", zipEntry.getName());
            ErrorLinea errorLinea = new ErrorLinea(null, null, null, ErrorCode.ERR102.getErrCode(), null, msg);
            error.getErrores().add(errorLinea);
            throw new javax.validation.ValidationException(msg);
        }
        else if (idTipoFichero.equals(tipoFicheroConst.CIRCULAR_CIRCULAR))
        {
            ItemFile itemFile;
            if (zipEntry.getName().contains("CIRA/")) {
                itemFile = findItemFile(expectedFiles, "cira");
                itemFile.setFound(true);

                Path p = Paths.get(zipEntry.getName());
                if (startsWith(p.getFileName().toString(),"R1-")) {
                    fileName = p.getFileName().toString();
                }
            } else  if (zipEntry.getName().contains("CIIA/")) {
                itemFile = findItemFile(expectedFiles, "ciia");
                itemFile.setFound(true);
            }
        }
        return fileName;
    }


    private boolean matchFileNameStart(String fileName, String patternStart) {
        return ERValidator.validate(patternStart + ".*", fileName, Pattern.CASE_INSENSITIVE);
    }


    /**
     * Valida un nombre de fichero según una expresión regular
     */
    private boolean validateFileNameComplete(String fileName, String pattern, ErrorFichero error)
    {
        boolean isValid = ERValidator.validate(pattern, fileName, Pattern.CASE_INSENSITIVE);
        if (!isValid) {
            String msg = SuperCsvMessages.getMessage("cnmc.validation.filezip.InvalidName.message", fileName);
            ErrorLinea errorLinea = new ErrorLinea(null, null, null, ErrorCode.ERR100.getErrCode(), null, msg);
            error.getErrores().add(errorLinea);

            logger.info(msg + ": " + pattern);
        }

        return isValid;
    }

    public boolean validateMinSize(OneJSON oneJSON, ItemFile itemFile, ZipEntry zipEntry, ErrorFichero error) throws ConfigurationException {
        boolean isValid = true;
        long minSize = 0;
        long fileSize = Math.round(zipEntry.getSize() / 1024 / 1024);
        String fileName = zipEntry.getName();
        Integer idTipoFichero = oneJSONHelper.getFichero(oneJSON).getTipoFichero().getIdTipoFichero();
        if (idTipoFichero.equals(tipoFicheroConst.CIRCULAR_CIRCULAR)) {

            if (itemFile.getId().equals("auditoria")) {
                minSize = new Long(propertyDAO.getPropertyValue(oneJSON, "file.minsize.auditoria"));
                isValid = fileSize >= minSize;
            }
        }

        if (!isValid) {
            String msg = SuperCsvMessages.getMessage("cnmc.validation.file.InvalidMinSize.message", fileName, fileSize, minSize);
            ErrorLinea errorLinea = new ErrorLinea(null, null, null, ErrorCode.ERR000.getErrCode(), null, msg);
            error.getErrores().add(errorLinea);

            logger.info(msg);
        }

        return isValid;
    }


    /**
     * Valida el nombre de un fichero (del ZIP) con la expresión regular del fichero de propiedades.
     */
    public ItemFile validateFilename(OneJSON oneJSON, String fileName, List<ItemFile> expectedFiles, ErrorFichero error) throws ConfigurationException, javax.validation.ValidationException{
        //TODO: validar con mascaras
        ItemFile itemFile = null;

        Integer idTipoFichero = oneJSONHelper.getFichero(oneJSON).getTipoFichero().getIdTipoFichero();
        if (idTipoFichero.equals(tipoFicheroConst.DISTRIBUCION_INVENTARIO)) {
            if (matchFileNameStart(fileName,"INVENTARIO_") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.inventario"), error)) {
                itemFile = findItemFile(expectedFiles,getIdFicheroFromFilename(oneJSON, fileName));
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"TRANSMISIONES_") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.transmisiones"), error)) {
                itemFile= findItemFile(expectedFiles,"9");
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"MODIFICACIONES_") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.modificaciones"), error)) {
                itemFile= findItemFile(expectedFiles,"modificaciones");
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"DEC_RESP_") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.decresp"), error)) {
                itemFile = findItemFile(expectedFiles,"10");
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"AUDITORIA_") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.auditoria"), error)) {
                itemFile = findItemFile(expectedFiles,"11");
                itemFile.setFound(true);
            }
        }
        else if (idTipoFichero.equals(tipoFicheroConst.DISTRIBUCION_AUDITORIA)) {
            if (matchFileNameStart(fileName,"AUDIT_R1-") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.audit"), error)) {
                itemFile = findItemFile(expectedFiles, getIdFicheroFromFilename(oneJSON, fileName));
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"SEGUIMIENTO_R1-") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.seguimiento"), error)) {
                itemFile = findItemFile(expectedFiles,"seguimiento");
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"CUADROS_RESUMEN_R1-") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.cuadro"), error)) {
                itemFile = findItemFile(expectedFiles,"cuadro");
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"DEC_RESP_") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.decresp"), error)) {
                itemFile = findItemFile(expectedFiles,"decresp");
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"AUDITORIA_R1-") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.auditoria"), error)) {
                itemFile = findItemFile(expectedFiles,"auditoria");
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"CIIA") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.ciia"), error)) {
                itemFile = findItemFile(expectedFiles,"ciia");
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"ACTAS") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.actas"), error)) {
                itemFile = findItemFile(expectedFiles,"actas");
                itemFile.setFound(true);
            }
        }
        else if (idTipoFichero.equals(tipoFicheroConst.DISTRIBUCION_PLANINVERSION)) {
            if (matchFileNameStart(fileName,"PI_R1-") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.plan"), error)) {
                itemFile = findItemFile(expectedFiles, getIdFicheroFromFilename(oneJSON, fileName));
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"PI_RESUMEN_R1-") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.resumen"), error)) {
                itemFile = findItemFile(expectedFiles,"resumen");
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"PI_RESUMEN_CCAA") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.resumenccaa"), error)) {
                itemFile = findItemFile(expectedFiles,"resumenccaa");
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"PI_PROYECTOS") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.proyectos"), error)) {
                itemFile = findItemFile(expectedFiles,"proyectos");
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"PI_MACRO") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.macro"), error)) {
                itemFile = findItemFile(expectedFiles,"macro");
                itemFile.setFound(true);
            }
        }
        else if (idTipoFichero.equals(tipoFicheroConst.CIRCULAR_CIRCULAR)) {
            if (matchFileNameStart(fileName,"CIR4_") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.formulario"), error)) {
                itemFile = findItemFile(expectedFiles,getIdFicheroFromFilename(oneJSON, fileName));
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"impuestos_R1-") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.impuestos"), error)) {
                itemFile = findItemFile(expectedFiles,"impuestos");
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"DEC_RESP_") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.decresp"), error)) {
                itemFile = findItemFile(expectedFiles,"decresp");
                itemFile.setFound(true);
            } else if (matchFileNameStart(fileName,"R1-") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.auditoria"), error)) {
                itemFile = findItemFile(expectedFiles, "auditoria");
                itemFile.setFound(true);
            }
        }
        else if (idTipoFichero.equals(tipoFicheroConst.TRANSPORTE_INVENTARIO)) {
             if (matchFileNameStart(fileName,"TRINV") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.altas"), error)) {
                 itemFile = findItemFile(expectedFiles, "TRINV");
                 itemFile.setFound(true);
             } else if (matchFileNameStart(fileName,"TRBAJ") && validateFileNameComplete(fileName, propertyDAO.getPropertyValue(oneJSON, "file.pattern.bajas"), error)) {
                 itemFile = findItemFile(expectedFiles,"TRBAJ");
                 itemFile.setFound(true);
            }
        }
        else
        {
            // TRANSPORTE_AUDITORIA ni TRANSPORTE_PLANINVERSION no se valida ningún contenido del ZIP
            logger.info(String.format("El tipo de fichero '%s' no tiene validación de ficheros",idTipoFichero));
        }
        return itemFile;
    }


    /**
     * Devuelve el idFichero del nombre del fichero csv de datos.
     * Precondición: el nombre del fichero es válido
     * Ejemplos: INVENTARIO_R1_001_1.txt; Salida: 1
     *           CIR4_2016_13C_R1-001_2015.txt; Salida: 1
     */
    public String getIdFicheroFromFilename (OneJSON oneJSON, String fileName){
        String idFicheroInFileName = null;

        Integer idTipoFichero = oneJSONHelper.getFichero(oneJSON).getTipoFichero().getIdTipoFichero();
        if (idTipoFichero.equals(tipoFicheroConst.DISTRIBUCION_INVENTARIO)
                || idTipoFichero.equals(tipoFicheroConst.DISTRIBUCION_AUDITORIA)
                || idTipoFichero.equals(tipoFicheroConst.DISTRIBUCION_PLANINVERSION)) {
            int i = fileName.lastIndexOf('_') + 1;
            idFicheroInFileName = fileName.substring(i, i + 1);
        } else if (idTipoFichero.equals(tipoFicheroConst.CIRCULAR_CIRCULAR)) {
            int i = fileName.substring(10).indexOf('_');
            idFicheroInFileName = fileName.substring(10,10 + i);
        }
        return idFicheroInFileName;
    }


    public TipoValidacion getTipoValidacion(OneJSON oneJSON) throws ConfigurationException {
        String validation = propertyDAO.getPropertyValue(oneJSON,"validations.type");
        return TipoValidacion.getByValue(validation);
    }

    public Boolean isValidateCenso(OneJSON oneJSON) throws ConfigurationException {
        return "true".equalsIgnoreCase(propertyDAO.getPropertyValue(oneJSON,"validations.censo"));
    }

    public DateUtil.DateRange getVigenciaAnioReferencia(OneJSON oneJSON, String anioReferencia) throws ConfigurationException {
        String[] periodoStr = propertyDAO.getPropertyValue(oneJSON,"aniosReferencia." + anioReferencia + ".periodo").split("-");
        if (periodoStr.length > 2) {
            throw new EverestRuntimeException("Property aniosReferencia formato de rango erróneo DD/MM/YYYY HH:MM - DD/MM/YYYY HH:MM");
        }

        try {
            DateUtil.DateRange rangoVigencia = dateUtil.new DateRange(periodoStr[0].trim(), periodoStr.length==2?periodoStr[1].trim():null, "dd/MM/yyyy HH:mm");
            return rangoVigencia;
        } catch (ParseException e) {
            throw new EverestRuntimeException("Property aniosReferencia formato fecha erróneo", e);
        }
    }


    private ItemFile findItemFile(List<ItemFile> itemFileList, String id) {
        for (ItemFile itemFile : itemFileList) {
            if (itemFile.getId().equalsIgnoreCase(id)) {
                return itemFile;
            }
        }
        throw new EverestRuntimeException("Item no encontrado " + id);
    }


    private boolean startsWith(String name, String iniName) {
        return name.toUpperCase().startsWith(iniName.toUpperCase());
    }

    /**
     * Devuelve el añoo de referencia para el que se entrega la informacion.
     * Ejemplo: Entrada: IN_2016_20160501_R1-534_2015.zip; Salida: 2015
     */
    public String getYearFromZipFilename (OneJSON oneJSON){
        String fileName = oneJSONHelper.getFichero(oneJSON).getNombreFichero();
        int i = fileName.lastIndexOf('_') + 1;
        return fileName.substring(i, i+4);
    }

}
