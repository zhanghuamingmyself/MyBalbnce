package com.zhanghuaming.mybalbnce.serial;

/**
 * Created by zhang on 2017/12/23.
 */

public interface SerialBack {
    int sIS_HAVING_PEOPLE = 1;//有人在上面
    int sIS_HAVING_WEIGHT = 2;//有稳定体重
    int sIS_SHOWING_AUTOCODE = 3;//正在显示验证码

    int sIS_NOTICE_SETTING = 4;//通知底板进入设置休眠时间模式
    int sIS_NOTICE_SETTIME = 5;//发送休眠时间
    int sIS_OUT_SETTING = 6;//通知底板退出设置休眠时间模式

    int sIS_BEGIN_BODY_FAT = 8;
    int sIS_BODY_FAT = 7;//体脂
    int sIS_BODY_ERROR = 9;



    void sInstabilityPeople(double weight);//1.底板通知android板有人站上去

    void sHaveweight(double weight);//2.底板通知android板稳定后的体重信息

    void sNoPeople();//底板通知android板人已经下来了



    void sAfterSettingCloseTime();//底板通知android已经修改休眠时间

    void sInSetingCloseTime();//底板回复android已经进入设置休眠时间模式

    void sBeginBodyfat(double fat);
    void sHaveBodyfat(double fat);//体脂
    void sBodyfatError();//体脂错误
}
