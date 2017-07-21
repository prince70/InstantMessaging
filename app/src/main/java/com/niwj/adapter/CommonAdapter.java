package com.niwj.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;


import com.niwj.control.ImageUtil;
import com.niwj.instantmessaging.R;
import com.niwj.widget.AsyncImageLoader;
import com.niwj.widget.ProcessImageView;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author 龙盛
 *         万能的ListView GridView 适配器
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mItemLayoutId;

    public CommonAdapter(Context context, List<T> datas, int itemLayoutId) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mDatas = datas;
        this.mItemLayoutId = itemLayoutId;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(convertView, parent);
        convert(viewHolder, getItem(position), position);
        return viewHolder.getConvertView();
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 具体实现数据填充的方法
     */
    protected abstract void convert(ViewHolder holder, T item, int position);

    private ViewHolder getViewHolder(View convertView, ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId);
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public static class ViewHolder {
        private final SparseArray<View> mViews;
        private View mConvertView;

        /**
         * 私有类型
         */
        private ViewHolder(Context context, ViewGroup parent, int layoutId) {
            this.mViews = new SparseArray<>();
            mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            //setTag
            mConvertView.setTag(this);
        }

        /**
         * 拿到一个ViewHolder对象
         */
        public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId) {
            if (convertView == null) {
                return new ViewHolder(context, parent, layoutId);
            }
            return (ViewHolder) convertView.getTag();
        }

        /**
         * 通过控件的id获取对应的控件，如果没有则加入views
         */
        public <T extends View> T getView(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = mConvertView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

        public View getConvertView() {
            return mConvertView;
        }

        /**
         * 为TextView设置文字
         */
        public ViewHolder setText(int viewId, String text) {
            TextView view = getView(viewId);
            view.setText(text);
            return this;
        }

        /**
         * 为TextView设置字体颜色
         */
        public ViewHolder setTextColor(int viewId, int color) {
            TextView view = getView(viewId);
            view.setTextColor(color);
            return this;
        }

        /**
         * 为ImageView设置图片
         */
        public ViewHolder setImageResource(int viewId, int drawbleId) {
            ImageView view = getView(viewId);
            view.setImageResource(drawbleId);
            return this;
        }

        /**
         * 为ProcessImageView设置图片
         */
        public ViewHolder setPImageResource(int viewId, int drawbleId) {
            ProcessImageView view = getView(viewId);
            view.setImageResource(drawbleId);
            return this;
        }

        /**
         * 为ImageView设置图片
         */
        public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
            ImageView view = getView(viewId);
            view.setImageBitmap(bm);
            return this;
        }


        /**
         * 为ImageView设置图片
         */
        public ViewHolder setImageByNet(int viewId, String coverURL) {
            final ImageView view = getView(viewId);
            AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
            view.setTag(coverURL);
            Drawable cachedImage = asyncImageLoader.loadDrawable(coverURL, new AsyncImageLoader.ImageCallback() {
                public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                    view.setImageDrawable(imageDrawable);
                }
            });
            if (cachedImage == null) {
                view.setImageResource(R.mipmap.ic_fail);
            } else {
                view.setImageDrawable(cachedImage);
            }
            return this;
        }

        /**
         * 为ProcessImageView设置图片
         */
        public ViewHolder setPImageBitmap(int viewId, Bitmap bm) {
            ProcessImageView view = getView(viewId);
            view.setImageBitmap(bm);
            return this;
        }

        /**
         * 为ImageView设置图片
         */
        public ViewHolder setImagePath(int viewId, String path) {
            return setImagePath(viewId, path, 0);
        }

        /**
         * 为ImageView设置图片
         *
         * @param imgId 路径不存在时显示的图片
         */
        public ViewHolder setImagePath(int viewId, String path, int imgId) {
            ImageView view = getView(viewId);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                view.setImageBitmap(bitmap);
            } else if (imgId != 0) {
                view.setImageResource(imgId);
            }
            return this;
        }

        /**
         * 为ImageView设置图片
         *
         * @param imgId 路径不存在时显示的图片
         */
        public ViewHolder setPImagePath(int viewId, String path, int imgId) {
            ProcessImageView view = getView(viewId);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                view.setImageBitmap(bitmap);
            } else if (imgId != 0) {
                view.setImageResource(imgId);
            }
            return this;
        }

        /**
         * 为ImageView设置正方形压缩的图片
         *
         * @param imgId 路径不存在时显示的图片
         */
        public ViewHolder setSquareImagePath(int viewId, String path, int size, int imgId) {
            ImageView view = getView(viewId);
            //获取经过压缩的图片
            Bitmap bitmap = ImageUtil.compressImageFromFile(path, size, size);

            if (bitmap != null) {
                //图片裁剪得到中间部分
                bitmap = ImageUtil.centerSquareScaleBitmap(bitmap, size);
                view.setImageBitmap(bitmap);
            } else if (imgId != 0) {
                view.setImageResource(imgId);
            }
            return this;
        }

        /**
         * 为ProcessImageView设置正方形压缩的图片
         *
         * @param imgId 路径不存在时显示的图片
         */
        public ViewHolder setSquarePImagePath(int viewId, String path, int size, int imgId) {
            ProcessImageView view = getView(viewId);
            //获取经过压缩的图片
            Bitmap bitmap = ImageUtil.compressImageFromFile(path, size, size);

            if (bitmap != null) {
                //图片裁剪得到中间部分
                bitmap = ImageUtil.centerSquareScaleBitmap(bitmap, size);
                view.setImageBitmap(bitmap);
            } else if (imgId != 0) {
                view.setImageResource(imgId);
            }
            return this;
        }

        public ViewHolder setChecked(int viewId, boolean isChecked) {
            RadioButton radioButton = getView(viewId);
            if (radioButton != null) {
                radioButton.setChecked(isChecked);
            }
            return this;
        }

        /**
         * 设置背景色
         */
        public ViewHolder setBackgroundColor(int viewId, int color) {
            getView(viewId).setBackgroundColor(color);
            return this;
        }

        /**
         * 设置背景
         */
        public ViewHolder setBackground(int viewId, Drawable bg) {
            getView(viewId).setBackground(bg);
            return this;
        }

        /**
         * 为GridView设置适配器
         */
        public ViewHolder setGridViewAdapter(int viewId, CommonAdapter<String> adapter) {
            GridView gridView = getView(viewId);
//            setGridViewHeightBasedOnChildren(gridView, adapter);
            gridView.setAdapter(adapter);
            return this;
        }

        /**
         * 根据GridView的内容计算其高度
         *
         * @param colnum 列数
         */
        @SuppressLint("NewApi")
        public static void setGridViewHeight(GridView gridView, Adapter adapter, int colnum) {
            if (adapter.getCount() <= 0) return;

            //计算行数
            int row = (adapter.getCount() - 1) / colnum;
            row++;

            //计算子项的高度
            View itemView = adapter.getView(0, null, gridView);
            itemView.measure(0, 0);
            int itemHeight = itemView.getMeasuredWidth();

            //设置GridView的高度
            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            params.height = itemHeight * row + gridView.getHorizontalSpacing() * (row - 1);
            gridView.setLayoutParams(params);
        }

        /**
         * 计算gridview高度
         */
        public static void setGridViewHeightBasedOnChildren(GridView gridView, CommonAdapter listAdapter) {
            if (listAdapter == null) {
                return;
            }
            int rows;
            int columns = 0;
            int horizontalBorderHeight = 0;
            Class<?> clazz = gridView.getClass();
            try {
                // 利用反射，取得每行显示的个数
                Field column = clazz.getDeclaredField("mRequestedNumColumns");
                column.setAccessible(true);
                columns = (Integer) column.get(gridView);
                // 利用反射，取得横向分割线高度
                Field horizontalSpacing = clazz
                        .getDeclaredField("mRequestedHorizontalSpacing");
                horizontalSpacing.setAccessible(true);
                horizontalBorderHeight = (Integer) horizontalSpacing.get(gridView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 判断数据总数除以每行个数是否整除。不能整除代表有多余，需要加一行
            if (listAdapter.getCount() % columns > 0) {
                rows = listAdapter.getCount() / columns + 1;
            } else {
                rows = listAdapter.getCount() / columns;
            }
            int totalHeight = 0;
            for (int i = 0; i < rows; i++) { // 只计算每项高度*行数
                View listItem = listAdapter.getView(i, null, gridView);
                listItem.measure(0, 0); // 计算子项View 的宽高
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }
            totalHeight += (horizontalBorderHeight) * (rows - 1);
            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            params.height = totalHeight;
            gridView.setLayoutParams(params);
        }

        /**
         * 计算ListView的高度
         */
        public static void getTotalHeightofListView(ListView listView, Adapter adapter) {
            if (adapter == null) {
                return;
            }
            int totalHeight = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                View mView = adapter.getView(i, null, listView);
//                mView.measure(
//                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                mView.measure(0, 0);
                totalHeight += mView.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }
}