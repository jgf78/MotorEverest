package es.cnmc.everest.processor.constraint;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.i18n.SuperCsvMessages;
import org.supercsv.util.CsvContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParseDateMultiple extends CellProcessorAdaptor implements StringCellProcessor {

    private final String[] dateFormatList;

    private final boolean lenient;


    public ParseDateMultiple(final String[] dateFormatList) {
        this(dateFormatList, false);
    }


    public ParseDateMultiple(final String[] dateFormatList, final boolean lenient) {
        super();
        checkPreconditions(dateFormatList);
        this.dateFormatList = dateFormatList;
        this.lenient = lenient;
    }



    public ParseDateMultiple(final String[] dateFormatList, final DateCellProcessor next) {
        this(dateFormatList, false, next);
    }

    public ParseDateMultiple(final String[] dateFormatList, final boolean lenient, final DateCellProcessor next) {
        super(next);
        checkPreconditions(dateFormatList);
        this.dateFormatList = dateFormatList;
        this.lenient = lenient;
    }


    private static void checkPreconditions(final String[] dateFormatList) {
        if ((dateFormatList == null) || dateFormatList.length==0) {
            throw new NullPointerException("dateFormatList should not be null or empty");
        }
    }


    public Object execute(final Object value, final CsvContext context) {
        validateInputNotNull(value, context);

        if( !(value instanceof String) ) {
            throw new SuperCsvCellProcessorException(String.class, value, context, this);
        }


        SimpleDateFormat formatter;
        Date result = null;
        for (String dateFormat : dateFormatList) {
            try {
                if (dateFormat.length()!=((String) value).length()) continue; // para evitar cosas como 2014 match con dd/mm/yyyy

                formatter = new SimpleDateFormat(dateFormat);
                formatter.setLenient(lenient);
                result = formatter.parse((String) value);
            } catch (ParseException e) {
            }
            if (result != null) return next.execute(result, context);
        }
        throw new SuperCsvCellProcessorException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.parsedatemultiple.InvalidDate.message", value), context, this);
    }
}

