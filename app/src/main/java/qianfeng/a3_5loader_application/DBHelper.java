package qianfeng.a3_5loader_application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/9/9 0009.
 */
public class DBHelper extends SQLiteOpenHelper {

    private final static String DBNAME = "qf.db";
    public final static String USERTABLE = "usertable";
    private final static int DBVERSION = 1;

    public DBHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //建表语句中，不能随意出现空格
        db.execSQL("CREATE TABLE IF NOT EXISTS " + USERTABLE + "(_id PRIMARY KEY,USERNAME,NICKNAME,AGE);");  //建表语句中，不能随意出现空格
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
