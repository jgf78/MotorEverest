package es.cnmc.everest;


/**
 * @author amiguel
 */
public class Constants {

    public static final int MAX_ERRORS_BY_DATA_FILE  = 1000;

    public static final String DDMMYYYY_DATE_FORMAT = "dd/MM/yyyy";
    public static final String YYYY_DATE_FORMAT = "yyyy";

    public static final String YYYY_DATE_REGEX = "[1|2]\\d{3}";
    public static final String MM_DATE_REGEX = "([0][1-9]|[1][0-2])";
    public static final String DD_DATE_REGEX = "([0-3][0-9])";
    public static final String DDMMYYYY_DATE_REGEX = DD_DATE_REGEX + "\\/" + MM_DATE_REGEX + "\\/" + YYYY_DATE_REGEX;

    public static final String YYYY_DDMMYYYY_REGEX = "((19|20)\\d{2}|\\d{2}\\/(0[1-9]|1[012])\\/(19|20)\\d{2})?";

}

