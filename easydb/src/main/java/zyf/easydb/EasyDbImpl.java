package zyf.easydb;

import android.content.Context;

import java.util.List;

import zyf.easydb.table.Table;

/**
 * EasyDb的实现类，实现了增删改查
 * <p/>
 * Created by ZhangYifan on 2016/7/29.
 */
class EasyDbImpl extends EasyDbBaseImpl {

    protected EasyDbImpl(Context context) {
        super(context);
    }

    protected EasyDbImpl(EasyDbConfig config) {
        super(config);
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
    public <T> List<T> query(Where where) throws DbException {
        List<T> list = null;

        return list;
    }

    @Override
    public <T> List<T> queryAll(Class<T> clazz) throws DbException {
        Table table = Table.getTableInstance(clazz);
        return table.queryAll(mDb);
    }

    @Override
    public <T> T queryByPrimaryKey(Class<T> clazz, Object primaryKey) throws DbException {
        return null;
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