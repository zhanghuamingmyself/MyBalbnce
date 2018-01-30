package com.zhanghuaming.mybalbnce;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zhanghuaming.mybalbnce.bean.LoginBack;
import com.zhanghuaming.mybalbnce.bean.SendWeightBack;
import com.zhanghuaming.mybalbnce.bean.UpdateBean;
import com.zhanghuaming.mybalbnce.http.RetrofixHelper;
import com.zhanghuaming.mybalbnce.serial.UartClient;
import com.zhanghuaming.mybalbnce.serial.UartClientNew;
import com.zhanghuaming.mybalbnce.utils.BannerItem;
import com.zhanghuaming.mybalbnce.utils.CommonUtils;
import com.zhanghuaming.mybalbnce.utils.FrameAnimation;
import com.zhanghuaming.mybalbnce.utils.LocalHelper;
import com.zhanghuaming.mybalbnce.utils.MySharedPreferences;
import com.zhanghuaming.mybalbnce.utils.NetWorkBack;
import com.zhanghuaming.mybalbnce.utils.NetworkImageHolderView;
import com.zhanghuaming.mybalbnce.utils.PhoneInfoUtils;
import com.zhanghuaming.mybalbnce.serial.SerialBack;
import com.zhanghuaming.mybalbnce.utils.SoundUtils;
import com.zhanghuaming.mybalbnce.utils.UpdateApk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView ivAnima;
    private LinearLayout tv_yzm;
    private SimpleDraweeView ivQRCode;
    private TextView tvAutoCode, tvTip1, tvTip2, tvCode, tvTime, tvNet;
    private FrameAnimation frameAnimation;//动态
    private boolean isLoging = false;//是否登录
    private UartClientNew client;//串口
    private String doweTime;
    private int stateNow = 0;
    private int lowWight = 2;
    private boolean havePeople = false;//当前是否有人在上面，只是解决现实二维码后紧接着使用的界面显示问题
    private double weightNow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.activity_main);
        initView();
        checkUpdate();
        PhoneInfoUtils phoneInfoUtils = new PhoneInfoUtils(MainActivity.this);
        if (phoneInfoUtils.getIccid() != null) {
            MySharedPreferences.save(MainActivity.this, MySharedPreferences.DevCode, phoneInfoUtils.getICCID().substring(0, phoneInfoUtils.getICCID().length() - 1));
        }
        if (RetrofixHelper.isNetworkConnected(MainActivity.this)) {
            login();
            tvNet.setText("(当前已联网)");
        }
        NetReceiver.setBack(new NetWorkBack() {
            @Override
            public void noNetwork() {
                tvNet.setText("(当前无网络)");
            }

            @Override
            public void wifiConnected() {
                tvNet.setText("(当前为wifi联网)");
                login();
            }

            @Override
            public void mobileConnected() {
                tvNet.setText("(当前为移动联网)");
                login();
            }
        });
        try {
            String lbString = MySharedPreferences.get(MainActivity.this, MySharedPreferences.LOGINBACK);
            if (lbString != null) {
                LoginBack lb = MyApplication.getApplication().getGson().fromJson(lbString, LoginBack.class);
                initShow(lb);
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        client = UartClientNew.getInstance(new SerialBack() {
            @Override
            public void sInstabilityPeople(double result) {
                weightNow = result;
                if (stateNow == 0 && result > lowWight) {
                    havePeople = true;
                    stateNow = SerialBack.sIS_HAVING_PEOPLE;
                    havePeople();
                } else if (stateNow == SerialBack.sIS_SHOWING_AUTOCODE && result == 0 && havePeople) {
                    havePeople = false;
                    Log.i(TAG, "stateNow---222---" + stateNow);
                    tvTip2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            backBegin(0);
                        }
                    }, 6000);

                } else if (stateNow == SerialBack.sIS_SHOWING_AUTOCODE && result > 0 && !havePeople) {
                    havePeople = true;
                    havePeople();
                } else if (stateNow == SerialBack.sIS_BODY_ERROR && result > 0 && !havePeople) {
                    havePeople = true;
                    havePeople();
                } else if (result == 0 && stateNow != SerialBack.sIS_SHOWING_AUTOCODE && stateNow != SerialBack.sIS_BODY_ERROR) {
                    Log.i(TAG, "no people no wait" + stateNow);
                    havePeople = false;
                    SoundUtils.getInstance().stopSound();
                    backBegin(0);
                }
            }


            @Override
            public void sNoPeople() {

            }

            @Override
            public void sAfterSettingCloseTime() {

            }

            @Override
            public void sInSetingCloseTime() {

            }


            @Override
            public void sHaveweight(final double weight) {
                //           if (stateNow == SerialBack.sIS_HAVING_PEOPLE) {
                //播放
                SoundUtils soundUtils = SoundUtils.getInstance();
                soundUtils.stopSound();
                soundUtils.playbeginFatSound();//播放声音
                stateNow = SerialBack.sIS_HAVING_WEIGHT;
                Log.i(TAG, "stateNow------------------" + stateNow);
                nowWeight = weight;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTip1.setText("体重测量完成");
                        tvTip2.setText("握扶手测脂肪");
                    }
                });
                //    sendWeightFat(nowWeight, 780.0);//测试
            }

            //        }


            @Override
            public void sBeginBodyfat(double fat) {
                if (stateNow == SerialBack.sIS_HAVING_WEIGHT) {
                    stateNow = SerialBack.sIS_BEGIN_BODY_FAT;
                    Log.i(TAG, "stateNow--------------------------------------" + stateNow);
                }
            }

            double nowWeight = 0;

            @Override
            public void sHaveBodyfat(final double fat) {
                //               if (stateNow == SerialBack.sIS_HAVING_WEIGHT || stateNow == SerialBack.sIS_BEGIN_BODY_FAT) {
                stateNow = SerialBack.sIS_BODY_FAT;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "阻抗stateNow-------------------" + fat + "---------" + stateNow, Toast.LENGTH_SHORT).show();
