package zyf.easydb;

import java.lang.reflect.Field;

import zyf.easydb.annotation.DbColumn;

/**
 * Created by ZhangYifan on 2016/7/26.
 */
public class Column {
    private Field field;
    private String columnName;
    private boolean isPrimaryKey;
    private boolean createForeignTable;
    private Class foreignClass;

    protected Column(Field field,DbColumn dbColumn){
        this.field=field;
        columnName=dbColumn.columnName();
        isPrimaryKey=dbColumn.isPrimaryKey();
        createForeignTable=dbColumn.createForeignTable();
        if (createForeignTable){
           foreignClass=dbColumn.foreignClass();
        }else {
            foreignClass=null;
        }
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isCreateForeignTable() {
        return createForeignTable;
    }

    public Class getForeignClass() {
        return foreignClass;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public ColumnTypeEnum getColumnDataBaseType(){
        Class clazz=field.getType();
        String fieldTypeName=clazz.getSimpleName();
        if ("String".equals(fieldTypeName)){
            return ColumnTypeEnum.TEXT;
        }else if ("int".equals(fieldTypeName)||"long".equals(fieldTypeName)){
            return ColumnTypeEnum.INTEGER;
        }else if ("float".equals(fieldTypeName)||"double".equals(fieldTypeName)){
            return ColumnTypeEnum.REAL;
        }
        return ColumnTypeEnum.NULL;
    }
}
