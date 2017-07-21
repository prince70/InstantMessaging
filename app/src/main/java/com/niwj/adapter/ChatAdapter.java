/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.niwj.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.niwj.control.SoundRecordUtil;
import com.niwj.control.UrlUtils;
import com.niwj.entity.Message;
import com.niwj.instantmessaging.ChatActivity;
import com.niwj.instantmessaging.R;
import com.niwj.widget.BigPicPopWindow;
import com.squareup.picasso.Picasso;


import org.kymjs.kjframe.KJBitmap;

import java.util.ArrayList;
import java.util.List;


/**
 * @author
 */
public class ChatAdapter extends BaseAdapter {
    private static final String TAG = "ChatAdapter";

    private final Context cxt;
    private List<Message> datas = null;
    private KJBitmap kjb;
    private ChatActivity.OnChatItemClickListener listener;
    private Typeface emojitf;

    public ChatAdapter(Context cxt, List<Message> datas, ChatActivity.OnChatItemClickListener listener) {
        this.cxt = cxt;
        if (datas == null) {
            datas = new ArrayList<Message>(0);
        }
        this.datas = datas;
        kjb = new KJBitmap();
        this.listener = listener;
        if (!"smartisan".equals(Build.MANUFACTURER)) {
            try {
                emojitf = Typeface.createFromAsset(cxt.getAssets(), "fonts/emoji.ttf");
            } catch (Exception e) {
            }
        }
    }

