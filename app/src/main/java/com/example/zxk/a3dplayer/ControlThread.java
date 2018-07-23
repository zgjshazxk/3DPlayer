package com.example.zxk.a3dplayer;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Zxk on 2017/5/25.
 */

public class ControlThread extends Thread {

    //采样率
    private int mSampleingRate = 88200;
    //单声道
    private int mSoundChannel = AudioFormat.CHANNEL_OUT_MONO;

    private Activity mActivity;
    private AudioTrack mAudioTrack;
    private byte[] data;
    private String mFileName;


    public ControlThread(Activity activity, String fileName) {
        mActivity = activity;
        mFileName = fileName;

        int bufferSize = AudioTrack.getMinBufferSize(mSampleingRate, mSoundChannel, AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                mSampleingRate,
                mSoundChannel,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);
    }

    @Override
    public void run() {
        super.run();
        try {
            if (null != mAudioTrack) {
                mAudioTrack.play();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream inputStream = mActivity.getResources().getAssets().open(mFileName);

            byte[] buffer = new byte[1024*60];
            //播放进度
            int playIndex = 0;
            //是否缓冲完成
            boolean isLoaded = false;

            while (null != mAudioTrack && AudioTrack.PLAYSTATE_STOPPED != mAudioTrack.getPlayState()) {
                int len;
                if (-1 != (len = inputStream.read(buffer))) {
                    byteArrayOutputStream.write(buffer, 0, len);
                    data = byteArrayOutputStream.toByteArray();
                } else {
                    isLoaded = true;
                }

                if (AudioTrack.PLAYSTATE_PAUSED == mAudioTrack.getPlayState()) {

                }

                if (AudioTrack.PLAYSTATE_PLAYING == mAudioTrack.getPlayState()) {
                    playIndex += mAudioTrack.write(data, playIndex, data.length - playIndex);
                    if (isLoaded && playIndex == data.length) {
                        mAudioTrack.stop();
                    }
                    if (playIndex < 0) {
                        mAudioTrack.stop();
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //设置左右声道平衡
    public void setBalance(int max,int balance){
        float b = (float)balance / (float)max;
        if (null != mAudioTrack){
            mAudioTrack.setStereoVolume(1-b,b);
            //mAudioTrack.setVolume(b);
        }
    }

    //设置左右声道是否可用
    public void setChannel(boolean left,boolean right){
        if (null != mAudioTrack){
            mAudioTrack.setStereoVolume(left?1:0,right?1:0);
            mAudioTrack.play();
        }
    }

    public void pause(){
        if (null != mAudioTrack){
            mAudioTrack.pause();
        }
    }

    public void play(){
        if (null != mAudioTrack){
            mAudioTrack.play();
        }
    }

    public void stoped(){
        if (null != mAudioTrack){
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

}
