package com.zhanghuaming.mybalbnce;

import android.content.Context;
import android.content.Intent;

import com.zhanghuaming.mybalbnce.bean.UpdateBean;

/**
 * Created by zhang on 2018/1/1.
 */

public class Update {
    static void sendMsg(Context context,String beanJson) {
        String updatePackageName = "com.zhanghuaming.mybalbnce";
        String updateActivityName = "MainActivity";
        Intent intent = new Intent(
                "com.zhanghuaming.upgrade.Update");
        intent.putExtra("UpdateBean", "{\"rstId\":1,\"object\":{\"appName\":\"app-debug.apk\",\"downloadUrl\":\"http://192.168.31.110:8080/gmsystem/updatefile/findbypath.do?path\\u003dapp-debug.apk\",\"md5\":\"05DDDE356DDEA1408B24BAD13E2CA812\",\"fileSize\":13439686}}");
        intent.putExtra("updatePackageName", updatePackageName);
        intent.putExtra("updateActivityName", updateActivityName);
        context.sendBroadcast(intent);
    }
}