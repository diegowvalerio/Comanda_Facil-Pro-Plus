package br.com.dw.comanda_facil_pro_plus.banco;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.MysqlDatabaseType;
import com.j256.ormlite.field.FieldType;

public class MariaDBType extends MysqlDatabaseType {
    private final static String DATABASE_URL_PORTION = "mysql";
    private final static String DRIVER_CLASS_NAME = "org.mariadb.jdbc.Driver";
    private final static String DATABASE_NAME = "MariaDB";

    @Override
    public boolean isDatabaseUrlThisType(String url, String dbTypePart) {
        return DATABASE_URL_PORTION.equals(dbTypePart);
    }

    @Override
    protected String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }

    @Override
    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    @Override
    protected void appendDateType(StringBuilder sb, FieldType fieldType, int fieldWidth) {
        /**
         * TIMESTAMP in MySQL does some funky stuff with the last-modification time. Values are 'not null' by default
         * with an automatic default of CURRENT_TIMESTAMP. Strange design decision.
         */
        if (isDatetimeFieldWidthSupported()) {
            sb.append("DATETIME(").append(fieldWidth).append(")");
        } else {
            sb.append("DATETIME");
        }
    }

    public boolean isDatetimeFieldWidthSupported() {
        return true;
    }
}
