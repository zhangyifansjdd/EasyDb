package zyf.easydb.convert;

/**
 * Created by ZhangYifan on 2016/7/27.
 */
public interface TypeConverter {
    <T> T convert(Object object);
}
