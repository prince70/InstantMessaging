package com.niwj.control;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import com.niwj.instantmessaging.R;
import com.niwj.widget.ImageToast;

import java.io.File;

/**
 * Created by Administrator on 2016/11/14.
 * @author 龙盛
 * 用于获取工作目录的工具类
 */

public class PathUtil {

    private static final String TAG = "PathUtil";

    /**
     * 创建目录
     */
    private static void createPath(String path){
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    /**
     * 获取APP目录
     */
    public static String getAppPath(){
        String path;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.e(TAG, "getUserPath():读取手机存储失败");
            path = "/sdcard";
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return path + "/WisdomHealthPlan/";
    }

    /**
     * 获取用户目录
     */
    public static String getUserPath(Context context){
        String userId = "prince70";
        if (userId.isEmpty()) return "";

        String path = getAppPath();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            ImageToast.ImageToast(context, R.mipmap.ic_help,"读取手机存储失败", Toast.LENGTH_SHORT);
//            ToastUtil.showToast(context, "读取手机存储失败");
        }

        path += "user/" + userId + "/";
        createPath(path);
        return path;
    }

    /**
     * 获取宝宝的目录
     */
    public static String getInfantPath(Context context){
        String path = getUserPath(context);
        path += "infant/";
        createPath(path);
        return path;
    }

    /**
     * 获取录音文件保存目录
     */
    public static String getSoundRecordPath(Context context){
        String path = getUserPath(context);
        path += "soundRecord/";
        createPath(path);
        return path;
    }

    /**
     * 获取临时文件夹
     */
    public static String getTempPath(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WisdomHealthPlan/temp/";
        createPath(path);
        return path;
    }

    /**
     * 获取临时图片文件名
     */
    public static String getTempPicFilename(){
        return getTempPath() + "temp.jpg";
    }

    /**
     * 获取缓存文件夹路径
     */
    public static String getCachePath(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WisdomHealthPlan/cache/";
        createPath(path);
        return path;
    }

    /**
     * Url转为文件路径
     */
    public static String url2Filename(String url){
        //转换为小写字母
        url = url.toLowerCase();
        //去掉 \ / : * ? " < > | ”.
        url = url.replaceAll("[\\\\\\, /,:,*,?,\",<,>,|,”,.]", "");
        //去掉http
        url = url.replace("http", "");

        StringBuffer sb = new StringBuffer(url);
        for (int i = 0; i < sb.length(); i++) {
            int ch = sb.charAt(i);
            //数字后移3位
            if (Character.isDigit(ch)){
                ch = ch - '0';
                ch = (ch + 3) % 10;
                ch += '0';
            }
            //字母后移9位
            else if(Character.isLetter(ch)){
                ch = ch - 'a';
                ch = (ch + 9) % 26;
                ch += 'a';
            }
            sb.setCharAt(i, (char) ch);
        }

        return sb.toString();
    }

    /**
     * 删除url缓存
     */
    public static void deleteCache(String url){
        String filename = getCachePath() + url2Filename(url);
        FileUtils.deleteFile(filename);
    }

    /**
     * 统计缓存大小
     */
    public static String getCacheSize(){
        return FileUtils.formatSize(FileUtils.getDirSize(getAppPath()));
    }

    /**
     * 清空缓存
     */
    public static void clearCache(){
        FileUtils.deleteFileOrDir(new File(getAppPath()));
    }
}
