package com.zhanghuaming.mybalbnce.utils;

/**
 * Created by zhang on 2017/12/23.
 */

public interface SerialBack {
    void sHavePeople();//1.底板通知android板有人站上去
    void sHaveweight(double weight);//2.底板通知android板稳定后的体重信息
    void sNoPeople();//底板通知android板人已经下来了
    void sAfterSettingCloseTime();//底板通知android已经修改休眠时间
}
