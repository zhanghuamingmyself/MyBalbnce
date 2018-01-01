package com.eto.upgrade.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;


import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;

/**
 * Created by Administrator on 2016/10/17.
 */
public class InstallApk {
    private static final String TAG = InstallApk.class.getSimpleName();
    private static String cmd_install = "pm install -r ";
    private static String cmd_uninstall = "pm uninstall ";

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

    public static void begin_install(String c) {
        String cmd = cmd_install + c;
        if (new File(cmd).exists()) {
            Log.e(TAG, "file is existe");
        }
        Log.e(TAG, "begin_install" + cmd);
        excuteSuCMD(cmd);

    }

    public static void begin_uninstall() {
        String cmd = cmd_uninstall + "com.kingsoft.website";
        Log.e(TAG, "begin_uninstall" + cmd);
        excuteSuCMD(cmd);
    }


    public static  int excuteSuCMD(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(
                    (OutputStream) process.getOutputStream());
            dos.writeBytes((String) "export LD_LIBRARY_PATH=/vendor/lib:/system/lib\n");
            cmd = String.valueOf(cmd);
            dos.writeBytes((String) (cmd + "\n"));
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            process.waitFor();
            int result = process.exitValue();
            return (Integer) result;
        } catch (Exception localException) {
            localException.printStackTrace();
            return -1;

        }
    }

}
