package com.zhanghuaming.mybalbnce.http;


import com.zhanghuaming.mybalbnce.StaticCfg;
import com.zhanghuaming.mybalbnce.bean.LoginBack;
import com.zhanghuaming.mybalbnce.bean.SendWeightBack;
import com.zhanghuaming.mybalbnce.bean.UpdateBean;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;


/**
 * Created by zhang on 2017/12/14.
 */

public interface RetrofixServiceInteface {

    @FormUrlEncoded
    @POST(StaticCfg.loginUrl)
    Observable<LoginBack> login(@Field("username") String username,
                                @Field("longitude") double longitude,
                                @Field("latitude") double latitude,
                                @Field("misi") String imsi);

    @FormUrlEncoded
    @POST(StaticCfg.sendWeightUrl)
    Observable<SendWeightBack> sendWeight(@Field("IotCardNumber") String number, @Field("weight") double weight);

    @FormUrlEncoded
    @POST(StaticCfg.getUpdateUrl)
    Observable<ResponseBody> getUpdate(@Field("IotCardNumber") String iotCardNumber,
                                     @Field("appName") String appName);

}
