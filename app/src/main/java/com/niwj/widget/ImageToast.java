package com.niwj.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.niwj.instantmessaging.R;


/**
 * Created by admin on 2017/2/15 0015.
 */

public class ImageToast {
    private static Toast toast;
    private static View toastLayout;
    private static ImageView imageView;
    private static TextView textView;


    public static void ImageToast(Context context , int imageId , String message , int duration){
        toast = new Toast(context);
        //显示的时间
        toast.setDuration(duration);
        //显示的位置
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toastLayout =  LayoutInflater.from(context).inflate(R.layout.toast_image, null);
        imageView = (ImageView) toastLayout.findViewById(R.id.iv_toast_img);
        imageView.setImageResource(imageId);

        textView = (TextView) toastLayout.findViewById(R.id.tv_toast_text);
        textView.setText(message);
        toast.setView(toastLayout);
        toast.show();
    }

    /**
     * 表示成功的短时间Toast，例：收藏成功
     * @param context
     * @param message
     */
    public static void showSuccessedToast(Context context, String message){
        toast = new Toast(context);
        //显示的时间
        toast.setDuration(Toast.LENGTH_SHORT);
        //显示的位置
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toastLayout =  LayoutInflater.from(context).inflate(R.layout.toast_image, null);
        imageView = (ImageView) toastLayout.findViewById(R.id.iv_toast_img);
        imageView.setImageResource(R.mipmap.ic_ok);

        textView = (TextView) toastLayout.findViewById(R.id.tv_toast_text);
        textView.setText(message);
        toast.setView(toastLayout);
        toast.show();
    }

    /**
     * 表示警示的短时间Toast，例：加载失败
     * @param context
     * @param message
     */
    public static void showWarnedImageToast(Context context, String message){
        toast = new Toast(context);
        //显示的时间
        toast.setDuration(Toast.LENGTH_SHORT);
        //显示的位置
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toastLayout =  LayoutInflater.from(context).inflate(R.layout.toast_image, null);
        imageView = (ImageView) toastLayout.findViewById(R.id.iv_toast_img);
        imageView.setImageResource(R.mipmap.ic_help);

        textView = (TextView) toastLayout.findViewById(R.id.tv_toast_text);
        textView.setText(message);
        toast.setView(toastLayout);
        toast.show();
    }

    /**
     * 表示错误的短时间Toast
     * @param context
     * @param message
     */
    public static void showFailedImageToast(Context context, String message){
        toast = new Toast(context);
        //显示的时间
        toast.setDuration(Toast.LENGTH_SHORT);
        //显示的位置
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toastLayout =  LayoutInflater.from(context).inflate(R.layout.toast_image, null);
        imageView = (ImageView) toastLayout.findViewById(R.id.iv_toast_img);
        imageView.setImageResource(R.mipmap.ic_cancel);

        textView = (TextView) toastLayout.findViewById(R.id.tv_toast_text);
        textView.setText(message);
        toast.setView(toastLayout);
        toast.show();
    }

    /**
     * 表示成功的长时间Toast
     * @param context
     * @param message
     */
    public static void showLongSuccessedImageToast(Context context, String message){
        toast = new Toast(context);
        //显示的时间
        toast.setDuration(Toast.LENGTH_LONG);
        //显示的位置
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toastLayout =  LayoutInflater.from(context).inflate(R.layout.toast_image, null);
        imageView = (ImageView) toastLayout.findViewById(R.id.iv_toast_img);
        imageView.setImageResource(R.mipmap.ic_ok);

        textView = (TextView) toastLayout.findViewById(R.id.tv_toast_text);
        textView.setText(message);
        toast.setView(toastLayout);
        toast.show();
    }

    /**
     * 表示警示的长时间Toast
     * @param context
     * @param message
     */
    public static void showLongWarnedImageToast(Context context, String message){
        toast = new Toast(context);
        //显示的时间
        toast.setDuration(Toast.LENGTH_LONG);
        //显示的位置
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toastLayout =  LayoutInflater.from(context).inflate(R.layout.toast_image, null);
        imageView = (ImageView) toastLayout.findViewById(R.id.iv_toast_img);
        imageView.setImageResource(R.mipmap.ic_help);

        textView = (TextView) toastLayout.findViewById(R.id.tv_toast_text);
        textView.setText(message);
        toast.setView(toastLayout);
        toast.show();
    }

    /**
     * 表示错误的长时间Toast
     * @param context
     * @param message
     */
    public static void showLongFailedImageToast(Context context, String message){
        toast = new Toast(context);
        //显示的时间
        toast.setDuration(Toast.LENGTH_LONG);
        //显示的位置
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toastLayout =  LayoutInflater.from(context).inflate(R.layout.toast_image, null);
        imageView = (ImageView) toastLayout.findViewById(R.id.iv_toast_img);
        imageView.setImageResource(R.mipmap.ic_cancel);

        textView = (TextView) toastLayout.findViewById(R.id.tv_toast_text);
        textView.setText(message);
        toast.setView(toastLayout);
        toast.show();
    }
}
