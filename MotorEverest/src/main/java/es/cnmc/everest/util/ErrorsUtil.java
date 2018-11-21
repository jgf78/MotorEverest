package es.cnmc.everest.util;

import es.cnmc.component.model.ErrorFichero;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by dromera on 17/04/2017.
 */
@Component
public class ErrorsUtil {


    /**
     * Devuelve el numero de errores de linea totales y elimina elementos de la lista sin error.
     */
    public long getNumberOfErrors(List<ErrorFichero> errors){
        long numTotalErrors = 0;

        for (int i = 0; i < errors.size(); i++) {
            numTotalErrors += errors.get(i).getErrores().size();
        }
        return numTotalErrors;
    }


    /**
     * Elimina los ErrorFichero que tengan su getErrores().size == 0
     */
    public void compactErrors(List<ErrorFichero> errors) {
        for (int i = 0; i < errors.size(); i++) {
            ErrorFichero error = errors.get(i);
            if (error.getErrores().size()==0){
                errors.remove(error);
                i = i - 1;
            }
        }
    }


}
