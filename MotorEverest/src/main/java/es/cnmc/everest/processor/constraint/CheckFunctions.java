package es.cnmc.everest.processor.constraint;

import es.cnmc.everest.processor.validator.BusinessValidation;
import org.apache.commons.lang3.StringUtils;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.i18n.SuperCsvMessages;
import org.supercsv.util.CsvContext;
import es.cnmc.everest.exception.EverestRuntimeException;

/**
 * Created by dromera on 22/06/2016.
 */
public class CheckFunctions {

    public static enum FunctionType {NULL, NOT_NULL}


    public static boolean check(CsvContext context, CellProcessor cellProcessor, FunctionType functionType, BusinessValidation.Validation.Check targetValidationField) {
        // TODO: Check un array index bound exception
        String columnValue = (String)context.getRowSource().get(targetValidationField.getColumnIndex()-1);

        Boolean ok = false;
        switch (functionType) {
            case NULL:
                if (!StringUtils.isEmpty(columnValue)) {
                    throw new SuperCsvCellProcessorException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.RequiredNullValue.message", targetValidationField.getColumn()), context, cellProcessor);
                }
                break;
            case NOT_NULL:
                if (StringUtils.isEmpty(columnValue)) {
                    throw new SuperCsvCellProcessorException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.RequiredValue.message", targetValidationField.getColumn()), context, cellProcessor);
                }
                break;
            default:
                throw new EverestRuntimeException("FunctionType no implementada: " + functionType);
        }
        return ok;
    }
}
