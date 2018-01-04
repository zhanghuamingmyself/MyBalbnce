package com.zhanghuaming.mybalbnce;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.zhanghuaming.mybalbnce.bean.MyAppInfo;
import com.zhanghuaming.mybalbnce.serial.UartClient;
import com.zhanghuaming.mybalbnce.utils.MySharedPreferences;
import com.zhanghuaming.mybalbnce.serial.SerialBack;
import com.zhanghuaming.mybalbnce.utils.PhoneInfoUtils;
import com.zhanghuaming.mybalbnce.utils.UpdateApk;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zhang on 2017/12/21.
 */

public class SettingActivity extends AppCompatActivity implements SerialBack {

    private static final String TAG = SettingActivity.class.getSimpleName();
    private Button btnOut, btnTime, btnTest, btnUpdate;
    private TimePicker timePickerDown, timePickerOpen;
    private UartClient client;
    private Subscriber subscriber;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        btnOut = (Button) findViewById(R.id.btn_out);
        btnTime = (Button) findViewById(R.id.btn_time);
        timePickerDown = (TimePicker) findViewById(R.id.timePickerDown);
        timePickerOpen = (TimePicker) findViewById(R.id.timePickerOpen);
        timePickerDown.setIs24HourView(true);
        timePickerOpen.setIs24HourView(true);
        client = UartClient.getInstance(this);
        String d = MySharedPreferences.get(SettingActivity.this, MySharedPreferences.DOWNTIME);
        if (d != null && d.indexOf(":") != -1) {
            String[] t = d.split(":");
            timePickerDown.setCurrentHour(Integer.parseInt(t[0]));
            timePickerDown.setCurrentMinute(Integer.parseInt(t[1]));
        }
        subscriber = new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                if (aLong == 30) {
                    Toast.makeText(SettingActivity.this, "设置关机时间失败", Toast.LENGTH_LONG).show();
                }
            }
        };
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Observable.interval(0, 1, TimeUnit.SECONDS)//
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber);

                byte[] buf = new byte[8];
                buf[0] = (byte) 0XFF;
                buf[1] = (byte) 0X04;
                buf[2] = (byte) 0X00;
                buf[3] = (byte) 0X00;
                buf[4] = (byte) 0X00;
                buf[5] = (byte) 0X00;
                buf[6] = (byte) 0X00;
                buf[7] = (byte) 0XFE;
                client.sendMsg(buf);

            }
        });
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        btnTest = (Button) findViewById(R.id.btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstUpgradeApk();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }
    }

    @Override
    public void sHavePeople() {

    }

    @Override
    public void sHaveweight(double weight) {

    }

    @Override
    public void sNoPeople() {

    }

    @Override
    public void sAfterSettingCloseTime() {
        byte[] buf = new byte[8];
        buf[0] = (byte) 0XFF;
        buf[1] = (byte) 0X07;
        buf[2] = (byte) 0X00;
        buf[3] = (byte) 0X00;
        buf[4] = (byte) 0X00;
        buf[5] = (byte) 0X00;
        buf[6] = (byte) 0X00;
        buf[7] = (byte) 0XFE;
        client.sendMsg(buf);
        Log.i(TAG, "" + timePickerDown.getCurrentHour());
        Log.i(TAG, "" + timePickerDown.getCurrentMinute());
        MySharedPreferences.save(SettingActivity.this, MySharedPreferences.DOWNTIME, "" + timePickerDown.getCurrentHour() + ":" + timePickerDown.getCurrentMinute());
        finish();
    }

    @Override
    public void sInSetingCloseTime() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm");
        String[] t = ft.format(dNow).split(":");
        byte[] buf = new byte[8];
        buf[0] = (byte) 0XFF;
        buf[1] = (byte) 0X05;
        buf[2] = (byte) Integer.parseInt(t[0]);
        buf[3] = (byte) Integer.parseInt(t[1]);
        buf[4] = timePickerOpen.getCurrentHour().byteValue();
        buf[5] = timePickerOpen.getCurrentMinute().byteValue();
        buf[6] = (byte) 0X00;
        buf[7] = (byte) 0XFE;
        client.sendMsg(buf);
    }

    void firstUpgradeApk() {
        int apk_res_id = R.raw.upgrade_apk;
        MyAppInfo info = UpdateApk.getAppInfo_ByPackageName(SettingActivity.this, "com.eto.upgrade");
        if (info == null) {
            install(apk_res_id);
        }

    }

    void install(int apk_res_id) {
        String path = UpdateApk.getRawFile(SettingActivity.this, apk_res_id);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
