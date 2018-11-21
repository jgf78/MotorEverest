package es.cnmc.everest.exception;

/**
 * Created by dromera on 08/07/2016.
 */
public class EverestRuntimeException extends RuntimeException {

    public EverestRuntimeException() {
        super();
    }

    public EverestRuntimeException(String message){
        super(message);
    }

    public EverestRuntimeException(String message, Throwable cause) {

        super(message, cause);
    }
}
