package net.comfreeze.lib.db.model;

public class FieldColumnMap {
    public static enum DataType {
        // SQLite Data Types
        NULL, INTEGER, REAL, TEXT, BLOB,
        // Extended Types
        LONG, FLOAT, DOUBLE, STRING, BOOLEAN, JSON_ARRAY, JSON_OBJECT
    }

    public String fieldName;
    public String columnName;
    public DataType fieldType;
    public DataType columnType;

    public static FieldColumnMap factory(String field, DataType fieldType, String column, DataType columnType) {
        FieldColumnMap map = new FieldColumnMap();
        map.fieldName = field;
        map.columnName = column;
        map.fieldType = fieldType;
        map.columnType = columnType;
        return map;
    }

    public static FieldColumnMap factory(String column, DataType columnType) {
        FieldColumnMap map = new FieldColumnMap();
        map.fieldName = column;
        map.columnName = column;
        map.fieldType = columnType;
        map.columnType = columnType;
        return map;
    }
}