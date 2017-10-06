package com.simon.utils;

import android.app.Application;

import org.xutils.x;

/**
 * Created by Simon on 2017/10/2.
 */

public class MyApplication extends Application {

    private static MyApplication mContext;

    public static MyApplication getInstance() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        x.Ext.init(this);
        // 是否输出debug日志, 开启debug会影响性能.
        //x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
