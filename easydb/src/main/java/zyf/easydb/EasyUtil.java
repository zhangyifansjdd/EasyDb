package zyf.easydb;

import android.content.Context;

/**
 * Created by ZhangYifan on 2016/7/29.
 */
public class EasyUtil {
    /**
     * 获取EasyDb实例
     * @param context
     * @return
     */
    public static EasyDb getEasyDb(Context context){
        return EasyDbImpl.getInstance(context);
    }
}
