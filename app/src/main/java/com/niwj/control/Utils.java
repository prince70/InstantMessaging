package com.niwj.control;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;


import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utils
 * Created by Yancy on 2015/12/6.
 */
public class Utils {

    private static final String TAG = "Utils";

    public static int getStatusBarHeight() {
        Class<?> c;
        Object obj;
        Field field;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return x;
    }

    public static void hideTitleBar(Activity activity, int resource, int steepToolBarColor) {
        if (android.os.Build.VERSION.SDK_INT > 18) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            LinearLayout whole_layout = (LinearLayout) activity.findViewById(resource);
            whole_layout.setBackgroundColor(steepToolBarColor);
            whole_layout.setPadding(0, activity.getResources().getDimensionPixelSize(getStatusBarHeight()), 0, 0);
        }
    }

    /**
     * 判断网络连接是否存在，并没有测试服务器连接
     * @param context
     * @return
     */
    public static boolean isNetAvailable(Context context){
        if (context != null ){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            boolean available = activeNetworkInfo.isAvailable();
            return available;
        }
        return false;
    }

    public static String getResString(Context context, int resId){
        return context.getResources().getString(resId);
    }

    public static int getColor(Context context, int colorId){
        return context.getResources().getColor(colorId);
    }

    /**
     * exist SDCard
     */
    public static boolean existSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getImageName() {
        String PATTERN = "yyyyMMddHHmmss";
        return new SimpleDateFormat(PATTERN, Locale.CHINA).format(new Date()) + ".jpg";
    }





}