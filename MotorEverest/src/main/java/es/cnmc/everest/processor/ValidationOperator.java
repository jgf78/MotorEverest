package es.cnmc.everest.processor;

/**
 * Created by dromera on 18/04/2016.
 */
public enum ValidationOperator {
        IN_OPTIONAL_THEN_CONSTRAINT("IN_OPTIONAL_THEN_CONSTRAINT"),
        IN_STRICT_THEN_TYPE("IN_STRICT_THEN_TYPE"),
        IN_STRICT_THEN_CHECKFUNCTION("IN_STRICT_THEN_CHECKFUNCTION"),
        IN_OPTIONAL_THEN_CHECKFUNCTION("IN_OPTIONAL_THEN_CHECKFUNCTION"),
        IN_STRICT_THEN_CONSTRAINT("IN_STRICT_THEN_CONSTRAINT"),
        IN_STRICT("IN_STRICT"),
        GREATER_EQUAL(">="),
        MINUS_EQUAL("<="),
        MINUS("<"),
        EQUALS("="),
        NOT_EQUALS("!="),
        GREATER(">");

        private String name;

        ValidationOperator(String name) {
                this.name = name;
        }

        public String getName() {
                return name;
        }
}
