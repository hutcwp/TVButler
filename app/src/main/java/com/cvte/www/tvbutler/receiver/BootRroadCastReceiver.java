package com.cvte.www.tvbutler.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cvte.www.tvbutler.service.FlowViewService;
import com.cvte.www.tvbutler.service.GrayService;
import com.cvte.www.tvbutler.service.WhiteService;

/**
 * Created by WuWeiLong on 2017/10/18.
 * Function:开机自启动实现
 */

public class BootRroadCastReceiver extends BroadcastReceiver{
    private
    final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_BOOT.equals(intent.getAction())){
            Toast.makeText(context, "开机自启动", Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(context, FlowViewService.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //必须添加这个标记，否则启动会失败
            context.startService(newIntent);
        }else if (intent.getAction().equals("customs.boot.aciton")){
            Toast.makeText(context, "被杀死后自启动", Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(context, FlowViewService.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //必须添加这个标记，否则启动会失败
            context.startService(newIntent);
        }

        //开启灰色保活
        Intent grayIntent = new Intent(context, GrayService.class);
        context.startService(grayIntent);

        //开启白色保活：前台方式
        //Intent whiteIntent = new Intent(context, WhiteService.class);
        //context.startService(whiteIntent);

        //发广播方式 通知 WakeReceiver 启动灰色保活亦可

    }
}
