package es.cnmc.everest;

import es.cnmc.everest.model.TipoFicheroEverest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * La propiedad nombre determina el subdirectorio y nombre del fichero de formato y de validaciones
 */
@Configuration
public class EverestConfiguration {

    @Bean
    public TipoFicheroEverest createCircularCircular(@Value("#{T(java.lang.Integer).parseInt('${tipofichero.circular.circular.id}')}") Integer id) {
        TipoFicheroEverest tipoFichero = new TipoFicheroEverest();
        tipoFichero.setId(id);
        tipoFichero.setNombre("circular");
        return tipoFichero;
    }

    @Bean
    public TipoFicheroEverest createDistribucionInventario(@Value("#{T(java.lang.Integer).parseInt('${tipofichero.distribucion.inventario.id}')}") Integer id) {
        TipoFicheroEverest tipoFichero = new TipoFicheroEverest();
        tipoFichero.setId(id);
        tipoFichero.setNombre("inventariodistribucion");
        return tipoFichero;
    }

    @Bean
        public TipoFicheroEverest createDistribucionAuditoria(@Value("#{T(java.lang.Integer).parseInt('${tipofichero.distribucion.auditoria.id}')}") Integer id) {
            TipoFicheroEverest tipoFichero = new TipoFicheroEverest();
            tipoFichero.setId(id);
            tipoFichero.setNombre("auditoriadistribucion");
            return tipoFichero;
    }

    @Bean
    public TipoFicheroEverest createDistribucionPlanesInversion(@Value("#{T(java.lang.Integer).parseInt('${tipofichero.distribucion.planinversion.id}')}") Integer id) {
        TipoFicheroEverest tipoFichero = new TipoFicheroEverest();
        tipoFichero.setId(id);
        tipoFichero.setNombre("planesinversiondistribucion");
        return tipoFichero;
    }

    @Bean
    public TipoFicheroEverest createTransporteInventario(@Value("#{T(java.lang.Integer).parseInt('${tipofichero.transporte.inventario.id}')}") Integer id) {
        TipoFicheroEverest tipoFichero = new TipoFicheroEverest();
        tipoFichero.setId(id);
        tipoFichero.setNombre("inventariotransporte");
        return tipoFichero;
    }

    @Bean
    public TipoFicheroEverest createTransporteAuditoria(@Value("#{T(java.lang.Integer).parseInt('${tipofichero.transporte.auditoria.id}')}") Integer id) {
        TipoFicheroEverest tipoFichero = new TipoFicheroEverest();
        tipoFichero.setId(id);
        tipoFichero.setNombre("auditoriatransporte");
        return tipoFichero;
    }

    @Bean
    public TipoFicheroEverest createTransportePlanesInversion(@Value("#{T(java.lang.Integer).parseInt('${tipofichero.transporte.planinversion.id}')}") Integer id) {
        TipoFicheroEverest tipoFichero = new TipoFicheroEverest();
        tipoFichero.setId(id);
        tipoFichero.setNombre("planesinversiontransporte");
        return tipoFichero;
    }

}
