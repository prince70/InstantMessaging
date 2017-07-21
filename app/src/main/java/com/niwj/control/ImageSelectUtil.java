package com.niwj.control;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


import com.niwj.imageselector.ImageConfig;
import com.niwj.imageselector.ImageSelector;
import com.niwj.imageselector.ImageSelectorActivity;
import com.niwj.instantmessaging.R;
import com.yalantis.ucrop.UCrop;

import java.util.ArrayList;
import java.util.List;

/**
 * 照片选择器的使用
 * @author wenbo
 */
public class ImageSelectUtil {

    private static final String TAG = "ImageSelectUtil";

    private String tempIconPath;    //选择的图片的路径
    private Activity activity;
    private String path;            //图片的保存路径
    private int imageSize;          //图片的边长
    private boolean isCrop = true;  //是否裁剪图片
    private SelectedFinishListener mListener;

    public ImageSelectUtil(Activity activity, int imageSize) {
        this(activity, "", imageSize);
        this.isCrop = false;
    }

    public ImageSelectUtil(Activity activity, String path, int imageSize) {
        this.activity = activity;
        this.path = (path == null ? "" : path);
        this.imageSize = DisplayUtil.dp2px(activity, imageSize);
    }

    /**
     * 打开图片选择器，返回路径的集合
     * @param activity
     * @param maxImageNum
     */
    public static ArrayList<String> selectorImage(Activity activity, int maxImageNum, ArrayList<String> listHasSelected) {

        if (null == listHasSelected) listHasSelected = new ArrayList<>();

        ImageConfig imageSelectorConfig = new ImageConfig.Builder(activity
                // ImageUniversalLoader 可用自己用的缓存库
                , new ImageUniversalLoader())
                // 如果在 4.4 以上，则修改状态栏颜色 （默认黑色）
                .steepToolBarColor(activity.getResources().getColor(R.color.themeColor))
                // 标题的背景颜色 （默认黑色）
                .titleBgColor(activity.getResources().getColor(R.color.themeColor))
                // 提交按钮字体的颜色  （默认白色）
                .titleSubmitTextColor(activity.getResources().getColor(R.color.white))
                // 标题颜色 （默认白色）
                .titleTextColor(activity.getResources().getColor(R.color.white))
                // 开启多选   （默认为多选）  (单选 为 singleSelect)
                .mutiSelect()
                // 多选时的最大数量   （默认 9 张）
                .mutiSelectMaxSize(maxImageNum)
                // 已选择的图片路径
                .pathList(listHasSelected)
                // 拍照后存放的图片路径（默认 /temp/picture）
                .filePath("/DCIM")
                // 开启拍照功能 （默认开启）
                .showCamera()
                .build();

        ImageSelector.open(imageSelectorConfig);   // 开启图片选择器
        return listHasSelected;
    }

    /**
     * 设置是否裁剪图片
     */
    public void setCrop(boolean crop) {
        isCrop = crop;
    }

    /**
     * 设置单选的路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 单选图片
     */
    public static void singleSelectImage(Activity activity){
        ImageConfig imageSelectorConfig = new ImageConfig.Builder(activity
                // ImageUniversalLoader 可用自己用的缓存库
                , new ImageUniversalLoader())
                // 如果在 4.4 以上，则修改状态栏颜色 （默认黑色）
                .steepToolBarColor(activity.getResources().getColor(R.color.text_gray))
                // 标题的背景颜色 （默认黑色）
                .titleBgColor(activity.getResources().getColor(R.color.text_gray))
                // 提交按钮字体的颜色  （默认白色）
                .titleSubmitTextColor(activity.getResources().getColor(R.color.transparent))
                // 标题颜色 （默认白色）
                .titleTextColor(activity.getResources().getColor(R.color.white))
                // 开启多选   （默认为多选）  (单选 为 singleSelect)
                .singleSelect()
                .showCamera()
                .build();

        ImageSelector.open(imageSelectorConfig);   // 开启图片选择器
    }

    public void setSelectFinishListener(SelectedFinishListener l){
        mListener = l;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (path == null || path.isEmpty()) return;

        //选择图片
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            ArrayList<String> list = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            if (!list.isEmpty()) {
                if (isCrop) {
                    //裁剪图片
                    tempIconPath = list.get(0);
                    Uri uri = Uri.parse("file://" + list.get(0));
                    ImageUtil.cropImg(activity, uri, Uri.parse("file://" + path), imageSize, imageSize);
                }else {
                    if (mListener != null){
                        mListener.onSelectedFinish(list);
                    }
                }
            }
        }
        //裁剪图片成功
        else if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            //图片裁剪完成
            if (mListener != null){
                ArrayList<String> paths = new ArrayList<>();
                paths.add(path);
                mListener.onSelectedFinish(paths);
            }
        }
        //裁剪失败
        else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            Log.e(TAG, cropError.getMessage());
        }
    }


    /**
     * 用药提醒添加药物
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResultForMedicine(int requestCode, int resultCode, Intent data){
        //选择头像图片
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            List<String> list = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            tempIconPath = list.get(0);
            if (mListener != null){
                ArrayList<String> paths = new ArrayList<>();
                paths.add(tempIconPath);
                mListener.onSelectedFinish(paths);
            }
        }
    }

    public interface SelectedFinishListener {
        /**
         * @param paths 图片的路径
         */
        void onSelectedFinish(ArrayList<String> paths);
    }
}
