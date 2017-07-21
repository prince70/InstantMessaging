package com.niwj.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.niwj.entity.ChatAtName;
import com.niwj.instantmessaging.R;
import com.squareup.picasso.Picasso;


import org.kymjs.kjframe.widget.RoundImageView;

import java.util.List;

/**
 * @群聊成员 Created by 伟金 on 2017/7/20.
 */

public class AtNameAdapter extends BaseAdapter {

    private Context mContext;
    private List<ChatAtName> mList;

    public AtNameAdapter(List<ChatAtName> mList, Context context) {
        this.mList = mList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.chat_item_atname, null);
            viewHolder.imageView = (RoundImageView) convertView.findViewById(R.id.chat_item__atname_avatar);
            viewHolder.name = (TextView) convertView.findViewById(R.id.chat_item_atname);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(mContext).load(mList.get(position).getImageId()).into(viewHolder.imageView);
        viewHolder.name.setText(mList.get(position).getNickname());
        return convertView;
    }

    class ViewHolder {
        RoundImageView imageView;
        TextView name;
    }
}
