package zyf.easydb;

/**
 * Created by ZhangYifan on 2016/7/26.
 */
public class DbException extends Exception {
    public DbException() {
    }

    public DbException(String detailMessage) {
        super(detailMessage);
    }

    public DbException(Throwable e) {

    }
}
