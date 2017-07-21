package com.niwj.control;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Administrator on 2016/11/12.
 * @author
 * 录音工具类
 */

public class SoundRecordUtil {

    private static final String TAG = "SoundRecordUtil";

    private static MediaPlayer mPlayer;
    private static MediaRecorder mRecorder;

    /**
     * 开始录音
     * @param fileName 录音文件保存位置
     */
    public static void startRecord(String fileName){
        startRecord(fileName, null);
    }

    /**
     * 开始录音
     * @param fileName 录音文件保存位置
     * @param listener 麦克风声音大小监听
     */
    public static void startRecord(String fileName, final GetSoundLevelListener listener){
        if (fileName == null){
            Log.e(TAG, "startRecord():fileName为null");
            return;
        }
        if (mRecorder != null){
            Log.e(TAG, "录音已经开始");
            return;
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try{
            mRecorder.prepare();
        }catch (IOException e){
            Log.e(TAG, "录音准备失败:\n" + e.toString());
            mRecorder = null;
        }
        mRecorder.start();

        if (listener != null){
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    while (mRecorder != null){
                        int level = mRecorder.getMaxAmplitude(); //Android实时获取音量分贝
                        if (listener != null){
                            listener.getLevel(level);
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * 停止录音
     */
    public static void stopRecord(){
        if (mRecorder != null){
            try {
                //下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
                //报错为：RuntimeException:stop failed
                mRecorder.setOnErrorListener(null);
                mRecorder.setOnInfoListener(null);
                mRecorder.setPreviewDisplay(null);
                mRecorder.stop();
            } catch (IllegalStateException e) {

            }catch (RuntimeException e) {

            }catch (Exception e) {

            }
            mRecorder.release();
            mRecorder = null;
        }
    }

    /**
     * 开始播放录音
     */
    public static void startPlay(String fileName, final MediaPlayer.OnCompletionListener listener){
        if (fileName == null){
            Log.e(TAG, "startPlay():fileName为null");
            return;
        }
        if (mPlayer != null){
            Log.e(TAG, "播放录音已经开始");
            return;
        }
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlay();
                listener.onCompletion(mp);
            }
        });  //播放结束回调
        try{
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        }catch (IOException e){
            Log.e(TAG, "播放录音失败:\n" + e.toString());
            mPlayer = null;
        }
    }

    /**
     * 停止播放录音
     */
    public static void stopPlay(){
        if (mPlayer != null){
            mPlayer.setOnCompletionListener(null);
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * 获取音频长度，单位是毫秒
     */
    public static int getRecordTime(Context context, String fileName){
        if (fileName == null){
            Log.e(TAG, "getRecordTime():fileName为null");
            return -1;
        }
        MediaPlayer mp = MediaPlayer.create(context, Uri.parse(fileName));
        if (mp == null){
            Log.e(TAG, "getRecordTime():读取录音文件失败");
            return -1;
        }
        int time = mp.getDuration();
        mp.release();
        return time;
    }

    /**
     * 获取麦克风声音大小回调
     */
    public interface GetSoundLevelListener{
        void getLevel(int level);
    }
}
