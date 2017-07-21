package com.niwj.control;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * FileUtils
 * Created by Yancy on 2015/12/2.
 */
public class FileUtils {


    private final static String PATTERN = "yyyyMMddHHmmss";


    public static File createTmpFile(Context context, String filePath) {

        String timeStamp = new SimpleDateFormat(PATTERN, Locale.CHINA).format(new Date());

        String externalStorageState = Environment.getExternalStorageState();

        File dir = new File(Environment.getExternalStorageDirectory() + filePath);

        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return new File(dir, timeStamp + ".jpg");
        } else {
            File cacheDir = context.getCacheDir();
            return new File(cacheDir, timeStamp + ".jpg");
        }

    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExists(String filename){
        if (filename == null) return false;
        File file = new File(filename);
        return file.exists();
    }

    /**
     * 删除文件
     */
    public static void deleteFile(String filename){
        File file = new File(filename);
        if (file != null && file.exists()){
            file.delete();
        }
    }

    public static void createFile(String filePath) {
        String externalStorageState = Environment.getExternalStorageState();

        File dir = new File(Environment.getExternalStorageDirectory() + filePath);
        File cropFile = new File(Environment.getExternalStorageDirectory() + filePath + "/crop");

        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            if (!cropFile.exists()) {
                cropFile.mkdirs();
            }

            File file = new File(dir, ".nomedia");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void copyfile(File fromFile, File toFile, Boolean rewrite) {
        if (!fromFile.exists()) {
            return;
        }
        if (!fromFile.isFile()) {
            return;
        }
        if (!fromFile.canRead()) {
            return;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        try {
            java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
            java.io.FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计文件夹大小
     */
    public static long getDirSize(String dirname){
        return getDirSize(new File(dirname));
    }

    /**
     * 统计文件夹大小
     */
    public static long getDirSize(File dir){
        if (dir == null || !dir.isDirectory()) return 0;  //不是文件夹
        long size = 0;
        File[] files = dir.listFiles();
        for (File file : files){
            size += file.length();
            if (file.isDirectory()){
                size += getDirSize(file);
            }
        }
        return size;
    }

    /**
     * 文件大小格式化
     */
    public static String formatSize(long size){
        final long kb = 1024;
        final long mb = kb * 1024;
        final long gb = mb * 1024;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        if (size < kb) {
            return size + "B";
        }
        if (size < mb) {
            return size / kb + "KB";
        }
        if (size < gb) {
            return decimalFormat.format((double) size / mb) + "MB";
        }

        return decimalFormat.format((double) size / gb) + "GB";
    }

    /**
     * 删除文件或文件夹
     */
    public static void deleteFileOrDir(File file){
        if (file == null || !file.exists()) return;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0;  i < files.length; i++){
                deleteFileOrDir(files[i]);
            }
        }
        file.delete();
    }
}