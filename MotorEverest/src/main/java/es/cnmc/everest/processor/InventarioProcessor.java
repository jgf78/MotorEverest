package es.cnmc.everest.processor;

import es.cnmc.everest.Constants;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;

import java.util.List;
/**
 * Devuelve los procesadores para validar los ficheros de inventario.
 *
 * @author amiguel
 */
public class InventarioProcessor extends CellProcessorAdaptor  { // implements CSVProcessor

    //TODO: multithread
    private List<SuperCsvCellProcessorException> commonSuppressedExceptions;


    public InventarioProcessor () {}

    public InventarioProcessor (CellProcessor next){
        super(next);
    }

     /**
     * Execute the next processor.
     *
     * @param value
     * @param context
     * @return
     */
    @Override
    public Object execute(Object value, CsvContext context) {
        try {
            return next.execute(value, context);
        } catch (SuperCsvCellProcessorException e) {
            if (commonSuppressedExceptions.size() > Constants.MAX_ERRORS_BY_DATA_FILE) {
                throw new SuperCsvException("Too many errors...");
            }
            commonSuppressedExceptions.add(e);
            return null;
        }
    }

    public List<SuperCsvCellProcessorException> getCommonSuppressedExceptions() {
        return commonSuppressedExceptions;
    }

    public void setCommonSuppressedExceptions(List<SuperCsvCellProcessorException> commonSuppressedExceptions) {
        this.commonSuppressedExceptions = commonSuppressedExceptions;
    }

}
