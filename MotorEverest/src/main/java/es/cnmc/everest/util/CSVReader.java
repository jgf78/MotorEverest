package es.cnmc.everest.util;

import es.cnmc.component.model.ErrorFichero;
import es.cnmc.component.model.ErrorLinea;
import es.cnmc.everest.Constants;
import es.cnmc.everest.exception.ErrorCode;
import es.cnmc.everest.processor.EverestProcessorContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author amiguel
 */
@Component
public class CSVReader {

    private final CsvPreference COMMA_PREFERENCE = new CsvPreference.Builder('"', ',', "\\r\\n").ignoreEmptyLines(true).maxLinesPerRow(0).build();
    private final CsvPreference SEMICOLON_PREFERENCE = new CsvPreference.Builder('"', ';', "\\r\\n").ignoreEmptyLines(true).maxLinesPerRow(0).build();
    private final Logger log = LoggerFactory.getLogger(CSVReader.class);

    /**
     * Lee y valida un fichero csv con los procesadores dados a partir de un input stream.
     * @param is
     * @param everestProcessorContainer
     * @param fileError
     * @throws IOException
     */
    public void validateFileCSV(InputStream is, EverestProcessorContainer everestProcessorContainer, ErrorFichero fileError) throws IOException {
        ICsvMapReader mapReader = new CsvMapReader(new InputStreamReader(is), SEMICOLON_PREFERENCE);
        validateFileCSV(mapReader, everestProcessorContainer, fileError);
    }

    /**
     * Lee y valida un fichero csv con los procesadores dados a partir de un nombre de fichero.
     *
     * @param fileName
     * @param everestProcessorContainer
     * @param fileError
     * @throws IOException
     */
    public void validateFileCSV(String fileName, EverestProcessorContainer everestProcessorContainer, ErrorFichero fileError) throws IOException {
        ICsvMapReader mapReader = new CsvMapReader(new FileReader(fileName), SEMICOLON_PREFERENCE);
        validateFileCSV(mapReader, everestProcessorContainer, fileError);
    }


    /**
     * Lee y valida un fichero csv con los procesadores dados.
     *
     * @param mapReader
     * @param everestProcessorContainer
     * @param fileError
     * @throws IOException
     */
    private void validateFileCSV(ICsvMapReader mapReader, EverestProcessorContainer everestProcessorContainer, ErrorFichero fileError) throws IOException {
        String[] header = everestProcessorContainer.getSyntaxValidationNames();
        log.info(String.format("Processing file %s..." , new Object[]{fileError.getNombreFichero()}));
        try {
            Map<String, Object> customerMap = null;
            do {
                try {
                    customerMap = mapReader.read(header, everestProcessorContainer.getProcessors());
                } catch (SuperCsvException e) {
                    if ((fileError.getErrores().size() > Constants.MAX_ERRORS_BY_DATA_FILE) || (everestProcessorContainer.getCommonSuppressedExceptions().size() > Constants.MAX_ERRORS_BY_DATA_FILE)) {
                        addErrorLinea(header, fileError, mapReader, new SuperCsvException("Too many errors...", e.getCsvContext()));
                        break;
                    } else {
                        addErrorLinea(header, fileError, mapReader, e);
                    }
                }
            } while (customerMap != null);
        } finally {
            addSuppressedExceptionsToError(header, fileError, everestProcessorContainer.getCommonSuppressedExceptions());
            fileError.setLineasLeidas(mapReader.getRowNumber());
            log.info(String.format("Processed file %s: %d rows. %d errors" , fileError.getNombreFichero(),mapReader.getRowNumber(),fileError.getErrores().size()));
        }
    }

