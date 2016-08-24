package zyf.easydb.table;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.LinkedHashMap;

import zyf.easydb.Column;
import zyf.easydb.DbException;

/**
 * Created by ZhangYifan on 2016/8/18.
 */
public interface TableInterface {

    String getTableName();

    Class getClazz();

    LinkedHashMap<String, Column> getColumns();

    String getPrimaryKeyName();

    boolean isExist(@NonNull SQLiteDatabase database) throws DbException;

    /**
     *
     * @param database
     * @param object
     * @return 插入数据的主键
     * @throws DbException
     */
    String insert(@NonNull SQLiteDatabase database,Object object) throws DbException;

    void delete(@NonNull SQLiteDatabase database,Object object) throws DbException;

    void create(@NonNull SQLiteDatabase database) throws DbException;

    void drop(@NonNull SQLiteDatabase database) throws DbException;
}
