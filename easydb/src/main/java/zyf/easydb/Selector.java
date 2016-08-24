package zyf.easydb;

import zyf.easydb.table.Table;

/**
 * Created by ZhangYifan on 2016/7/25.
 */
public class Selector<T> {

    public static final String ORDERBY_DESC = "DESC";
    public static final String ORDERBY_ASC = "ASC";

    private Table mTable;
    private String[] mDisplayColumns;
    private String mQueryColumns;
    private String[] mQueryArgs;
    private String mOrderBy;

    private Selector(Class<T> clazz) {
        try {
            mTable = Table.getTableInstance(clazz);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static <T> Selector<T> fromTable(Class<T> clazz) {
        return new Selector<T>(clazz);
    }

    public Selector<T> setDisplayColumns(String[] displayColumns) {
        mDisplayColumns = displayColumns;
        return this;
    }

    public Selector<T> setQueryColumn(String queryColumn) {
        mQueryColumns = queryColumn;
        return this;
    }

    public Selector<T> setQueryArgs(String[] queryArgs) {
        mQueryArgs = queryArgs;
        return this;
    }

    public Selector<T> setOrderBy(String orderBy) {
        mOrderBy = orderBy;
        return this;
    }

    public Table getTable() {
        return mTable;
    }

    public String getTableName() {
        return mTable.getTableName();
    }

    public String[] getDisplayColumns() {
        return mDisplayColumns;
    }

    public String getQueryColumns() {
        return mQueryColumns;
    }

    public String[] getQueryArgs() {
        return mQueryArgs;
    }

    public String getOrderBy() {
        return mOrderBy;
    }

    public String toSqlString() throws DbException {
        // TODO: 2016/8/5 加一些判断，判定数据的合法性，在生成sql语句
//        int nullNum=0;
//        if (mQueryColumns==null)
//            nullNum++;
//        if (mQueryArgs==null)
//            nullNum++;
//        if (nullNum==1||(nullNum==0&&mQueryColumns.length!=mQueryArgs.length)){
//            throw new DbException("查询参数设置不正确！");
//        }
        return toString();
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        // TODO: 2016/8/5 根据成员变量，组成sql语句
        return builder.toString();
    }
}
