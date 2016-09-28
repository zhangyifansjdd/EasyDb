package zyf.testeasydb;

import java.util.List;

import zyf.easydb.column.DbColumn;
import zyf.easydb.table.DbTable;


/**
 * Created by ZhangYifan on 2016/6/29.
 */
@DbTable(tableName = "user")
public class User implements Cloneable {

    private static final long serialVersionUID = 1L;

    @DbColumn(columnName = "userid", isPrimaryKey = true)
    private int userId;

//    @DbColumn(columnName = "name")
    private String name;

    @DbColumn(createForeignTable = true, foreignClass = Home.class)
    private Home home;

    @DbColumn(createForeignTable = true, foreignClass = Salary.class)
    private List<Salary> salaries;

    public User() {
    }

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Salary> getSalaries() {
        return salaries;
    }

    public void setSalaries(List<Salary> salaries) {

        this.salaries = salaries;
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
