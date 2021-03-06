package zyf.testeasydb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import zyf.easydb.DbException;
import zyf.easydb.EasyDb;
import zyf.easydb.EasyDbConfig;
import zyf.easydb.Where;

public class MainActivity extends AppCompatActivity {

    private EasyDb mEasyDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mEasyDb = EasyUtil.getEasyDb(this);
        mEasyDb = new EasyDb.Builder().buildEasyDb(new EasyDbConfig(getApplication()));
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
        User user = new User(1, "ZYF");
        List<Salary> salaries = new ArrayList<>();
        Salary salary1 = new Salary(1, 1000);
        Salary salary2 = new Salary(2, 2000);
        salaries.add(salary1);
        salaries.add(salary2);
        user.setSalaries(salaries);
        user.setHome(new Home("金地", 140));
        mEasyDb.insert(user);
        List<User> list = mEasyDb.queryAll(User.class);
        Where where = new Where();
        where.andExpress(new Where.Express("id", ">", "10"));
        where.orExpress(new Where.Express("name","=","ZYF"));
        Log.i("ZYF1", "test: "+where.toString());
//        List<User> list1 = mEasyDb.query(selector);
        mEasyDb.delete(user);
        Log.i("ZYF", "test: ");
    }
}
