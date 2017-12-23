package com.zhanghuaming.mybalbnce;

import android.os.Environment;

/**
 * Created by zhang on 2017/12/14.
 *配置文件
 */

public class StaticCfg {
    private static final String serviceIP ="https://cc.zoogrow.com/";//视频服务器ip或域名
    private static final String servicePort = "";//视频服务器端口号
    public static final String baseUrl = serviceIP+"api/";
    public static final String loginUrl = "login";
    public static final String sendWeightUrl = "UploadWeight";
    public static final int BAUDRATE = 115200;//串口波特率
    public static final String TTY_PATH = "/dev/ttyMT2";//串口位置;



    public static double latitude = 0;    //获取纬度信息,不用自己设，会更新的
    public static double longitude = 0;    //获取经度信息,不用自己设，会更新的
}
