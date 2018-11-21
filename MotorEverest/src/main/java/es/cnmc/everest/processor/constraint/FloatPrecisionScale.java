package es.cnmc.everest.processor.constraint;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.i18n.SuperCsvMessages;
import org.supercsv.util.CsvContext;

    /**
     * Created by dromera on 18/04/2016.
     *
     * Valida que el valor de suministrados es uno de los valores provistos
     *
     */
    public class FloatPrecisionScale extends CellProcessorAdaptor implements StringCellProcessor {

        private int decimalSize;
        private int scaleSize;
        private boolean allowsNegative;

        public FloatPrecisionScale(int precision, int scale, boolean allowsNegative) {
            this.decimalSize = precision - scale;
            this.scaleSize = scale;
            this.allowsNegative = allowsNegative;
        }

        public FloatPrecisionScale(int precision, int scale, boolean allowsNegative, StringCellProcessor next) {
            super(next);
            this.decimalSize =  precision - scale;
            this.scaleSize = scale;
            this.allowsNegative = allowsNegative;
        }

        @Override
        public Object execute(Object value, CsvContext context) {
            this.validateInputNotNull(value, context);

            String result = (String)value;
            boolean isNegative = result.startsWith("-");
            if (!allowsNegative && isNegative) {
                throw new SuperCsvConstraintViolationException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.floatprecisionscale.InvalidSign.message",value), context, this);
            }

            String[] parts = result.split("\\.");
            if (parts.length != 2) {
                throw new SuperCsvConstraintViolationException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.floatprecisionscale.InvalidDecimal.messsage", value), context, this);
            }
            if (decimalSize < (String.valueOf(parts[0]).length() + (isNegative?-1:0))) {
                throw new SuperCsvConstraintViolationException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.floatprecisionscale.LengthDigitsInteger.message", value, decimalSize), context, this);
            }
            if (scaleSize != String.valueOf(parts[1]).length()) {
                throw new SuperCsvConstraintViolationException(SuperCsvMessages.getMessage("org.supercsv.exception.cellprocessor.constraint.floatprecisionscale.LengthDigitsFract.message", value, scaleSize), context, this);
            }

            return this.next.execute(value, context);
    }
}
