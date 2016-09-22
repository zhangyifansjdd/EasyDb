
[一个简单易用的数据库框架](https://github.com/zhangyifansjdd)

**gradle引用：**
<pre><code>
compile 'zyf.easydb:easydb:0.0.5'
</code></pre>

实体类注解设置的实例：

1.最外层的类
<pre><code>
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

2.作为User中的一个成员变量保存
<pre><code>
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

3.作为User中一个使用list保存的类
<pre><code>
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

</code></pre>

`下一步编写计划：` 
1.线程安全问题
2.事务的开启
3.数据库的配置类
4.数据库的升级
5.Selector的编写
