package zyf.easydb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

import zyf.easydb.table.Table;

/**
 * EasyDb的基础实现类，主要实现了数据库的创建，表的创建及销毁
 *
 * Created by ZhangYifan on 2016/7/29.
 */
abstract class EasyDbBaseImpl implements EasyDb{
    protected SQLiteDatabase mDb;

    protected EasyDbBaseImpl(Context context) {
        File s=context.getExternalFilesDir(null);
        File file = new File(context.getExternalFilesDir(null), "datebase.db");
        mDb = SQLiteDatabase.openOrCreateDatabase(file, null);
    }

    @Override
    public void createTableIfNotExist(Class clazz) throws DbException {
        Table table = Table.getTableInstance(clazz);
        boolean isExist = table.isExist(mDb);
        if (!isExist) {
            table.create(mDb);
        }
    }

    @Override
    public void dropTable(Class clazz) throws DbException {
        Table table = Table.getTableInstance(clazz);
        boolean isExist = table.isExist(mDb);
        if (isExist) {
            table.drop(mDb);
        }
    }

    @Override
    public void close() throws IOException {
        mDb.close();
    }

    @Override
    public void execSQL(String sql) throws DbException {
        if (sql==null||sql.length()==0) {
            throw new DbException("sql语句为空！");
        }
        mDb.execSQL(sql);
    }
}
