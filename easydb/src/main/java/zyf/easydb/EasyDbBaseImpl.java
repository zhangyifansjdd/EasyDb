package zyf.easydb;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

import zyf.easydb.table.Table;

/**
 * EasyDb的基础实现类，主要实现了数据库的创建，表的创建及销毁
 * <p>
 * Created by ZhangYifan on 2016/7/29.
 */
abstract class EasyDbBaseImpl implements EasyDb {
    protected SQLiteDatabase mDb;
    protected EasyDbConfig mConfig;

    private static EasyDb sInstance;

    protected EasyDbBaseImpl(Context context) {
        File file = new File(context.getExternalFilesDir(null), "datebase.db");
        mDb = SQLiteDatabase.openOrCreateDatabase(file, null);
    }

    protected EasyDbBaseImpl(EasyDbConfig config) {
        mConfig = config;
        mDb = SQLiteDatabase.openOrCreateDatabase(config.getDbPath(), null);
        // TODO: 2016/9/23 检查数据库版本号，不一致则升级
        SharedPreferences sp = mConfig.getContext().getSharedPreferences("dbVersion", Context.MODE_PRIVATE);
        int oldVersion = sp.getInt("dbVersion", -1);
        if (config.getDbVersion() > oldVersion) {
            config.getListener().onUpdate(mDb, oldVersion, config.getDbVersion());
        }
        sp.edit().putInt("dbVersion", config.getDbVersion()).commit();

    }

    public static synchronized EasyDb getInstance() {
        return sInstance;
    }

    public static EasyDb buildEasyDb(EasyDbConfig config) {
        if (sInstance == null) {
            if (config.getListener() == null) {
                config.setListener(config.new EasyDbUpdateListenerImpl());
            }
            sInstance = new EasyDbImpl(config);
        }
        return sInstance;
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
        if (sql == null || sql.length() == 0) {
            throw new DbException("sql语句为空！");
        }
        mDb.execSQL(sql);
    }
}
