package zyf.easydb;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by ZhangYifan on 2016/9/22.
 */

public class EasyDbConfig {
    //默认的数据库名称
    private String dbName;
    //默认数据库版本号
    private int dbVersion;
    //是否开启事务
    private boolean allowTransaction;
    //默认数据库路径
    private File dbPath;
    //数据库更新的监听器
    private EasyDbUpdateListener listener;

    private Context context;

    public EasyDbConfig(Application context) {
        this.context=context;
        dbName="easydb.db";
        dbVersion=1;
        allowTransaction=true;
        dbPath = new File(context.getExternalFilesDir(null), dbName);
    }

    public EasyDbConfig setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public EasyDbConfig setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
        return this;
    }

    public EasyDbConfig setAllowTransaction(boolean allowTransaction) {
        this.allowTransaction = allowTransaction;
        return this;
    }

    public EasyDbConfig setDbPath(File dbPath) {
        this.dbPath = dbPath;
        return this;
    }

    public String getDbName() {
        return dbName;
    }

    public int getDbVersion() {
        return dbVersion;
    }

    public boolean isAllowTransaction() {
        return allowTransaction;
    }

    public File getDbPath() {
        return dbPath;
    }

    public void setListener(EasyDbUpdateListener listener) {
        this.listener = listener;
    }

    public EasyDbUpdateListener getListener() {
        return listener;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public interface EasyDbUpdateListener {
        public void onUpdate(SQLiteDatabase db, int oldVersion, int newVeision);
    }

    public class EasyDbUpdateListenerImpl implements EasyDbUpdateListener{
        @Override
        public void onUpdate(SQLiteDatabase db, int oldVersion, int newVeision) {

        }
    }
}
