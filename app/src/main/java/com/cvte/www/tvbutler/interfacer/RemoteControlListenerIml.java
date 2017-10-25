package com.cvte.www.tvbutler.interfacer;

import android.app.Instrumentation;
import android.content.Context;

import com.cvte.www.tvbutler.utils.LogUtil;
import com.cvte.www.tvbutler.utils.Utils;

/**
 * Created by cwp on 2017/10/18.
 * 遥控事件监听事件的具体实现接口
 */

public class RemoteControlListenerIml implements IRemoteControlListener{

    private  Context mContext;

    private static String TAG = "RemoteControlListenerIml" ;

    //用来做测试的类，可以通过它来进行遥控按键的实现
    private Instrumentation instrumentation;

    private static final int KEY_UP = 19;
    private static final int KEY_DOWN = 20;
    private static final int KEY_LEFT  = 21;
    private static final int KEY_RIGHT  = 22;
    private static final int KEY_HOME  = 3;
    private static final int KEY_BACK  = 4;
    private static final int KEY_MENU  = 82;


    public RemoteControlListenerIml(Context context) {
        this.mContext = context;
        instrumentation = new Instrumentation();
    }

    @Override
    public void keyUp() {
        LogUtil.D(TAG,"keyUp");
        Utils.toast(mContext,"keyUp");

        click(KEY_UP);

    }

    @Override
    public void keyDown() {
        LogUtil.D(TAG,"keyDown");
        Utils.toast(mContext,"keyDown");

        click(KEY_DOWN);
    }

    @Override
    public void keyLeft() {
        LogUtil.D(TAG,"keyLeft");
        Utils.toast(mContext,"keyLeft");
        click(KEY_LEFT);
    }

    @Override
    public void keyRight() {
        LogUtil.D(TAG,"keyRight");
        Utils.toast(mContext,"keyRight");
        click(KEY_RIGHT);
    }

    @Override
    public void keyMenu() {
        LogUtil.D(TAG,"keyMenu");
        Utils.toast(mContext,"keyMenu");
        click(KEY_MENU);
    }

    @Override
    public void keyBack() {
        LogUtil.D(TAG,"keyBack");
        Utils.toast(mContext,"keyBack");
        click(KEY_BACK);
    }

    @Override
    public void keyHome() {
        LogUtil.D(TAG,"keyHome");
        Utils.toast(mContext,"keyHome");
        click(KEY_HOME);
    }

    @Override
    public void keyClose() {
        LogUtil.D(TAG,"keyClose");
        Utils.toast(mContext,"keyClose");

    }



    /**
     * 模拟实际按键点击
      * @param keyCode 按键代码
     */
    private void click(final int keyCode){
        // 必需在线程中运行,否者报错
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.D("click","click方法，keyCode"+keyCode);
                LogUtil.D("click","#######before#######");
                instrumentation.sendKeyDownUpSync(keyCode);
                LogUtil.D("click","#######after#######");
            }
        });
        t.start();
    }
}
