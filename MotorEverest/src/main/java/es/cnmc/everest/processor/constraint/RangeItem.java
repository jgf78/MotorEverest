package es.cnmc.everest.processor.constraint;

/**
 * Created by dromera on 19/04/2016.
 */
public class RangeItem {
    public enum Operator {
        EQ("="), MINUS("<"), MINUS_EQ("<="), GREATER(">"), GREATER_EQ(">=");

        private String op;

        Operator(String op) {
            this.op = op;
        }

        public String getValue() {
            return this.op;
        }
    }

    private Double value;
    private Operator operator;

    public boolean validate(Double valueCheck) {
        boolean result = false;
        switch (operator) {
            case EQ:
                result =  valueCheck.compareTo(value) == 0;
                break;
            case MINUS:
                result = valueCheck.compareTo(value) < 0;
                break;
            case MINUS_EQ:
                result = valueCheck.compareTo(value) <= 0;
                break;
            case GREATER:
                result = valueCheck.compareTo(value) > 0;
                break;
            case GREATER_EQ:
                result = valueCheck.compareTo(value) <= 0;
                break;
        }
        return result;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}
