package com.cvte.www.tvbutler.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cvte.www.tvbutler.R;

import com.cvte.www.tvbutler.app.TVButlerApplication;
import com.cvte.www.tvbutler.interfacer.IRemoteControlListener;
import com.cvte.www.tvbutler.interfacer.RemoteControlListenerIml;
import com.cvte.www.tvbutler.manager.FlowWindowManager;
import com.cvte.www.tvbutler.utils.LogUtil;

/**
 * Created by cwp on 2017/10/18.
 * 悬浮遥控菜单布局
 */

public class FloatMenuView extends LinearLayout {

    private static String TAG = "FloatMenuView";

    /**
     * 悬浮遥控窗口的宽度
     */
    private int mMenuViewWidth;

    /**
     * 悬浮遥控窗口的高度

     */
    private int mMenuViewHeight;

    /**
     * 悬浮窗遥控器的根View
     */
    private View mControlViewRoot;

    /**
     * 悬浮窗遥控器的每个按键
     */
    private Button mBtnLeft;
    private Button mBtnRight;
    private Button mBtnUp;
    private Button mBtnDown;
    private Button mBtnMenu;
    private Button mBtnBack;
    private Button mBtnHome;
    private Button mBtnClose;

    /**
     * 遥控事件的实现接口
     */
    private IRemoteControlListener mKeyListener;



    /**
     * 构造方法
     */
    public FloatMenuView(Context context) {
        super(context);
        LogUtil.D(TAG, "构造函数");
        LayoutInflater.from(TVButlerApplication.getContext()).inflate(R.layout.layout_float_menu, this);
        initView();
        //initView 必须先执行，因为要初始化view
        initData();
        initSetClickListener();
    }

    /**
     * 初始化View
     */
    private void initView() {
        LogUtil.D(TAG, "初始化视图");
        mControlViewRoot = findViewById(R.id.ly_root);
        mBtnLeft = findView(R.id.btn_left);
        mBtnRight = findView(R.id.btn_right);
        mBtnUp = findView(R.id.btn_up);
        mBtnDown = findView(R.id.btn_down);
        mBtnMenu = findView(R.id.btn_menu);
        mBtnBack = findView(R.id.btn_back);
        mBtnHome = findView(R.id.btn_home);
        mBtnClose = findView(R.id.btn_close);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        LogUtil.D(TAG, "初始化数据");
        mMenuViewWidth = mControlViewRoot.getLayoutParams().width;
        mMenuViewHeight = mControlViewRoot.getLayoutParams().height;
        mKeyListener = new RemoteControlListenerIml(getContext());
    }

    /**
     * 初始化点击事件
     */
    public void initSetClickListener() {
        LogUtil.D(TAG, "初始化点击事件监听器");
        mBtnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FlowWindowManager.getInstance().changeView(getContext());
            }
        });
        mBtnLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyListener.keyLeft();
            }
        });
        mBtnRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyListener.keyRight();
            }
        });
        mBtnUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyListener.keyUp();
            }
        });
        mBtnDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyListener.keyDown();
            }
        });
        mBtnMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyListener.keyMenu();
            }
        });
        mBtnHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyListener.keyHome();
            }
        });
        mBtnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyListener.keyBack();
            }
        });
    }

    /**
     * 封装findViewById
     */
    private <T extends View> T findView(int id) {
        return (T) mControlViewRoot.findViewById(id);
    }

    /**
     * 获取悬浮窗的宽度
     * @return 悬浮窗的宽度
     */
    public int getmMenuViewWidth() {
        return mMenuViewWidth;
    }

    /**
     * 获取悬浮窗的高度
     * @return 悬浮窗的高度
     */
    public int getmMenuViewHeight() {
        return mMenuViewHeight;
    }
}