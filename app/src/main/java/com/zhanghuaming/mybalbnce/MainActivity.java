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
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zhanghuaming.mybalbnce.bean.LoginBack;
import com.zhanghuaming.mybalbnce.bean.SendWeightBack;
import com.zhanghuaming.mybalbnce.http.RetrofixHelper;
import com.zhanghuaming.mybalbnce.serial.UartClient;
import com.zhanghuaming.mybalbnce.utils.BannerItem;
import com.zhanghuaming.mybalbnce.utils.FrameAnimation;
import com.zhanghuaming.mybalbnce.utils.LocalHelper;
import com.zhanghuaming.mybalbnce.utils.MySharedPreferences;
import com.zhanghuaming.mybalbnce.utils.NetworkImageHolderView;
import com.zhanghuaming.mybalbnce.utils.PhoneInfoUtils;
import com.zhanghuaming.mybalbnce.utils.SerialBack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;


public class MainActivity extends Activity implements SerialBack {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView ivAnima;
    private SimpleDraweeView ivQRCode;
    private TextView tvAutoCode, tvTip1, tvTip2, tvCode, tvTime;
    private FrameAnimation frameAnimation;
    private boolean isLoging = false;
    private UartClient client;

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
        client = UartClient.getInstance(this);
        if (MySharedPreferences.get(MainActivity.this, MySharedPreferences.DevCode) != null) {
            Observable.timer(1, TimeUnit.SECONDS).map(new Func1<Long, Object>() {
                @Override
                public Object call(Long aLong) {
                    login();
                    return null;
                }
            }).subscribe();
        } else {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        havePeople();
        tvTime.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendWeight(182.16);
                tvTime.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backBegin();
                    }
                }, 6000 * 2);
            }
        }, 6000 * 2);
    }

    void login() {
        PhoneInfoUtils phoneInfoUtils = new PhoneInfoUtils(MainActivity.this);
        Observable<LoginBack> back = RetrofixHelper.login(MySharedPreferences.get(MainActivity.this, MySharedPreferences.DevCode), StaticCfg.longitude, StaticCfg.latitude, phoneInfoUtils.getMISI());
        back.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<LoginBack>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
                try {
                    String lbString = MySharedPreferences.get(MainActivity.this, MySharedPreferences.LOGINBACK);
                    if (lbString != null) {
                        LoginBack lb = MyApplication.getApplication().getGson().fromJson(lbString, LoginBack.class);
                        initShow(lb);
                    } else {
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }

            }

            @Override
            public void onNext(LoginBack loginBack) {
                Log.i(TAG, "json---" + loginBack.toString());
                if (loginBack.getStatus() == 1) {
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
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }
        });
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                frameAnimation = new FrameAnimation(ivAnima, getRes(), 500, true);
                // SoundUtils soundUtils = SoundUtils.getInstance();
                //soundUtils.playSound();//播放声音
                tvTip1.setText("正在测量中");
                tvTip2.setText("请保持平衡");
            }
        });
    }


    void sendWeight(double weight) {
        Observable<SendWeightBack> back = RetrofixHelper.sendWeight(MySharedPreferences.get(MainActivity.this, MySharedPreferences.DevCode), weight);
        back.doOnNext(new Action1<SendWeightBack>() {
            @Override
            public void call(SendWeightBack responseBody) {

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
                e.printStackTrace();
            }

            @Override
            public void onNext(SendWeightBack responseBody) {
                Log.i(TAG, "体重返回" + responseBody.toString());
                sendWeightBack(responseBody.msg);
            }
        });
    }

    void sendWeightBack(String autoCode) {//发送体重信息到服务器后返回
        // SoundUtils soundUtils = SoundUtils.getInstance();
        //soundUtils.playSound();//播放声音
        if (!frameAnimation.isPause()) {
            frameAnimation.pauseAnimation();
            frameAnimation.release();
            frameAnimation = null;
        }
        ivAnima.setVisibility(View.INVISIBLE);
        tvAutoCode.setVisibility(View.VISIBLE);
        tvAutoCode.setText(autoCode + "(验证码)");
        tvTip1.setText("输入验证码");
        tvTip2.setText("免费查看体重");
    }

    void backBegin() {//恢复没人界面
        tvTip2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (frameAnimation!= null &&!frameAnimation.isPause()) {
                    frameAnimation.pauseAnimation();
                    frameAnimation.release();
                    frameAnimation = null;
                }
                ivAnima.setVisibility(View.VISIBLE);
                tvAutoCode.setVisibility(View.INVISIBLE);
                tvTip1.setText("微信扫描关注");
                tvTip2.setText("体重数据自己知");
            }
        }, 3000);
    }

    void initView() {
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);
        ivAnima = (ImageView) findViewById(R.id.iv_anima);
        ivQRCode = (SimpleDraweeView) findViewById(R.id.iv_qrcode);
        tvAutoCode = (TextView) findViewById(R.id.tv_authcode);
        tvTip1 = (TextView) findViewById(R.id.tv_tip1);
        tvTip2 = (TextView) findViewById(R.id.tv_tip2);
        tvCode = (TextView) findViewById(R.id.tv_code);
        tvTime = (TextView) findViewById(R.id.tv_time);
        ivQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private ConvenientBanner convenientBanner;
    private List<BannerItem> bannerItems = new ArrayList<>();
    private List<String> webPathList;//每张图片的绝对地址

    void addWebPicture(List<String> webPictureAddr) {//添加轮播图
        webPathList = webPictureAddr;
        for (int i = 0; i < webPathList.size(); i++) {
            bannerItems.add(new BannerItem("", webPathList.get(i)));
        }

        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, bannerItems)
                .startTurning(3000)     //设置自动切换（同时设置了切换时间间隔）
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

    void beginTimeShow() {
        Observable.interval(0, 1, TimeUnit.MINUTES)
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
                    }
                });
    }


    @Override
    public void sHavePeople() {
        havePeople();
    }

    @Override
    public void sHaveweight(double weight) {
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
        sendWeight(weight);
    }

    @Override
    public void sNoPeople() {
        backBegin();
    }

    @Override
    public void sAfterSettingCloseTime() {

    }
}