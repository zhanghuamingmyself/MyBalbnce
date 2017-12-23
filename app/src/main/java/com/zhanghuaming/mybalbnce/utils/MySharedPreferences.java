package com.zhanghuaming.mybalbnce.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by zhang on 2017/7/3.
 */

public class MySharedPreferences {


    private final static String TAG =MySharedPreferences.class.getSimpleName();
    public static final String LOGINBACK = "loginback";
    public static final String DevCode = "username";
    public static final String PASSWORD = "password";
    public static final String NUMBER = "number";//设备编号

     public static void save(Context context,String key,String value){

         SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("save", Context.MODE_PRIVATE);
         SharedPreferences.Editor editor = preferences.edit();
         editor.putString(key, value);
         editor.commit();
         Log.i(TAG,"保存了"+key+":   "+value);
    }

    public static String  get(Context context,String key){
        SharedPreferences read = context.getApplicationContext().getSharedPreferences("save", Context.MODE_PRIVATE);

        //步骤2：获取文件中的值
        String value = read.getString(key, "");
        Log.i(TAG,"读取了"+key+":   "+value);
        return value;
    }

}
