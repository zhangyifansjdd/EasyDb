package zyf.easydb;

import java.util.ArrayList;
import java.util.List;

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
    private List<Express> mExpresses;

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

    public void addExpress(Express express){
        if (mExpresses==null)
            mExpresses=new ArrayList<>();
        mExpresses.add(express);
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

//        StringBuilder builder = new StringBuilder();
        // TODO: 2016/8/5 根据成员变量，组成sql语句
        return mExpresses.get(0).toString();
    }

    public class Express{
        private String columnName;
        private String sign;
        private String value;

        public Express(String columnName, String sign, String value) {
            this.columnName = columnName;
            this.sign = sign;
            this.value = value;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "select * from "+ mTable.getTableName()+" where "+columnName+sign+"'"+value+"'"+";";
        }
    }
}
