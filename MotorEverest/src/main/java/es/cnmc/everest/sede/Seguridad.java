package es.cnmc.everest.sede;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.cnmc.component.model.Rol;
import es.cnmc.everest.exception.EverestRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Created by dromera on 10/04/2017.
 */
@Component
public class Seguridad {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UtilRest utilRest;

    @Autowired
    @Qualifier("sedeRestTemplate")
    private RestTemplate rest;

    private String urlRoles = "/motorsede/interno/seguridad/v1/roles/empresa/{nif}";


    public Rol[] getRoles(String nif) throws IOException {
        String restUrl = utilRest.composeURL(urlRoles);
        HttpHeaders headers = utilRest.getHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        //ResponseEntity<Rol[]> response  = rest.exchange(restUrl, HttpMethod.GET, entity, Rol[].class, nif);
        //Rol[] rolList = response.getBody();
        ResponseEntity<String> response  = rest.exchange(restUrl, HttpMethod.GET, entity, String.class, nif);
        String responseBody = response.getBody();
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Rol[] rolList = objectMapper.readValue(responseBody, Rol[].class);
            if (rolList == null) {
                rolList = new Rol[0];
            }
            return rolList;
        } else {
            String msg = String.format("Error en invocación a %s (%s). StatusCode=%s", restUrl, nif, response.getStatusCode());
            logger.error(msg);
            logger.error("Response body: " + responseBody);
/*
            if (RestUtil.isError(response.getStatusCode())) {
                ObjectMapper objectMapper = new ObjectMapper();
                String errorStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response.getBody());
                //ProblemJson problemJson = objectMapper.readValue(response.getBody(), ProblemJson.class);
                logger.error(errorStr);
            } else {*/
                throw new EverestRuntimeException(msg);
            //}
        }
    }
}
