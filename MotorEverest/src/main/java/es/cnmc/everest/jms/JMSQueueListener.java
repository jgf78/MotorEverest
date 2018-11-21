package es.cnmc.everest.jms;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.cnmc.component.model.OneJSON;
import es.cnmc.everest.model.helper.OneJSONHelper;
import es.cnmc.everest.monitoring.MailSender;
import es.cnmc.everest.service.EverestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.text.SimpleDateFormat;

@Service("generalEventReceiver")
@Scope("prototype")
public class JMSQueueListener implements MessageListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Logger loggerSolicitudes = LoggerFactory.getLogger("solicitudes");

    private String newline = System.getProperty("line.separator");

    @Autowired
    private OneJSONHelper oneJSONHelper;

    @Autowired
    private EverestService everestService;

    @Autowired
    private MailSender mailSender;




    public void onMessage(final Message message) {
        if (!(message instanceof TextMessage)) {
            String className = (message==null)?null:message.getClass().getName();
            throw new IllegalArgumentException("El mensaje no es TextMessage: " + className);
        }

        OneJSON oneJSON = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

            // Obtiene un OneJSON a partir de un string JSON
            TextMessage textMessage = (TextMessage) message;
            String jsonMessage = textMessage.getText();
            logger.info("JMS recibido:\n " + jsonMessage.replace("\r\n",newline));

            oneJSON = mapper.readValue(jsonMessage, OneJSON.class);

            // Valida que el formato OneJSON sea lo que espera el Motor Everest
            oneJSONHelper.checkJMSEverestFormat(oneJSON);

            // Procesa la solicitud de carga
            logger.info("Procesando solicitud de carga...");
            everestService.process(oneJSON);
        }
        catch (Exception e)
        {
            loggerSolicitudes.info(String.format("Resultado solicitud '%s' - '%s'", (oneJSON==null)?null:oneJSON.getUuidEntrada(), "ERROR NO CONTROLADO"));
            logger.error("Error!",e);
            mailSender.sendEmailError(e);
        }
    }




}