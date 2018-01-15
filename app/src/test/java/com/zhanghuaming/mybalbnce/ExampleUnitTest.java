package com.zhanghuaming.mybalbnce;

import android.util.Log;
import android.widget.Toast;

import com.zhanghuaming.mybalbnce.serial.UartClient;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        System.out.println(year + "/" + month + "/" + date + " " + hour + ":" + minute + ":" + second);
    }

    @Test
    public void byteTest() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm");
        String[] t = ft.format(dNow).split(":");
        byte[] buf = new byte[8];
        buf[0] = (byte) 0XFF;
        buf[1] = (byte) 0X05;
        buf[2] = (byte) Integer.parseInt(t[0]);
        buf[3] = (byte) Integer.parseInt(t[1]);
        buf[4] = 0X00;
        buf[5] = 0X00;
        buf[6] = (byte) 0X00;
        buf[7] = (byte) 0XFE;
        System.out.print(buf);
    }

    @Test
    public void testByte() {
        byte[] buf = new byte[8];
        buf[0] = (byte) 0XAC;
        buf[1] = (byte) 0X02;
        buf[2] = (byte) 0X00;
        buf[3] = (byte) 0X00;
        buf[4] = (byte) 0X00;
        buf[5] = (byte) 0X00;
        buf[6] = (byte) 0XCE;
        buf[7] = (byte) 0XCE;

        int sum =0;
        for(int i=2;i<7;i++){
            sum+=buf[i];
        }
        String bb=UartClient.IntToHexNoHead(sum);
        System.out.println(bb);
       // System.out.println(UartClient.IntToHexNoHead2(buf[1]));
    }

    @Test
    public void test16Int(){
        String s= "0065";

        System.out.print((Integer.parseInt(s, 16)));
    }


    int localState = 2;
    int stateNow = 1;
    int stateTito = 0;
    boolean h = true;
    @Test
    public void testRX(){
        Observable.interval(0, 1, TimeUnit.SECONDS)//状态复位
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if(localState==stateNow&&localState!=0){
                            if(stateTito<=30){
                                stateTito++;
                                if(h&&stateTito==15){
                                    h=false;
                                    stateNow=2;
                                    System.out.print("临时跟换");
                                }

                            }else {
                                stateTito =0;
                                System.out.print("一个状态维持超过30秒");
                            }
                        }else {
                            System.out.print("状态自己变了");
                            localState = stateNow;
                            stateTito=0;
                        }
                    }
                });
        while (true);
    }
}