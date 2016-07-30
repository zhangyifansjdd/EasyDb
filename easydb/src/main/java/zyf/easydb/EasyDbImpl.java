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

/**
 * Created by ZhangYifan on 2016/7/29.
 */
class EasyDbImpl extends EasyDbBaseImpl {
    private static EasyDb sInstance;

    private EasyDbImpl(Context context){
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
        // TODO: 2016/7/29 增加外键的支持
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

                switch (type) {
                    case "String":
                        contentValues.put(fieldName, (String) val);
                        break;
                    case "int":
                        contentValues.put(fieldName, (int) val);
                        break;
                    case "long":
                        contentValues.put(fieldName, (long) val);
                        break;
                    case "float":
                        contentValues.put(fieldName, (float) val);
                        break;
                    case "double":
                        contentValues.put(fieldName, (double) val);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        mDb.insert(table.getTableName(), null, contentValues);
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
    public <T> T query(Selector<T> selector) throws DbException {
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
            String type=field.getType().getSimpleName();
            switch (type) {
                case "String":
                    primaryKeyValue=(String)field.get(object);
                    break;
                case "int":
                    primaryKeyValue=(int)field.get(object)+"";
                    break;
                case "long":
                    primaryKeyValue=(long)field.get(object)+"";
                    break;
                case "float":
                    primaryKeyValue=(float)field.get(object)+"";
                    break;
                case "double":
                    primaryKeyValue=(double)field.get(object)+"";
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mDb.delete(table.getTableName(), primaryKeyName+"=?", new String[]{primaryKeyValue});
    }

    @Override
    public void deleteAll(Class clazz) throws DbException {
        Table table = Table.getTableInstance(clazz);
        if (table.isExist(mDb)) {
            mDb.delete(table.getTableName(), null, null);
        }
    }
}
