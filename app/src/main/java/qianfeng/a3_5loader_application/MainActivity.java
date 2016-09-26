package qianfeng.a3_5loader_application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;



public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static SQLiteDatabase db;
    private LoaderManager supportLoaderManager;
    private SimpleCursorAdapter simpleCursorAdapter;

    //Loader:异步加载
    // 因为当数据库比较大时，查询如果放在主线程中，会引起主线程阻塞，所以，可以开一个异步加载器，在子线程中查找数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this).getReadableDatabase();

        supportLoaderManager = getSupportLoaderManager();

        // initLoader:里面的第一个参数：是这个loader的唯一标识符，谁调用这个id，就是在调用这个loader，防止多个loader时，分不清哪个loader
        supportLoaderManager.initLoader(1,null,this); // 第三个参数填this，是我想让这个Activity来作为实现这个接口的类。
        // 第三个参数表示loader的回调接口
        // initLoader():这个方法就是在创建一个Loader，会调用实现它的接口的类中的onCreateLoader方法,然后可以在这个方法里面，new出你的异步查询类，并返回它。
        // 那么如果需要用到异步查询类的话，就要继承自AsyncTaskLoader(),那么就要重写这个继承类里面的方法。然后当AsyncTaskLoader的子类在loadInBackgroun中读取
        // 数据成功后，就会调用initLoader的实现类里面的onLoadFinished()方法。
        // 问题1：谁是这个initLoader的实现类，请看public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
        // 注意后面的implements LoaderManager.LoaderCallbacks<Cursor>，这里的泛型的类型是Cursor

        ListView lv = (ListView) findViewById(R.id.lv);
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.item, null,  // 这里传个null，是因为待会可以利用adapter.swapLoader()进行
                new String[]{"USERNAME", "NICKNAME"}, new int[]{R.id.tv_username, R.id.tv_nickname},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lv.setAdapter(simpleCursorAdapter);


    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) { // loader被创建时，回调这个方法(程序从上往下走，回调就是程序执行它之前的代码)
        // 创建Loader时回调该方法

        // 在这里要新建一个异步装载器

        return new MyLoader(this); // 写这个return的时候，要记得创建一个类，继承自AsyncTaskLoader<Cursor>

        // 一旦new了 MyLoader对象之后，就马上执行里面onStartLoading，loadInBackground()方法

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // 异步装载器读取数据成功后，马上让它和原有的数据进行交换
        // adapter要抽取成全局的
        simpleCursorAdapter.swapCursor(data);// data是新的Cursor，simpleCursorAdapter里面是旧的Cursor，执行完这个方法后，simpleCursorAdapter里面的数据就变成是data了，是新的，刚刚读取好的数据（只是一部分）

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapter.swapCursor(null);
    }

    public void add(View view) {  // 添加数据
        ContentValues values = new ContentValues();
        values.put("USERNAME","zhangsan");
        values.put("NICKNAME","张三");
        values.put("AGE",66);
        db.insert(DBHelper.USERTABLE,null,values);

        supportLoaderManager.restartLoader(1,null,this); // 一直异步加载数据？
    }


    // AsyncTaskLoader异步装载器，在子线程中查询数据，然后自动更新UI
    static class MyLoader extends AsyncTaskLoader<Cursor> { // 这里要用static，是内部类

        public MyLoader(Context context) {
            super(context);
        }

        // 类似于AsyncTaskLoader的onPreExecute方法
        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            // 第一次强制执行数据读取。
            forceLoad();// 第一次调用Cursor之前，就强制让你执行这个方法。是因为防止你第一次的时候不查询，就没法加载到数据了。
        }


        // 类似于AsyncTaskLoader的doInBackground方法
        @Override
        public Cursor loadInBackground() {
            // 在子线程中执行查询,并将结果返回给cursor
            Cursor cursor = db.rawQuery("select * from " + DBHelper.USERTABLE, null); // db要想在static类里面查询，就要做成是static变量
            return cursor;

        }
    }

}
