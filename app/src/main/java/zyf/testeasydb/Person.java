package zyf.testeasydb;


import zyf.easydb.column.DbColumn;
import zyf.easydb.table.DbTable;

/**
 * Created by ZhangYifan on 2016/7/27.
 */
@DbTable(tableName = "person")
public class Person {
    @DbColumn(columnName = "id",isPrimaryKey = true)
    private int id;
    private String name;

    public Person() {
    }

    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
