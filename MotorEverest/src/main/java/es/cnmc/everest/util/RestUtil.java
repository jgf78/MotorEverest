package es.cnmc.everest.util;

import org.springframework.http.HttpStatus;

/**
 * Created by dromera on 04/05/2017.
 */
public class RestUtil {

    public static boolean isError(HttpStatus status) {
        HttpStatus.Series series = status.series();
        return (HttpStatus.Series.CLIENT_ERROR.equals(series) || HttpStatus.Series.SERVER_ERROR.equals(series));
    }
}
