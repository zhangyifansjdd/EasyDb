package zyf.easydb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zyf.easydb.annotation.DbColumn;

/**
 * EasyDb的实现类，实现了增删改查
 *
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
    public void save(Object object) throws DbException {
        Class clazz = object.getClass();

        createTable(clazz);
        Table table = Table.getTableInstance(clazz);
        ContentValues contentValues = new ContentValues();
        LinkedHashMap<String, Column> columnLinkedHashMap = table.getColumnLinkedHashMap();
        Iterator iterator = columnLinkedHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            try {
                Map.Entry<String, Column> entry = (Map.Entry<String, Column>) iterator.next();
                String fieldName = entry.getKey();
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object val = field.get(object);
                String type = field.getType().getSimpleName();
                Column column = entry.getValue();

                if (val == null) continue;

                if (column.isCreateForeignTable()) {
                    List<?> forgienObjects = (List<?>) val;
                    if (forgienObjects != null && forgienObjects.size() > 0) {
                        try {
                            Field primaryKeyField = clazz.getDeclaredField(table.getPrimaryKeyName());
                            primaryKeyField.setAccessible(true);
                            Column foreignColumnClone = (Column) new Column(primaryKeyField, primaryKeyField.getAnnotation(DbColumn.class)).clone();
                            foreignColumnClone.setPrimaryKey(false);
                            Table foreignTable = Table.getTableInstance(column.getForeignClass());
                            foreignTable.getColumnLinkedHashMap().put(table.getPrimaryKeyName(), foreignColumnClone);
                            Object primaryKeyVal = primaryKeyField.get(object);
                            for (Object foreignObject : forgienObjects) {
                                save(foreignObject, table.getPrimaryKeyName(), primaryKeyVal, primaryKeyField.getType().getSimpleName());
                            }
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                putContentValueWithType(contentValues, fieldName, val, type);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        mDb.insert(table.getTableName(), null, contentValues);
    }

    private void save(Object object, String foreignKeyName, Object foreignKeyVal, String foreignKeyType) throws DbException {
        Class clazz = object.getClass();

        createTable(clazz);
        Table table = Table.getTableInstance(clazz);
        ContentValues contentValues = new ContentValues();
        LinkedHashMap<String, Column> columnLinkedHashMap = table.getColumnLinkedHashMap();
        Iterator iterator = columnLinkedHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            try {
                Map.Entry<String, Column> entry = (Map.Entry<String, Column>) iterator.next();
                String fieldName = entry.getKey();
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object val = field.get(object);
                String type = field.getType().getSimpleName();
                Column column = entry.getValue();

                if (val == null) continue;

                if (column.isCreateForeignTable()) {
                    List<?> forgienObjects = (List<?>) val;
                    if (forgienObjects != null && forgienObjects.size() > 0) {
                        try {
                            Field primaryKeyField = clazz.getDeclaredField(table.getPrimaryKeyName());
                            primaryKeyField.setAccessible(true);
                            Column foreignColumnClone = (Column) new Column(primaryKeyField, primaryKeyField.getAnnotation(DbColumn.class)).clone();
                            foreignColumnClone.setPrimaryKey(false);
                            Table foreignTable = Table.getTableInstance(column.getForeignClass());
                            foreignTable.getColumnLinkedHashMap().put(table.getPrimaryKeyName(), foreignColumnClone);
                            Object primaryKeyVal = primaryKeyField.get(object);
                            for (Object foreignObject : forgienObjects) {
                                save(foreignObject, table.getPrimaryKeyName(), primaryKeyVal, primaryKeyField.getType().getSimpleName());
                            }
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                putContentValueWithType(contentValues, fieldName, val, type);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        putContentValueWithType(contentValues, foreignKeyName, foreignKeyVal, foreignKeyType);

        mDb.insert(table.getTableName(), null, contentValues);
    }

    private void putContentValueWithType(ContentValues contentValues, String key, Object val, String type) {
        switch (type) {
            case "String":
                contentValues.put(key, (String) val);
                break;
            case "boolean":
                contentValues.put(key, (boolean) val);
                break;
            case "Boolean":
                contentValues.put(key, (Boolean) val);
                break;
            case "byte":
                contentValues.put(key, (byte) val);
                break;
            case "Byte":
                contentValues.put(key, (Byte) val);
                break;
            case "int":
                contentValues.put(key, (int) val);
                break;
            case "Integer":
                contentValues.put(key, (Integer) val);
                break;
            case "long":
                contentValues.put(key, (long) val);
                break;
            case "Long":
                contentValues.put(key, (Long) val);
                break;
            case "float":
                contentValues.put(key, (float) val);
                break;
            case "Float":
                contentValues.put(key, (Float) val);
                break;
            case "double":
                contentValues.put(key, (double) val);
            case "Double":
                contentValues.put(key, (Double) val);
                break;
        }
    }

    @Override
    public void saveAll(List<Object> objects) throws DbException {
        if (objects == null || objects.size() == 0) {
            throw new DbException("List空指针或size为0!");
        }
        // TODO: 2016/7/27 这样做效率会有所降低，以后改进
        for (Object object : objects) {
            save(object);
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
        return null;
    }

    @Override
    public <T> List<T> queryAll(Class<T> clazz) throws DbException {
        Table table = Table.getTableInstance(clazz);

        Cursor cursor = mDb.query(table.getTableName(), null, null, null, null, null, null);
        if (cursor == null) return null;

        List<T> list = new ArrayList<>();
        LinkedHashMap<String, Column> columnLinkedHashMap = table.getColumnLinkedHashMap();
        while (cursor.moveToNext()) {
            try {
                T instance = clazz.newInstance();
                Iterator iterator = columnLinkedHashMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Column> entry = (Map.Entry<String, Column>) iterator.next();
                    String fieldName = entry.getKey();
                    Column column = entry.getValue();
                    Field field = clazz.getDeclaredField(fieldName);
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
                    field.set(instance, value);
                }
                list.add(instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
    }

    // TODO: 2016/7/29 增加外键的支持
    @Override
    public void delete(Object object) throws DbException {
        Class clazz = object.getClass();

        Table table = Table.getTableInstance(clazz);
        String primaryKeyValue = null;
        String primaryKeyName = table.getPrimaryKeyName();
        if (primaryKeyName == null) {
            throw new DbException("没有找到主键！");
        }
        try {
            Field field = clazz.getDeclaredField(primaryKeyName);
            field.setAccessible(true);
            String type = field.getType().getSimpleName();
            switch (type) {
                case "String":
                    primaryKeyValue = (String) field.get(object);
                    break;
                case "int":
                    primaryKeyValue = (int) field.get(object) + "";
                    break;
                case "long":
                    primaryKeyValue = (long) field.get(object) + "";
                    break;
                case "float":
                    primaryKeyValue = (float) field.get(object) + "";
                    break;
                case "double":
                    primaryKeyValue = (double) field.get(object) + "";
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mDb.delete(table.getTableName(), primaryKeyName + "=?", new String[]{primaryKeyValue});

        //如果含有外键，则也需要删除对应的数据
        if (table.haveForeignTable() != null) {
            Table foreignTable = Table.getTableInstance(table.haveForeignTable());
            mDb.delete(foreignTable.getTableName(), primaryKeyName + "=?", new String[]{primaryKeyValue});
        }
    }

    @Override
    public void deleteAll(Class clazz) throws DbException {
        Table table = Table.getTableInstance(clazz);
        if (table.isExist(mDb)) {
            mDb.delete(table.getTableName(), null, null);

            //如果含有外键，则也需要删除对应的数据
            if (table.haveForeignTable() != null) {
                Table foreignTable = Table.getTableInstance(table.haveForeignTable());
                mDb.delete(foreignTable.getTableName(), null, null);
            }
        }
    }
}