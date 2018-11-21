package es.cnmc.everest.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by dromera on 17/04/2017.
 */
@Component
public class TipoFicheroConst {


    @Value("${tipofichero.circular.circular.id}")
    public Integer CIRCULAR_CIRCULAR;

    @Value("${tipofichero.distribucion.inventario.id}")
    public Integer DISTRIBUCION_INVENTARIO;

    @Value("${tipofichero.distribucion.auditoria.id}")
    public Integer DISTRIBUCION_AUDITORIA;

    @Value("${tipofichero.distribucion.planinversion.id}")
    public Integer DISTRIBUCION_PLANINVERSION;

    @Value("${tipofichero.transporte.inventario.id}")
    public Integer TRANSPORTE_INVENTARIO;

    @Value("${tipofichero.transporte.auditoria.id}")
    public Integer TRANSPORTE_AUDITORIA;

    @Value("${tipofichero.transporte.planinversion.id}")
    public Integer TRANSPORTE_PLANINVERSION;

}
