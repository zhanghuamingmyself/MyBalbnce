package com.zhanghuaming.mybalbnce.bean;

import java.util.List;

/**
 * Created by zhang on 2017/12/18.
 */

public class LoginBack {

    /**
     * status : 1
     * msg : {"number":"T00001","QrCode":"https://adShufflingmin.alilinju.com//Uploads/shopQrCode/867836/1890.jpg","slideshow":["https://alyimg.alilinju.com/mingpian/store_ad/2017-11-03/59fc39c2c6a07.jpg","https://alyimg.alilinju.com/mingpian/store_ad/2017-10-12/59df361461e7e.JPG","https://alyimg.alilinju.com/mingpian/store_ad/2017-10-12/59df35eda6112.JPG"]}
     */

    private int status;
    private MsgBean msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public MsgBean getMsg() {
        return msg;
    }

    public void setMsg(MsgBean msg) {
        this.msg = msg;
    }

    public static class MsgBean {
        /**
         * number : T00001
         * QrCode : https://adShufflingmin.alilinju.com//Uploads/shopQrCode/867836/1890.jpg
         * slideshow : ["https://alyimg.alilinju.com/mingpian/store_ad/2017-11-03/59fc39c2c6a07.jpg","https://alyimg.alilinju.com/mingpian/store_ad/2017-10-12/59df361461e7e.JPG","https://alyimg.alilinju.com/mingpian/store_ad/2017-10-12/59df35eda6112.JPG"]
         */

        private String number;
        private String QrCode;
        private List<String> slideshow;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getQrCode() {
            return QrCode;
        }

        public void setQrCode(String QrCode) {
            this.QrCode = QrCode;
        }

        public List<String> getSlideshow() {
            return slideshow;
        }

        public void setSlideshow(List<String> slideshow) {
            this.slideshow = slideshow;
        }

        @Override
        public String toString() {
            return "MsgBean{" +
                    "number='" + number + '\'' +
                    ", QrCode='" + QrCode + '\'' +
                    ", slideshow=" + slideshow +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LoginBack{" +
                "status=" + status +
                ", msg=" + msg +
                '}';
    }
}
