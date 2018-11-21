package es.cnmc.everest.processor;

import es.cnmc.everest.processor.validator.BusinessValidation;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

import java.util.List;

/**
 * @author amiguel
 */
public interface CSVProcessor {

    CellProcessor[] getProcessors(List list, List<BusinessValidation.Validation> businessValidations, List<SuperCsvCellProcessorException> suppressedExceptions);


}
