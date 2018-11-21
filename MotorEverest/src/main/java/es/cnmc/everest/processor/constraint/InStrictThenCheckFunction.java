package es.cnmc.everest.processor.constraint;

import es.cnmc.everest.processor.validator.BusinessValidation;
import org.apache.commons.lang3.StringUtils;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.i18n.SuperCsvMessages;
import org.supercsv.util.CsvContext;
import es.cnmc.everest.exception.EverestRuntimeException;

import java.util.List;

/**
 * Created by dromera on 18/04/2016.
 *
 * Valida que el valor de suministrados es uno de los valores provistos
 *
 */
public class InStrictThenCheckFunction extends CellProcessorAdaptor implements DoubleCellProcessor, LongCellProcessor {

    private BusinessValidation.Validation businessValidation = null;

    public InStrictThenCheckFunction(CellProcessor next) {
        super(next);
    }

    public InStrictThenCheckFunction(BusinessValidation.Validation businessValidation) {
        this.businessValidation = businessValidation;
    }

    public InStrictThenCheckFunction(BusinessValidation.Validation businessValidation, CellProcessor next) {
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
            throw new SuperCsvConstraintViolationException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.instrictthencheckfunction.InvalidValue.message", value,sourceValidationField.getValues()), context, this);
        }

        String funtionsName = StringUtils.split(targetValidationField.getValues(), ";")[indexFound];
        CheckFunctions.check(context, this, CheckFunctions.FunctionType.valueOf(funtionsName), targetValidationField);
        return next.execute(value, context);
    }

}

