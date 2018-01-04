package com.zhanghuaming.mybalbnce;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.util.Log;

import com.zhanghuaming.mybalbnce.utils.NetWorkBack;

/**
 * Created by zhang on 2018/1/4.
 */

public class NetReceiver extends BroadcastReceiver {

    private static final String TAG = "NetReceiver";
    private static NetWorkBack mNetWorkBack;

    public static void setBack(final NetWorkBack netWorkBack){
        mNetWorkBack = netWorkBack;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        State wifiState = null;
        State mobileState = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (wifiState != null && mobileState != null
                && State.CONNECTED != wifiState
                && State.CONNECTED == mobileState) {
            Log.i(TAG, "手机网络连接成功");
            if(mNetWorkBack !=null){
                mNetWorkBack.mobileConnected();
            }
        } else if (wifiState != null && mobileState != null
                && State.CONNECTED != wifiState
                && State.CONNECTED != mobileState) {
            Log.i(TAG, "手机没有任何的网络");
            if(mNetWorkBack !=null){
                mNetWorkBack.noNetwork();
            }
        } else if (wifiState != null && State.CONNECTED == wifiState) {
            Log.i(TAG, "无线网络连接成功");
            if(mNetWorkBack !=null){
                mNetWorkBack.wifiConnected();
            }
        }

    }

}
