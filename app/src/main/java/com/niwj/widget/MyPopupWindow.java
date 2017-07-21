package com.niwj.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.niwj.adapter.CommonAdapter;


/**
 * Created by Administrator on 2016/8/15.
 * @author
 * 用于创建弹窗
 */
public class MyPopupWindow extends PopupWindow {
    private View convertView;       //内容布局
    private Context context;

    /**
     * @param context
     * @param convertViewId 内容布局
     */
    public MyPopupWindow(Context context, int convertViewId){
        super(LayoutInflater.from(context).inflate(convertViewId, null));
        this.context = context;
        this.convertView = getContentView();
        this.setFocusable(true);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); //防止弹窗被虚拟按键挡住
    }

    /**
     * 设置宽度
     */
    public MyPopupWindow setWindowWidth(int width){
        this.setWidth(width);
        return this;
    }

    /**
     * 设置高度
     */
    public MyPopupWindow setWindowHeight(int height){
        this.setHeight(height);
        return this;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (!isShowing()) {
            super.showAtLocation(parent, gravity, x, y);
        }
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }

    protected Context getContext(){
        return context;
    }

    /**
     * 获取View
     */
    public <T extends View> T getView(int viewId){
        return (T) convertView.findViewById(viewId);
    }

    /**
     * 设置TextView
     */
    public MyPopupWindow setText(int viewId, String text){
        ((TextView) getView(viewId)).setText(text);
        return this;
    }

    /**
     * 设置ImageView
     */
    public MyPopupWindow setImage(int viewId, Bitmap bitmap){
        ((ImageView) getView(viewId)).setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置ImageView
     */
    public MyPopupWindow setImage(int viewId, int resourceId){
        ((ImageView) getView(viewId)).setImageResource(resourceId);
        return this;
    }

    /**
     * 设置ImageView
     */
    public MyPopupWindow setImage(int viewId, Drawable drawable){
        ((ImageView) getView(viewId)).setImageDrawable(drawable);
        return this;
    }

    /**
     * 设置ListView和GridView的适配器
     */
    public MyPopupWindow setAdapter(int viewId, CommonAdapter adapter){
        ListView listView = (ListView) getView(viewId);
        if (listView != null) {
            listView.setAdapter(adapter);
        }
        return this;
    }

    /**
     * 设置ListView和GridView的适配器
     * @param maxScreenPart 最多可占用的屏幕高度百分比
     */
    public MyPopupWindow setAdapter(int viewId, CommonAdapter adapter, double maxScreenPart){
        ListView listView = (ListView) getView(viewId);
        if (listView != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            int maxHeight = (int) (wm.getDefaultDisplay().getHeight() * maxScreenPart);

            //统计listView中所有子view的总高度
            int height = 0;
            for (int i = 0; i < adapter.getCount(); i++){
                View itemView = adapter.getView(i, null, listView);
                itemView.measure(0, 0);
                height += itemView.getMeasuredHeight();
            }

            if (height > maxHeight) {
                params.height = maxHeight;
            }
            listView.setAdapter(adapter);
        }
        return this;
    }

    /**
     * 设置点击监听
     */
    public MyPopupWindow setViewOnClickListener(int viewId, View.OnClickListener listener){
        getView(viewId).setOnClickListener(listener);
        return this;
    }

    public MyPopupWindow setOnItemClickListener(int viewId, AdapterView.OnItemClickListener listener){
        ((ListView) getView(viewId)).setOnItemClickListener(listener);
        return this;
    }

    public MyPopupWindow setOnItemLongClickListener(int viewId, AdapterView.OnItemLongClickListener listener){
        ((ListView) getView(viewId)).setOnItemLongClickListener(listener);
        return this;
    }

    public View.OnTouchListener getDefaultTouchListener(){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return true;
            }
        };
    }

    public String getResString(int resId){
        return getContext().getResources().getString(resId);
    }
}
