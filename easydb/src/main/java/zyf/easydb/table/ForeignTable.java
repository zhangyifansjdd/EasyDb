package zyf.easydb.table;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import zyf.easydb.Column;
import zyf.easydb.ColumnTypeEnum;
import zyf.easydb.DbException;

/**
 * 外键对应的表
 * Created by ZhangYifan on 2016/8/18.
 */
public class ForeignTable extends Table {
    private Table mTable;
    private Column mColumn;

    protected ForeignTable(Class clazz, Table table) {
        super(clazz);

        mTable = table;
        HashMap<String, Column> columns = mTable.getColumns();
        Set set = columns.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Column> entry = (Map.Entry<String, Column>) iterator.next();
            Column column = entry.getValue();
            if (column.isPrimaryKey()) {
                mColumn = column;
            }
        }
    }

    @Override
    public void create(SQLiteDatabase database) throws DbException {
        super.create(database);

        String sql1 = "alter table " + mTableName + " add " + mTable.getPrimaryKeyName() + " " + ColumnTypeEnum.INTEGER +
                " references " + mTable.getTableName() + "(" + mTable.getPrimaryKeyName() + ")" + ";";

        database.execSQL(sql1);
    }

    public void insert(@NonNull SQLiteDatabase database, Object object,String val) throws DbException{
        String primaryKeyVal=insert(database,object);

        String sql="update "+mTableName+" set "+mTable.getPrimaryKeyName()+"="+"'"+val+"'"+" where "+getPrimaryKeyName()+"="+"'"+primaryKeyVal+"';";
        database.execSQL(sql);
    }
}
