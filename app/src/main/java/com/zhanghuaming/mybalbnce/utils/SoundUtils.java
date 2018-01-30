package com.zhanghuaming.mybalbnce.utils;

import android.media.MediaPlayer;
import com.zhanghuaming.mybalbnce.R;
import com.zhanghuaming.mybalbnce.MyApplication;

/**
 * Created by zhang on 2017/12/18.
 */

public class SoundUtils {
    private MediaPlayer mPlayer = null;
    private static SoundUtils mSoundUtils;

    public static SoundUtils getInstance() {
        if (mSoundUtils == null) {
            mSoundUtils = new SoundUtils();
        }
        return mSoundUtils;
    }

    private SoundUtils() {

    }

    public void playFocusSound() {//人站上去
        try {
            if (mPlayer == null) {
                mPlayer = MediaPlayer.create(MyApplication.getApplication(), R.raw.begin);//重新设置要播放的音频
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        stopSound();
                        if (mPlayer == null) {
                            mPlayer = MediaPlayer.create(MyApplication.getApplication(), R.raw.begin2);//重新设置要播放的音频
                            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    stopSound();
                                }
                            });
                        }
                        mPlayer.start();
                    }
                });
            }
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playFatSound() {//体脂失败
        try {
            if (mPlayer == null) {
                mPlayer = MediaPlayer.create(MyApplication.getApplication(), R.raw.reset);//重新设置要播放的音频
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        stopSound();
                    }
                });
            }
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void playbeginFatSound() {//体重获取到
        try {
            if (mPlayer == null) {
                mPlayer = MediaPlayer.create(MyApplication.getApplication(), R.raw.beginfat);//重新设置要播放的音频
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        stopSound();
                    }
                });
            }
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void playInputSound() {//输入二维码提示音
        try {
            if (mPlayer == null) {
                mPlayer = MediaPlayer.create(MyApplication.getApplication(), R.raw.look);//重新设置要播放的音频
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        stopSound();
                    }
                });
            }
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playerrorSound() {//其他错误提示音
        try {
            if (mPlayer == null) {
                mPlayer = MediaPlayer.create(MyApplication.getApplication(), R.raw.otherfail);//重新设置要播放的音频
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        stopSound();
                    }
                });
            }
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stopSound() {
        try {
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
                mPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
