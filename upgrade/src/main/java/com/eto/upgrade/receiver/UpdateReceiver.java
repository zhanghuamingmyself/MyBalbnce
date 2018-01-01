package com.eto.upgrade.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.eto.upgrade.StaticCfg;
import com.eto.upgrade.UpdateApk;
import com.eto.upgrade.bean.UpdateBean;
import com.google.gson.Gson;

/**
 * Created by zhang on 2018/1/1.
 */

public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String str = intent.getStringExtra("UpdateBean");
        StaticCfg.updatePackageName = intent.getStringExtra("updatePackageName");
        StaticCfg.updateActivityName = intent.getStringExtra("updateActivityName");
        Gson gson = new Gson();
        UpdateBean updateBean = gson.fromJson(str, UpdateBean.class);
        UpdateApk.DownloadAndUpdate(context, updateBean);
        Toast.makeText(context, StaticCfg.updatePackageName + StaticCfg.updateActivityName + "received" + updateBean.toString(), Toast.LENGTH_SHORT).show();
    }
}
