package com.zhanghuaming.mybalbnce.utils;

import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.zhanghuaming.mybalbnce.StaticCfg;

/**
 * Created by zhang on 2017/12/21.
 */

public class MyLocationListener extends BDAbstractLocationListener {
    private static final String TAG = MyLocationListener.class.getSimpleName();
    public double latitude = 0;    //获取纬度信息
    public double longitude = 0;    //获取经度信息

    @Override
    public void onReceiveLocation(BDLocation location) {

        latitude = location.getLatitude();    //获取纬度信息
        longitude = location.getLongitude();    //获取经度信息
        float radius = location.getRadius();    //获取定位精度，默认值为0.0f

        Log.i(TAG, "经度：" + longitude);
        Log.i(TAG, "纬度：" + latitude);

        StaticCfg.latitude = latitude;
        StaticCfg.longitude = longitude;
        String coorType = location.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

        int errorCode = location.getLocType();
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
    }
}
