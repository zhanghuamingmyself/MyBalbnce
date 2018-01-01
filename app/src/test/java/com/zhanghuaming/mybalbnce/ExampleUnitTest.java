package com.zhanghuaming.mybalbnce;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        System.out.println(year + "/" + month + "/" + date + " " +hour + ":" +minute + ":" + second);
    }

    @Test
    public void byteTest(){
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
}