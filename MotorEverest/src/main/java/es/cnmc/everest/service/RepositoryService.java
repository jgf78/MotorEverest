package es.cnmc.everest.service;

import es.cnmc.component.model.Fichero;
import es.cnmc.component.model.OneJSON;
import es.cnmc.component.model.Procedimiento;
import es.cnmc.component.service.exception.StorageException;
import es.cnmc.component.storage.StorageComponent;
import es.cnmc.component.storage.StorageContext;
import es.cnmc.everest.exception.EverestRuntimeException;
import es.cnmc.everest.model.helper.OneJSONHelper;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dromera on 10/05/2016.
 */

@Service
public class RepositoryService {

    @Value("${storage.s3.accessKey}")
    private String accessKey;
    
    @Value("${storage.s3.secretKey}")
    private String secretKey;

    @Autowired
    private StorageComponent storageComponentS3;

    @Autowired
    private OneJSONHelper oneJSONHelper;

    private final int CONNECT_TIMEOUT = 15000;
    private final int READ_TIMEOUT = 15000;


    /**
     * Descarga un fichero del S3.
     */
    public InputStream download(OneJSON oneJSON) throws StorageException, IOException {
        Procedimiento procedimiento = oneJSON.getProcedimientoEntrada();
        Fichero fichero = oneJSONHelper.getFichero(oneJSON);
        return (storageComponentS3.download(new StorageContext(secretKey, accessKey, procedimiento.getIdAlmacenamientoBase()), fichero.getPathAlmacenamiento()));
    }


    /**
     * Descarga un fichero de una URL
     */
    public InputStream download(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) (new URL(url).openConnection());
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpStatus.SC_MOVED_PERMANENTLY) {
            String location = conn.getHeaderField("Location");
            conn = (HttpURLConnection) (new URL(location).openConnection());
            //conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.connect();
            responseCode = conn.getResponseCode();
        }
        if (responseCode == HttpStatus.SC_OK) {
            return conn.getInputStream();
        } else {
            throw new EverestRuntimeException("No se ha podido descargar el fichero " + url + " HttpStatus: " + responseCode);
        }
    }

    /**
     * Sube un archivo al S3.
     */
    public void upload (String idAlmacenamientoBase, String pathAlmacenamiento, String fileName, long fileLength, InputStream stream) throws StorageException {
        storageComponentS3.uploadFile(new StorageContext(secretKey, accessKey, idAlmacenamientoBase), pathAlmacenamiento, fileName, fileLength, stream);
    }




}
