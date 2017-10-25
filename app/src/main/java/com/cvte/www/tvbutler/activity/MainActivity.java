package com.cvte.www.tvbutler.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import com.cvte.www.tvbutler.R;
import com.cvte.www.tvbutler.adapter.RecyclerviewAdapter;
import com.cvte.www.tvbutler.entity.AppInfo;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.cvte.www.tvbutler.entity.WhiteList;
import com.cvte.www.tvbutler.service.FlowViewService;
import com.cvte.www.tvbutler.utils.Utils;



/**
 * Created by WuWeiLong on 2017/10/17.
 * Function：主页面
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.total)
    TextView mTotal;
    @BindView(R.id.valid)
    TextView mValid;
    @BindView(R.id.cpu)
    TextView mCpu;
    @BindView(R.id.applist)
    RecyclerView mApplist;
    @BindView(R.id.btn_clear)
    Button mBtnClear;

    static List<AppInfo> mList = new ArrayList<>();
    WhiteList mWhiteListBean = Utils.getWhiteListBean();//从配置文件中获取 WhiteList 对象

    public static final int REFRESH = 0x000001;


    private RecyclerviewAdapter mAdapter;

    Context mContext;

    List<String> mWhiteList = mWhiteListBean.getmWhiteList();  //白名单集

    private final static String TAG = MainActivity.class.getSimpleName();

    private static boolean status = true;


    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    @OnClick(R.id.btn_clear)
    public void onClick() {
        Utils.killAll(mContext,mWhiteList);
        //刷新后台列表
        mAdapter.refresh(Utils.getAppInfos(mContext));
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG,"onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mContext = this;
        new MyThread().start();

        Intent intent = new Intent(MainActivity.this, FlowViewService.class);
        startService(intent);

        initRecyclerView();//初始化

        //应用一开启就查看白名单数据，验证数据是否被持久化
        Log.e(TAG,"持久化数据： "+mWhiteList.toString());

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cvte.www.CLEAR_MEMORY");
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
        // 注册本地广播监听器
    }

    private void initRecyclerView() {
        //创建一个线性布局管理器
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mApplist.setLayoutManager(layoutManager);

        //设置适配器
        mAdapter = new RecyclerviewAdapter(mContext,Utils.getAppInfos(mContext));//将所有后台应用显示出来
        mApplist.setAdapter(mAdapter);


        //CheckBox 的监听，设置白名单
        mAdapter.setOnCheckedChangeListener(new RecyclerviewAdapter.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(View view, boolean isChecked, String packageName) {
                if (isChecked){
                    //把该 packageName 加入过滤名单中。
                    Utils.toast(mContext,packageName+" 被加入白名单");
                    if (!mWhiteList.contains(packageName)){
                        mWhiteList.add(packageName);//加入白名单
                        Utils.saveWhiteListBean(mWhiteListBean);//更新配置文件
                    }
                    Log.e(TAG,"添加后的白名单数据："+ mWhiteList.toString());
                }else{
                    Utils.toast(mContext,packageName+" 从白名单移除");
                    mWhiteList.remove(packageName);//移出白名单
                    Utils.saveWhiteListBean(mWhiteListBean);//更新配置文件
                    Log.e(TAG,"移除后的白名单数据："+ mWhiteList.toString());
                }
            }
        });


        //添加点击事件
        mAdapter.setOnItemClickListener(new RecyclerviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e(TAG,"单击");

            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.e("TAG","长按");
            }
        });

    }



    @Override
    protected void onDestroy() {
        status = false;

        mHandler.removeCallbacksAndMessages(null);

        localBroadcastManager.unregisterReceiver(localReceiver);//解除注册

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        status = true;
        new MyThread().start();
        super.onResume();
    }

    @Override
    protected void onPause() {

        status = false;
        super.onPause();
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.refresh(Utils.getAppInfos(context));
            mAdapter.notifyDataSetChanged();
        }
    }

    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            this.obtainMessage();
            if (msg.what == MainActivity.REFRESH) {
                if (mTotal != null) {//this.getTotalMemory()
                    mTotal.setText("总内存：" + Utils.getTotalMemoryLong()/1024+" MB");
                }
                if (mValid != null) {//this.getAvailMemory()
                    mValid.setText("可使用内存：" + Utils.getAvailMemoryLong(mContext)/1024+" MB");
                }
                if (mCpu != null) {
                    mCpu.setText("CPU：" + Math.round(Utils.getUsage()) + "%");
                    //Math.round() 就近取入
                }
            }
        }
    };


    public class MyThread extends Thread {

        public void run() {
            //mList = getAppInfos();//获取所有正在开启中的应用。
            mList = Utils.getAppInfos(mContext);
            while (status) {
                Message msg = new Message();
                msg.what = REFRESH;
                msg.obj = this;
                mHandler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

    }
}

