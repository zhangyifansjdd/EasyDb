package zyf.testeasydb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import zyf.easydb.DbException;
import zyf.easydb.EasyDb;
import zyf.easydb.EasyUtil;

public class MainActivity extends AppCompatActivity {

    private EasyDb mEasyDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEasyDb = EasyUtil.getEasyDb(this);

        new Thread() {
            @Override
            public void run() {
                try {
                    test();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void test() throws DbException {
        mEasyDb.dropTable(User.class);
//        mEasyDb.deleteAll(User.class);
////        mEasyDb.dropTable(User.class);
        User user = new User(1, "ZYF");
//        try {
//            User user1 = (User) user.clone();
//            user1.setName("KJHKHKH");
//            Log.i("ZYF", "test: ");
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
        List<Salary> salaries = new ArrayList<>();
        Salary salary1 = new Salary(1, 1000);
        Salary salary2 = new Salary(2, 2000);
        salaries.add(salary1);
        salaries.add(salary2);
        user.setSalaries(salaries);
        mEasyDb.save(user);
//        mEasyDb.delete(user);
//        Table table=Table.getTableInstance(User.class);
//        mEasyDb.dropTable(User.class);
//        mEasyDb.createTableIfNotExist(User.class);
//        mEasyDb.dropTable(User.class);
        Log.i("ZYF", "test: ");
    }
}
