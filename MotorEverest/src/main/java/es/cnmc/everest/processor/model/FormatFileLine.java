package es.cnmc.everest.processor.model;


public class FormatFileLine {

    private static final String[] SYNTAX_TYPE_HEADERS = new String[]{"fileId", "name", "type", "length", "min", "max", "nullable"};

    private String fileId;
    private String name;
    private String type;
    private String length;
    private String min;
    private String max;
    private String nullable;

    public static String[] getSyntaxTypeHeaders() {
        return SYNTAX_TYPE_HEADERS;
    }


    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable () {
        return "true".equals(this.nullable);
    }

    @Override
    public String toString() {
        return "FormatFileLine{" +
                "fileId='" + fileId + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", length='" + length + '\'' +
                ", min='" + min + '\'' +
                ", max='" + max + '\'' +
                ", nullable='" + nullable + '\'' +
                '}';
    }
}
