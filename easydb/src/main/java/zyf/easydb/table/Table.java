package zyf.easydb.table;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zyf.easydb.Column;
import zyf.easydb.DbException;
import zyf.easydb.annotation.DbColumn;
import zyf.easydb.annotation.DbTable;

/**
 * 一张表的信息
 * 包含表名，类信息，及所有列的信息
 * 实现了容器的单例模式
 * <p>
 * Created by ZhangYifan on 2016/7/26.
 */
public class Table extends TableInterfaceBaseImpl {

    protected static HashMap<String, Table> sTableInstances;
    protected HashMap<String, ForeignTable> mForeignTables;

    protected Table(Class clazz) {
        super(clazz);

        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            mForeignTables = new HashMap<>();
            for (Field field : fields) {
                DbColumn dbColumn = field.getAnnotation(DbColumn.class);
                if (dbColumn.createForeignTable()) {
                    ForeignTable foreignTable = new ForeignTable(dbColumn.foreignClass(), this);
                    mForeignTables.put(dbColumn.foreignClass().getSimpleName(), foreignTable);
                }
            }
        }
    }

    /**
     * 容器单例模式实现table的单例及存储，提高获取table的效率
     *
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
                throw new DbException("没有设置" + clazz.getSimpleName() + "的表名!");
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

    /**
     * 该表是否含有外键
     *
     * @return
     */
    public boolean haveForeignTable() {
        boolean have = false;
        if (mForeignTables != null && mForeignTables.size() > 0)
            have = true;
        return have;
    }

    protected HashMap<String, ForeignTable> getForeignTables() {
        return mForeignTables;
    }

    @Override
    public String insert(@NonNull SQLiteDatabase database, Object object) throws DbException {
        if (object==null)
            throw new DbException("插入数据为空，不可插入！");

        Class clazz = object.getClass();
        Iterator iterator = mColumnLinkedHashMap.entrySet().iterator();
        StringBuilder sqlBuilder = new StringBuilder("insert into ");
        sqlBuilder.append(mTableName);
        List<String> columnsString = new ArrayList<>();
        List<String> valuesString = new ArrayList<>();
        String primaryKeyVal = null;

        try {
            Field field = clazz.getDeclaredField(getPrimaryKeyName());
            field.setAccessible(true);
            primaryKeyVal = field.get(object).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (iterator.hasNext()) {
            try {
                Map.Entry<String, Column> entry = (Map.Entry<String, Column>) iterator.next();
                String fieldName = entry.getKey();
                Column column = entry.getValue();
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object val = field.get(object);

                if (fieldName == null || "".equals(fieldName) || val == null) continue;

                if (column.isCreateForeignTable()) {
                    ForeignTable foreignTable = mForeignTables.get(column.getForeignClass().getSimpleName());
                    List list = (List) val;
                    for (Object obj : list) {

                        foreignTable.insert(database, obj, primaryKeyVal);
                    }
                    continue;
                }

                columnsString.add(column.getColumnName());
                valuesString.add(val.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sqlBuilder.append("(");
        for (String s : columnsString) {
            sqlBuilder.append(s).append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(") values(");
        for (String s : valuesString) {
            sqlBuilder.append("'").append(s).append("'").append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        sqlBuilder.append(");");

        String sql = sqlBuilder.toString();
        database.execSQL(sql);

        return primaryKeyVal;
    }

    @Override
    public void delete(@NonNull SQLiteDatabase database, Object object) throws DbException {

    }

    /**
     * 创建表
     * 若需要外键表则一并创建
     *
     * @param database
     * @throws DbException
     */
    // TODO: 2016/7/29 要能对付更加复杂的实体类
    @Override
    public void create(SQLiteDatabase database) throws DbException {
        if (mColumnLinkedHashMap == null || mColumnLinkedHashMap.isEmpty()) {
            throw new DbException("获取表列失败，创建" + mTableName + "表失败！");
        } else {
            StringBuilder sqlBuilder = new StringBuilder("create table if not exists ");
            sqlBuilder.append(mTableName).append('(');
            Iterator iterator = mColumnLinkedHashMap.entrySet().iterator();
            boolean havePrimaryKey = false;
            while (iterator.hasNext()) {
                Map.Entry<String, Column> entry = (Map.Entry<String, Column>) iterator.next();
                Column column = entry.getValue();
                if (!column.isCreateForeignTable()) {
                    sqlBuilder.append(column.getColumnName()).append(' ').append(column.getColumnDataBaseType());
                }
                if (column.isPrimaryKey()) {
                    sqlBuilder.append(" primary key");
                    havePrimaryKey = true;
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

            //创建出外键表
//            if (haveForeignTable()) {
//                for (ForeignTable foreignTable : mForeignTables) {
//                    foreignTable.create(database);
//                }
//            }
            Iterator foreignIterator = mForeignTables.entrySet().iterator();
            while (foreignIterator.hasNext()) {
                Map.Entry<String, ForeignTable> entry = (Map.Entry<String, ForeignTable>) foreignIterator.next();
                ForeignTable foreignTable = entry.getValue();
                foreignTable.create(database);
            }
        }
    }

    /**
     * 删除表结构
     * 如果含有外键表，则一并删除
     *
     * @param database
     * @throws DbException
     */
    @Override
    public void drop(SQLiteDatabase database) throws DbException {
        String sql = "drop table if exists " + mTableName;
        database.execSQL(sql);

        //如果有外键链接，还需要把外键的表删除掉
//        if (haveForeignTable()) {
//            for (ForeignTable foreignTable : mForeignTables) {
//                foreignTable.drop(database);
//            }
//        }
        Iterator foreignIterator = mForeignTables.entrySet().iterator();
        while (foreignIterator.hasNext()) {
            Map.Entry<String, ForeignTable> entry = (Map.Entry<String, ForeignTable>) foreignIterator.next();
            ForeignTable foreignTable = entry.getValue();
            foreignTable.drop(database);
        }
    }
}
