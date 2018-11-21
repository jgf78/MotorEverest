package es.cnmc.everest.exception;

/**
 * Created by dromera on 14/06/2016.
 */
public enum ErrorCode {
        ERR000("000","DefaultError"),
        ERR001_1("001","DMinMax"),
        ERR001_2("001","LMinMax"),
        ERR002("002","Equals"),
        ERR003("003","ForbidSubStr"),
        ERR004_1("004","IsElementOf"),
        ERR004_2("004","IsIncludedIn"),
        ERR005_1("005","NotNull"),
        ERR005_2("005","StrNotNullOrEmpty"),
        ERR006("006","RequireHashCode"),
        ERR007("007","RequireSubStr"),
        ERR008("008","Strlen"),
        ERR009("009","StrMinMax"),
        ERR010("010","StrRegEx"),
        ERR011("011","Unique"),
        ERR012("012","UniqueHashCode"),
        //
        ERR013("013","FmtBool"),
        ERR014("014","FmtDate"),
        ERR015("015","FmtNumber"),
        ERR016("016","FmtSqlTime"),
        //
        ERR017("017","ParseBigDecimal"),
        ERR018("018","ParseBool"),
        ERR019("019","ParseChar"),
        ERR020("020","ParseDate"),
        ERR021("021","ParseDouble"),
        ERR022("022","ParseEnum"),
        ERR023("023","ParseInt"),
        ERR024("024","ParseLong"),
        ERR025("025","ParseSqlTime"),
        //
        ERR026("026","CheckDocument"),
        ERR027("027","ComparisonOperator"),
        ERR028("028","FloatPrecisionScale"),
        ERR029_1("029","InOptionalThenConstraint"),
        ERR029_2("029","InStrictThenConstraint"),
        ERR030("030","InStrict"),
        ERR031("031","InStrictThenType"),
        ERR032("032","ParseDateMultiple"),
        // Errores que no son por clase
        ERR100("100","Nombre fichero/directorio inválido"),
        ERR101("101","Fichero/directorio obligatorio"),
        ERR102("102","Subdirectorios no permitidos"),
        //
        ERR999("999","InternalServerError");

        private String errCode;
        private String className;

        ErrorCode(String errCode, String className) {
            this.errCode = errCode;
            this.className = className;
        }

        public String getErrCode() {
            return errCode;
        }

        public String getClassName() { return className;}

        public static String getByClass(Object o) {
            String className = o.getClass().getSimpleName();
            for (ErrorCode errorCode : values()) {
                if (errorCode.getClassName().equals(className)) {
                    return errorCode.getErrCode();
                }
            }
            return ERR000.getErrCode();
        }
}
