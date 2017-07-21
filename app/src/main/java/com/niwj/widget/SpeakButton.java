package com.niwj.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import com.niwj.control.PhoneUtil;
import com.niwj.control.SoundRecordUtil;
import com.niwj.instantmessaging.R;


/**
 * Created by Administrator on 2016/11/12.
 * @author 龙盛
 * 说话按钮
 */

public class SpeakButton extends Button {

    public void setOnSendListener(OnSendListener onSendListener) {
        this.onSendListener = onSendListener;
    }

    private final String TAG = "SpeakButton";
    private final int BORDER;   //状态切换的边界
    private final int RECORD_LIMIT_TIME = 60;   //录音最大时间，秒

    private SoundRecordPopWindow soundRecordPopWindow;  //录音弹窗
    private boolean isSave = true;                      //是否保存录音文件
    private boolean isRecording;                        //是否是录音状态
    private int recordTime = 9999;                      //录音时间计时
    private OnSendListener onSendListener;              //发送消息回调

    public SpeakButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        BORDER = -PhoneUtil.getScreenHeight(context) / 10;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                //手指按下
                case MotionEvent.ACTION_DOWN:
                    if (event.getPointerCount() == 1) {
                        startRecord();
                    }
                    break;
                //手指移动
                case MotionEvent.ACTION_MOVE:
                    if (isRecording && recordTime > 10) {
                        if (event.getY(0) < BORDER) {
                            isRecordStatus(false);
                        } else {
                            isRecordStatus(true);
                        }
                    }
                    break;
                //手指松开
                case MotionEvent.ACTION_UP:
                    if (event.getPointerCount() == 1) {
                        stopRecord();
                    }
                    break;
            }
        return super.onTouchEvent(event);
    }

    /**
     * 开始录音
     */
    private void startRecord(){
        if (!isRecording){
            isRecording = true;
            setText("松开结束");
            //延时300毫秒再显示弹窗
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    //再次确认是否是按下状态
                    if (getText().toString().equals("松开结束")) {
                        if (soundRecordPopWindow == null) {
                            soundRecordPopWindow = new SoundRecordPopWindow(getContext());
                        }
                        soundRecordPopWindow.show();

                        //计时的线程
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                //重置时间
                                recordTime = RECORD_LIMIT_TIME;

                                while(soundRecordPopWindow != null && recordTime > 0){
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    recordTime--;

                                    if (recordTime <= 10){
                                        post(new Runnable() {
                                            @Override
                                            public void run() {
                                                soundRecordPopWindow.setText("还可以说" + recordTime + "秒");
                                            }
                                        });
                                    }
                                }
                                if (soundRecordPopWindow != null) {
                                    post(new Runnable() {
                                        @Override
                                        public void run() {
                                            stopRecord();
                                        }
                                    });
                                }
                            }
                        }.start();
                    }
                }
            }, 300);
            isSave = true;
        }
    }

    /**
     * 停止录音
     */
    private void stopRecord(){
        if (isRecording){
            isRecording = false;
            setText("按住说话");
            if (soundRecordPopWindow != null) {
                soundRecordPopWindow.dismiss();
                if (isSave && onSendListener != null && checkRecordTime()) {
                    onSendListener.send(soundRecordPopWindow.getFilePath());
                } else {
                    soundRecordPopWindow.deleteFile();
                }
                soundRecordPopWindow = null;
            }
        }
    }

    /**
     * 检查录音时间
     */
    private boolean checkRecordTime(){
        //判断录音时间是否过短
        int time = SoundRecordUtil.getRecordTime(getContext(), soundRecordPopWindow.getFilePath());
        if (time < 0){
            Log.e(TAG, "dismiss():读取录音文件失败");
            return false;
        }
        //长度小于一秒则丢弃
        if (time < 1000){
            ImageToast.ImageToast(getContext(), R.mipmap.ic_help,  getContext().getString(R.string.short_time_record), Toast.LENGTH_SHORT);
//            ToastUtil.showToast(getContext(), "录音时间过短");
            return false;
        }
        return true;
    }

    /**
     * 设置录音状态的提示
     */
    private void isRecordStatus(boolean status){
        isSave = status;
        setText(status ? "松开结束" : "松开手指，取消发送");
        if (soundRecordPopWindow != null) {
            soundRecordPopWindow.isRecordStatus(status);
        }
    }

    public interface OnSendListener{
        void send(String filePath);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (soundRecordPopWindow != null){
            soundRecordPopWindow.dismiss();
            soundRecordPopWindow = null;
        }
        super.onDetachedFromWindow();
    }
}
