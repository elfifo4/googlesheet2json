package com.eladfinish.sheet2json;

import android.app.Application;

import com.eladfinish.sheet2json.utils.MyDebugTree;
import com.eladfinish.sheet2json.utils.ReleaseTree;

import timber.log.Timber;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initializeDebug();
    }

    private void initializeDebug() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new MyDebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }
    }
}
