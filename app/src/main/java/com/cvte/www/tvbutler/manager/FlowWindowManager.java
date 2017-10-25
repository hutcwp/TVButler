package com.cvte.www.tvbutler.manager;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

import com.cvte.www.tvbutler.app.TVButlerApplication;
import com.cvte.www.tvbutler.utils.LogUtil;
import com.cvte.www.tvbutler.view.FloatCircleView;
import com.cvte.www.tvbutler.view.FloatMenuView;
import com.cvte.www.tvbutler.view.RocketLauncherView;

/**
 * Created by cwp on 2017/10/17.
 * 用于管理悬浮窗口的Manager类
 */
public class FlowWindowManager {

    private static String TAG = "FlowWindowManager";

    /**
     * 0代表遥控菜单布局
     */
    private static final int FLOAT_MENU_VIEW = 0;

    /**
     * 1代表小球布局
     */
    private static final int FLOAT_CIRCLE_VIEW = 1;

    /**
     * 用于标识当前显示的布局
     */
    private int mCurrentLayout = FLOAT_CIRCLE_VIEW;

    /**
     * 记录小球隐藏的位置距离屏幕的 x
     */
    private int mParamsX = 0;

    /**
     * 记录小球隐藏的位置距离屏幕的 y
     */
    private int mParamsY = 0;

    /**
     * 窗口管理器，用来创建悬浮小窗
     */
    private WindowManager mWindowManager;

    /**
     * 悬浮小球
     */
    private FloatCircleView mCircleView;


    /**
     * 悬浮菜单
     */
    private FloatMenuView mMenuView;


    /**
     * 火箭发射台的实例
     */
    private RocketLauncherView mRocketLauncherView;

    /**
     * 悬浮小球的参数
     */
    private WindowManager.LayoutParams mCircleParams;


    /**
     * 悬浮遥控菜单的参数
     */
    private WindowManager.LayoutParams mMenuParams;


    /**
     * 火箭发射台的参数
     */
    private WindowManager.LayoutParams mLauncherParams;

    /**
     * FloatWindowManager的单例类
     */
    private static FlowWindowManager mInstance;

    /**
     * 创建遥控菜单的悬浮View
     *
     * @param context 上下文
     */
    public void createMenuView(Context context) {
        mCurrentLayout = FLOAT_MENU_VIEW;
        mMenuView = new FloatMenuView(context);
        if (mMenuParams == null) {
            mMenuParams = new WindowManager.LayoutParams();
            initParams(mMenuParams);
            mMenuParams.width = mMenuView.getmMenuViewWidth();
            mMenuParams.height = mMenuView.getmMenuViewHeight();
            LogUtil.D(TAG, "width:" + mMenuView.getmMenuViewWidth() + " --- mMenuView.getmMenuViewHeight():" + mMenuView.getmMenuViewHeight());
        }
        mMenuParams.x = mParamsX;
        mMenuParams.y = mParamsY;
        getWindowManager(TVButlerApplication.getContext()).addView(mMenuView, mMenuParams);
    }

    /**
     * 创建悬浮小球窗口
     *
     * @param context 上下文
     */
    public void createCircleView(Context context) {
        mCurrentLayout = FLOAT_CIRCLE_VIEW;
        mCircleView = new FloatCircleView(context);
        if (mCircleParams == null) {
            mCircleParams = new WindowManager.LayoutParams();
            initParams(mCircleParams);
            //必须设置宽度和高度，默认会是match_parent，会覆盖整个屏幕，阻塞其他的布局的触摸事件获得响应
            mCircleParams.width = mCircleView.getCircleViewWidth();
            mCircleParams.height = mCircleView.getCircleViewHeight();
        }
        mCircleParams.x = mParamsX;
        mCircleParams.y = mParamsY;
        mCircleView.setParams(mCircleParams);
        getWindowManager(TVButlerApplication.getContext()).addView(mCircleView, mCircleParams);
    }

