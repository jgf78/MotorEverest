package es.cnmc.everest.util;

/**
 * Created by dromera on 27/05/2016.
 */
public class ValidateDocument {

    private static String VALID_NIF_LETTER = "TRWAGMYFPDXBNJZSQVHLCKE";
    private static String VALID_NIE_LETTER = "XYZ";

    private static String VALID_CIF_FIRST_LETTER = "ABCDEFGHJNPQRSUVW";
    private static String VALID_CIF_CD_LETTER = "JABCDEFGHI";

    private enum TypeDC {DIGIT, LETTER, ANY};
    private enum TypePosition {PAIR, ODD};


    public boolean validateNIF(String value, boolean checkSize) {
        if (value == null) return false;

        String nif = value.trim().toUpperCase();
        if (nif.isEmpty()) return false;
        if (checkSize && (nif.length()!=9)) return false;

        char letter = nif.charAt(nif.length() - 1);
        if (!Character.isLetter(letter)) {
            return false;
        }

        int dniValue;
        try {
            dniValue = Integer.parseInt(nif.substring(0, nif.length() - 1));
            if (dniValue < 0) return false;
        } catch (NumberFormatException e) {
            return false;
        }

        return VALID_NIF_LETTER.charAt(dniValue % 23) == letter;
    }

    public boolean validateNIE(String value) {
        if (value == null) return false;

        String cif = value.trim().toUpperCase();
        int indexLetter = VALID_NIE_LETTER.indexOf(cif.charAt(0));
        if (indexLetter<0) return false;

        String nif = indexLetter + cif.substring(1);
        return validateNIF(nif,true);
    }

    public boolean validateCIF(String value) {
        if ((value == null) || value.length()!=9) return false;

        String cif = value.toUpperCase();
        String firstLetter = String.valueOf(cif.charAt(0));
        if (!VALID_CIF_FIRST_LETTER.contains(firstLetter)) return false;

        char controlDigit = cif.charAt(cif.length() - 1);
        TypeDC typeDC = controlDigitMustBeLetter(firstLetter);
        if (typeDC.equals(TypeDC.LETTER) && !Character.isLetter(controlDigit)) return false;
        if (typeDC.equals(TypeDC.DIGIT) && !Character.isDigit(controlDigit)) return false;

        String cifDigits  =cif.substring(1, 8);
        try {
            if (Integer.parseInt(cifDigits) < 0) return false;
        } catch (NumberFormatException e) {
            return false;
        }

        int lastDigit = (sumByPosition(cifDigits, TypePosition.PAIR) + sumByPosition(cifDigits, TypePosition.ODD)) % 10;
        lastDigit = (lastDigit!=0)?10-lastDigit:lastDigit;

        boolean ok = false;
        if (typeDC.equals(TypeDC.DIGIT)) {
            ok = lastDigit == Character.getNumericValue(controlDigit);
        } else if (typeDC.equals(TypeDC.LETTER)) {
            ok = VALID_CIF_CD_LETTER.charAt(lastDigit) == controlDigit;
        } else { // ANY
            ok = (lastDigit == Character.getNumericValue(controlDigit)) || (VALID_CIF_CD_LETTER.charAt(lastDigit) == controlDigit);
        }
        return ok;
    }


    private TypeDC controlDigitMustBeLetter(String firstLetter) {
        TypeDC typeDC;
        if ("PQSW".contains(firstLetter)) {
            typeDC = TypeDC.LETTER;
        } else if ("ABEH".contains(firstLetter)) {
            typeDC = TypeDC.DIGIT;
        } else {
            typeDC = TypeDC.ANY;
        }
        return typeDC;
    }

    private int sumByPosition(String value, TypePosition typePosition) {
        int sum = 0, num;
        int i = typePosition.equals(TypePosition.PAIR)?1:0;

        for (; i<value.length(); i+=2) {
            num = Character.getNumericValue(value.charAt(i));
            if (typePosition.equals(TypePosition.PAIR)) {
                sum += num;
            } else {  // TypePosition.ODD
                num = 2*num;
                sum += (num / 10) + (num%10);
            }
        }
        return sum;
    }
}
