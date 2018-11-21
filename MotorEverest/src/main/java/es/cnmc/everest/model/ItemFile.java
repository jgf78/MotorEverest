package es.cnmc.everest.model;

/**
 * Created by dromera on 17/05/2016.
 */
public class ItemFile {

    public enum TypeFile {FILE, FOLDER};

    private String id;
    private String name;
    private String path;
    private boolean required;
    private boolean validate;
    private boolean found;
    private boolean declResponsable;
    private boolean auditoria;
    private TypeFile typeFile;

    public boolean isAuditoria() {
        return auditoria;
    }

    public void setAuditoria(boolean auditoria) {
        this.auditoria = auditoria;
    }

    public boolean isDeclResponsable() {
        return declResponsable;
    }

    public void setDeclResponsable(boolean declResponsable) {
        this.declResponsable = declResponsable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public TypeFile getTypeFile() {
        return typeFile;
    }

    public void setTypeFile(TypeFile typeFile) {
        this.typeFile = typeFile;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }










}
