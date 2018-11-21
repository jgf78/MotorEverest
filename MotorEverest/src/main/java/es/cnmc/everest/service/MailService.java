package es.cnmc.everest.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.commons.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeUtility;
import java.io.*;
import java.util.HashMap;
import java.util.List;

/**
 * @author amiguel
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${motoreverest.mail.host}")
    private String host;

    @Value("${motoreverest.mail.username}")
    private String username;

    @Value("${motoreverest.mail.password}")
    private String password;

    @Value("${motoreverest.mail.from}")
    private String from;

    /**
     * Envía un correo electrónico.
     * @param subject
     * @param addresses
     * @param message
     * @param attachmentPath
     * @param attachmentName
     * @throws EmailException
     */
    public void send(String subject, List<String> addresses, String message,
                     String attachmentPath, String attachmentName, HashMap<String, String> params) throws EmailException, IOException {
        EmailAttachment attachment = attach(attachmentPath, "", attachmentName);
        Email email = compose(subject, addresses, message, attachment, params);
        email.send();
    }

    /**
     * Compone un correo electrónico con adjunto.
     * @param subject
     * @param addresses
     * @param message
     * @param attachment
     * @return
     * @throws EmailException
     */
    private Email compose(String subject, List<String> addresses, String message, EmailAttachment attachment, HashMap<String, String> params) throws EmailException,  IOException {
        HtmlEmail email = new HtmlEmail();
        email.setHtmlMsg("<html><body>" + message + "</body></html>");
        email.setTextMsg(message);

        if (attachment != null) {
            email.attach(attachment);
        }

        email.setHostName(host);
        email.setFrom(from);

        try {
            email.setSubject(MimeUtility.encodeText(subject, "UTF-8", "Q"));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }

        //email.setSubject(subject);
        for (String address:addresses) {
            email.addTo(address);
        }
        String formattedMessage = getTextFromTemplate(params, message, 300);
        email.setMsg(formattedMessage);
        /* Not required authentication!
        email.setAuthenticator(new DefaultAuthenticator(username, password));
        email.setSSLOnConnect(false);
        email.setStartTLSEnabled(false);
        */
        //email.setCharset(EmailConstants.UTF_8);

        return email;
    }

    /**
     * Crea un adjunto.
     * @param path
     * @param description
     * @param name
     * @return
     */
    private EmailAttachment attach(String path, String description, String name) {

        EmailAttachment attachment = null;

        //create the attachment
        if (path != null) {
            attachment = new EmailAttachment();
            attachment.setPath(path);
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
            attachment.setDescription(description);
            attachment.setName(name);
        }

        return attachment;
    }

    /**
     * Obtiene texto formateado a partir de otro texto.
     * @param object
     * @param text
     * @return
     * @throws IOException
     */
    public String getTextFromTemplate (Object object, String text, int initialSize) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Reader reader = new StringReader(text);
        Mustache mustache = mf.compile(reader, null);
        Writer writer = new StringWriter(initialSize);
        mustache.execute(writer, object).flush();
        return writer.toString();
    }

}