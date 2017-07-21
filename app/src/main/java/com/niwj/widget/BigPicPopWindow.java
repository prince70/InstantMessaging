package com.niwj.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.niwj.control.ImageUtil;
import com.niwj.control.PhoneUtil;
import com.niwj.instantmessaging.R;


/**
 * Created by Administrator on 2016/9/25.
 * @author 龙盛
 * 显示大图的弹窗
 */

public class BigPicPopWindow extends MyPopupWindow{

    private String picPath;     //图片路径
    private Bitmap imgBitmap;      //图片

    /**
     * @param context
     * @param picPath 图片路径
     */
    public BigPicPopWindow(Context context, String picPath) {
        super(context, R.layout.pop_big_pic);
        this.picPath = picPath;

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        //黑色背景色
        setBackgroundDrawable(new ColorDrawable(0xFF000000));

        //加载图片
        LoadImageTask task = new LoadImageTask();
        task.execute();
    }

    /**
     * 设置删除按钮的点击监听
     */
    public BigPicPopWindow setOnDelClickListener(final View.OnClickListener l){
        ImageView iv_del = getView(R.id.iv_del);
        iv_del.setVisibility(View.VISIBLE);
        iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onClick(v);
                dismiss();
            }
        });
        return this;
    }

    public void show(){
        showAtLocation(new View(getContext()), Gravity.TOP, 0, 0);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //回收图片
        if (null != imgBitmap) {
            imgBitmap.recycle();
            imgBitmap = null;
        }
    }

    /**
     * 加载图片
     */
    private Bitmap loadImage(String picPath){
        int screenWidth = PhoneUtil.getScreenWidth(getContext());
        int screenHeight = PhoneUtil.getScreenHeight(getContext());
        //压缩图片大小
        Bitmap bitmap = ImageUtil.zoomImgScale(BitmapFactory.decodeFile(picPath), screenWidth * 2, screenHeight * 2);
        return bitmap;
    }

    /**
     * 异步加载图片
     */
    private class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {

        private LoadingDialog loadingDialog = new LoadingDialog(getContext());

        @Override
        protected void onPreExecute() {
            loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dismiss();
                }
            });
            loadingDialog.show();
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return loadImage(picPath);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            picPath = null;
            if (null != bitmap) {
                ImageView iv_pic = getView(R.id.iv_pic);
                iv_pic.setImageBitmap(bitmap);
                imgBitmap = bitmap;
            }
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }
}