//                    }
//                });
                sendWeightFat(nowWeight, fat);
//                } else {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this, "阻抗stateNow---" + fat + "---------" + stateNow, Toast.LENGTH_SHORT).show();
//                        }
//                    });
            }

            //        }

            @Override
            public void sBodyfatError() {
                stateNow = SerialBack.sIS_BODY_ERROR;
                //播放
                SoundUtils soundUtils = SoundUtils.getInstance();
                soundUtils.stopSound();
                soundUtils.playFatSound();//播放声音
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (frameAnimation != null && !frameAnimation.isPause()) {
                            frameAnimation.pauseAnimation();
                            frameAnimation.release();
                        }
                        ivAnima.setVisibility(View.VISIBLE);
                        tv_yzm.setVisibility(View.INVISIBLE);
                        tvTip1.setText("体脂测试失败");
                        tvTip2.setText("请重新测试");
                        tvTime.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                backBegin(0);
                            }
                        }, 6000);

                    }
                });
            }
        });
        client.start();//打开串口
        frameAnimation = new FrameAnimation(ivAnima, getRes(), 500, true);
        if (frameAnimation != null && !frameAnimation.isPause()) {
            frameAnimation.pauseAnimation();
            frameAnimation.release();
        }
    }

    void login() {
        if (MySharedPreferences.get(MainActivity.this, MySharedPreferences.DevCode) != null && MySharedPreferences.get(MainActivity.this, MySharedPreferences.DevCode) != "null") {
            PhoneInfoUtils phoneInfoUtils = new PhoneInfoUtils(MainActivity.this);
            Log.e(TAG, "手机信息" + "----" + phoneInfoUtils.getProvidersName() + "----" + phoneInfoUtils.getPhoneInfo() + "-----" + phoneInfoUtils.getIccid());
            Observable<LoginBack> back = RetrofixHelper.login(MySharedPreferences.get(MainActivity.this, MySharedPreferences.DevCode), StaticCfg.longitude, StaticCfg.latitude, phoneInfoUtils.getIMSI());
            back.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<LoginBack>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, e.getMessage());
                }

                @Override
                public void onNext(LoginBack loginBack) {
                    Log.i(TAG, "json---" + loginBack.toString());
                    if (loginBack.getStatus() == 0) {
                        isLoging = true;
                        MySharedPreferences.save(MainActivity.this, MySharedPreferences.LOGINBACK, MyApplication.getApplication().getGson().toJson(loginBack));
                        if (LocalHelper.mLocationClient.isStarted()) {
                            LocalHelper.mLocationClient.stop();
                        }
                        initShow(loginBack);
                    } else {
                        try {
                            String lbString = MySharedPreferences.get(MainActivity.this, MySharedPreferences.LOGINBACK);
                            if (lbString != null) {
                                LoginBack lb = MyApplication.getApplication().getGson().fromJson(lbString, LoginBack.class);
                                initShow(lb);
                            } else {
                                Intent i = new Intent(MainActivity.this, SettingActivity.class);
                                startActivity(i);
                                finish();
                            }
                        } catch (Exception ee) {
                            ee.printStackTrace();
                            Intent i = new Intent(MainActivity.this, SettingActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }
            });

        } else {
            Toast.makeText(MainActivity.this, "没有登录信息", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(i);
            finish();
        }
        checkUpdateService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doweTime = MySharedPreferences.get(MainActivity.this, MySharedPreferences.DOWNTIME);

    }


    void checkUpdateService() {
        try {
            boolean isRunning = CommonUtils.isServiceRunning(MainActivity.this, "com.eto.upgrade.UpdateService");
            if (!isRunning) {
                Intent in = new Intent();
                in.setClassName("com.eto.upgrade", "com.eto.upgrade.UpdateService");
                startService(in);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "MSG_CHECK_UPDATESERVICE start fail");
        }
    }


    void initShow(LoginBack loginBack) {
        addWebPicture(loginBack.getMsg().getSlideshow());
        tvCode.setText("设备号:" + loginBack.getMsg().getNumber());
        if (loginBack.getMsg().getQrCode() != null && !loginBack.getMsg().getQrCode().equals("null")) {
            Uri uri = Uri.parse(loginBack.getMsg().getQrCode());
            ivQRCode.setImageURI(uri);
        }
        beginTimeShow();//开始显示时间
    }

    void havePeople() {
        stateNow = SerialBack.sIS_HAVING_PEOPLE;
        Log.i(TAG, "stateNow-" + stateNow);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_yzm.setVisibility(View.INVISIBLE);
                ivAnima.setVisibility(View.VISIBLE);
                if (frameAnimation != null) {
                    frameAnimation.restartAnimation();
                } else {
                    frameAnimation = new FrameAnimation(ivAnima, getRes(), 500, true);
                }
                SoundUtils soundUtils = SoundUtils.getInstance();
                soundUtils.stopSound();
                soundUtils.playFocusSound();//播放声音

                tvTip1.setText("体重测量中");
                tvTip2.setText("请保持平衡");
            }
        });
    }


    void sendWeightFat(final double weight, final double fat) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(MainActivity.this, "正在上传" + weight + "体重" + fat, Toast.LENGTH_SHORT).show();
