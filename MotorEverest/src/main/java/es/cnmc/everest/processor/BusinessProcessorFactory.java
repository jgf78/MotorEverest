package es.cnmc.everest.processor;

import es.cnmc.everest.processor.model.FormatFileLine;
import es.cnmc.everest.processor.constraint.*;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import es.cnmc.everest.processor.validator.BusinessValidation;
import es.cnmc.everest.exception.EverestRuntimeException;

import javax.xml.bind.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dromera on 18/04/2016.
 */
public class BusinessProcessorFactory {

    public static List<BusinessValidation.Validation> getBusinessValidations(String validationFilename) {
        BusinessValidation businessValidation;
        try {
            JAXBContext jabxContext = JAXBContext.newInstance(BusinessValidation.class);
            Unmarshaller unmarshaller = jabxContext.createUnmarshaller();
            unmarshaller.setEventHandler(
                    new ValidationEventHandler() {
                        public boolean handleEvent(ValidationEvent event ) {
                            throw new EverestRuntimeException(event.getMessage(),
                                    event.getLinkedException());
                        }
                    });
            businessValidation = (BusinessValidation) unmarshaller.unmarshal(new File(validationFilename));
        } catch (JAXBException e) {
            throw new EverestRuntimeException("No se ha podido cargar el fichero de validaciones " + validationFilename, e);
        }

        return businessValidation.getValidation();
    }


    public static CellProcessor getProcessor(List<BusinessValidation.Validation> businessValidationList, FormatFileLine formatFileLine) {
        List<BusinessValidation.Validation> businessValidationListColumn = new ArrayList<>();
        CellProcessor cellProcessor = null;

        for (BusinessValidation.Validation validation : businessValidationList) {
            if (validation.getCondition().getColumn().equalsIgnoreCase(formatFileLine.getName())) {
                businessValidationListColumn.add(validation);
            }
        }

        if (!businessValidationListColumn.isEmpty()) {
            cellProcessor = buildCellProcessor(businessValidationListColumn, 0);
        }

        return cellProcessor;
    }

    private static CellProcessor buildCellProcessor(List<BusinessValidation.Validation> businessValidationList, int index) {
        ValidationOperator validationOperator;
        CellProcessor businessCellProcessor = null;
        BusinessValidation.Validation validation = businessValidationList.get(index);
        boolean isLastCellProcessor = index == businessValidationList.size() - 1;

        validationOperator = ValidationOperator.valueOf(validation.getCondition().getOperator());
        switch (validationOperator) {
            case IN_STRICT:
                if (isLastCellProcessor) {
                    businessCellProcessor = new InStrict(validation);
                } else {
                    businessCellProcessor = new InStrict(validation, buildCellProcessor(businessValidationList, ++index));
                }
                break;
            case GREATER:
            case GREATER_EQUAL:
            case MINUS:
            case MINUS_EQUAL:
            case EQUALS:
            case NOT_EQUALS:
                if (isLastCellProcessor) {
                    businessCellProcessor = new ComparisonOperator(validation);
                } else {
                    businessCellProcessor = new ComparisonOperator(validation, (DoubleCellProcessor) buildCellProcessor(businessValidationList, ++index));
                }
                break;
            case IN_OPTIONAL_THEN_CONSTRAINT:
                if (isLastCellProcessor) {
                    businessCellProcessor = new InOptionalThenConstraint(validation);
                } else {
                    businessCellProcessor = new InOptionalThenConstraint(validation, buildCellProcessor(businessValidationList, ++index));
                }
                break;
            case IN_STRICT_THEN_CONSTRAINT:
                if (isLastCellProcessor) {
                    businessCellProcessor = new InStrictThenConstraint(validation);
                } else {
                    businessCellProcessor = new InStrictThenConstraint(validation, buildCellProcessor(businessValidationList, ++index));
                }
                break;
            case IN_STRICT_THEN_TYPE:
                if (isLastCellProcessor) {
                    businessCellProcessor = new InStrictThenType(validation);
                } else {
                    businessCellProcessor = new InStrictThenType(validation, buildCellProcessor(businessValidationList, ++index));
                }
                break;
            case IN_STRICT_THEN_CHECKFUNCTION:
                if (isLastCellProcessor) {
                    businessCellProcessor = new InStrictThenCheckFunction(validation);
                } else {
                    businessCellProcessor = new InStrictThenCheckFunction(validation, buildCellProcessor(businessValidationList, ++index));
                }
                break;
            case IN_OPTIONAL_THEN_CHECKFUNCTION:
                if (isLastCellProcessor) {
                    businessCellProcessor = new InOptionalThenCheckFunction(validation);
                } else {
                    businessCellProcessor = new InOptionalThenCheckFunction(validation, buildCellProcessor(businessValidationList, ++index));
                }
                break;
            default:
                throw new EverestRuntimeException("ValidationOperator no contemplado: " + validationOperator);
        }

        return businessCellProcessor;
    }

}
