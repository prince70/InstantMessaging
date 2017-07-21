package com.niwj.instantmessaging;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "MainActivity";
    public static final String CREATE_GROUP_CUSTOM_KEY = "create_group_custom_key";
    public static final String SET_DOWNLOAD_PROGRESS = "set_download_progress";
    public static final String IS_DOWNLOAD_PROGRESS_EXISTS = "is_download_progress_exists";
    public static final String CUSTOM_MESSAGE_STRING = "custom_message_string";
    public static final String CUSTOM_FROM_NAME = "custom_from_name";
    public static final String DOWNLOAD_ORIGIN_IMAGE = "download_origin_image";
    public static final String DOWNLOAD_THUMBNAIL_IMAGE = "download_thumbnail_image";
    public static final String IS_UPLOAD = "is_upload";
    public static final String LOGOUT_REASON = "logout_reason";
    private TextView mTv_showOfflineMsg;
    private TextView tv_refreshEvent;
    public static final String DOWNLOAD_INFO = "download_info";
    public static final String INFO_UPDATE = "info_update";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        JMessageClient.registerEventReceiver(this);
        initView();
    }
    private void initView() {
        setContentView(R.layout.activity_type);
        Button bt_type = (Button) findViewById(R.id.bt_about_setting);
        Button bt_createMessage = (Button) findViewById(R.id.bt_create_message);
        Button bt_groupInfo = (Button) findViewById(R.id.bt_group_info);
        Button bt_conversation = (Button) findViewById(R.id.bt_conversation);
        Button bt_friend = (Button) findViewById(R.id.bt_friend);

        mTv_showOfflineMsg = (TextView) findViewById(R.id.tv_showOfflineMsg);
        tv_refreshEvent = (TextView) findViewById(R.id.tv_refreshEvent);

        bt_type.setOnClickListener(this);
        bt_createMessage.setOnClickListener(this);
        bt_groupInfo.setOnClickListener(this);
        bt_conversation.setOnClickListener(this);
        bt_friend.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            /**
             * 设置相关
             */
            case R.id.bt_about_setting:
//                intent.setClass(getApplicationContext(), SettingMainActivity.class);
//                startActivityForResult(intent, 0);
                break;
            /**
             *创建消息
             */
            case R.id.bt_create_message:
//                intent.setClass(getApplicationContext(), CreateMessageActivity.class);
//                startActivity(intent);
                break;
            /**
             *会话
             */
            case R.id.bt_group_info:
//                intent.setClass(getApplicationContext(), GroupInfoActivity.class);
//                startActivity(intent);
                break;
            /**
             *群组
             */
            case R.id.bt_conversation:
//                intent.setClass(getApplicationContext(), ConversationActivity.class);
//                startActivity(intent);
                break;
            /**
             *好友相关
             */
            case R.id.bt_friend:
//                intent.setClass(getApplicationContext(), FriendContactManager.class);
//                startActivity(intent);
                break;
            default:
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 8) {
            mTv_showOfflineMsg.setText("");
            tv_refreshEvent.setText("");
        }
    }
    /**
     * 通知栏点击事件。如果上层使用了sdk提供的通知栏消息，则当点击通知栏时，sdk将会发送此事件给上层，
     * 上层通过onEvent方法接收事件，可以自定义点击通知栏之后的跳转。
     *
     * @param event
     */
    public void onEvent(NotificationClickEvent event) {
//        toActivity(getBaseContext());
        Log.e(TAG, "onEvent: " + "调用了onEvent方法");

        /**
         * 弹窗的消息，点击后跳转
         */

        final cn.jpush.im.android.api.model.Message msg = event.getMessage();
        final Intent notificationIntent = new Intent(this, ChatActivity.class);
        MessageContent content = msg.getContent();
        Log.e(TAG, "onEvent: " + msg);
        Log.e(TAG, "onEvent: " + content);
        Log.e(TAG, "onEvent: " + msg.getContentType());
        switch (msg.getContentType()) {
            case text:

                Log.e("收到的信息是文本信息", "--------------------");
                TextContent textContent = (TextContent) content;
                notificationIntent.setFlags(1);//flag为1 文本
                Bundle bundle = new Bundle();
                bundle.putInt("type", 3);
                bundle.putString("Txt_content", textContent.getText());
                bundle.putString("Txt_tousername", msg.getFromUser().getNickname());
                bundle.putLong("Txt_time", msg.getCreateTime());//消息创建的时间
                notificationIntent.putExtras(bundle);
                startActivity(notificationIntent);

                break;
            case image:
                final Intent intentImage = new Intent(getApplicationContext(), ChatActivity.class);
                final ImageContent imageContent = (ImageContent) msg.getContent();
                //其实sdk是会自动下载缩略图的.本方法是当sdk自动下载失败时可以手动调用进行下载而设计的.同理于语音下载
                /**=================     下载图片信息中的缩略图    =================*/
                imageContent.downloadThumbnailImage(msg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int i, String s, File file) {
                        if (i == 0) {
//                            Toast.makeText(getApplicationContext(), "下载缩略图成功", Toast.LENGTH_SHORT).show();
                            intentImage.putExtra("Img_tousername",msg.getFromUser().getNickname());
                            intentImage.putExtra("Img_time",msg.getCreateTime());
                            intentImage.putExtra(DOWNLOAD_THUMBNAIL_IMAGE, file.getPath());
                        } else {
//                            Toast.makeText(getApplicationContext(), "下载原图失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "downloadThumbnailImage" + ", responseCode = " + i + " ; desc = " + s);
                        }
                    }
                });

                /**=================     下载图片消息中的原图    =================*/
                imageContent.downloadOriginImage(msg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int i, String s, File file) {
                        if (i == 0) {
//                            Toast.makeText(getApplicationContext(), "下载原图成功", Toast.LENGTH_SHORT).show();
                            intentImage.putExtra(IS_UPLOAD, imageContent.isFileUploaded() + "");
                            intentImage.putExtra(DOWNLOAD_ORIGIN_IMAGE, file.getPath());
                            startActivity(intentImage);
                        } else {
//                            Toast.makeText(getApplicationContext(), "下载原图失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "downloadOriginImage" + ", responseCode = " + i + " ; desc = " + s);
                        }
                    }
                });
                break;
            case voice:
                final Intent intentVoice = new Intent(getApplicationContext(), ChatActivity.class);
                final VoiceContent voiceContent = (VoiceContent) msg.getContent();
                final int duration = voiceContent.getDuration();//音频时长
                final String format = voiceContent.getFormat();//音频格式
                /**=================     下载语音文件    =================*/
                voiceContent.downloadVoiceFile(msg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int i, String s, File file) {
                        if (i == 0) {
//                            Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();
                            /**
                             * 成功接收音频显示的信息
                             */
                            intentVoice.putExtra(DOWNLOAD_INFO, "path = " + file.getPath() + "\n" + "duration = " + duration + "\n" + "format = " + format + "\n");
                            intentVoice.putExtra("voice",file.getPath());
                            intentVoice.putExtra("Voice_tousername",msg.getFromUser().getNickname());
                            intentVoice.putExtra("Voice_time",msg.getCreateTime());
                            intentVoice.putExtra("duration",duration);
                            intentVoice.putExtra("format",format);
                            startActivity(intentVoice);
                        } else {
//                            Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "downloadVoiceFile" + ", responseCode = " + i + " ; desc = " + s);
                        }
                    }
                });
                break;
            case file:
                UserInfo fromUser = msg.getFromUser();
                String userName = fromUser.getUserName();
                String appKey = fromUser.getAppKey();
                ConversationType targetType = msg.getTargetType();

                int id = msg.getId();

                notificationIntent.putExtra("user", userName);
                notificationIntent.putExtra("appkey", appKey);
                notificationIntent.putExtra("msgid", id);
                notificationIntent.putExtra("isGroup", targetType + "");
                notificationIntent.setFlags(10);

                startActivity(notificationIntent);
                break;

            case location:
                LocationContent locationContent = (LocationContent) content;
                String address = locationContent.getAddress();
                Number latitude = locationContent.getLatitude();
                Number scale = locationContent.getScale();
                Number longitude = locationContent.getLongitude();

                String la = String.valueOf(latitude);
                String sc = String.valueOf(scale);
                String lo = String.valueOf(longitude);

                notificationIntent.setFlags(4);
                notificationIntent.putExtra("address", address);
                notificationIntent.putExtra("latitude", la);
                notificationIntent.putExtra("scale", sc);
                notificationIntent.putExtra("longitude", lo);

                startActivity(notificationIntent);
                break;

            default:
                break;
        }
    }

    /**
     * 离线消息事件
     *
     * @param event
     */
    public void onEventMainThread(OfflineMessageEvent event) {
        Conversation conversation = event.getConversation();
        List<cn.jpush.im.android.api.model.Message> newMessageList = event.getOfflineMessageList();//获取此次离线期间会话收到的新消息列表
        List<Integer> offlineMsgIdList = new ArrayList<>();
        if (conversation != null && newMessageList != null) {
            for (cn.jpush.im.android.api.model.Message msg : newMessageList) {
                offlineMsgIdList.add(msg.getId());
            }
            mTv_showOfflineMsg.append(String.format(Locale.SIMPLIFIED_CHINESE, "收到%d条来自%s的离线消息。\n", newMessageList.size(), conversation.getTargetId()));
            mTv_showOfflineMsg.append("会话类型 = " + conversation.getType() + "\n消息ID = " + offlineMsgIdList + "\n\n");
        } else {
            mTv_showOfflineMsg.setText("conversation is null or new message list is null");
        }
    }

    /**
     * 会话刷新事件. 当会话的相关信息被sdk更新时，sdk会上抛此事件通知上层，上层需要更新已拿到的会话对象。或者刷新UI
     *
     * @param event
     */
    public void onEventMainThread(ConversationRefreshEvent event) {
        Conversation conversation = event.getConversation();
        ConversationRefreshEvent.Reason reason = event.getReason();
        if (conversation != null) {
            tv_refreshEvent.append(String.format(Locale.SIMPLIFIED_CHINESE, "收到ConversationRefreshEvent事件,待刷新的会话是%s.\n", conversation.getTargetId()));
            tv_refreshEvent.append("事件发生的原因 : " + reason);
        } else {
            tv_refreshEvent.setText("conversation is null");
        }
    }

}

