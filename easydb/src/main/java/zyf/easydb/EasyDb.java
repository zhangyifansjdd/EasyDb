package zyf.easydb;

import java.io.Closeable;
import java.util.List;

/**
 * Created by ZhangYifan on 2016/7/25.
 */
public interface EasyDb extends Closeable {
    void save(Object object) throws DbException;

    void saveAll(List<Object> objects) throws DbException;

    void update(Object object) throws DbException;

    void updateAll(List<Object> objects) throws DbException;

    <T> T query(Selector<T> selector) throws DbException;

    <T> List<T> queryAll(Class<T> clazz) throws DbException;

    void delete(Object object) throws DbException;

    void deleteAll(Class clazz) throws DbException;

    void createTable(Class clazz) throws DbException;

    void dropTable(Class clazz) throws DbException;
}