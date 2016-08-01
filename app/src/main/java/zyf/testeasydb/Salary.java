package zyf.testeasydb;


import zyf.easydb.annotation.DbColumn;
import zyf.easydb.annotation.DbTable;

/**
 * Created by ZhangYifan on 2016/7/20.
 */
@DbTable(tableName = "salary")
public class Salary {
    @DbColumn(columnName = "salaryId" ,isPrimaryKey = true)
    int salaryId;

    @DbColumn(columnName = "price")
    int price;

    public Salary() {
    }

    public Salary(int salaryId, int price) {
        this.salaryId = salaryId;
        this.price = price;
    }

    public int getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(int salaryId) {
        this.salaryId = salaryId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
