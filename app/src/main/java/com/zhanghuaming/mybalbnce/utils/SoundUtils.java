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

    public void playSound() {
        try {
            if (mPlayer == null) {
                mPlayer = MediaPlayer.create(MyApplication.getApplication(), R.raw.dudu);//重新设置要播放的音频
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
