package zyf.easydb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

/**
 * Created by ZhangYifan on 2016/7/29.
 */
public abstract class EasyDbBaseImpl implements EasyDb{
    protected SQLiteDatabase mDb;

    protected EasyDbBaseImpl(Context context) {
        File f = new File(context.getExternalFilesDir(null), "datebase.db");
        mDb = SQLiteDatabase.openOrCreateDatabase(f, null);
    }

    @Override
    public void createTable(Class clazz) throws DbException {
        Table table = new Table(clazz);
        boolean isExist = table.isExist(mDb);
        if (!isExist) {
            table.create(mDb);
        }
    }

    @Override
    public void dropTable(Class clazz) throws DbException {
        Table table = new Table(clazz);
        boolean isExist = table.isExist(mDb);
        if (isExist) {
            table.drop(mDb);
        }
    }

    @Override
    public void close() throws IOException {
        mDb.close();
    }
}
