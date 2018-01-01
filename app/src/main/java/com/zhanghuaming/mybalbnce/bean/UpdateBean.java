package com.zhanghuaming.mybalbnce.bean;


public class UpdateBean {

    public int rstId;
    public SoftInfo object;

    public  class SoftInfo {

        public String appName; //
        public String packageName; //包名
        public String versionName;      //当前版本
        public String updateInfo;  //当前版本更新说明
        public String downloadUrl;  // 升级下载地址
        public String md5;
        public int fileSize;// kb
    }
}
