package com.cvte.www.tvbutler.interfacer;

/**
 * Created by cwp on 2017/10/18.
 * 遥控按键的监听接口
 */
public interface IRemoteControlListener {

    void keyUp();
    void keyDown();
    void keyLeft();
    void keyRight();
    void keyMenu();
    void keyBack();
    void keyHome();
    void keyClose();

}
