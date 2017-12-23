package com.zhanghuaming.mybalbnce.utils;

import android.content.Context;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by zhang on 2017/12/21.
 */

public class LocalHelper {
    public static LocationClient mLocationClient = null;
    public static MyLocationListener myListener = new MyLocationListener();

    public static void initGPS(Context context) {
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
     //   option.setScanSpan(5000);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }
}
