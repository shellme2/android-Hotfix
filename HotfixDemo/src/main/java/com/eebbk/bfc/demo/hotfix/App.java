package com.eebbk.bfc.demo.hotfix;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.eebbk.bfc.hotfix.HotfixManager;

/**
 * Created by Simon on 2017/1/20.
 */

public class App extends Application {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        HotfixManager.init(this);
    }
}
