package es.cnmc.everest.model.helper;

import es.cnmc.everest.model.TipoFicheroConst;
import es.cnmc.everest.model.TipoFicheroEverest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dromera on 19/04/2017.
 */
@Component
public class TipoFicheroEverestHelper {

    @Autowired
    private TipoFicheroConst tipoFicheroConst;

    @Autowired
    private List<TipoFicheroEverest> tipoFicheroList;

    private Set<Integer> transporteFiles;

    @PostConstruct
    private void init() {
        transporteFiles = new HashSet<>();
        transporteFiles.add(tipoFicheroConst.TRANSPORTE_AUDITORIA);
        transporteFiles.add(tipoFicheroConst.TRANSPORTE_INVENTARIO);
        transporteFiles.add(tipoFicheroConst.TRANSPORTE_PLANINVERSION);
    }


    public TipoFicheroEverest valueOf(Integer id) {
        for (TipoFicheroEverest tipoFichero : tipoFicheroList) {
            if (tipoFichero.getId().equals(id)) return tipoFichero;
        }
        return null;
    }

    public boolean isFicheroTransporte(Integer idTipoFichero) {
        return transporteFiles.contains(idTipoFichero);
    }
}