    public void refresh(List<Message> datas) {
        if (datas == null) {
            datas = new ArrayList<Message>(0);
        }
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).getIsSend() ? 1 : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        final ViewHolder holder;
        final Message data = datas.get(position);
        if (v == null) {
            holder = new ViewHolder();
            if (data.getIsSend()) {
                v = View.inflate(cxt, R.layout.chat_item_list_right, null);
            } else {
                v = View.inflate(cxt, R.layout.chat_item_list_left, null);
            }
            holder.layout_content = (RelativeLayout) v.findViewById(R.id.chat_item_layout_content);
            holder.img_avatar = (ImageView) v.findViewById(R.id.chat_item_avatar);
            holder.img_chatimage = (ImageView) v.findViewById(R.id.chat_item_content_image);
            holder.img_sendfail = (ImageView) v.findViewById(R.id.chat_item_fail);
            holder.progress = (ProgressBar) v.findViewById(R.id.chat_item_progress);
            holder.tv_chatcontent = (TextView) v.findViewById(R.id.chat_item_content_text);
            holder.tv_date = (TextView) v.findViewById(R.id.chat_item_date);
            holder.toUserName = (TextView) v.findViewById(R.id.toUserName);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        /**
         * 接收的人的名字
         */
        if (data.getIsSend()) {

        } else {
            holder.toUserName.setText(datas.get(position).getToUserName());  //先保存到本地
        }



        holder.tv_date.setText(datas.get(position).getTime());
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            Date date = df.parse(datas.get(position).getTime());
//            long time = date.getTime();
//            Log.e(TAG, "getView: 时间"+time);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


        holder.tv_date.setVisibility(View.VISIBLE);

        if (emojitf != null) {
            holder.tv_chatcontent.setTypeface(emojitf);
        }


        //如果是文本类型，则隐藏图片，如果是图片则隐藏文本
        if (data.getType() == Message.MSG_TYPE_TEXT) {
            holder.img_chatimage.setVisibility(View.GONE);
            holder.tv_chatcontent.setVisibility(View.VISIBLE);
            if (data.getContent().contains("href")) {
                holder.tv_chatcontent = UrlUtils.handleHtmlText(holder.tv_chatcontent, data
                        .getContent());  //HTML语言
            } else {
                holder.tv_chatcontent = UrlUtils.handleText(holder.tv_chatcontent, data
                        .getContent());
            }
        } else {
            holder.tv_chatcontent.setVisibility(View.GONE);
            holder.img_chatimage.setVisibility(View.VISIBLE);

            //如果内存缓存中有要显示的图片，且要显示的图片不是holder复用的图片，则什么也不做，否则显示一张加载中的图片
            if (kjb.getMemoryCache(data.getContent()) != null && data.getContent() != null &&
                    data.getContent().equals(holder.img_chatimage.getTag())) {
            } else {
                Picasso.with(cxt).load(R.mipmap.default_head).into(holder.img_chatimage);
            }
            kjb.display(holder.img_chatimage, data.getContent(), 300, 300);
        }

        //如果是表情或图片，则不显示气泡，如果是图片则显示气泡
        if (data.getType() != Message.MSG_TYPE_TEXT && data.getType() != Message.MSG_TYPE_VOICE) {
            holder.layout_content.setBackgroundResource(android.R.color.transparent);
        } else {
            if (data.getIsSend()) {
                holder.layout_content.setBackgroundResource(R.drawable.chat_to_bg_selector);
            } else {
                holder.layout_content.setBackgroundResource(R.drawable.chat_from_bg_selector);
            }
        }


        /**
         * 如果是语音类型，则点击播放
         */
        if (data.getType() == Message.MSG_TYPE_VOICE) {
            holder.img_chatimage.setVisibility(View.GONE);
            holder.tv_chatcontent.setVisibility(View.VISIBLE);
            int time = SoundRecordUtil.getRecordTime(cxt, data.getContent()) / 1000;  //每次都要new一个对象，才不会重复
            holder.tv_chatcontent.setText("语音消息  " + formatTime(time));
            final String voicepath = data.getContent();
            holder.tv_chatcontent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.tv_chatcontent.setText("正在播放");
                    SoundRecordUtil.startPlay(voicepath, new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            int time = SoundRecordUtil.getRecordTime(cxt, data.getContent()) / 1000;  //每次都要new一个对象，才不会重复
                            holder.tv_chatcontent.setText("语音消息  " + formatTime(time));
                        }
                    });
                }
            });


        }


        //显示头像
        if (data.getIsSend()) {
//            kjb.display(holder.img_avatar, data.getFromUserAvatar()); //本人的头像
            Picasso.with(cxt).load(R.mipmap.profile_test).into(holder.img_avatar);

        } else {
//            kjb.display(holder.img_avatar, data.getToUserAvatar());  //返回信息的人的头像
            Picasso.with(cxt).load(R.mipmap.ic_user_header_img_example).into(holder.img_avatar);
        }

        if (listener != null) {
            holder.img_chatimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (data.getType()) {
                        case Message.MSG_TYPE_PHOTO:
//                            listener.onPhotoClick(position);
//                            点击打开大图
                            new BigPicPopWindow(cxt, data.getContent()).show();
                            break;
                        case Message.MSG_TYPE_FACE:
                            listener.onFaceClick(position);
                            break;
                    }
                }
            });
        }

        //消息发送的状态
        switch (data.getState()) {
            case Message.MSG_STATE_FAIL:  //失败
                holder.progress.setVisibility(View.GONE);
                holder.img_sendfail.setVisibility(View.VISIBLE);
                break;
            case Message.MSG_STATE_SUCCESS:   //成功
                holder.progress.setVisibility(View.GONE);
                holder.img_sendfail.setVisibility(View.GONE);
                break;
            case Message.MSG_STATE_SENDING:    //发送中
                holder.progress.setVisibility(View.VISIBLE);
                holder.img_sendfail.setVisibility(View.GONE);
                break;
        }
        return v;
    }

    static class ViewHolder {
        TextView toUserName;
        TextView tv_date;
        ImageView img_avatar;
        TextView tv_chatcontent;
        ImageView img_chatimage;
        ImageView img_sendfail;
        ProgressBar progress;
        RelativeLayout layout_content;
    }

    /**
     * 时间格式化
     */
    private String formatTime(int second) {
        if (second < 60) {
            return second + "秒";
        }
        return second / 60 + "分" + (second % 60 != 0 ? second % 60 + "秒" : "");
    }
}
