package es.cnmc.everest.sede;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 * Created by dromera on 10/04/2017.
 */
@Component
public class UtilRest {

    @Value("${apicnmc.domain}")
    private String apicnmcDomanin;


    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    private HttpHeaders createHeaders( String username, String password ){
        return new HttpHeaders(){
            {
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")) );
                String authHeader = "Basic " + new String( encodedAuth );
                set( "Authorization", authHeader );
                set ("Content-Type", MediaType.APPLICATION_JSON.toString());
            }
        };
    }

    public String composeURL(String url) {
        return new StringBuilder(apicnmcDomanin).append(url).toString();
    }
}


