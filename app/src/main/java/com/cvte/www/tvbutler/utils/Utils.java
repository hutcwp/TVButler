package com.cvte.www.tvbutler.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import com.cvte.www.tvbutler.entity.AppInfo;
import com.cvte.www.tvbutler.entity.WhiteList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WuWeiLong on 2017/10/17.
 * Function:工具类，包括吐司、系统内存大小、CPU使用率、清理后台应用等
 */

public class Utils {
    private final static String TAG = Utils.class.getSimpleName();

    //提供给计算辅助：CPU 使用情况
    static long total = 0;
    static long valid = 0;
    static double cpu = 0;

    //保存文件的路径
    static String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/WhiteList.txt";

    //保存文件的路径
    //static String path2= Environment.getExternalStorageDirectory().getAbsolutePath()+"/AdapterBean.txt";

    public static void toast(Context context, String msg){
        Toast.makeText( context, msg, Toast.LENGTH_SHORT).show();
    }
    /*
      * *获取可用内存大小：long 类型,单位为 KB,除以1024 转换 为 MB
      */
    public static long getAvailMemoryLong(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem/1024;
    }

    // 获取总共内存大小
    public static long getTotalMemoryLong() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;

        long i = 0;
        String[] arrayOfString;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            arrayOfString = str2.split("\\s+");

            i = Long.parseLong(arrayOfString[1]);

            localBufferedReader.close();
        } catch (IOException e) {
             e.printStackTrace();
        }
        return i;
    }


    // CPU 使用率
    public static double getUsage() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();

            String[] toks = load.split(" ");

            long currTotal = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]);
            long currIdle = Long.parseLong(toks[5]);

            cpu = (currTotal - total) * 100.0f / (currTotal - total + currIdle - valid);  // CPU 使用率
            total = currTotal;
            valid = currIdle;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return cpu;
    }
    /*
        * 杀死后台进程,清理内存
        * 稳妥
        */
    public static void killAll(Context context,List<String> WhiteList){
        List<String> mWhiteList = WhiteList;
        Log.e(TAG +" killAll()",mWhiteList.contains("com.cvte.www.cvte")+" com.cvte.www.cvte");
        Log.e(TAG +" killAll()",mWhiteList.contains("com.cvte.www.testclear")+" com.cvte.www.testclear");
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        //获得所有已经安装的应用
        packages = pm.getInstalledApplications(0);

        ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        String myPackage = context.getApplicationContext().getPackageName();
        for (ApplicationInfo packageInfo : packages) {
            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1){
                continue;
            }else
                if(packageInfo.packageName.equals(myPackage)){
                continue;
            }else
                if(mWhiteList.contains(packageInfo.packageName)){
                continue;   //过滤白名单
            }
            Log.e(TAG + "killAll()","真正被我 Kill 掉的应用 "+ packageInfo.packageName);
            mActivityManager.killBackgroundProcesses(packageInfo.packageName);
        }
    }

    /**
     * 获取正在运行的APP列表
     */
    public static List<AppInfo> getAppInfos(Context context){
        List<AppInfo> appInfos = new ArrayList<>();
        // 获取正在运行的进程
        ActivityManager mActivityManager = (ActivityManager)context
                .getSystemService(Context.ACTIVITY_SERVICE);
        // 查询所有已经安装的应用程序
        PackageManager packageManager = context.getPackageManager();
        //List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);

        List<ActivityManager.RunningAppProcessInfo> appProcesses
                = mActivityManager.getRunningAppProcesses();//获取存活在后台的应用进程

        List<ApplicationInfo> packages;
        packages = packageManager.getInstalledApplications(0);//获得所有已经安装的应用，但是并不是一定在后台运行中。。。


        String myPackage = context.getApplicationContext().getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            for (ApplicationInfo packageInfo : packages) {
                if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) continue;
                if (packageInfo.packageName.equals(myPackage))  continue;
                if (packageInfo.processName.equals(appProcess.processName)) {  //如果确实存活就显示出来
                    AppInfo appInfo = new AppInfo();
                    // 获得应用名
                    appInfo.setName(packageManager.getApplicationLabel(packageInfo).toString());
                    // 获得应用包名
                    appInfo.setPackageName(packageInfo.packageName);
                    appInfo.setIcon(packageInfo.loadIcon(packageManager));
                    appInfo.setPid(packageInfo.uid);
                    appInfos.add(appInfo);
                    continue;
                }
                continue;
            }
        }
        return appInfos;
    }


    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    public static int getStatusBarHeight(Context context) {

            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                return context.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return 0;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     *  Created by wuweilong on 2017/10/18
     *  Function:操作本地序列化 WhiteList 白名单
     */
    public static WhiteList getWhiteListBean(){
        ObjectInputStream ois=null;
        WhiteList whitelist = new WhiteList();
        try {
            Log.e("TAG", "持久化数据的路径 "+new File(path).getAbsolutePath());
            //获取输入流
            ois=new ObjectInputStream(new FileInputStream(new File(path)));
            //获取文件中的数据
            whitelist = (WhiteList) ois.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (ois!=null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return whitelist;
    }
    public static void saveWhiteListBean(WhiteList whiteList){
        ObjectOutputStream fos=null;
        try {

            //如果文件不存在就创建文件
            File file=new File(path);
            //file.createNewFile();
            //获取输出流
            //这里如果文件不存在会创建文件，这是写文件和读文件不同的地方
            fos=new ObjectOutputStream(new FileOutputStream(file));

            //要使用writeObject
            fos.writeObject(whiteList);;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (fos!=null) {
                    fos.close();
                }
            } catch (IOException e) {
            }

        }
    }

}
