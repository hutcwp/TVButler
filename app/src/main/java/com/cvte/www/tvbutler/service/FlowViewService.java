package com.cvte.www.tvbutler.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cvte.www.tvbutler.manager.FlowWindowManager;
import com.cvte.www.tvbutler.utils.LogUtil;

/**
 * 用于开启悬浮窗口的服务
 */
public class FlowViewService extends Service {

    public static String Tag = "Service";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        LogUtil.D(Tag ,"FlowViewService onCreate");
        FlowWindowManager.getInstance().createCircleView(getApplicationContext());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LogUtil.D(Tag ,"FlowViewService Destroy");
    }

}
