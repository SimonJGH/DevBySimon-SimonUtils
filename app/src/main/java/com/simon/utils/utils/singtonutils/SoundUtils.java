package com.simon.utils.utils.singtonutils;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import com.simon.utils.MyApplication;

import java.io.IOException;

/**
 * 播放音乐工具类
 * Created by Administrator on 2017/12/21.
 */
@SuppressWarnings("all")
public class SoundUtils {
    private MediaPlayer mMediaPlayer;

    private SoundUtils() {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(beepListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SoundUtils getInstance() {
        return SafeMode.mSoundUtils;
    }

    public static class SafeMode {
        private static final SoundUtils mSoundUtils = new SoundUtils();
    }

    /**
     * 播放声音 不能同时播放多种音频
     * 消耗资源较大
     *
     * @param rawId
     * @param volume 0.50f
     */
    public void playSoundByMedia(int rawId, int volume) {
        boolean playing = mMediaPlayer.isPlaying();
        if (playing) {
            mMediaPlayer.reset();
        }
        try {
            AssetFileDescriptor file = MyApplication.getInstance().getResources().openRawResourceFd(
                    rawId);
            mMediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mMediaPlayer.setVolume(Float.parseFloat(String.valueOf(volume * 0.01)), Float.parseFloat(String.valueOf(volume * 0.01)));
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            mMediaPlayer = null;
        }
    }

    private static final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    /**
     * 适合播放声音短，文件小
     * 可以同时播放多种音频
     * 消耗资源较小
     */
    public static void playSound(int rawId) {
        SoundPool soundPool;
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频的数量
            builder.setMaxStreams(1);
            //AudioAttributes是一个封装音频各种属性的类
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        } else {
            //第一个参数是可以支持的声音数量，第二个是声音类型，第三个是声音品质
            soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        }
        //第一个参数Context,第二个参数资源Id，第三个参数优先级
        soundPool.load(MyApplication.getInstance(), rawId, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(1, 1, 1, 0, 0, 1);
            }
        });
        //第一个参数id，即传入池中的顺序，第二个和第三个参数为左右声道，第四个参数为优先级，第五个是否循环播放，0不循环，-1循环
        //最后一个参数播放比率，范围0.5到2，通常为1表示正常播放
//        soundPool.play(1, 1, 1, 0, 0, 1);
        //回收Pool中的资源
        //soundPool.release();
    }

    /**
     * 释放资源
     */
    public void releaseSoundSource() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        // mMediaPlayer.release();
    }
}
