package es.cnmc.everest.monitoring;

import es.cnmc.component.model.Fichero;
import es.cnmc.component.model.OneJSON;
import es.cnmc.everest.model.helper.OneJSONHelper;
import es.cnmc.everest.service.MailService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by dromera on 05/10/2016.
 */
@Service
public class MailSender {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MailService mailService;

    @Autowired
    private OneJSONHelper oneJSONHelper;

    @Value("${motoreverest.entorno}")
    private String environment;

    @Value("${motoreverest.mail.error.to}")
    private String mailErrorTo;

    @Value("${motoreverest.mail.error.message}")
    private String mailErrorMessage;

    @Value("${motoreverest.mail.cargaOk.to}")
    private String mailCargaOkTo;

    @Value("${motoreverest.mail.cargaOk.message}")
    private String mailCargaOkMessage;

    private static final String MAIL_TO_SEPARATOR=";";


    @Async
    public void sendEmailError(Object error) {
        sendEmailError(null, error);
    }

    @Async
    public void sendEmailError(OneJSON oneJSON, Object error) {
        try {
            HashMap<String,String> params = new HashMap<>();
            if (oneJSON != null) {
                Fichero ficheroAdjunto = oneJSONHelper.getFichero(oneJSON);
                String idProcedimiento = oneJSON.getProcedimientoEntrada()!=null?String.valueOf(oneJSON.getProcedimientoEntrada().getIdProcedimiento()) : "";
                String idFichero = "";
                String uuidFichero = "";
                String zipFileName = "";
                if (ficheroAdjunto != null) {
                    idFichero = String.valueOf(ficheroAdjunto.getIdFichero());
                    uuidFichero = String.valueOf(ficheroAdjunto.getUuid());
                    zipFileName = ficheroAdjunto.getNombreFichero();
                }
                params.put("uuidEntrada", oneJSON.getUuidEntrada());
                params.put("idProcedimiento", idProcedimiento);
                params.put("idFichero", idFichero);
                params.put("uuidFichero", uuidFichero);
                params.put("zipFileName", zipFileName);
            }
            if (error instanceof Exception) {
                params.put("exception", ((Exception)error).getMessage() + " Trace: " + ExceptionUtils.getStackTrace((Exception)error));
            } else {
                params.put("exception", error.toString());
            }

            mailService.send(environment + ": Error Motor Everest",Arrays.asList(mailErrorTo.split(MAIL_TO_SEPARATOR)),mailErrorMessage,null,null,params);
            logger.info(String.format("Email enviado a %s ...",mailErrorTo));
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
        }
    }

    @Async
    public void sendEmailCargaOk(OneJSON oneJSON) {
        if (!"PROD".equalsIgnoreCase(environment)) return;

        try {
            Fichero fichero = oneJSONHelper.getFichero(oneJSON);

            HashMap<String, String> params = new HashMap<>();
            params.put("numeroRegistroGeneral", oneJSON.getAsientoRegistro().getNumeroRegistroGeneral());
            params.put("tipoFichero", fichero.getTipoFichero().getNombrecorto());
            params.put("nombreFichero", fichero.getNombreFichero());

            mailService.send("Nueva carga", Arrays.asList(mailCargaOkTo.split(MAIL_TO_SEPARATOR)), mailCargaOkMessage, null, null, params);
            logger.info(String.format("Email enviado a %s ...",mailCargaOkTo));
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }


}