//            }
//        });
        byte[] buf = new byte[8];
        buf[0] = (byte) 0XFF;
        buf[1] = (byte) 0X03;
        buf[2] = (byte) 0X00;
        buf[3] = (byte) 0X00;
        buf[4] = (byte) 0X00;
        buf[5] = (byte) 0X00;
        buf[6] = (byte) 0X00;
        buf[7] = (byte) 0XFE;
        client.sendMsg(buf);
        Observable<SendWeightBack> back = RetrofixHelper.sendWeight(MySharedPreferences.get(MainActivity.this, MySharedPreferences.DevCode), weight, fat);
        back.doOnNext(new Action1<SendWeightBack>() {
            @Override
            public void call(SendWeightBack responseBody) {
                SoundUtils soundUtils = SoundUtils.getInstance();
                soundUtils.stopSound();
                soundUtils.playInputSound();//播放声音
                byte[] buf = new byte[8];
                buf[0] = (byte) 0XFF;
                buf[1] = (byte) 0X01;
                buf[2] = (byte) 0X00;
                buf[3] = (byte) 0X00;
                buf[4] = (byte) 0X00;
                buf[5] = (byte) 0X00;
                buf[6] = (byte) 0X00;
                buf[7] = (byte) 0XFE;
                client.sendMsg(buf);

            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<SendWeightBack>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "上传体重错误--" + e.getMessage(), Toast.LENGTH_LONG).show();
                SoundUtils soundUtils = SoundUtils.getInstance();
                soundUtils.stopSound();
                soundUtils.playerrorSound();
            }

            @Override
            public void onNext(SendWeightBack responseBody) {
                Log.i(TAG, "服务器返回" + responseBody.toString());
                if (responseBody.status == 0) {
                    sendWeightFatBack(responseBody.msg);
                } else {
                    Toast.makeText(MainActivity.this, "服务器返回" + responseBody.status, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void sendWeightFatBack(String autoCode) {//发送体重信息到服务器后返回
        stateNow = SerialBack.sIS_SHOWING_AUTOCODE;
        Log.i(TAG, "stateNow-===-" + stateNow);
        if (frameAnimation != null && !frameAnimation.isPause()) {
            frameAnimation.pauseAnimation();
            frameAnimation.release();
        }
        ivAnima.setVisibility(View.INVISIBLE);
        tv_yzm.setVisibility(View.VISIBLE);
        String autoCodeString = "";
        for(int i =0;i<autoCode.length();i++){
            if(i==autoCode.length()-1) {
                autoCodeString += autoCode.charAt(i);
            }else {
                autoCodeString += autoCode.charAt(i) + " ";
            }
        }
        tvAutoCode.setText(autoCodeString);
        tvTip1.setText("输入验证码");
        tvTip2.setText("接收体脂信息");
    }

    void backBegin(int time) {//恢复没人界面
        stateNow = 0;
        Log.i(TAG, "stateNow-++++-" + stateNow);
        if (weightNow == 0) {
            tvTip2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (frameAnimation != null && !frameAnimation.isPause()) {
                        frameAnimation.pauseAnimation();
                        frameAnimation.release();
                    }
                    ivAnima.setVisibility(View.VISIBLE);
                    tv_yzm.setVisibility(View.INVISIBLE);
                    tvTip1.setText("微信扫码关注");
                    tvTip2.setText("免费测试体脂");
                }
            }, time);
        }

    }

    void initView() {
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);
        ivAnima = (ImageView) findViewById(R.id.iv_anima);
        tv_yzm = (LinearLayout) findViewById(R.id.tv_yzm);
        ivAnima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                havePeople();
//                tvTime.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        sendWeightFat(82.16, 12.1);
//                        tvTime.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                backBegin(6000);
//                            }
//                        }, 6000 * 2);
//                    }
//                }, 6000 * 2);
            }
        });
        ivQRCode = (SimpleDraweeView) findViewById(R.id.iv_qrcode);
        tvAutoCode = (TextView) findViewById(R.id.tv_authcode);
        tvTip1 = (TextView) findViewById(R.id.tv_tip1);
        tvTip2 = (TextView) findViewById(R.id.tv_tip2);
        tvCode = (TextView) findViewById(R.id.tv_code);
        tvTime = (TextView) findViewById(R.id.tv_time);
        ivQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        tvNet = (TextView) findViewById(R.id.tv_net);
    }

    void checkUpdate() {
        Observable<ResponseBody> back = RetrofixHelper.getUpdate(MySharedPreferences.get(MainActivity.this, MySharedPreferences.DevCode), UpdateApk.getAppInfo_BySelf(MainActivity.this).appName);
        back.subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = responseBody.string();
                    Log.i(TAG, "responBody for update ----" + s);
                    if (MyApplication.getApplication().getGson().fromJson(s, UpdateBean.class).status == 0) {
                        Update.sendMsg(MainActivity.this, s);
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ConvenientBanner convenientBanner;
    private List<BannerItem> bannerItems;
    private List<String> webPathList;//每张图片的绝对地址

    void addWebPicture(List<String> webPictureAddr) {//添加轮播图
        webPathList = webPictureAddr;
        bannerItems = null;
        bannerItems = new ArrayList<>();
        for (int i = 0; i < webPathList.size(); i++) {
            bannerItems.add(new BannerItem("", webPathList.get(i)));
        }

        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, bannerItems)
                .startTurning(6000)     //设置自动切换（同时设置了切换时间间隔）
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL) //设置指示器位置（左、中、右）
                .setPageIndicator(new int[]{R.drawable.dot_unselected, R.drawable.dot_selected})
                //  .setManualPageable(true);  //设置手动影响（设置了该项无法手动切换）
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent i = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(i);
                    }
                }); //设置点击监听事件

    }

    private int[] getRes() {//获取动画资源
        TypedArray typedArray = getResources().obtainTypedArray(R.array.ani);
        int len = typedArray.length();
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }


    private int localState = 0;//保存状态
    private int tito = 0;
    void beginTimeShow() {
        Observable.interval(0, 1, TimeUnit.SECONDS)//状态复位
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
//                        if (aLong % 40 == 0) {
//                            if (localState == stateNow&&stateNow !=0) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(MainActivity.this,"stateNow"+stateNow,Toast.LENGTH_SHORT).show();
//                                        havePeople = false;
//                                        backBegin(0);
//                                    }
//                                });
//                            }
//                            localState =stateNow;
//                        }
                        if (localState == stateNow&&stateNow !=0) {
                            if(tito <40){
                                tito++;
                            }else {
                                tito=0;
                                havePeople = false;
                                backBegin(0);
                            }
                        }else if(localState != stateNow&&stateNow !=0){
                            localState = stateNow;
                            tito = 0;
                        }else {
                            tito = 0;
                        }
                    }
                });
        Observable.interval(0, 1, TimeUnit.MINUTES)//时间显示，关机，重新登录
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (!isLoging && aLong % 5 == 0) {
                            login();
                        }
                        Date dNow = new Date();
                        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd ' ' HH:mm");
                        tvTime.setText("" + ft.format(dNow));
//                        SimpleDateFormat ftd = new SimpleDateFormat("HH:mm");
//                        if (ftd.format(dNow).equals(doweTime)) {
//                            Log.i(TAG, "匹配");
//                            byte[] buf = new byte[8];
//                            buf[0] = (byte) 0XFF;
//                            buf[1] = (byte) 0X08;
//                            buf[2] = (byte) 0X00;
//                            buf[3] = (byte) 0X00;
//                            buf[4] = (byte) 0X00;
//                            buf[5] = (byte) 0X00;
//                            buf[6] = (byte) 0X00;
//                            buf[7] = (byte) 0XFE;
//                            client.sendMsg(buf);
//                        } else {
//                            Log.i(TAG, "关机时间对比" + ftd.format(dNow) + "------" + doweTime);
//                        }
                    }
                });
    }


}
