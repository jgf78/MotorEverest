package es.cnmc.everest.exception;

import es.cnmc.everest.util.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;


/**
 * Created by dromera on 04/05/2017.
 */
public class RestErrorHandler implements ResponseErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        logger.error("Rest response error: {} {}", response.getStatusCode(), response.getStatusText());
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return RestUtil.isError(response.getStatusCode());
    }

}
