package com.zhanghuaming.mybalbnce.bean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.zhanghuaming.mybalbnce.utils.CommandUtils;
import com.zhanghuaming.mybalbnce.utils.UpdateApk;

import java.io.File;

/**
 * Created by Administrator on 2016/10/17.
 */
public class InstallApk {

    public static final String ACTION_INSTALL = "com.eto.upgrade.action.install";

    public static final String EXTRA_PARAM1 = "com.eto.upgrade.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.eto.upgrade.extra.PARAM2";

    public static final String INTALL_APP_PACKAGENAME= "com.eto.upgrade";
    public static final String INTALL_APP_SERVICENAME= "com.eto.upgrade.UpdateService";






    // 提示安装
    public static void openFile(Context context, File file) {
        // TODO Auto-generated method stub
        Log.i("OpenFile", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }



    public static boolean installApk_Cmd(Context context ,String apkFilePath){
        MyAppInfo info = UpdateApk.getApkInfo_ByApkFile(context,apkFilePath);
        if(info != null) {
            try {
                if(!info.packageName.equals(context.getPackageName())){
                    System.out.println("install apk " + apkFilePath);
                    String  ret = CommandUtils.hpcmdExec("pm install -r "+apkFilePath);
                    System.out.println(ret);
                    return true;
                }else{
                    myToast(context, "不能自我安装！");
                }

            } catch (Exception e) {
                e.printStackTrace();
                myToast(context, "安装" + info.appName + "失败！");
            }
        }else{
            myToast(context,"安装 apk 失败！");

        }
        return false;

    }



    static void myToast(final Context context,final String txt){

        if(context instanceof Activity){
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();
                }
            });

        }

        System.out.println(txt);
    }

}
