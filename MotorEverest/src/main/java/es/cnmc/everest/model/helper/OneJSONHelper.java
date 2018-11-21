package es.cnmc.everest.model.helper;

import es.cnmc.component.model.Fichero;
import es.cnmc.component.model.OneJSON;
import es.cnmc.component.model.RolEmpresa;
import es.cnmc.component.model.formulario.Campo;
import es.cnmc.component.model.formulario.Seccion;
import es.cnmc.component.model.formulario.SubSeccion;
import es.cnmc.everest.exception.EverestRuntimeException;
import es.cnmc.everest.model.EmpresaElectrica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utilidad a para un OneJSON del tipo Everest
 */
@Component
public class OneJSONHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${tipofichero.justificante}")
    private Integer idTipoFicheroJustificante;

    @Autowired
    private TipoFicheroEverestHelper tipoFicheroEverestHelper;

    // Secciones
    public final String SECCION_INFO = "Información aportada en el formulario";

    // Subsecciones
    public final String SUBSECCION_EMPRESA = "Empresa";
    public final String SUBSECCION_ANIO_REFERENCIA = "Año de referencia para el que se entrega la información";

    // Empresa
    public final String CAMPO_NOMBRE = "_02nombre";
    public final String CAMPO_CIF = "_03nif";
    public final String CAMPO_R = "_04codigo";

    // Año referencia
    public final String CAMPO_ANIO_REFERENCIA = "_01anyoReferencia";



    /**
     * Según las anotaciones de los beans no puede ser nulo:
     *  - fichero.idTipoFichero
     *  - fichero.idFichero
     *  - procedimientoEntrada.idProcedimiento
     * @param oneJSON
     */
    public void checkJMSEverestFormat(OneJSON oneJSON) {
        if (oneJSON == null) {
            throw new EverestRuntimeException("oneJSON nulo");
        }
        if ((oneJSON.getUuidEntrada() == null) || "".equals(oneJSON.getUuidEntrada())) {
            throw new EverestRuntimeException("oneJSON.uuidEntrada vacío");
        }
        if ((oneJSON.getInteresados() == null) || (oneJSON.getInteresados().size() != 1)) {
            throw new EverestRuntimeException("oneJSON.interesados vacío (null o size=0) ó con más de un interesado");
        }
        if (oneJSON.getInteresados().get(0).getRoles() == null) {
            throw new EverestRuntimeException("oneJSON.interesados(0).roles vacío (null)");
        }
        if ((oneJSON.getPresentadores() == null) || (oneJSON.getPresentadores().size() != 1)) {
            throw new EverestRuntimeException("oneJSON.presentadores vacío (null o size=0) ó con más de un presentador");
        }
        if ((oneJSON.getUrlJustificante() == null) || "".equals(oneJSON.getUrlJustificante())) {
            throw new EverestRuntimeException("oneJSON.justificante vacío");
        }

        // Ficheros
        if ((oneJSON.getFicheros() == null) || "".equals(oneJSON.getFicheros().size() != 2)) {
            throw new EverestRuntimeException("oneJSON.ficheros vacío o con size<>2");
        } else if (getFichero(oneJSON)==null) {
            throw new EverestRuntimeException("oneJSON.ficheros no contiene un documento adjtunto permitido");
        } else if ((getFichero(oneJSON).getTipoFichero() == null) || (getFichero(oneJSON).getIdTipoFichero() == null)) {
            throw new EverestRuntimeException("oneJSON.ficheros.fichero.tipoFichero vacío");
        }

        // Justificante
        if (getJustificante(oneJSON)==null) {
            throw new EverestRuntimeException("oneJSON.ficheros no contienen el justificante");
        }

        // Procedimiento
        if (oneJSON.getProcedimientoEntrada() == null) {
            throw new EverestRuntimeException("oneJSON.procedimiento nulo");
        }

        // Carga
        if (oneJSON.getCarga() == null) {
            throw new EverestRuntimeException("oneJSON.carga nula");
        } else if ("CANCELADO".equals(oneJSON.getCarga().getCodigoEstado())) {
            throw new EverestRuntimeException("oneJSON.carga.codigoEstado es CANCELADO");
        } else if ((oneJSON.getCarga().getNumeroRegistroGeneral() == null) || "".equals(oneJSON.getCarga().getNumeroRegistroGeneral())) {
            throw new EverestRuntimeException("oneJSON.carga.numeroRegistroGeneral vacío");
        }

        // Secciones.Empresa
        // Excepción para CIRCULAR y DISTRIBUCION si no existe la sección "Empresa" o no vienen el R1
        getEmpresa(oneJSON);

        getAnioReferencia(oneJSON);
    }

    public Fichero getFichero(OneJSON oneJSON) {
        for (Fichero fichero : oneJSON.getFicheros()) {
            if (tipoFicheroEverestHelper.valueOf(fichero.getIdTipoFichero()) != null) return fichero;
        }
        return null;
    }


    public Fichero getJustificante(OneJSON oneJSON) {
        for (Fichero fichero : oneJSON.getFicheros()) {
            if (fichero.getIdTipoFichero().equals(idTipoFicheroJustificante)) return fichero;
        }
        return null;
    }

    /**
     * Precondición: oneJSON debe de contener un fichero de tipo conocido y un único interesado
     * Los ficheros de transporte no tienen R1 en la sección empresa. Además vienen con nombres de etiquetas distintos al resto de ficheros.
     * @param oneJSON
     * @return
     * @exception EverestRuntimeException para ficheros de TRANSPORTE que no se encuentra la sección empresa y/o código R1
     */
    public EmpresaElectrica getEmpresa(OneJSON oneJSON) {
        EmpresaElectrica empresaElectrica = new EmpresaElectrica();
        Fichero fichero = getFichero(oneJSON);
        if (tipoFicheroEverestHelper.isFicheroTransporte(fichero.getIdTipoFichero())) {
            empresaElectrica.setNif(oneJSON.getInteresados().get(0).getNif());
            empresaElectrica.setNombre(oneJSON.getInteresados().get(0).getNombre());
        } else {
            // Ya que hay que ir al nodo a conseguir el R1... cojo la empresa de ahí, no de interesados
            Seccion seccionInfoAportada = findSeccion(oneJSON.getSecciones(), SECCION_INFO);
            List<Campo> camposEmpresa = findSubseccion(seccionInfoAportada.getSubSecciones(), SUBSECCION_EMPRESA).getCampos();

            empresaElectrica.setNif(findValorCampo(camposEmpresa, CAMPO_CIF));
            empresaElectrica.setNombre(findValorCampo(camposEmpresa, CAMPO_NOMBRE));
            empresaElectrica.setCodigoR(findValorCampo(camposEmpresa, CAMPO_R).toUpperCase());
        }

        return empresaElectrica;
    }


    public String getAnioReferencia(OneJSON oneJSON) {
        Seccion seccionInfoAportada = findSeccion(oneJSON.getSecciones(), SECCION_INFO);
        List<Campo> camposAnioReferencia = findSubseccion(seccionInfoAportada.getSubSecciones(), SUBSECCION_ANIO_REFERENCIA).getCampos();

        return findValorCampo(camposAnioReferencia, CAMPO_ANIO_REFERENCIA);
    }


    public List<RolEmpresa> roles(OneJSON oneJSON) {
        return oneJSON.getInteresados().get(0).getRoles();
    }

    private Seccion findSeccion(List<Seccion> secciones, String nombre) {
        for (Seccion seccion : secciones) {
            if (seccion.getNombre().equals(nombre)) {
                return seccion;
            }
        }
        throw new EverestRuntimeException("Seccion " + nombre + " no encontrada");
    }


    private SubSeccion findSubseccion(List<SubSeccion> subsecciones, String nombre) {
        for (SubSeccion subseccion : subsecciones) {
            if (subseccion.getNombre().equals(nombre)) {
                return subseccion;
            }
        }
        throw new EverestRuntimeException("SubSeccion " + nombre + " no encontrada");
    }

    private static Campo findCampo(List<Campo> campos, String nombre) {
        for (Campo campo : campos) {
            if (campo.getNombre().equals(nombre)) {
                return campo;
            }
        }
        throw new EverestRuntimeException("Campo " + nombre + " no encontrada");
    }

    private static String findValorCampo(List<Campo> campos, String nombre) {
        String valor = findCampo(campos,nombre).getValor();
        return valor.trim();
    }


}
