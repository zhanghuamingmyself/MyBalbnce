package com.eto.upgrade.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.eto.upgrade.StaticCfg;
import com.eto.upgrade.UpdateApk;
import com.eto.upgrade.bean.UpdateBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by zhang on 2018/1/1.
 */

public class UpdateReceiver extends BroadcastReceiver {

    private static final String TAG = "UpdateReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String str = intent.getStringExtra("UpdateBean");
        StaticCfg.updatePackageName = intent.getStringExtra("updatePackageName");
        StaticCfg.updateActivityName = intent.getStringExtra("updateActivityName");
        Gson gson =  new GsonBuilder().create();
        UpdateBean updateBean = gson.fromJson(str, UpdateBean.class);
        Log.i(TAG,"get bean Receiver----"+str);
        Log.i(TAG,"get bean Receiver----"+updateBean.toString());
        if(updateBean.status!=0) {
            UpdateApk.DownloadAndUpdate(context, updateBean);
            //Toast.makeText(context, StaticCfg.updatePackageName + StaticCfg.updateActivityName + "received" + updateBean.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
