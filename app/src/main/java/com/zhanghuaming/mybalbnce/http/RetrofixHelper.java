package com.zhanghuaming.mybalbnce.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhanghuaming.mybalbnce.MyApplication;
import com.zhanghuaming.mybalbnce.bean.LoginBack;
import com.zhanghuaming.mybalbnce.bean.SendWeightBack;
import com.zhanghuaming.mybalbnce.bean.UpdateBean;
import com.zhanghuaming.mybalbnce.utils.MyLocationListener;




import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


/**
 * Created by zhang on 2017/12/14.
 */

public class RetrofixHelper {

    private static final String TAG = RetrofixHelper.class.getSimpleName();

    public static Observable<ResponseBody> getUpdate(String iotCardNumber, String appName) {
        return MyApplication.getApplication().checkRetrofix().getUpdate(iotCardNumber,appName).subscribeOn(Schedulers.io());
    }

    public static Observable<LoginBack> login(String username,double longitude,double latitude,String imsi) {
        Log.i(TAG,"登录信息"+username+"---"+longitude+"---"+latitude+"---"+imsi);
        return MyApplication.getApplication().checkRetrofix().login(username,longitude,latitude,imsi).subscribeOn(Schedulers.io());
    }

    public static Observable<SendWeightBack> sendWeight(String number, double weight,double fat) {
        return MyApplication.getApplication().checkRetrofix().sendWeight(number, weight,fat).subscribeOn(Schedulers.io());
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
