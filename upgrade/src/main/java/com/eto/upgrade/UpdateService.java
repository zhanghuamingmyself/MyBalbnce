package com.eto.upgrade;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.eto.upgrade.utils.CommonUtils;
import com.eto.upgrade.utils.LogFileUtils;



public class UpdateService extends Service {
    private static final String TAG = "UpdateService";
    boolean isRunning = false;
    Context mContext;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub  
        super.onCreate();
        mContext = this;
        isRunning = true;
        LogFileUtils.setDebug(true);
        LogFileUtils.logTxt("Service Create()");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        LogFileUtils.logTxt("onStartCommand flags=" + flags + ",startId=" + startId + ",intent=" + intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){}
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub  
        return null;
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        LogFileUtils.logTxt("onDestroy");
        super.onDestroy();

    }


    static boolean package1_installed = false;
    public static void checkInstalled(Context context) {

        if (CommonUtils.isAppInstalled(context, StaticCfg.updatePackageName)) {
            package1_installed = true;
            checkRunning(context);
        } else {
            package1_installed = false;
        }
        LogFileUtils.logTxt(StaticCfg.updatePackageName + ": installed=" + package1_installed);
    }

    public static void checkRunning(Context context) {
        try {

            if (package1_installed) {
                boolean isRunning = CommonUtils.isAppRunning(context, StaticCfg.updatePackageName);
                if (!isRunning) {

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName cn = new ComponentName(StaticCfg.updatePackageName, StaticCfg.updatePackageName + "."+StaticCfg.updateActivityName);
                    intent.setComponent(cn);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    context.startActivity(intent);
                    LogFileUtils.logTxt("start activity " + StaticCfg.updatePackageName);
                }
                System.out.println("upgrade: " + StaticCfg.updatePackageName + " is installed And service run state=" + isRunning);
            } else {
                System.out.println("upgrade: all listen app is not installed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("upgrade:  start fail");
        }
    }

}  
