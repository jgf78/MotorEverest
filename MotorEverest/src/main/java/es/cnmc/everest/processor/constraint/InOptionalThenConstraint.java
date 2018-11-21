package es.cnmc.everest.processor.constraint;

import es.cnmc.everest.processor.validator.BusinessValidation;
import org.apache.commons.lang3.StringUtils;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.i18n.SuperCsvMessages;
import org.supercsv.util.CsvContext;
import es.cnmc.everest.exception.EverestRuntimeException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dromera on 18/04/2016.
 *
 * Valida que el valor de suministrados es uno de los valores provistos
 *
 */
public class InOptionalThenConstraint extends CellProcessorAdaptor implements DoubleCellProcessor {

    private BusinessValidation.Validation businessValidation = null;

    public InOptionalThenConstraint(CellProcessor next) {
        super(next);
    }

    public InOptionalThenConstraint(BusinessValidation.Validation businessValidation) {
        this.businessValidation = businessValidation;
    }

    public InOptionalThenConstraint(BusinessValidation.Validation businessValidation, CellProcessor next) {
        super(next);
        this.businessValidation = businessValidation;
    }

    @Override
    public Object execute(Object value, CsvContext context) {
        this.validateInputNotNull(value, context);
        if (businessValidation == null) {
            throw new EverestRuntimeException(String.format("%s: A Validation object must to be suplied for validating", getClass().getName()));
        }

        BusinessValidation.Validation.Condition sourceValidationField = businessValidation.getCondition();
        BusinessValidation.Validation.Check targetValidationField = businessValidation.getCheck();
        if ((sourceValidationField.getPosition() != null) || (sourceValidationField.getLength() != null)) {
            throw new EverestRuntimeException(String.format("%s: 'position'[%s] and/or 'length'[%s] can't be set", getClass().getName(), sourceValidationField.getPosition(), sourceValidationField.getLength()));
        }

        // TODO: contemplar valores del estilo {1,2,3}, tener en cuenta si son números o cadenas
        // TODO: usar Set en la lista de valores permitidos
        Double columnValue;
        if (value instanceof Integer) {
            columnValue = new Double((Integer)value);
        } else { // double
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

        if ((indexFound == -1) && targetValidationField.getDefaultValue().isEmpty()) { // si no hay default value es que cuando no se cumple ninguna condición no hay que comprobar nada más
            return this.next.execute(value, context);
        }

        String[] tokenValues = StringUtils.split(targetValidationField.getValues(),";");
        String[] correctValues = null;
        if (indexFound == -1) {
            correctValues = new String[]{targetValidationField.getDefaultValue()};
        } else {
            correctValues = StringUtils.split(StringUtils.removePattern(tokenValues[indexFound],"[\\{\\}]"),",");
        }

        String processedValueConstraint = null;
        String columnValueConstraint = (String)context.getRowSource().get(targetValidationField.getColumnIndex()-1);
        if (columnValueConstraint != null) {
            int beginIndex = 0;
            int endIndex = ((String) columnValueConstraint).length();
            if (targetValidationField.getPosition() != null) {
                beginIndex = targetValidationField.getPosition() - 1;
            }
            if (targetValidationField.getLength() != null) {
                endIndex = beginIndex + targetValidationField.getLength();
            }
            if (endIndex > ((String) columnValueConstraint).length()) {
                processedValueConstraint = "";
            } else {
                processedValueConstraint = ((String) columnValueConstraint).substring(beginIndex, endIndex);
            }
        }

        if (Arrays.asList(correctValues).contains(processedValueConstraint)) {
            return this.next.execute(value, context);
        } else {
            throw new SuperCsvConstraintViolationException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.inoptionalthenconstraint.InvalidValue.message",processedValueConstraint,targetValidationField.getColumn(),StringUtils.join(correctValues,",")), context, this);
        }
    }

 }

