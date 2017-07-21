package com.niwj.instantmessaging;

import android.app.Application;

import org.litepal.LitePal;

import cn.jpush.im.android.api.JMessageClient;

/**
 * Created by prince70 on 2017/7/21.
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 极光IM
         */
        JMessageClient.setDebugMode(true);
        JMessageClient.init(this);
        /**
         * litepal数据库
         */
        LitePal.initialize(this);
    }
}
