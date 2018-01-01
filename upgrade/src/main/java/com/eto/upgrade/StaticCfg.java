package com.eto.upgrade;

/**
 * Created by zhang on 2017/12/14.
 *配置文件
 */

public class StaticCfg {
    private static final String serviceIP ="https://cc.zoogrow.com/";//视频服务器ip或域名
    private static final String servicePort = "";//视频服务器端口号
    public static final String baseUrl = serviceIP+"api/";
    public static final String getUpdateUrl = "UpdateBean";
    public static String devCode = "13715645207";//模拟物联网卡卡号
    public static String updateAppName = "xxxx";
    public static String updatePackageName="com.zhanghuaming.mybalbnce";
    public static String updateActivityName ="MainActivity";

}
