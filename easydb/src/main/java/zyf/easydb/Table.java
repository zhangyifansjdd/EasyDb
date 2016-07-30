package zyf.easydb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import zyf.easydb.annotation.DbColumn;
import zyf.easydb.annotation.DbTable;

/**
 * Created by ZhangYifan on 2016/7/26.
 */
public class Table {
    private String tableName;
    private Class clazz;
    private static HashMap<String, Table> sTableInstances;
    private LinkedHashMap<String, Column> mColumnLinkedHashMap;

    private Table(Class clazz) {
        DbTable dbTable = (DbTable) clazz.getAnnotation(DbTable.class);
        tableName = dbTable.tableName();
        this.clazz = clazz;
        mColumnLinkedHashMap = getColumns(clazz);
    }

    /**
     * 容器单例模式实现table的单例及存储，提高获取table的效率
     * @param clazz
     * @return
     * @throws DbException
     */
    public static synchronized Table getTableInstance(Class clazz) throws DbException {
        Table table = null;
        String tableName = null;
        DbTable dbTable = (DbTable) clazz.getAnnotation(DbTable.class);
        if (dbTable != null) {
            tableName = dbTable.tableName();
            if (tableName == null) {
                throw new DbException("没有设置" + clazz.getSimpleName() + "表名!");
            }
        }
        if (sTableInstances == null) {
            sTableInstances = new HashMap<>();
        }
        table = sTableInstances.get(tableName);
        if (table == null) {
            table = new Table(clazz);
            sTableInstances.put(tableName, table);
        }
        return table;
    }


    public String getTableName() {
        return tableName;
    }

    public LinkedHashMap<String, Column> getColumnLinkedHashMap() {
        return mColumnLinkedHashMap;
    }

    public boolean isExist(SQLiteDatabase database) throws DbException {
        boolean isExist = false;
        Cursor cursor = database.rawQuery("SELECT COUNT(*) AS c FROM sqlite_master WHERE type='table' AND name='" + tableName + "'", null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(0);
                    if (count > 0) {
                        isExist = true;
                    }
                }
            } catch (Throwable e) {
                throw new DbException(e);
            }
        }
        return isExist;
    }

    private Class haveForeignTable() {
        Class foreignClass = null;
        Iterator iterator = mColumnLinkedHashMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Column> entry = (Map.Entry<String, Column>) iterator.next();
            Column column = entry.getValue();
            if (column.isCreateForeignTable())
                foreignClass = column.getForeignClass();
        }
        return foreignClass;
    }

    public String getPrimaryKeyName() {
        Iterator iterator = mColumnLinkedHashMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Column> entry = (Map.Entry<String, Column>) iterator.next();
            String fieldName = entry.getKey();
            Column column = entry.getValue();
            if (column.isPrimaryKey())
                return fieldName;
        }
        return null;
    }

    @Override
    public String toString() {
        return getTableName();
    }

    private LinkedHashMap<String, Column> getColumns(Class clazz) {
        LinkedHashMap<String, Column> columnLinkedHashMap = new LinkedHashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //当实体类为一个内部类时，内部类会多一个成员变量，引用的外部类
            //当遇到该引用的成员变量，catch住，不往columnLinkedHashMap里添加
            try {
                DbColumn dbColumn = field.getAnnotation(DbColumn.class);
                columnLinkedHashMap.put(field.getName(), new Column(field, dbColumn));
            } catch (Exception e) {
            }
        }
        return columnLinkedHashMap;
    }

    // TODO: 2016/7/29 要能对付更加复杂的实体类
    public void create(SQLiteDatabase database) throws DbException {
        if (mColumnLinkedHashMap == null || mColumnLinkedHashMap.isEmpty()) {
            throw new DbException("获取表列失败，创建" + tableName + "表失败！");
        } else {
            StringBuilder sqlBuilder = new StringBuilder("create table if not exists ");
            sqlBuilder.append(tableName).append('(');
            Iterator iterator = mColumnLinkedHashMap.entrySet().iterator();
            boolean isNeedCreateForeign = false;
            boolean havePrimaryKey = false;
            String foreignColumnKey = null;
            Column foreignColumn = null;
            Class foreignClass = null;
            while (iterator.hasNext()) {
                Map.Entry<String, Column> entry = (Map.Entry<String, Column>) iterator.next();
                String fieldName = entry.getKey();
                Column column = entry.getValue();
                if (!column.isCreateForeignTable()) {
                    sqlBuilder.append(column.getColumnName()).append(' ').append(column.getColumnDataBaseType());
                } else {
                    isNeedCreateForeign = true;
                    // TODO: 2016/7/26 此处需要改进 不在注解上指定class  而是自动获取到List里面的泛型
                    foreignClass = column.getForeignClass();
                }
                if (column.isPrimaryKey()) {
                    sqlBuilder.append(" primary key");
                    havePrimaryKey = true;
//                    mPrimaryKeyName=column.getColumnName();
                    //该表的主键作为另一个表的外键
                    foreignColumnKey = fieldName;
                    foreignColumn = column;
                }
                if (!column.isCreateForeignTable())
                    sqlBuilder.append(',');
            }
            // 创建表之前需要判断是否含有主键
            if (!havePrimaryKey) {
                throw new DbException("没有主键，请在注解中添加主键！");
            }

            sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
            sqlBuilder.append(')');
            String sql = sqlBuilder.toString();
            database.execSQL(sql);

            if (isNeedCreateForeign) {
                Table table = new Table(foreignClass);
                //将该列改为外键，不再是主键
                foreignColumn.setPrimaryKey(false);
                table.getColumnLinkedHashMap().put(foreignColumnKey, foreignColumn);
                table.create(database);
            }
        }
    }

    public void drop(SQLiteDatabase database) throws DbException {
        String sql = "drop table if exists " + tableName;
        database.execSQL(sql);
        //如果有外键链接，还需要把外键的表删除掉
        Class c = haveForeignTable();
        if (haveForeignTable() != null) {
            Table table = new Table(haveForeignTable());
            table.drop(database);
        }
    }
}
