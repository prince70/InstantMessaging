/*
 *
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
package com.niwj.instantmessaging;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;


import com.niwj.adapter.ChatAdapter;
import com.niwj.control.ImageSelectUtil;
import com.niwj.control.KJChatKeyboard;
import com.niwj.entity.Message;
import com.niwj.entity.SessionMessage;
import com.niwj.imageselector.ImageSelector;
import com.niwj.imageselector.ImageSelectorActivity;
import com.niwj.others.DisplayRules;
import com.niwj.others.Emojicon;
import com.niwj.others.Faceicon;
import com.niwj.others.OnOperationListener;
import com.niwj.widget.Header;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.KJLoger;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

import static com.niwj.control.KJChatKeyboard.AtName;
import static com.niwj.instantmessaging.MainActivity.DOWNLOAD_ORIGIN_IMAGE;


/**
 * 聊天主界面
 * <p>
 */
public class ChatActivity extends KJActivity {
    public static final String GroupId = "10122916";//群组ID
    public static final String UserAtAppKey = "1567c50559b34beabfecf300";//用户跨应用
    private Conversation mConversation;
    private static final String TAG = "ChatActivity";
    private boolean sendTxtState;

    public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0x1;

    private Header header;
    private KJChatKeyboard box;
    private ListView mRealListView;
    private String picturePath;
    private SessionMessage sessionMessage;//会话记录

    List<Message> datas = new ArrayList<Message>();
    private ChatAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        DataSupport.deleteAll(SessionMessage.class);//删除

        /**
         * 语音
         */
        String voice = getIntent().getStringExtra("voice");
        String voice_tousername = getIntent().getStringExtra("Voice_tousername");
        long voice_time = getIntent().getLongExtra("Voice_time", 0);
        String voiceCreateTime = transferLongToDate("yyyy-MM-dd HH:mm:ss", voice_time);
        Log.e(TAG, "onCreate: 语音" + voice + "  " + voice_tousername + "    " + voiceCreateTime);
        if (voice != null && voice_tousername != null && voiceCreateTime != null) {
            sessionMessage = new SessionMessage();
            sessionMessage.setType(Message.MSG_TYPE_VOICE);//消息类型
            sessionMessage.setContent(voice);//消息内容
            sessionMessage.setToUserName(voice_tousername);
            sessionMessage.setSend(false);//是否为发送方
            sessionMessage.setTime(voiceCreateTime);//发送时间
            sessionMessage.save();
        }


        /**
         * 图片
         */
        String picPath = getIntent().getStringExtra(DOWNLOAD_ORIGIN_IMAGE);//消息内容  图片路径
        String img_tousername = getIntent().getStringExtra("Img_tousername");
        long img_time = getIntent().getLongExtra("Img_time", 0);
        String ImgCreateTime = transferLongToDate("yyyy-MM-dd HH:mm:ss", img_time);//接收时间
        Log.e(TAG, "onCreate: 图片" + picPath + "  " + img_tousername + "    " + ImgCreateTime);
        if (picPath != null && img_tousername != null && ImgCreateTime != null) {
            sessionMessage = new SessionMessage();
            sessionMessage.setType(Message.MSG_TYPE_PHOTO);//消息类型
            sessionMessage.setContent(picPath);//消息内容
            sessionMessage.setToUserName(img_tousername);
            sessionMessage.setSend(false);//是否为发送方
            sessionMessage.setTime(ImgCreateTime);//发送时间
            sessionMessage.save();
        }

