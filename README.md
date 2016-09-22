
[一个简单易用的数据库框架](https://github.com/zhangyifansjdd)\n
**gradle引用：**
<pre><code>compile 'zyf.easydb:easydb:0.0.5'
</code></pre>\n

实体类注解设置的实例：
<pre><code>package zyf.testeasydb;
           
           import java.util.List;
           
           import zyf.easydb.column.DbColumn;
           import zyf.easydb.table.DbTable;
           
           
           /**
            * Created by ZhangYifan on 2016/6/29.
            */
           @DbTable(tableName = "user")
           public class User implements Cloneable {
               @DbColumn(columnName = "userid", isPrimaryKey = true)
               private int userId;
           
               @DbColumn(columnName = "name")
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

</code></pre>\n


<pre><code>package zyf.testeasydb;
           
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

</code></pre>\n

<pre><code>package zyf.testeasydb;
           
           
           import zyf.easydb.column.DbColumn;
           import zyf.easydb.table.DbTable;
           
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

</code></pre>\n

`下一步编写计划：` 
1.线程安全问题
2.事务的开启
3.数据库的配置类
4.数据库的升级
5.Selector的编写
