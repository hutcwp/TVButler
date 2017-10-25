package com.cvte.www.tvbutler.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cvte.www.tvbutler.R;
import com.cvte.www.tvbutler.manager.FlowWindowManager;
import com.cvte.www.tvbutler.utils.LogUtil;
import com.cvte.www.tvbutler.utils.Utils;

/**
 * Created by cwp on 2017/10/18.
 * 自定义悬浮小球窗口
 */

public class FloatCircleView extends LinearLayout {


    String TAG = "FloatView";

    /**
     * 小火箭发射的速度
     */
    private static int mSpeed ;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;

    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager mWindowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float mXInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float mYInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float mXDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float mYDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float mXInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float mYInView;

    /**
     * 记录当前手指是否按下
     */
    private boolean isPressed;

    /**
     * 小球控件
     */
    private LinearLayout mCircleView;

    /**
     * 小火箭控件
     */
    private ImageView mIvRocket;

    /**
     * 记录小悬浮窗的宽度
     */
    private int mCircleViewWidth;

    /**
     * 记录小悬浮窗的高度
     */
    private int mCircleViewHeight;

    /**
     * 记录小火箭的宽度
     */
    private int mRocketWidth;

    /**
     * 记录小火箭的高度
     */
    private int mRocketHeight;
    /**
     * 显示当前使用内存的TextView
     */
    private TextView mTvPercent;

    /**
     * 用于更新当前使用内存显示的TextView，即更新UI
     */
    private Handler mHandler = new Handler();

    /**
     * 用于定时更新UI的一个Runnable
     */
    private UpdateMemoryRunnable mUpdateMemoryRunnable = null;

    /**
     * 本地广播管理类
     */
    private LocalBroadcastManager localBroadcastManager;

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public FloatCircleView(Context context) {
        super(context);
        LogUtil.D(TAG, "构造函数");

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());//获取本地广播管理器实例
        LayoutInflater.from(context).inflate(R.layout.layout_float_circle, this);

