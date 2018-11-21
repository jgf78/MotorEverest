package es.cnmc.everest.processor.constraint;

import es.cnmc.everest.processor.ValidationOperator;
import es.cnmc.everest.processor.validator.BusinessValidation;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
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
public class ComparisonOperator extends CellProcessorAdaptor implements LongCellProcessor, DoubleCellProcessor {

    private BusinessValidation.Validation businessValidation = null;

    public ComparisonOperator(DoubleCellProcessor next) {
        super(next);
    }

    public ComparisonOperator(BusinessValidation.Validation businessValidation) {
        this.businessValidation = businessValidation;
    }

    public ComparisonOperator(BusinessValidation.Validation businessValidation, DoubleCellProcessor next) {
        super(next);
        this.businessValidation = businessValidation;
    }

    @Override
    public Object execute(Object value, CsvContext context) {
        this.validateInputNotNull(value, context);
        if (businessValidation == null) {
            throw new EverestRuntimeException(String.format("%s: A Validation object must to be suplied for validating", new Object[]{getClass().getName()}));
        }

        BusinessValidation.Validation.Condition sourceValidationField = businessValidation.getCondition();
        if ((sourceValidationField.getPosition() != null) || (sourceValidationField.getLength() != null)) {
            throw new EverestRuntimeException(String.format("%s: 'position'[%s] and/or 'length'[%s] can't be set", getClass().getName(), sourceValidationField.getPosition(), sourceValidationField.getLength()));
        }

        Double result;
        Object columnValue;
        if (value instanceof Integer) {
            result = new Double((Integer) value);
        } else { // double
            result = (Double) value;
        }

        String[] values = sourceValidationField.getValues().split(",");
        if ((values == null) || (values.length != 1)) {
            throw new EverestRuntimeException(String.format("%s: 'values' can not be null and must have a size equals 1", getClass().getName()));
        }

        Double valueToCompare;
        try {
            valueToCompare = Double.parseDouble(values[0]);
        } catch (NumberFormatException var5) {
            throw new EverestRuntimeException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.comparisonoperator.InvalidLong.message", values[0]));
        }
        ValidationOperator validationOperator = ValidationOperator.valueOf(sourceValidationField.getOperator());
        switch (validationOperator) {
            case GREATER: {
                if (result.compareTo(valueToCompare) > 0) {
                    return this.next.execute(value, context);
                }
                break;
            }
            case GREATER_EQUAL: {
                if (result.compareTo(valueToCompare) >= 0) {
                    return this.next.execute(value, context);
                }
                break;
            }
            case MINUS: {
                if (result.compareTo(valueToCompare) < 0){
                    return this.next.execute(value, context);
                }
                break;
            }
            case MINUS_EQUAL: {
                if (result.compareTo(valueToCompare) <= 0){
                    return this.next.execute(value, context);
                }
                break;
            }
            case EQUALS: {
                if (result.compareTo(valueToCompare) == 0) {
                    return this.next.execute(value, context);
                }
                break;
            }
            case NOT_EQUALS:
                if (result.compareTo(valueToCompare) != 0) {
                    return this.next.execute(value, context);
                }
                break;
            default: {
                 throw new EverestRuntimeException(String.format("%s: [%s] is not soported", getClass().getName(), validationOperator));
            }
        }
        throw new SuperCsvConstraintViolationException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.comparisonoperator.InvalidComparison.message",value, validationOperator.getName(), values[0]), context, this);
    }
}

