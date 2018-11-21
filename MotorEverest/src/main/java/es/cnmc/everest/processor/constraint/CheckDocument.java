package es.cnmc.everest.processor.constraint;

import es.cnmc.everest.util.ValidateDocument;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.i18n.SuperCsvMessages;
import org.supercsv.util.CsvContext;
import es.cnmc.everest.exception.EverestRuntimeException;

/**
 * Created by dromera on 27/05/2016.
 */
public class CheckDocument extends CellProcessorAdaptor implements StringCellProcessor {

    public enum TypeDocument {NIF, CIF, NIE}

    private TypeDocument[] typeDocumentList;
    private ValidateDocument validateDocument;


    public CheckDocument(TypeDocument[] typeDocumentList) {
        this.typeDocumentList =  typeDocumentList;
        this.validateDocument = new ValidateDocument();
    }

    public CheckDocument(TypeDocument[] typeDocumentList , CellProcessor next) {
        super(next);
        this.typeDocumentList =  typeDocumentList;
        this.validateDocument = new ValidateDocument();
    }



    @Override
    public Object execute(Object value, CsvContext context) {
        this.validateInputNotNull(value, context);

        boolean ok=false;
        String result = (String)value;
        for (TypeDocument typeDocument : typeDocumentList) {
            switch (typeDocument) {
            case NIF:
                ok = validateDocument.validateNIF(result, true);
                break;
            case CIF:
                ok = validateDocument.validateCIF(result);
                break;
            case NIE:
                ok = validateDocument.validateNIE(result);
                break;
            default:
                throw new EverestRuntimeException("Tipo de documento no contemplado: " + typeDocument);
            }
            if (ok) break;
        }
        if (ok) {
            return this.next.execute(value, context);
        }

        throw new SuperCsvConstraintViolationException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.checkdocument.InvalidDocument.message", value), context, this);
    }
}
