package com.zhanghuaming.mybalbnce.http;

import android.content.Context;
import android.util.Log;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhanghuaming.mybalbnce.MyApplication;
import com.zhanghuaming.mybalbnce.bean.LoginBack;
import com.zhanghuaming.mybalbnce.bean.SendWeightBack;
import com.zhanghuaming.mybalbnce.utils.MyLocationListener;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;


import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


/**
 * Created by zhang on 2017/12/14.
 */

public class RetrofixHelper {

    private static final String TAG = RetrofixHelper.class.getSimpleName();




    public static Observable<LoginBack> login(String username,double longitude,double latitude,String misi) {
        Log.i(TAG,"登录信息"+username+"---"+longitude+"---"+latitude+"---"+misi);
        return MyApplication.getApplication().checkRetrofix().login(username,longitude,latitude,misi).subscribeOn(Schedulers.io());
    }

    public static Observable<SendWeightBack> sendWeight(String number, double weight) {
        return MyApplication.getApplication().checkRetrofix().sendWeight(number, weight).subscribeOn(Schedulers.io());
    }

}