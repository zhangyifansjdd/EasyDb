package zyf.easydb.table;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.List;

import zyf.easydb.column.Column;
import zyf.easydb.column.ColumnTypeEnum;
import zyf.easydb.DbException;

/**
 * 外键对应的表
 * Created by ZhangYifan on 2016/8/18.
 */
public class ForeignTable extends Table {
    private Table mTable;

    protected ForeignTable(Class clazz, Table table) {
        super(clazz);

        mTable = table;
//        HashMap<String, Column> columns = mTable.getColumns();
//        Set set = columns.entrySet();
//        Iterator iterator = set.iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, Column> entry = (Map.Entry<String, Column>) iterator.next();
//            Column column = entry.getValue();
//            if (column.isPrimaryKey()) {
//                mPrimaryForeignColumn = column;
//            }
//        }
    }

    @Override
    public void create(SQLiteDatabase database) throws DbException {
        super.create(database);

        StringBuilder sqlBuilder=new StringBuilder("alter table ");
        sqlBuilder.append(mTableName).append(" add ").append(mTable.getPrimaryKeyName())
                .append(" ").append(ColumnTypeEnum.INTEGER)
                .append(" references ").append(mTable.getTableName())
                .append("(").append(mTable.getPrimaryKeyName())
                .append(");");
        String sql=sqlBuilder.toString();
        database.execSQL(sql);
    }

    public void insert(@NonNull SQLiteDatabase database, Object object, String val) throws DbException {
        String primaryKeyVal = insert(database, object);

        StringBuilder sqlBuilder=new StringBuilder("update ");
        sqlBuilder.append(mTableName).append( " set ").append(mTable.getPrimaryKeyName())
                .append("='").append(val).append("' where ").append(getPrimaryKeyName())
                .append("='").append(primaryKeyVal).append("';");
        String sql= sqlBuilder.toString();
        database.execSQL(sql);
    }

    public <T> List<T> queryByPrimaryForeignKey(@NonNull SQLiteDatabase database, Column column, String val){

        return null;
    }
}
