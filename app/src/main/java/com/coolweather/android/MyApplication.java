package com.coolweather.android;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.android.db.greendao.DaoMaster;
import com.coolweather.android.db.greendao.DaoSession;

public class MyApplication extends Application {

    private static final String DB_NAME = "coll_weather";   //数据库名
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        initGreenDAO();
    }

    //初始化greenDAO
    private void initGreenDAO() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(MyApplication.this, DB_NAME);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

}


/*
DaoMaster:
    DaoMaster是greenDAO使用入口，DAOMaster包含数据库对象（sqliteDatabase）,管理DAO类（不是对象）。可以创建删除表，其内部类OpenHelper和DevOpenHelper是SQLiteOpenHelper实现。
DaoSession:
    管理所有可用DAO对象，通过getter方法获取指定DAO对象。提供了像insert,load,update,refresh和delete这样的方法
DaoXxx:
    数据访问对象（DAO）
*/
