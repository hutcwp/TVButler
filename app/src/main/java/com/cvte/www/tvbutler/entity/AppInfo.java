package com.cvte.www.tvbutler.entity;

import android.graphics.drawable.Drawable;

/**
 * Created by WuWeiLong on 2017/10/17.
 * Function：后台应用实体类
 */

public class AppInfo {

    @Override
    public String toString() {
        return "AppInfo{" +
                "packageName='" + packageName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    private String name;
    private String packageName;
    private Drawable icon;
    private int pid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
