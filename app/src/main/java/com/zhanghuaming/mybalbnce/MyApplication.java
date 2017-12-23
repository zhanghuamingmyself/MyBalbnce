package com.zhanghuaming.mybalbnce;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhanghuaming.mybalbnce.http.RetrofixHelper;
import com.zhanghuaming.mybalbnce.http.RetrofixServiceInteface;
import com.zhanghuaming.mybalbnce.serial.UartClient;
import com.zhanghuaming.mybalbnce.utils.LocalHelper;

import org.apache.http.conn.ssl.SSLSocketFactory;


import java.security.KeyManagementException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.zhanghuaming.mybalbnce.StaticCfg.baseUrl;

/**
 * Created by zhang on 2017/12/18.
 */

public class MyApplication extends Application {
    private static MyApplication context;
    public OkHttpClient client;
    private Retrofit retrofit;
    private Gson gson;
    private RetrofixServiceInteface serviceInteface;

    public Gson getGson() {
        return gson;
    }

    public static MyApplication getApplication() {
        if (context != null) {
            return context;
        } else {
            return null;
        }
    }

    public RetrofixServiceInteface checkRetrofix() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd hh:mm:ss")
                    .create();
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(MyApplication.getApplication().client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        if (serviceInteface == null) {
            serviceInteface = retrofit.create(RetrofixServiceInteface.class);
        }
        return serviceInteface;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocalHelper.initGPS(getApplicationContext());
        Fresco.initialize(this);
        context = this;
        client = new OkHttpClient.Builder().build();
         UartClient client = UartClient.getInstance(null);//告诉底板收到认证码
        client.start();//打开串口
    }
}
