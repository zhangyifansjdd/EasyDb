package zyf.easydb.column;

import java.lang.reflect.Field;

/**
 * 列信息
 * 包含对应成员变量，列名，是否是主键，是否需要创建外键关联表及类型
 * 实现了克隆模式，以免在复用修改时对原对象产生影响
 * <p/>
 * Created by ZhangYifan on 2016/7/26.
 */
public class Column implements Cloneable {
    private Field field;
    private String columnName;
    private boolean isPrimaryKey;
    private boolean createForeignTable;
    private Class foreignClass;
    private boolean ignore;
    private boolean autoGeneratePrimaryKey;

    public Column(Field field, DbColumn dbColumn) {
        this.field = field;
        if (dbColumn == null) {
            //没有给成员变量设置该注解，则使用该变量名作为列名
            columnName = field.getName();
            return;
        } else {
            if ("".equals(dbColumn.columnName())) {
                columnName = field.getName();
            } else {
                columnName = dbColumn.columnName();
            }
        }
        isPrimaryKey = dbColumn.isPrimaryKey();
        ignore = dbColumn.ignore();
        autoGeneratePrimaryKey = dbColumn.autoGeneratePrimaryKey();
//        createForeignTable = dbColumn.createForeignTable();
//        if (createForeignTable) {
//            foreignClass = dbColumn.foreignClass();
//        } else {
//            foreignClass = null;
//        }
        if (dbColumn.foreignClass() != Class.class) {
            foreignClass = dbColumn.foreignClass();
            createForeignTable = true;
        } else {
            foreignClass = null;
        }
    }

    public Field getField() {
        return field;
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

    public void setField(Field field) {
        this.field = field;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setCreateForeignTable(boolean createForeignTable) {
        this.createForeignTable = createForeignTable;
    }

    public void setForeignClass(Class foreignClass) {
        this.foreignClass = foreignClass;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isAutoGeneratePrimaryKey() {
        return autoGeneratePrimaryKey;
    }

    public void setAutoGeneratePrimaryKey(boolean autoGeneratePrimaryKey) {
        this.autoGeneratePrimaryKey = autoGeneratePrimaryKey;
    }

    public ColumnTypeEnum getColumnDataBaseType() {
        Class clazz = field.getType();
        String fieldTypeName = clazz.getSimpleName();
        if ("String".equals(fieldTypeName)) {
            return ColumnTypeEnum.TEXT;
        } else if ("int".equals(fieldTypeName) || "long".equals(fieldTypeName)) {
            return ColumnTypeEnum.INTEGER;
        } else if ("float".equals(fieldTypeName) || "double".equals(fieldTypeName)) {
            return ColumnTypeEnum.REAL;
        }
        return ColumnTypeEnum.NULL;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
