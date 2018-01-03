package com.eto.upgrade.bean;


public class UpdateBean {

    /**
     * status : 1
     * msg : {"versionName":"v1.01","updateInfo":"1","downloadUrl":"https://cc.zoogrow.com/Scale/apk/2018-01-02/upgrade_apk.apk","md5":"A6768CB0F6BC1A22E3C0870A8E763B2F","fileSize":"2392529"}
     */

    public int status;
    public MsgBean msg;


    public static class MsgBean {
        /**
         * versionName : v1.01
         * updateInfo : 1
         * downloadUrl : https://cc.zoogrow.com/Scale/apk/2018-01-02/upgrade_apk.apk
         * md5 : A6768CB0F6BC1A22E3C0870A8E763B2F
         * fileSize : 2392529
         */

        public String versionName;
        public String updateInfo;
        public String downloadUrl;
        public String md5;
        public String fileSize;

    }
}
