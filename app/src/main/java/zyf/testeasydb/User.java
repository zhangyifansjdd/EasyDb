package zyf.testeasydb;

import java.util.List;

import zyf.easydb.annotation.DbColumn;
import zyf.easydb.annotation.DbTable;


/**
 * Created by ZhangYifan on 2016/6/29.
 */
@DbTable(tableName = "user")
public class User implements Cloneable{
    @DbColumn(columnName = "userid", isPrimaryKey = true)
    private int userId;

    @DbColumn(columnName = "name")
    private String name;

    @DbColumn(createForeignTable = true, foreignClass = Salary.class)
    private List<Salary> salaries;

//    @DbColumn(createForeignTable = true,foreignClass = String.class)
//    private String[] friends;

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

//    public String[] getFriends() {
//        return friends;
//    }
//
//    public void setFriends(String[] friends) {
//        this.friends = friends;
//    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
