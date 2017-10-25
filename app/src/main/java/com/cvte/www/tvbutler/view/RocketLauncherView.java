package com.cvte.www.tvbutler.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cvte.www.tvbutler.R;

/**
 * Created by cwp on 2017/10/19.
 * 火箭发射台布局
 */

public class RocketLauncherView extends LinearLayout {

    /**
     * 记录火箭发射台的宽度
     */
    private int mRocketWidth;

    /**
     * 记录火箭发射台的高度
     */
    private int mRocketHeight;

    /**
     * 火箭发射台的背景图片
     */
    private ImageView mIvlauncher;

    public RocketLauncherView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.launcher, this);
        mIvlauncher = (ImageView) findViewById(R.id.launcher_img);
        mRocketWidth = mIvlauncher.getLayoutParams().width;
        mRocketHeight = mIvlauncher.getLayoutParams().height;
    }

    /**
     * 更新火箭发射台的显示状态。如果小火箭被拖到火箭发射台上，就显示发射。
     */
    public void updateLauncherStatus(boolean isReadyToLaunch) {
        if (isReadyToLaunch) {
            mIvlauncher.setImageResource(R.drawable.launcher_bg_fire);
        } else {
            mIvlauncher.setImageResource(R.drawable.launcher_bg_hold);
        }
    }

    /**
     * 获取火箭宽度
     *
     * @return 火箭宽度
     */
    public int getRocketWidth() {
        return mRocketWidth;
    }

    /**
     * 获取火箭高度
     *
     * @return 火箭高度
     */
    public int getRocketHeight() {
        return mRocketHeight;
    }

}