package zyf.easydb.column;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZhangYifan on 2016/7/25.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * 每张表必须存在一个主键
 * createForeignTable为true，则必须指定foreignClass
 */
public @interface DbColumn {
    //列名
    String columnName() default "";

    //该列是否是主键 必须存在一个主键
    boolean isPrimaryKey() default false;

    //是否创建另一张表，并关联外键
    boolean createForeignTable() default false;

    //若需要关键外键，需指定外键表的类型
    Class foreignClass() default Class.class;
}
