package zyf.easydb.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import zyf.easydb.DbException;
import zyf.easydb.Selector;
import zyf.easydb.column.Column;
import zyf.easydb.column.DbColumn;

/**
 * 一张表的信息
 * 包含表名，类信息，及所有列的信息
 * 实现了容器的单例模式
 * <p/>
 * Created by ZhangYifan on 2016/7/26.
 */
public class Table extends TableInterfaceBaseImpl {

    protected static ConcurrentHashMap<String, Table> sTableInstances;
    protected HashMap<String, ForeignTable> mForeignTables;
    protected HashMap<ForeignTable, Field> mForeignTableFieldHashMap;

    protected Table(Class clazz) {
        super(clazz);
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            mForeignTables = new HashMap<>();
            mForeignTableFieldHashMap = new HashMap<>();
            for (Field field : fields) {
                DbColumn dbColumn = field.getAnnotation(DbColumn.class);
                if (dbColumn != null && dbColumn.createForeignTable()) {
                    ForeignTable foreignTable = new ForeignTable(dbColumn.foreignClass(), this);
                    mForeignTables.put(dbColumn.foreignClass().getSimpleName(), foreignTable);
                    mForeignTableFieldHashMap.put(foreignTable, field);
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
            sTableInstances = new ConcurrentHashMap<>();
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
        if (object == null) {
            throw new DbException("插入数据为空，不可插入！");
        }

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
                    if (val instanceof List) {
                        List list = (List) val;
                        for (Object obj : list) {

                            foreignTable.insert(database, obj, primaryKeyVal);
                        }
                    } else {
                        foreignTable.insert(database, val, primaryKeyVal);
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
        String primaryKeyName = getPrimaryKeyName();
        String primaryKeyVal = null;
        try {
            Field field = mClazz.getDeclaredField(primaryKeyName);
            field.setAccessible(true);
            primaryKeyVal = field.get(object).toString();

            String type = field.getType().getSimpleName();
            boolean b = type.equals("String");
            String s = b ? "'" : "";

            String sql = "delete from " + mTableName + " where " + primaryKeyName + "=" + s + primaryKeyVal + s + ";";
            database.execSQL(sql);

            if (haveForeignTable()) {
                Iterator foreignIterator = mForeignTables.entrySet().iterator();
                while (foreignIterator.hasNext()) {
                    Map.Entry<String, ForeignTable> entry = (Map.Entry<String, ForeignTable>) foreignIterator.next();
                    ForeignTable foreignTable = entry.getValue();
//                    foreignTable.delete(database, primaryKeyVal);
                    // TODO: 2016/8/25 foreignField不对
                    Field foreignField = mForeignTableFieldHashMap.get(foreignTable);
                    foreignField.setAccessible(true);

                    Object foreignFieldObj = foreignField.get(object);
                    if (foreignFieldObj instanceof List) {
                        List list = (List) foreignField.get(object);
                        if (list != null && list.size() > 0) {
                            for (Object obj : list) {
                                foreignTable.delete(database, obj);
                            }
                        }
                    } else {
                        foreignTable.delete(database, foreignFieldObj);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll(@NonNull SQLiteDatabase database) throws DbException {
        String sql = "delete from " + mTableName + ";";
        database.execSQL(sql);

        if (haveForeignTable()) {
            Iterator foreignIterator = mForeignTables.entrySet().iterator();
            while (foreignIterator.hasNext()) {
                Map.Entry<String, ForeignTable> entry = (Map.Entry<String, ForeignTable>) foreignIterator.next();
                ForeignTable foreignTable = entry.getValue();
                foreignTable.deleteAll(database);
            }
        }
    }

    @Override
    public <T> List<T> query(@NonNull SQLiteDatabase database, Selector<T> selector) throws DbException {
        String sql = null;
        if (selector != null) {
            sql = selector.toString();
        } else {
            sql = "select * from " + mTableName;
        }
        Cursor cursor = database.rawQuery(sql, null);
        List<T> list = null;
        if (cursor != null) {
            list = new ArrayList<>();
            while (cursor.moveToNext()) {
                try {
                    T instance = (T) mClazz.newInstance();
                    Column primaryKeyColumn = null;
                    String primaryKeyVal = null;
                    Iterator iterator = mColumnLinkedHashMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Column> entry = (Map.Entry<String, Column>) iterator.next();
                        String fieldName = entry.getKey();
                        Column column = entry.getValue();
                        Field field = mClazz.getDeclaredField(fieldName);
                        field.setAccessible(true);

                        String type = field.getType().getSimpleName();
                        Object value = null;
                        switch (type) {
                            case "String":
                                value = cursor.getString(cursor.getColumnIndex(column.getColumnName()));
                                break;
                            case "int":
                                value = cursor.getInt(cursor.getColumnIndex(column.getColumnName()));
                                break;
                            case "long":
                                value = cursor.getLong(cursor.getColumnIndex(column.getColumnName()));
                                break;
                            case "float":
                                value = cursor.getFloat(cursor.getColumnIndex(column.getColumnName()));
                                break;
                            case "double":
                                value = cursor.getDouble(cursor.getColumnIndex(column.getColumnName()));
                        }
                        if (column.isPrimaryKey()) {
                            primaryKeyColumn = column;
                            primaryKeyVal = value.toString();
                        }
                        field.set(instance, value);

                    }

                    if (haveForeignTable()) {
                        Iterator foreignIterator = mForeignTables.entrySet().iterator();
                        while (foreignIterator.hasNext()) {
                            Map.Entry<String, ForeignTable> entry = (Map.Entry<String, ForeignTable>) foreignIterator.next();
                            ForeignTable foreignTable = entry.getValue();
                            Field foreignTableField = mForeignTableFieldHashMap.get(foreignTable);
                            foreignTableField.setAccessible(true);
                            // TODO: 2016/8/25 查找外键
                            Selector s = Selector.fromTable(foreignTable.getClazz());
                            Selector.Express express = s.new Express(primaryKeyColumn.getColumnName(), "=", primaryKeyVal);
                            s.addExpress(express);
                            List list1 = foreignTable.query(database, s);
                            if (foreignTableField.getType().isAssignableFrom(List.class)) {
                                foreignTableField.set(instance, list1);
                            } else {
                                foreignTableField.set(instance, list1.get(0));
                            }
                        }
                    }

                    list.add(instance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    @Override
    public <T> List<T> queryAll(@NonNull SQLiteDatabase database) throws DbException {
        return query(database, null);
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
        Iterator foreignIterator = mForeignTables.entrySet().iterator();
        while (foreignIterator.hasNext()) {
            Map.Entry<String, ForeignTable> entry = (Map.Entry<String, ForeignTable>) foreignIterator.next();
            ForeignTable foreignTable = entry.getValue();
            foreignTable.drop(database);
        }
    }
}
