package zyf.easydb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

/**
 * Created by ZhangYifan on 2016/7/29.
 */
abstract class EasyDbBaseImpl implements EasyDb{
    protected SQLiteDatabase mDb;

    protected EasyDbBaseImpl(Context context) {
        File file = new File(context.getExternalFilesDir(null), "datebase.db");
        mDb = SQLiteDatabase.openOrCreateDatabase(file, null);
    }

    @Override
    public void createTable(Class clazz) throws DbException {
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
}
