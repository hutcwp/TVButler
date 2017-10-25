package com.cvte.www.tvbutler.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/10/18.
 * Function:白名单实体类
 */

public class WhiteList implements Serializable {

    public WhiteList() {
        mWhiteList = new ArrayList<>();
    }

    private List<String> mWhiteList;

    public List<String> getmWhiteList() {
        return mWhiteList;
    }

    public void setmWhiteList(List<String> mWhiteList) {
        this.mWhiteList = mWhiteList;
    }

    @Override
    public String toString() {
        return "WhiteList{" +
                "mWhiteList=" + mWhiteList +
                '}';
    }
}
