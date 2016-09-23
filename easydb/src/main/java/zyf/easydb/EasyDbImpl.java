package zyf.easydb;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import zyf.easydb.table.Table;

/**
 * EasyDb的实现类，实现了增删改查
 * <p/>
 * Created by ZhangYifan on 2016/7/29.
 */
class EasyDbImpl extends EasyDbBaseImpl {
    private static EasyDb sInstance;

    private EasyDbImpl(Context context) {
        super(context);
    }

    public static synchronized EasyDb getInstance(Context context) {
        // TODO: 2016/7/27 调整单例模式，提高效率
        if (sInstance == null) {
            sInstance = new EasyDbImpl(context);
        }
        return sInstance;
    }

    @Override
    public void insert(Object object) throws DbException {
        Class clazz = object.getClass();

        // TODO: 2016/8/24 如果表没创建则先创建表
        createTableIfNotExist(clazz);

        Table table = Table.getTableInstance(clazz);
        table.insert(mDb, object);
    }

    @Override
    public void insertAll(List<Object> objects) throws DbException {
        if (objects == null || objects.size() == 0) {
            throw new DbException("List空指针或size为0!");
        }
        for (Object object : objects) {
            insert(object);
        }
    }

    @Override
    public void update(Object object) throws DbException {

    }

    @Override
    public void updateAll(List<Object> objects) throws DbException {

    }

    @Override
    public <T> List<T> query(Selector<T> selector) throws DbException {
        Cursor cursor = mDb.query(selector.getTableName(), selector.getDisplayColumns(), selector.getQueryColumns(), selector.getQueryArgs(), null, null, selector.getOrderBy());
        List<T> list = null;
        if (cursor != null) {
            list = new ArrayList<>();
            while (cursor.moveToNext()) {
                try {
                    T instance = (T) selector.getTable().getClazz().newInstance();
                    for (String disPlaycolumn : selector.getDisplayColumns()) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    @Override
    public <T> List<T> queryAll(Class<T> clazz) throws DbException {
        Table table=Table.getTableInstance(clazz);
        return table.queryAll(mDb);
//        // TODO: 2016/8/5 添加外键支持
//        Table table = Table.getTableInstance(clazz);
//
//        Cursor cursor = mDb.query(table.getTableName(), null, null, null, null, null, null);
//        if (cursor == null) return null;
//
//        List<T> list = new ArrayList<>();
//        LinkedHashMap<String, Column> columnLinkedHashMap = table.getColumns();
//        while (cursor.moveToNext()) {
//            try {
//                T instance = clazz.newInstance();
//                Column primaryKeyColumn = null;
//                String primaryKeyVal = null;
//                Iterator iterator = columnLinkedHashMap.entrySet().iterator();
//                while (iterator.hasNext()) {
//                    Map.Entry<String, Column> entry = (Map.Entry<String, Column>) iterator.next();
//                    String fieldName = entry.getKey();
//                    Column column = entry.getValue();
//                    Field field = clazz.getDeclaredField(fieldName);
//                    field.setAccessible(true);
//
//                    if (column.isPrimaryKey()) {
//                        primaryKeyColumn = column;
//                    }
//
//                    String type = field.getType().getSimpleName();
//                    Object value = null;
//                    switch (type) {
//                        case "String":
//                            value = cursor.getString(cursor.getColumnIndex(column.getColumnName()));
//                            break;
//                        case "int":
//                            value = cursor.getInt(cursor.getColumnIndex(column.getColumnName()));
//                            break;
//                        case "long":
//                            value = cursor.getLong(cursor.getColumnIndex(column.getColumnName()));
//                            break;
//                        case "float":
//                            value = cursor.getFloat(cursor.getColumnIndex(column.getColumnName()));
//                            break;
//                        case "double":
//                            value = cursor.getDouble(cursor.getColumnIndex(column.getColumnName()));
//                    }
//                    field.set(instance, value);
//                    if (column.isPrimaryKey()) {
//                        primaryKeyVal = value + "";
//                    }
//                }
//                if (table.haveForeignTable()) {
//
////                    Table foreignTable = Table.getTableInstance(table.haveForeignTable());
////                    Cursor cursor1 = mDb.query(foreignTable.getTableName(),null,primaryKeyColumn.getColumnName()+"=?",new String[]{primaryKeyVal},null,null,null);
//
//                }
//                list.add(instance);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        cursor.close();
//        return list;
    }

    @Override
    public void delete(Object object) throws DbException {
        Class clazz = object.getClass();

        Table table = Table.getTableInstance(clazz);
        table.delete(mDb, object);
    }

    @Override
    public void deleteAll(Class clazz) throws DbException {
        Table table = Table.getTableInstance(clazz);
        if (table != null && table.isExist(mDb)) {
            table.deleteAll(mDb);
        }
    }
}