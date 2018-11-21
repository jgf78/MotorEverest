package es.cnmc.everest.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author amiguel
 */
public class ERValidator {

    public static boolean validate (String pattern, String str, int flags){
        Pattern r = Pattern.compile(pattern, flags);
        Matcher m = r.matcher(str);
        return m.matches();
    }

}
