package es.cnmc.everest.processor.constraint;

import es.cnmc.everest.exception.EverestRuntimeException;
import es.cnmc.everest.processor.validator.BusinessValidation;
import org.apache.commons.lang3.StringUtils;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.i18n.SuperCsvMessages;
import org.supercsv.util.CsvContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by dromera on 18/04/2016.
 *
 * Valida que el valor de suministrados es uno de los valores provistos
 *
 */
public class InStrictThenType extends CellProcessorAdaptor implements DoubleCellProcessor, LongCellProcessor {

    private BusinessValidation.Validation businessValidation = null;

    public InStrictThenType(CellProcessor next) {
        super(next);
    }

    public InStrictThenType(BusinessValidation.Validation businessValidation) {
        this.businessValidation = businessValidation;
    }

    public InStrictThenType(BusinessValidation.Validation businessValidation, CellProcessor next) {
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
        BusinessValidation.Validation.Check targetValidationField = businessValidation.getCheck();
//        if ((sourceValidationField.getPosition() != null) || (sourceValidationField.getLength() != null)) {
//            throw new EverestRuntimeException(String.format("%s: 'position'[%s] and/or 'length'[%s] can't be set", new Object[]{getClass().getName(), sourceValidationField.getPosition(), sourceValidationField.getLength()}));
//        }

        Double columnValue;
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
            columnValue = Double.valueOf(((String)value).substring(beginIndex, endIndex));
        } else {
            columnValue = (Double)value;
        }

        List<RangeItem> rangeItemList = UtilsContraint.splitToRangeItem(StringUtils.split(sourceValidationField.getValues(),";"));
        int indexFound = -1;
        for (int i=0; i<rangeItemList.size(); i++) {
            if (rangeItemList.get(i).validate(columnValue)) {
                indexFound = i;
                break;
            }
        }
        if (indexFound == -1) {
            throw new SuperCsvConstraintViolationException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.instrictthentype.InvalidValue.message", value,sourceValidationField.getValues()), context, this);
        }

        String[] tokenValues = StringUtils.split(targetValidationField.getValues(), ";");
        String[] correctValues;
        if (indexFound == -1) {
            correctValues = new String[]{targetValidationField.getDefaultValue()};
        } else {
            correctValues = StringUtils.split(StringUtils.removePattern(tokenValues[indexFound], "[\\{\\}]"), ",");
        }

        // TODO: Check un array index bound exception
        String columnValueConstraint = (String)context.getRowSource().get(targetValidationField.getColumnIndex()-1);
        if (columnValueConstraint == null) {
            return next.execute(columnValue, context);
           // throw new SuperCsvCellProcessorException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.instrictthentype.InvalidType.message", targetValidationField.getColumn(),StringUtils.join(correctValues,",")), context, this);
        }

        for (String correctType : correctValues) {
            switch (TypeCheck.fromValue(correctType)) {
                case DATE_DDMMYYY:
                case DATE_MMYYY:
                case YEAR:
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat(correctType);
                        formatter.setLenient(false);
                        formatter.parse(columnValueConstraint);
                        return next.execute(columnValue, context);
                    } catch (ParseException e) {
                        // continúa intentado aplicar el formato correcto
                    }
                    break;
                default:
                    throw new EverestRuntimeException(String.format("TypeConstraint %s unknown", correctType));
            }
        }
        throw new SuperCsvCellProcessorException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.instrictthentype.InvalidFormat.message",columnValueConstraint, targetValidationField.getColumn(), StringUtils.join(correctValues,",")), context, this);
    }

}

