package es.cnmc.everest.jms;

import es.cnmc.component.jms.JMSMessagesProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import es.cnmc.component.model.util.KeyValue;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Singleton helper to resend dismissed messages.
 *
 * @author amiguel
 */
@Component
public class JMSHelper {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static Boolean dismissMessage = Boolean.FALSE;
    private static JMSHelper jmsHelper;
    private static List<KeyValue> dismissedMessages = new ArrayList<>();


    public static JMSHelper getInstance() {
        if (jmsHelper == null) {
            jmsHelper = new JMSHelper();
        }
        return jmsHelper;
    }

    public void sendDismissedMessages(JMSMessagesProducer producer, String queue, String dismiss) {

        int k = 0;
        int dismissed = 0;

        try {

            //tokenize message ids to dismiss, comma-separated
            HashSet<String> dismissHash = new HashSet<>();
            if (dismiss != null) {
                log.debug("Dismissed messages: " + dismissed);
                StringTokenizer st = new StringTokenizer(dismiss.replaceAll(" ", "").trim(), ",");
                while (st.hasMoreTokens()) {
                    dismissHash.add(st.nextToken());
                }
            }

            //if dismissed saved messages, then resend them to queue
            while (!dismissedMessages.isEmpty()) {
                k++;
                KeyValue keyValue = dismissedMessages.get(0);
                String value = (String) keyValue.getValue();
                //if not to dismiss, reinject the message
                if (!dismissHash.contains(keyValue.getKey())) {
                    producer.sendTextMessage(value, queue);
                    log.debug("Reinject message to queue: " + keyValue.getKey());
                } else {//descarta
                    dismissed++;
                }
                dismissedMessages.remove(keyValue);
            }

        } catch (JMSException e) {
            log.error(e.getMessage(),e);
        }
    }

    public static Boolean getDismissMessage() {
        return dismissMessage;
    }

    public static void setDismissMessage(Boolean dismissMessage) {
        JMSHelper.dismissMessage = dismissMessage;
    }

    public static List<KeyValue> getDismissedMessages() {
        return dismissedMessages;
    }

    public static void setDismissedMessages(List<KeyValue> dismissedMessages) {
        JMSHelper.dismissedMessages = dismissedMessages;
    }

}