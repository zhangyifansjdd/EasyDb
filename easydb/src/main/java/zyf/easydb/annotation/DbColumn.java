package zyf.easydb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZhangYifan on 2016/7/25.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbColumn {
    String columnName() default "";
    boolean isPrimaryKey() default false;
    boolean createForeignTable() default false;
    Class foreignClass() default Class.class;
}
