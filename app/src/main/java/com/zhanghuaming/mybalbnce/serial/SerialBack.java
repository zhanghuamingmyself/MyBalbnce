package com.zhanghuaming.mybalbnce.serial;

/**
 * Created by zhang on 2017/12/23.
 */

public interface SerialBack {
    int sISHAVINGPEOPLE = 1;//有人在上面
    int sISHAVINGWEIGHT = 2;//有稳定体重
    int sISSHOWINGAUTOCODE = 3;//正在显示验证码

    int sISNOTICESETTING = 4;//通知底板进入设置休眠时间模式
    int sISNOTICESETTIME = 5;//发送休眠时间
    int sISOUTSETTING = 6;//通知底板退出设置休眠时间模式


    void sHavePeople();//1.底板通知android板有人站上去

    void sHaveweight(double weight);//2.底板通知android板稳定后的体重信息

    void sNoPeople();//底板通知android板人已经下来了



    void sAfterSettingCloseTime();//底板通知android已经修改休眠时间

    void sInSetingCloseTime();//底板回复android已经进入设置休眠时间模式


}
