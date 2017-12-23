package com.zhanghuaming.mybalbnce;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.zhanghuaming.mybalbnce.serial.UartClient;
import com.zhanghuaming.mybalbnce.utils.MySharedPreferences;
import com.zhanghuaming.mybalbnce.utils.PhoneInfoUtils;
import com.zhanghuaming.mybalbnce.utils.SerialBack;

/**
 * Created by zhang on 2017/12/21.
 */

public class SettingActivity extends AppCompatActivity implements SerialBack {

    private static final String TAG = SettingActivity.class.getSimpleName();
    private Button btnOut, btnTime;
    private TimePicker timePickerDown, timePickerOpen;
    private  UartClient client;

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
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "" + timePickerDown.getCurrentHour());
                Log.i(TAG, "" + timePickerDown.getCurrentMinute());
                byte[] buf = new byte[16];
                buf[0] = (byte) 0XFF;
                buf[1] = (byte) 0X04;
                buf[2] = (byte) 0X00;
                buf[3] = (byte) 0X00;
                buf[4] = (byte) 0X00;
                buf[5] = (byte) 0X00;
                buf[6] = (byte) 0X00;
                buf[7] = (byte) 0XFE;
                buf[8] = (byte) 0XFF;
                buf[9] = (byte) 0X05;
                buf[10] = timePickerDown.getCurrentHour().byteValue();
                buf[11] = timePickerDown.getCurrentMinute().byteValue();
                buf[12] = timePickerOpen.getCurrentHour().byteValue();
                buf[13] = timePickerOpen.getCurrentMinute().byteValue();
                buf[14] = (byte) 0X00;
                buf[15] = (byte) 0XFE;
                client.sendMsg(buf);

            }
        });
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
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
        finish();
    }
}