        initView();
        initData();
        //使用Handler进行UI更新
        new Thread(mUpdateMemoryRunnable).start();

    }

    /**
     * 初始化数据，小火箭，小球的宽和高
     * 状态栏的高度和updateMemoryRunnable初始化
     */
    private void initData() {
        LogUtil.D(TAG, "初始化数据");
        mRocketWidth = mIvRocket.getLayoutParams().width;
        mRocketHeight = mIvRocket.getLayoutParams().height;
        mCircleViewWidth = mCircleView.getLayoutParams().width;
        mCircleViewHeight = mCircleView.getLayoutParams().height;

        statusBarHeight = Utils.getStatusBarHeight(getContext());
        mSpeed = Utils.dip2px(getContext(),14);
        mUpdateMemoryRunnable = new UpdateMemoryRunnable() {
            @Override
            void doThings() {
                updateMemoryPercent();
            }
        };
    }

    /**
     * 初始化View
     */
    private void initView() {
        LogUtil.D(TAG, "初始化布局");
        mTvPercent = (TextView) findViewById(R.id.tv_percent);
        mIvRocket = (ImageView) findViewById(R.id.rocket_img);
        mCircleView = (LinearLayout) findViewById(R.id.small_window_layout);
    }

    /***
     * 重写触摸事件，实现小球的移动，自动滑到边缘和单击事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.D(TAG, "点击事件：ACTION_DOWN");
                isPressed = true;
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                //手指到当前控件左边的距离getX()
                mXInView = event.getX();
                mYInView = event.getY();
                //手指距离屏幕左边的距离getRawX()
                mXDownInScreen = event.getRawX();
                mYDownInScreen = event.getRawY() - statusBarHeight;
                mXInScreen = event.getRawX();
                mYInScreen = event.getRawY() - statusBarHeight;
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtil.D(TAG, "点击事件：ACTION_MOVE");
                mXInScreen = event.getRawX();
                mYInScreen = event.getRawY() - statusBarHeight;
                //手指移动的时候更新小悬浮窗的位置。
                // dx：x方向上移动的距离，dy:y方向上移动的距离
                int dx = (int) (mXInScreen - mXInView);
                int dy = (int) (mYInScreen - mYInView);
                // 手指移动的时候更新小悬浮窗的状态和位置
                //更新状态
                updateViewStatus();
                //更新位置
                updateViewPosition(dx, dy);
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.D(TAG, "点击事件：ACTION_UP");
                isPressed = false;
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if (mXDownInScreen == mXInScreen && mYDownInScreen == mYInScreen) {
                    circleViewClick();
                } else {
                    if (FlowWindowManager.getInstance().isReadyToLaunch()) {
                        launchRocket();
                    } else {
                        //更新当前悬浮小窗的状态，如果是火箭就换回小球
                        updateViewStatus();

                        LogUtil.D(TAG, "是否超过半个屏幕?" + (mXInScreen > FlowWindowManager.getInstance().getScreenWidth() >> 1));
                        //小球自动回归屏幕边缘
                        HideToMargin();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }


    /**
     * 根据实际情况将小球隐藏到屏幕边缘（左，右）
     */
    private void HideToMargin() {
        LogUtil.D(TAG, "隐藏小球方法触发");
        if (mXInScreen > FlowWindowManager.getInstance().getScreenWidth() >> 1) {
            updateViewPosition(FlowWindowManager.getInstance().getScreenWidth(), mParams.y);
        } else {
            updateViewPosition(0, mParams.y);
        }
    }

    /**
     * 小悬浮框的点击事件
     */
    private void circleViewClick() {
        LogUtil.D(TAG, "小球单击事件触发");
//        Utils.toast(getContext(), "点击了");
        FlowWindowManager.getInstance().changeView(getContext());
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     *
     * @param paramsX 参数 x
     * @param paramsY 参数 y
     */
    private void updateViewPosition(int paramsX, int paramsY) {
        LogUtil.D(TAG, "更新悬浮窗在屏幕中的位置");
        mParams.x = paramsX;
        mParams.y = paramsY;
        mWindowManager.updateViewLayout(this, mParams);
        FlowWindowManager.getInstance().updateLauncher();
        LogUtil.D(TAG, "更新了");
    }

    private void clearMemory() {
        LogUtil.D(TAG, "清理内存方法触发");
        Utils.killAll(getContext(), Utils.getWhiteListBean().getmWhiteList());
        Intent intent = new Intent("com.cvte.www.CLEAR_MEMORY");
        localBroadcastManager.sendBroadcast(intent); // 发送本地广播
        Log.e("TAG", "clearMemory()");
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新当前使用到的内存
     */
    public void updateMemoryPercent() {
        LogUtil.D(TAG, "更新当前内存显示");
        if (mTvPercent != null) {
            long usingMemory = (Utils.getTotalMemoryLong() - Utils.getAvailMemoryLong(getContext()));
            int percent = (int) (usingMemory * 100 / Utils.getTotalMemoryLong());
            String result = percent + "%";
            mTvPercent.setText(result);
            if (percent > 60) {
                Drawable drawable = getResources().getDrawable(R.drawable.back_cicle_full);
                mTvPercent.setBackgroundDrawable(drawable);
            } else {
                Drawable drawable = getResources().getDrawable(R.drawable.back_circle_normal);
                mTvPercent.setBackgroundDrawable(drawable);
            }
        }
    }

    /**
     * 终止当前View,做一些清理工作，如关闭在运行的线程
     */
    public void destroy() {
        LogUtil.D(TAG, "执行销毁时的清理工作");
        mUpdateMemoryRunnable.stop();
    }

    /**
     * 定时功能的Runnable
     */
    abstract class UpdateMemoryRunnable implements Runnable {
        //用于停止线程
        private boolean status = true;

        @Override
        public void run() {
            while (status) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        doThings();
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        abstract void doThings();

        //用来改变标志位进而停止线程
        private void stop() {
            LogUtil.D(TAG, "实时更新使用内存内容的线程停止");
            status = false;
        }
    }

    /**
     * 用于发射小火箭。
     */
    private void launchRocket() {
        LogUtil.D(TAG, "发射火箭");
        clearMemory();
        FlowWindowManager.getInstance().removeLauncher(getContext());
        new LaunchTask().execute();
    }

    /**
     * 更新View的显示状态，判断是显示悬浮窗还是小火箭。
     */
    private void updateViewStatus() {
        LogUtil.D(TAG, "更新视图状态");
        if (isPressed && mIvRocket.getVisibility() != View.VISIBLE) {
            LogUtil.D(TAG, "显示小火箭");
            mParams.width = mRocketWidth;
            mParams.height = mRocketHeight;
            mWindowManager.updateViewLayout(this, mParams);
            mCircleView.setVisibility(View.GONE);
            mIvRocket.setVisibility(View.VISIBLE);
            FlowWindowManager.getInstance().createLauncher(getContext());
        } else if (!isPressed) {
            LogUtil.D(TAG, "显示小球");
            mParams.width = mCircleViewWidth;
            mParams.height = mCircleViewHeight;
            mWindowManager.updateViewLayout(this, mParams);
            mCircleView.setVisibility(View.VISIBLE);
            mIvRocket.setVisibility(View.GONE);
            FlowWindowManager.getInstance().removeLauncher(getContext());
        }
    }

    /**
     * 开始执行发射小火箭的任务。
     */
    private class LaunchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // 在这里对小火箭的位置进行改变，从而产生火箭升空的效果
            while (mParams.y > 0) {
                mParams.y = mParams.y - mSpeed;
                publishProgress();
                try {
                    Thread.sleep(8);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            mWindowManager.updateViewLayout(FloatCircleView.this, mParams);
        }

        @Override
        protected void onPostExecute(Void result) {
            // 火箭升空结束后，回归到悬浮窗状态
            updateViewStatus();
            mParams.x = (int) (mXDownInScreen - mXInView);
            mParams.y = (int) (mYDownInScreen - mYInView);
            mWindowManager.updateViewLayout(FloatCircleView.this, mParams);
        }
    }

    /**
     * 外部获取到悬浮窗的参数 mParams
     *
     * @return mParams 悬浮窗的参数
     */
    public WindowManager.LayoutParams getParams() {
        return mParams;
    }

    /**
     * 获取小球的宽度
     *
     * @return 小球的宽度
     */
    public int getCircleViewWidth() {
        return mCircleViewWidth;
    }

    /**
     * 获取小球的高度
     *
     * @return 小球的高度
     */
    public int getCircleViewHeight() {
        return mCircleViewHeight;
    }

}
