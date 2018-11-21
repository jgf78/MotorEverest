package es.cnmc.everest.sede;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.cnmc.component.jms.JMSMessagesProducer;
import es.cnmc.component.model.Evento;
import es.cnmc.component.model.EventoFichero;
import es.cnmc.component.model.OneJSON;
import es.cnmc.everest.model.helper.OneJSONHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.Date;

/**
 * Created by dromera on 25/08/2016.
 */
@Service
public class EventService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${queue.eventos.name}")
    private String eventosQueue;

    @Value("${queue.aplazadas.name}")
    private String aplazadasQueue;

    @Value("${evento.idTipoEvento}")
    private Integer idTipoEvento;

    @Autowired
    private JMSMessagesProducer producer;

    @Autowired
    private OneJSONHelper oneJSONHelper;


    /**
     * Envia un mensaje Evento a la cola de eventos de la Sede.
     */
    public void sendEventToSede(OneJSON oneJSON, String codEstado, String nombreEvento, long errorNum) throws IOException, JMSException {
        Date now = new Date();
        Integer idFichero = oneJSONHelper.getFichero(oneJSON).getIdFichero();

        Assert.notNull(idFichero);

        EventoFichero eventoFichero = new EventoFichero();
        eventoFichero.setCodigoEstado(codEstado);
        eventoFichero.setFechaAlta(now);
        eventoFichero.setHayFicheroErrores(errorNum > 0);
        eventoFichero.setIdFichero(idFichero);
        eventoFichero.setRegistroErrores(errorNum);

        Evento ev = new Evento();
        ev.setFecha(now);
        ev.setIdTipoEvento(idTipoEvento);
        ev.setNombre(nombreEvento);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.valueToTree(ev);
        node.putPOJO("detalle", eventoFichero);

        log.info("Enviado mensaje a '" + eventosQueue + "': " + codEstado);
        producer.sendJSONMessage(node, eventosQueue, null);
    }


    /**
     * Envia el OneJSON a la cola de "solicitudes aplazadas"
     */
    public void sendEventToAplazadas(OneJSON oneJSON) throws IOException, JMSException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.valueToTree(oneJSON);

        log.info("Reenviando mensaje aplazado a '" + aplazadasQueue + "'");
        producer.sendJSONMessage(node, aplazadasQueue, null);
    }

}
