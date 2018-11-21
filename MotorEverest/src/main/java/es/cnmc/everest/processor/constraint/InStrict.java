package es.cnmc.everest.processor.constraint;

import es.cnmc.everest.processor.validator.BusinessValidation;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.i18n.SuperCsvMessages;
import org.supercsv.util.CsvContext;
import es.cnmc.everest.exception.EverestRuntimeException;


/**
 * Created by dromera on 18/04/2016.
 *
 * Valida que el valor de suministrados es uno de los valores provistos
 *
 */
public class InStrict extends CellProcessorAdaptor implements StringCellProcessor, LongCellProcessor {

    private BusinessValidation.Validation businessValidation = null;

    public InStrict(CellProcessor next) {
        super(next);
    }

    public InStrict(BusinessValidation.Validation businessValidation) {
        this.businessValidation = businessValidation;
    }

    public InStrict(BusinessValidation.Validation businessValidation, CellProcessor next) {
        super(next);
        this.businessValidation = businessValidation;
    }

    @Override
    public Object execute(Object value, CsvContext context) {
        this.validateInputNotNull(value, context);
        if (businessValidation == null) {
            throw new EverestRuntimeException(String.format("%s: A Validation object must to be suplied for validating", new Object[]{getClass().getName()}));
        }

        // TODO: check el instanceof value
        BusinessValidation.Validation.Condition sourceValidationField = businessValidation.getCondition();
   /*
        int beginIndex = 0;
        int endIndex = ((String)value).length();
        if (sourceValidationField.getPosition() != null) {
            beginIndex = sourceValidationField.getPosition() - 1;
        }
        if (sourceValidationField.getLength() != null) {
            endIndex = beginIndex + sourceValidationField.getLength();
        }
        */
        Object columnValue;
        if (value instanceof Integer) {
            columnValue = new Double((Integer)value);
        } else if (value instanceof String) {
            int beginIndex = 0;
            int endIndex = ((String)value).length();
            if (sourceValidationField.getPosition() != null) {
                beginIndex = sourceValidationField.getPosition() - 1;
            }
            if (sourceValidationField.getLength() != null) {
                endIndex = beginIndex + sourceValidationField.getLength();
            }
            columnValue = ((String)value).substring(beginIndex, endIndex);
        } else {
            columnValue = (Double)value;
        }


   //     String valueToCompare = ((String)value).substring(beginIndex, endIndex);
        String[] values = sourceValidationField.getValues().split(";");
        for (String valueFixed : values) {
            if (columnValue.equals((columnValue instanceof Double)?Double.valueOf(valueFixed):valueFixed)) {
                return this.next.execute(value, context);
            }
        }

        throw new SuperCsvConstraintViolationException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.instrict.InvalidValue.message",columnValue, sourceValidationField.getValues()), context, this);
    }
}

