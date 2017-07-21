package com.niwj.widget;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.niwj.control.PathUtil;
import com.niwj.control.SoundRecordUtil;
import com.niwj.instantmessaging.R;

import java.io.File;
import java.util.Date;

/**
 * Created by Administrator on 2016/11/12.
 * @author
 * 录音弹窗
 */

public class SoundRecordPopWindow extends MyPopupWindow{

    /**
     * 获取录音文件路径
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 删除录音文件
     */
    public void deleteFile(){
        File file = new File(filePath);
        if (file.exists()){
            file.delete();
        }
    }

    /**
     * 是否是录音状态
     */
    public void isRecordStatus(boolean status){
        setText(status ? "手指上滑，取消发送" : "松开手指，取消发送");
    }

    private final String TAG = "SoundRecordPopWindow";

    private View[] levelView = new View[5];

    private Handler mHandler = new Handler();
    private String filePath;                    //保存录音文件的路径

    public SoundRecordPopWindow(Context context) {
        super(context, R.layout.pop_soundrecord);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setTouchable(false);

        levelView[0] = getView(R.id.level_1);
        levelView[1] = getView(R.id.level_2);
        levelView[2] = getView(R.id.level_3);
        levelView[3] = getView(R.id.level_4);
        levelView[4] = getView(R.id.level_5);

        //没有文件夹则创建文件夹
        filePath = PathUtil.getSoundRecordPath(context);
        if (filePath != null) {
            filePath += new Date().getTime() + ".3gp";
        }
    }

    /**
     * 设置音量大小等级
     */
    private void setSoundLevel(int level){
        for (int i = 0; i < 5; i++){
            levelView[i].setVisibility(level > i ? View.VISIBLE : View.GONE);
        }
    }

    public void setText(String text){
        ((TextView) getView(R.id.tv_text)).setText(text);
    }

    public void show(){
        this.showAtLocation(new View(getContext()), Gravity.CENTER, 0, 0);

        //开始录音
        SoundRecordUtil.startRecord(filePath, new SoundRecordUtil.GetSoundLevelListener() {
            @Override
            public void getLevel(int level) {
                if (level <= 400){
                    level = 0;
                }else{
                    level = (level / 4000) + 1;
                }
                final double finalLevel = level;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setSoundLevel((int) finalLevel);
                    }
                });
            }
        });
    }

    @Override
    public void dismiss() {
        //停止录音
        SoundRecordUtil.stopRecord();

        super.dismiss();
    }
}
