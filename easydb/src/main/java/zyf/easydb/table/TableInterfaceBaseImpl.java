package zyf.easydb.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import zyf.easydb.column.Column;
import zyf.easydb.DbException;
import zyf.easydb.column.DbColumn;

/**
 * Created by ZhangYifan on 2016/8/19.
 */
public abstract class TableInterfaceBaseImpl implements TableInterface {

    protected String mTableName;
    protected Class mClazz;
    protected LinkedHashMap<String, Column> mColumnLinkedHashMap;

    protected TableInterfaceBaseImpl(Class clazz){
        DbTable dbTable = (DbTable) clazz.getAnnotation(DbTable.class);
        mTableName = dbTable.tableName();
        this.mClazz = clazz;
        getColumns();
    }

    @Override
    public String getTableName() {
        return mTableName;
    }

    @Override
    public LinkedHashMap<String, Column> getColumns() {
        if (mColumnLinkedHashMap == null) {
            mColumnLinkedHashMap = new LinkedHashMap<>();
            Field[] fields = mClazz.getDeclaredFields();
            for (Field field : fields) {
                //当实体类为一个内部类时，内部类会多一个成员变量，引用的外部类
                //当遇到该引用的成员变量，catch住，不往columnLinkedHashMap里添加
                try {
                    DbColumn dbColumn = field.getAnnotation(DbColumn.class);
                    mColumnLinkedHashMap.put(field.getName(), new Column(field, dbColumn));
                } catch (Exception e) {
                }
            }
        }
        return mColumnLinkedHashMap;
    }

    /**
     * 该表是否已经被创建
     *
     * @param database
     * @return
     * @throws DbException
     */
    @Override
    public boolean isExist(@NonNull SQLiteDatabase database) throws DbException {
        boolean isExist = false;
        Cursor cursor = database.rawQuery("SELECT COUNT(*) AS c FROM sqlite_master WHERE type='table' AND name='" + mTableName + "'", null);
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

    @Override
    public Class getClazz() {
        return mClazz;
    }

    /**
     * 获取该表的主键列名
     *
     * @return
     */
    @Override
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
        return mTableName;
    }
}