        /**
         * 文本
         */
        String content = getIntent().getStringExtra("Txt_content");//消息内容  文本消息
        String tousername = getIntent().getStringExtra("Txt_tousername");//来自谁
        long time = getIntent().getLongExtra("Txt_time", 0);
        String msgCreateTime = transferLongToDate("yyyy-MM-dd HH:mm:ss", time);//接收时间
        Log.e(TAG, "initMessageInputToolBox: 接收到的值" + "\n" + "   " + content + "   " + tousername + "   " + time + "     " + msgCreateTime + "   ");
        if (content != null && tousername != null && msgCreateTime != null) {
            sessionMessage = new SessionMessage();
            sessionMessage.setType(Message.MSG_TYPE_TEXT);//消息类型
            sessionMessage.setContent(content);//消息内容
            sessionMessage.setToUserName(tousername);//谁发过来的
            sessionMessage.setSend(false);//是否为发送方
            sessionMessage.setTime(msgCreateTime);//发送时间
            sessionMessage.save();
        }

        /**
         * 接收消息是onCreate
         */

        List<SessionMessage> all = DataSupport.findAll(SessionMessage.class);//查找
        for (SessionMessage s : all) {
            Log.e(TAG, "onCreate: 数据库列表" + s.toString());
            String sqlcontent = s.getContent();
            String sqltime = s.getTime();
            int sqltype = s.getType();
            boolean sqlsend = s.isSend();
            String sqltoUserName = s.getToUserName();
            Message message = new Message(sqltype, Message.MSG_STATE_SUCCESS,
                    "Tom", "avatar", sqltoUserName,
                    "avatar", sqlcontent, sqlsend, true, sqltime);
            datas.add(message);
            adapter.refresh(datas);
            //自动跳转到最后一条信息
            mRealListView.post(new Runnable() {
                @Override
                public void run() {
                    mRealListView.setSelection(adapter.getCount() - 1);
                }
            });
        }


    }

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_chat);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        box = (KJChatKeyboard) findViewById(R.id.chat_msg_input_box);
        mRealListView = (ListView) findViewById(R.id.chat_listview);
        header = (Header) findViewById(R.id.header);
        mRealListView.setSelector(android.R.color.transparent);
        /**
         * 初始化数据
         */
        initMessageInputToolBox();
        initListView();
    }

    private void initMessageInputToolBox() {


        box.setOnOperationListener(new OnOperationListener() {
            @Override
            //发送
            public void send(String content) {
                Log.e(TAG, "send: 传过来的值"+AtName);
                if (!AtName.isEmpty()) {
                    CreateGroupTextMsg(GroupId, content, UserAtAppKey, AtName);
                } else {
                    //   String GroupId, String content, String appKey, String AtName
                    CreateGroupTextMsg(GroupId, content, null, null);//
                }

                Message message = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS,
                        "Tom", "avatar", "Jerry",
                        "avatar", content, true, true, tranferDate("yyyy-MM-dd HH:mm:ss", new Date()));
                datas.add(message);
                adapter.refresh(datas);
                Log.e("NWJ", "send:文本聊天记录    " + content);
                sessionMessage = new SessionMessage();
                sessionMessage.setType(Message.MSG_TYPE_TEXT);//消息类型
                sessionMessage.setContent(content);//消息内容
                sessionMessage.setSend(true);//是否为发送方
                sessionMessage.setTime(tranferDate("yyyy-MM-dd HH:mm:ss", new Date()));//发送时间
                sessionMessage.save();

//                createReplayMsg(message);
            }

            @Override
            public void sendVoice(String path) {
                CreateGroupVoiceMsg(GroupId, path);
                Message message = new Message(Message.MSG_TYPE_VOICE, Message.MSG_STATE_SUCCESS,
                        "Tom", "avatar", "Jerry",
                        "avatar", path, true, true, tranferDate("yyyy-MM-dd HH:mm:ss", new Date()));
                datas.add(message);
                adapter.refresh(datas);
                Log.e("NWJ", "sendVoice: 语音聊天记录" + path);
                sessionMessage = new SessionMessage();
                sessionMessage.setType(Message.MSG_TYPE_VOICE);//消息类型
                sessionMessage.setContent(path);//消息内容
                sessionMessage.setSend(true);//是否为发送方
                sessionMessage.setTime(tranferDate("yyyy-MM-dd HH:mm:ss", new Date()));//发送时间
                sessionMessage.save();
//                createReplayMsg(message);
            }

            @Override
            public void selectedFace(Faceicon content) {
                Message message = new Message(Message.MSG_TYPE_FACE, Message.MSG_STATE_SUCCESS,
                        "Tom", "avatar", "Jerry", "avatar", content.getPath(), true, true,
                        tranferDate("yyyy-MM-dd HH:mm:ss", new Date()));
                datas.add(message);
                adapter.refresh(datas);
//                createReplayMsg(message);
            }

            @Override
            public void selectedEmoji(Emojicon emoji) {
                box.getEditTextBox().append(emoji.getValue());
            }

            @Override
            public void selectedBackSpace(Emojicon back) {
                DisplayRules.backspace(box.getEditTextBox());
            }

            @Override
            public void selectedFunction(int index) {
                switch (index) {
                    case 0:
                        goToAlbum();
                        break;
                    case 1:
                        //打开相机
                        ImageSelectUtil.selectorImage(ChatActivity.this, 9, null);
                        break;
                }
            }
        });

        List<String> faceCagegory = new ArrayList<>();
