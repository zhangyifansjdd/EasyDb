package zyf.testeasydb;

import zyf.easydb.column.DbColumn;
import zyf.easydb.table.DbTable;

/**
 * Created by ZhangYifan on 2016/9/22.
 */
@DbTable(tableName = "home")
public class Home {
    @DbColumn(isPrimaryKey = true,columnName = "address")
    private String address;
    @DbColumn(columnName = "price")
    private int price;

    public Home() {
    }

    public Home(String address, int price) {
        this.address = address;
        this.price = price;
    }
}