    /**
     * 创建一个火箭发射台，位置为屏幕底部。
     */
    public void createLauncher(Context context) {
        int screenWidth = getScreenWidth();
        int screenHeight = getScreenHeight();
        if (mRocketLauncherView == null) {
            mRocketLauncherView = new RocketLauncherView(context);
            if (mLauncherParams == null) {
                mLauncherParams = new WindowManager.LayoutParams();
                LogUtil.D(TAG, "ParamsX:" + (screenWidth >> 1 - (mRocketLauncherView.getRocketWidth() >> 1)));
                mLauncherParams.x = (screenWidth >> 1) - (mRocketLauncherView.getRocketWidth() >> 1);
                mLauncherParams.y = screenHeight - mRocketLauncherView.getRocketHeight();
                initParams(mLauncherParams);
                mLauncherParams.width = mRocketLauncherView.getRocketWidth();
                mLauncherParams.height = mRocketLauncherView.getRocketHeight();
            }
            getWindowManager(TVButlerApplication.getContext()).addView(mRocketLauncherView, mLauncherParams);
        }
    }

    /**
     * * 抽取掉一些相同的代码
     */
    private void initParams(WindowManager.LayoutParams params) {
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.format = PixelFormat.RGBA_8888;
        params.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.LEFT | Gravity.TOP;
    }

    /**
     * 切换布局，小球和遥控布局进行切换
     *
     * @param context 上下文
     */
    public void changeView(Context context) {
        if (mCurrentLayout == FLOAT_MENU_VIEW) {
            removeFlowMenuLayout(context);
            createCircleView(context);
        } else {
            removeFlowCircleLayout(context);
            createMenuView(context);
        }
    }

    /**
     * 移除悬浮遥控菜单布局
     *
     * @param context 上下文
     */
    public void removeFlowMenuLayout(Context context) {
        if (mMenuView != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(mMenuView);
            mMenuView = null;
        }
    }

    /**
     * 移除悬浮小球布局
     *
     * @param context 上下文
     */
    public void removeFlowCircleLayout(Context context) {
        if (mCircleView != null) {
            WindowManager.LayoutParams params = mCircleView.getParams();
            //记录一下小球消失时的x和y坐标，用于恢复
            if (params != null) {
                mParamsX = params.x;
                mParamsY = params.y;
            }
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(mCircleView);
            mCircleView.destroy();
            mCircleView = null;
        }
    }

    /**
     * 将火箭发射台从屏幕上移除。
     *
     * @param context 上下文
     */
    public void removeLauncher(Context context) {
        if (mRocketLauncherView != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(mRocketLauncherView);
            mRocketLauncherView = null;
        }
    }

    /**
     * 更新火箭发射台的显示状态。
     */
    public void updateLauncher() {
        if (mRocketLauncherView != null) {
            mRocketLauncherView.updateLauncherStatus(isReadyToLaunch());
        }
    }

    /**
     * 判断小火箭是否准备好发射了。
     *
     * @return 当火箭被发到发射台上返回true，否则返回false。
     */
    public boolean isReadyToLaunch() {
        //如果火箭尚未创建，直接返回false
        if (mCircleParams == null || mLauncherParams == null) {
            return false;
        } else {
            //通过对比小火箭和发射台是否重合来判断是否需要发射
            if ((mCircleParams.x > mLauncherParams.x
                    && mCircleParams.x + mCircleParams.width < mLauncherParams.x + mLauncherParams.width)
                    && (mCircleParams.y + mCircleParams.height > mLauncherParams.y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得屏幕的宽度
     *
     * @return 屏幕的宽度
     */
    public int getScreenWidth() {
        return mWindowManager.getDefaultDisplay().getWidth();
    }

    /**
     * 获得屏幕的高度
     *
     * @return 屏幕的高度
     */
    public int getScreenHeight() {
        return mWindowManager.getDefaultDisplay().getHeight();
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context 必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 单例类的实现，非线程安全
     *
     * @return FlowWindowManager的单例
     */
    public static FlowWindowManager getInstance() {

        if (mInstance == null) {
            mInstance = new FlowWindowManager();
        }
        return mInstance;
    }


    //构造方法
    private FlowWindowManager() {

    }


}
