package zyf.easydb;

import java.io.Closeable;
import java.util.List;

/**
 * EasyDb接口，也是该工具的入口
 *
 * Created by ZhangYifan on 2016/7/25.
 */
public interface EasyDb extends Closeable {
    /**
     * 保存单个object
     * 如果对应的表不存在则会先创建表然后在保存
     * @param object
     * @throws DbException
     */
    void save(Object object) throws DbException;

    /**
     * 保存一系列objects
     * @param objects
     * @throws DbException
     */
    void saveAll(List<Object> objects) throws DbException;

    /**
     * 对一个object进行更新
     * @param object
     * @throws DbException
     */
    void update(Object object) throws DbException;

    /**
     * 更新一系列objects
     * @param objects
     * @throws DbException
     */
    void updateAll(List<Object> objects) throws DbException;

    /**
     * 查找数据
     * @param selector 选择器，用来构成查询条件
     * @param <T> 返回的实体类的类型
     * @return
     * @throws DbException
     */
    <T> List<T> query(Selector<T> selector) throws DbException;

    /**
     * 查找某一张表的全部数据
     * @param clazz 用于确定查询哪张表
     * @param <T> 返回的实体类的类型
     * @return
     * @throws DbException
     */
    <T> List<T> queryAll(Class<T> clazz) throws DbException;

    /**
     * 删除某一条数据
     * @param object
     * @throws DbException
     */
    void delete(Object object) throws DbException;

    /**
     * 删除某张表中全部数据
     * @param clazz
     * @throws DbException
     */
    void deleteAll(Class clazz) throws DbException;

    /**
     * 创建表
     * 如果存在则不会重复创建
     * @param clazz
     * @throws DbException
     */
    void createTable(Class clazz) throws DbException;

    /**
     * 删除某张表结构
     * @param clazz
     * @throws DbException
     */
    void dropTable(Class clazz) throws DbException;
}