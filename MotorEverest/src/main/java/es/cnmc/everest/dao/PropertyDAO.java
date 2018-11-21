package es.cnmc.everest.dao;

import es.cnmc.component.model.OneJSON;
import es.cnmc.everest.exception.EverestRuntimeException;
import es.cnmc.everest.model.TipoFicheroEverest;
import es.cnmc.everest.model.helper.OneJSONHelper;
import es.cnmc.everest.model.helper.TipoFicheroEverestHelper;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dromera on 10/04/2017.
 */
@Component
public class PropertyDAO {

    public static final String PROPERTIES_FILE_NAME = System.getProperty("user.home") + File.separator + "configuracion" + File.separator + "motoreverest" + File.separator + "motoreverest-database.properties";

    PropertiesConfiguration props;

    @Autowired
    private TipoFicheroEverestHelper tipoFicheroEverestHelper;

    @Autowired
    private OneJSONHelper oneJSONHelper;


    @PostConstruct
    private void init() throws ConfigurationException {
        props = new PropertiesConfiguration(PROPERTIES_FILE_NAME);
        props.setReloadingStrategy(new FileChangedReloadingStrategy());
        props.setListDelimiter(',');
    }


    public String getPropertyValue(OneJSON oneJSON, String item) throws ConfigurationException {
        String property = getPrefix(oneJSON) + "." + item;

        //Configuration configFile = config.subset(prefix);
        String value = props.getString(property);
        if ((value==null) || value.isEmpty()) {
            throw new EverestRuntimeException(String.format("Property [%s] no encontrada en '%s'", property, PROPERTIES_FILE_NAME));
        }
        return value;
    }

    public String getPropertyValue(String property) throws ConfigurationException {
        String value = props.getString(property);
        if ((value==null) || value.isEmpty()) {
            throw new EverestRuntimeException(String.format("Property [%s] no encontrada en '%s'", property, PROPERTIES_FILE_NAME));
        }
        return value;
    }

    public List<String> getPropertyList(String property) throws ConfigurationException {
        List<String> valueList = Arrays.asList(props.getStringArray(property));
        if (valueList.isEmpty()) {
            throw new EverestRuntimeException(String.format("Property [%s] no encontrada o lista vacía en '%s'", property, PROPERTIES_FILE_NAME));
        }
        return valueList;
    }

    private String getPrefix(OneJSON oneJSON) {
        Integer idProcedimiento = oneJSON.getProcedimientoEntrada().getIdProcedimiento();
        Integer idTipoFichero = oneJSONHelper.getFichero(oneJSON).getIdTipoFichero();
        return "idProc." + String.valueOf(idProcedimiento) + ".idTipoFichero." + String.valueOf(idTipoFichero);
    }

    public String getPathFormatFile (OneJSON oneJSON, String year, String idItemFile) {
        String path = composePathValidationFile(oneJSON, year, idItemFile);
        return path + "-format.csv";
    }

    public String getPathValidateionFile (OneJSON oneJSON, String year, String idItemFile) {
        String path = composePathValidationFile(oneJSON, year, idItemFile);
        return path + "-business-validation.xml";
    }


    private String composePathValidationFile (OneJSON oneJSON, String year, String idItemFile) {
        Integer idTipoFichero = oneJSONHelper.getFichero(oneJSON).getTipoFichero().getIdTipoFichero();
        TipoFicheroEverest tipoFicheroEverest = tipoFicheroEverestHelper.valueOf(idTipoFichero);

        StringBuilder formatFileDir = new StringBuilder();
        formatFileDir.append(System.getProperty("user.home")).append(File.separator);
        formatFileDir.append("configuracion").append(File.separator);
        formatFileDir.append("motoreverest").append(File.separator);
        formatFileDir.append("everest").append(File.separator);
        formatFileDir.append(year).append(File.separator);
        formatFileDir.append(tipoFicheroEverest.getNombre()).append(File.separator);
        formatFileDir.append(year).append(".").append(idItemFile).append("-").append(tipoFicheroEverest.getNombre());

        return formatFileDir.toString();
    }

}
