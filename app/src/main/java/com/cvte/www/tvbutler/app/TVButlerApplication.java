package com.cvte.www.tvbutler.app;

import android.app.Application;
import android.content.Context;

import com.cvte.www.tvbutler.adapter.RecyclerviewAdapter;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by cwp on 2017/10/19.
 * 用于leakCanary的检测和Application的获取
 */

public class TVButlerApplication extends Application{

    private static Context context;

    public TVButlerApplication() {
    }

    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        if (LeakCanary.isInAnalyzerProcess(this)) {

            return;
        }
        LeakCanary.install(this);

    }
    public static Context getContext() {
                return context;
    }



}
