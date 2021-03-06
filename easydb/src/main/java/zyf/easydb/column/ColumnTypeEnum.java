package zyf.easydb.column;

/**
 * Sqlite数据存储类型的枚举
 * <p>
 * Created by ZhangYifan on 2016/7/26.
 */
public enum ColumnTypeEnum {
    TEXT("TEXT"),//字符串
    BLOB("BLOB"),//二进制对象
    INTEGER("INTEGER"),//带符号的整型
    REAL("REAL"),//浮点数
    NULL("NULL");//空值

    private final String value;

    ColumnTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
