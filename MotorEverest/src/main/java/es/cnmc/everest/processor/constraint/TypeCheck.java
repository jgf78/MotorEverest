package es.cnmc.everest.processor.constraint;

/**
 * Created by dromera on 27/04/2016.
 */
public enum TypeCheck {
    YEAR("yyyy"), DATE_DDMMYYY("dd/mm/yyyy"), DATE_MMYYY("mm/yyyy");

    private String value;

    TypeCheck(String value) {
        this.value = value;
    }

    public static TypeCheck fromValue(String value) {
        for (TypeCheck typeCheck : values()) {
            if (typeCheck.value.equals(value)) return typeCheck;
        }
        return null;
    }

}