//        File faceList = FileUtils.getSaveFolder("chat");
        File faceList = new File("");
        if (faceList.isDirectory()) {
            File[] faceFolderArray = faceList.listFiles();
            for (File folder : faceFolderArray) {
                if (!folder.isHidden()) {
                    faceCagegory.add(folder.getAbsolutePath());
                }
            }
        }

        box.setFaceData(faceCagegory);
        mRealListView.setOnTouchListener(getOnTouchListener());
    }

    private void initListView() {
        /**
         * 打开聊天，从数据库中加载聊天记录  不同类型
         */


        byte[] emoji = new byte[]{
                (byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x81
        };
        //倒数第三个参数区别是谁发送的信息
        Message message = new Message(Message.MSG_TYPE_TEXT,
                Message.MSG_STATE_SUCCESS, "\ue415", "avatar", "倪医生", "avatar",
                new String(emoji), false, true, tranferDate("yyyy-MM-dd HH:mm:ss", new Date()));   //初始化的接收表情
        Message message4 = new Message(Message.MSG_TYPE_TEXT,
                Message.MSG_STATE_SUCCESS, "Tom", "avatar", "倪医生", "avatar",
                "你好,这里是家庭医生服务团队,请问有什么可以帮到您的?",
                false, true, tranferDate("yyyy-MM-dd HH:mm:ss", new Date()));


        datas.add(message);
        datas.add(message4);

        adapter = new ChatAdapter(this, datas, getOnChatItemClickListener());
        mRealListView.setAdapter(adapter);
    }

    /**
     * 发送什么 返回什么
     *
     * @param message
     */
    private void createReplayMsg(Message message) {
        final Message reMessage = new Message(message.getType(), Message.MSG_STATE_SUCCESS, "Tom",
                "avatar", "Jerry", "avatar", message.getType() == Message.MSG_TYPE_TEXT ? "返回:"
                + message.getContent() : message.getContent(), false,
                true, tranferDate("yyyy-MM-dd HH:mm:ss", new Date()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    Thread.sleep(1000 * (new Random().nextInt(3) + 1));//随机睡眠1~4秒钟
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            datas.add(reMessage); //返回信息
                            adapter.refresh(datas); //刷新布局
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && box.isShow()) {
            box.hideLayout();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 跳转到选择相册界面
     */
    private void goToAlbum() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {  //判断sdk版本
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    REQUEST_CODE_GETIMAGE_BYSDCARD);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    REQUEST_CODE_GETIMAGE_BYSDCARD);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        /**
         * 图片
         */
        if (requestCode == REQUEST_CODE_GETIMAGE_BYSDCARD) {
            Uri dataUri = data.getData();
            if (dataUri != null) {
                File file = FileUtils.uri2File(aty, dataUri);
                CreateGroupImageMsg(GroupId, file.getAbsolutePath());
                Message message = new Message(Message.MSG_TYPE_PHOTO, Message.MSG_STATE_SUCCESS,
                        "Tom", "avatar", "Jerry",
                        "avatar", file.getAbsolutePath(), true, true, tranferDate("yyyy-MM-dd HH:mm:ss", new Date()));
                datas.add(message);
                adapter.refresh(datas);
                Log.e("NWJ", "图片 聊天记录 " + file.getAbsolutePath());
                sessionMessage = new SessionMessage();
                sessionMessage.setType(Message.MSG_TYPE_PHOTO);//消息类型
                sessionMessage.setContent(file.getAbsolutePath());//消息内容
                sessionMessage.setSend(true);//是否为发送方
                sessionMessage.setTime(tranferDate("yyyy-MM-dd HH:mm:ss", new Date()));//发送时间
                sessionMessage.save();
            }
        }
        /**
         * 拍照  选择发送
         */
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> paths = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            for (String path : paths) {
                CreateGroupImageMsg(GroupId, path);
                Message message = new Message(Message.MSG_TYPE_PHOTO, Message.MSG_STATE_SUCCESS,
                        "Tom", "avatar", "Jerry",
                        "avatar", path, true, true, tranferDate("yyyy-MM-dd HH:mm:ss", new Date()));
                datas.add(message);
                adapter.refresh(datas);
                Log.e("NWJ", "拍照聊天记录: " + path);
                sessionMessage = new SessionMessage();
                sessionMessage.setType(Message.MSG_TYPE_PHOTO);//消息类型
                sessionMessage.setContent(path);//消息内容
                sessionMessage.setSend(true);//是否为发送方
                sessionMessage.setTime(tranferDate("yyyy-MM-dd HH:mm:ss", new Date()));//发送时间
                sessionMessage.save();
            }
        }
    }

    /**
     * 若软键盘或表情键盘弹起，点击上端空白处应该隐藏输入法键盘
     *
     * @return 会隐藏输入法键盘的触摸事件监听器
     */
    private View.OnTouchListener getOnTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                box.hideLayout();
                box.hideKeyboard(aty);
                return false;
            }
        };
    }

    /**
     * @return 聊天列表内存点击事件监听器
     */
    private OnChatItemClickListener getOnChatItemClickListener() {
        return new OnChatItemClickListener() {
            @Override
            public void onPhotoClick(int position) {
                KJLoger.debug(datas.get(position).getContent() + "点击图片的");
                ViewInject.toast(aty, datas.get(position).getContent() + "点击图片的");
            }

            @Override
            public void onTextClick(int position) {
                KJLoger.debug(datas.get(position).getContent() + "点击文本的");
                ViewInject.toast(aty, datas.get(position).getContent() + "点击文本的");
            }

            @Override
            public void onFaceClick(int position) {
                KJLoger.debug(datas.get(position).getContent() + "点击表情的");
                ViewInject.toast(aty, datas.get(position).getContent() + "点击表情的");
            }
        };
    }

    /**
     * 聊天列表中对内容的点击事件监听
     */
    public interface OnChatItemClickListener {
        void onPhotoClick(int position);

        void onTextClick(int position);

        void onFaceClick(int position);
    }

    @Override
    public void finish() {
        super.finish();

    }

    /**
     * 发送图片是onRestart
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        List<SessionMessage> all = DataSupport.findAll(SessionMessage.class);
        for (SessionMessage s : all) {
            Log.e(TAG, "onRestart: 数据库列表" + s.toString());
        }
    }

    /**
     * 毫秒转换为时间
     *
     * @param dateFormat
     * @param millsec
     * @return
     */
    public String transferLongToDate(String dateFormat, Long millsec) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        Date date = new Date(millsec);
        return format.format(date);
    }

    /**
     * 将时间格式化为 2017-07-19 10:36:23
     *
     * @param dateFormat
     * @param date
     * @return
     */
    public String tranferDate(String dateFormat, Date date) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(date);
    }

    /**
     * 群聊文本信息
     *
     * @param GroupId
     * @param content
     * @param appKey
     * @param AtName
     */
    private void CreateGroupTextMsg(String GroupId, String content, String appKey, String AtName) {
        if (!TextUtils.isEmpty(GroupId) && TextUtils.isEmpty(AtName)) {
            long gid = Long.parseLong(GroupId);
            cn.jpush.im.android.api.model.Message message = JMessageClient.createGroupTextMessage(gid, content);
            message.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        sendTxtState = true;
                        Log.e(TAG, "gotResult:文本 " + "发送成功" + sendTxtState);
                    } else {
                        sendTxtState = false;
                        Log.e(TAG, "gotResult:文本 " + "发送失败" + sendTxtState);
                    }
                }
            });
            JMessageClient.sendMessage(message);
        } else if (!TextUtils.isEmpty(GroupId) && !TextUtils.isEmpty(AtName)) {
            final List<UserInfo> infoList = new ArrayList<>();
            final MessageContent content1 = new TextContent(content);
            mConversation = JMessageClient.getGroupConversation(Long.parseLong(GroupId));
            if (null != mConversation) {
                mConversation = Conversation.createGroupConversation(Long.parseLong(GroupId));
            }
            JMessageClient.getUserInfo(AtName, appKey, new GetUserInfoCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                    if (responseCode == 0) {
                        infoList.add(info);
                        cn.jpush.im.android.api.model.Message message = mConversation.createSendMessage(content1, infoList, null);//此处null可以替换为自定义fromName
                        message.getAtUserList(new GetUserInfoListCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
                                if (responseCode == 0) {
                                    Log.e(TAG, "gotResult: " + "获取成员列表成功");
                                } else {
                                    Log.e(TAG, "gotResult: " + "获取成员列表失败");
                                }
                            }
                        });
                        JMessageClient.sendMessage(message);
                    } else {
                        Log.e(TAG, "gotResult: " + "用户不存在");
                    }
                }
            });
        } else {
            Log.e(TAG, "CreateGroupTextMsg: " + "参数错误");
        }
    }

    /**
     * 群聊语音信息
     *
     * @param groupId
     * @param VoicePath
     */
    private void CreateGroupVoiceMsg(String groupId, String VoicePath) {
        File file3gp = new File(VoicePath);
        if (!file3gp.exists()) {
            Log.e(TAG, "CreateGroupVoiceMsg: " + "语音消息不存在");
            return;
        }
        if (TextUtils.isEmpty(groupId)) {
            Log.e(TAG, "CreateGroupVoiceMsg: " + "群组ID为空");
            return;
        }
        long gid = Long.parseLong(groupId);
        mConversation = JMessageClient.getGroupConversation(gid);
        if (null == mConversation) {
            mConversation = Conversation.createGroupConversation(gid);
        }
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(String.valueOf(file3gp));
            player.prepare();
            int duration = player.getDuration();
            cn.jpush.im.android.api.model.Message voiceMessage = JMessageClient.createGroupVoiceMessage(gid, file3gp, duration);
            voiceMessage.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        Log.e(TAG, "gotResult:语音 " + "发送成功");
                    } else {
                        Log.e(TAG, "gotResult:语音 " + "发送失败");
                    }
                }
            });
            JMessageClient.sendMessage(voiceMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 群聊图片信息
     *
     * @param groupId
     * @param ImgPath
     */
    private void CreateGroupImageMsg(String groupId, String ImgPath) {
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(ImgPath)) {
            Log.e(TAG, "CreateGroupImageMsg: 图片" + "图片或者群聊ID为空");
            return;
        }
        long gid = Long.parseLong(groupId);
        File file = new File(ImgPath);
        try {
            cn.jpush.im.android.api.model.Message imageMessage = JMessageClient.createGroupImageMessage(gid, file);
            imageMessage.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        Log.e(TAG, "gotResult: 图片" + "发送成功");
                    } else {
                        Log.e(TAG, "gotResult: 图片" + "发送失败");
                    }
                }
            });
            JMessageClient.sendMessage(imageMessage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
