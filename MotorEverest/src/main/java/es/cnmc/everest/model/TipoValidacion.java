package es.cnmc.everest.model;

/**
 * Created by dromera on 17/04/2017.
 */
public enum TipoValidacion {

    TODAS("todas"), NINGUNA("ninguna"), APLAZADA("aplazada");

    private String value;

    TipoValidacion (String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TipoValidacion getByValue(String value) {
        for (TipoValidacion tipoValidacion : values()) {
            if (tipoValidacion.value.equals(value)) return tipoValidacion;
        }
        return null;
    }

}