    /**
     * Lee un fichero csv y devuelve los valores seteados en una lista de objectos de la clase indicada.
     *
     * @param fileName
     * @param nameMapping
     * @return
     * @throws Exception
     */
    public List<Object> readFileCSV(String fileName, String[] nameMapping, ErrorFichero error, String className) throws IOException, ReflectiveOperationException  {

        ICsvMapReader mapReader = null;
        List<Object> list = new ArrayList<>();

        mapReader = new CsvMapReader(new FileReader(fileName), COMMA_PREFERENCE);
        Map<String, String> customerMap;
        boolean cont = true;
        while (cont) {
            try {
                customerMap = mapReader.read(nameMapping);
                cont = customerMap != null;
                if (cont) {
                   list.add(buildObject(nameMapping, customerMap, className));
                }
            } catch (SuperCsvException e) {
                addErrorLinea(nameMapping, error, mapReader, e);
            }
        }

        return list;
    }

    /**
     * Devuelve por reflexion un objeto seteado con los valores del map.
     *
     * @param nameMapping
     * @param customerMap
     * @param clazzName
     * @return
     * @throws NoSuchElementException
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     */
    private Object buildObject(String[] nameMapping, Map<String, String> customerMap, String clazzName) throws NoSuchElementException,
            NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, InstantiationException{

        Class clazz = Class.forName(clazzName);
        Object instance = clazz.newInstance();
        for (int i = 0; i < customerMap.size(); i++) {
            String key = nameMapping[i];
            String obj = customerMap.get(key);
            if (obj==null) throw new NoSuchElementException(key);
            Field field = clazz.getDeclaredField(key);
            String methodName = "set" + firstLetterToUppercase(field.getName());
            Method method = clazz.getMethod(methodName, String.class);
            method.invoke(instance, obj);
        }

        return instance;

    }

    /**
     * Devuelve la palabra comenzando con mayuscula.
     *
     * @param str
     * @return
     */
    private String firstLetterToUppercase (String str){
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * Informa y agrega un error de validacion.
     *
     * @param error
     * @param mapReader
     * @param e
     */
    private String addErrorLinea(String[] header, ErrorFichero error, ICsvMapReader mapReader, SuperCsvException e) {
        String errorCode = ErrorCode.ERR000.getErrCode();
        String campo = "";
        int numColumna = 0;
        String msg = "Line: " + mapReader.getLineNumber() + "; Row: " + mapReader.getRowNumber();
        if (e.getCsvContext() != null) {
            msg += "; Column: " + e.getCsvContext().getColumnNumber();
            numColumna = e.getCsvContext().getColumnNumber();
            campo = header[e.getCsvContext().getColumnNumber() - 1];
            if (e instanceof SuperCsvCellProcessorException) {
                errorCode = ErrorCode.getByClass(((SuperCsvCellProcessorException)e).getProcessor()); //"ERROR_VALIDACION_CSV"
            }
        }
        msg += "; Error: " + e.getMessage();

        ErrorLinea errorLinea = new ErrorLinea(new Long(mapReader.getLineNumber()), /*new Long(mapReader.getRowNumber())*/null, numColumna, errorCode, campo, e.getMessage());
        error.getErrores().add(errorLinea);
        return msg;
    }


    private void addSuppressedExceptionsToError (String[] header, ErrorFichero error, List<SuperCsvCellProcessorException> commonSuppressedExceptions){
        int numErrors = 0;
        String campo;
        ErrorLinea errorLinea;

        for (SuperCsvCellProcessorException e : commonSuppressedExceptions) {
            numErrors++;
            campo = header[e.getCsvContext().getColumnNumber() - 1];
            if (numErrors < Constants.MAX_ERRORS_BY_DATA_FILE) {
                errorLinea = new ErrorLinea(new Long(e.getCsvContext().getLineNumber()), /*new Long(e.getCsvContext().getRowNumber())*/null, e.getCsvContext().getColumnNumber(),  ErrorCode.getByClass(e.getProcessor()), campo, e.getMessage());
                error.getErrores().add(errorLinea);
            }else {
                errorLinea = new ErrorLinea(null, null, null, ErrorCode.ERR000.getErrCode(), null, "Too many errors...");
                error.getErrores().add(errorLinea);
                break;
            }
        }

    }

}
