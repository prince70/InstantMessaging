package com.niwj.control;

import android.content.Context;
import android.widget.ImageView;

import com.niwj.imageselector.ImageLoader;
import com.niwj.instantmessaging.R;
import com.squareup.picasso.Picasso;


import java.io.File;

/**
 * ImageUniversalLoader
 * Created by Yancy on 2015/12/6.
 */
public class ImageUniversalLoader implements ImageLoader {

    private static ImageUniversalLoader imageUniversalLoader;

    public static ImageUniversalLoader getInstance() {
        if (imageUniversalLoader == null) {
            imageUniversalLoader = new ImageUniversalLoader();
        }
        return imageUniversalLoader;
    }

    /**
     * 从本地path加载图片资源
     *
     * @param context
     * @param path
     * @param imageView
     */
    @Override
    public void displayImage(Context context, String path, ImageView imageView) {
        File file = new File(path);
        Picasso.with(context)
                .load(file)
                .transform(new CropSquareTransformation())
                .error(R.mipmap.ic_user_header_img_example)
                .into(imageView);

    }

    /**
     * 从网络url加载图片资源
     *
     * @param context
     * @param url
     * @param imageView
     */
    public void displayImageWeb(Context context, String url, ImageView imageView) {

        Picasso.with(context)
                .load(url).error(R.mipmap.ic_user_header_img_example)
                .into(imageView);
    }
}