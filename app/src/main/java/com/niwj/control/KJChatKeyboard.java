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
package com.niwj.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.niwj.adapter.AtNameAdapter;
import com.niwj.adapter.FaceCategroyAdapter;
import com.niwj.entity.ChatAtName;
import com.niwj.instantmessaging.R;
import com.niwj.others.OnOperationListener;
import com.niwj.widget.SpeakButton;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupMembersCallback;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * 控件主界面
 *
 * @author
 */
public class KJChatKeyboard extends RelativeLayout implements
        SoftKeyboardStateHelper.SoftKeyboardStateListener {

    public interface OnToolBoxListener {
        void onShowFace();
    }

    public static String AtNickName="";
    public static String AtName="";
    private static final String TAG = "KJChatKeyboard";
    public static final int LAYOUT_TYPE_HIDE = 0;
    public static final int LAYOUT_TYPE_FACE = 1;
    public static final int LAYOUT_TYPE_MORE = 2;
    public static final String GroupId = "10122916";//群组ID
    private List<ChatAtName> mAtNameList = new ArrayList<>();//数据列表
    private ListView mListView;//绑定控件
    private AtNameAdapter mAtNameAdapter;//适配器

    private boolean isTextStatus = true;                                //是否是编辑文本消息状态
    /**
     * 最上层输入框
     */
    private EditText mEtMsg;
    private CheckBox mBtnFace;
    private CheckBox mBtnMore;
    private Button mBtnSend;
    private ImageView iv_sendStatus;
    private SpeakButton btn_sendSound;

    /**
     * 表情
     */
    private ViewPager mPagerFaceCagetory;
    private RelativeLayout mRlFace;
    private RelativeLayout mRlChatAtName;
    private RelativeLayout mRlMsgToolBox;

    private TextView cancel_atname;
    private PagerSlidingTabStrip mFaceTabs;

    private int layoutType = LAYOUT_TYPE_HIDE;
    private FaceCategroyAdapter adapter;  //点击表情按钮时的适配器

    private List<String> mFaceData;

    private Context context;
    private OnOperationListener listener;
    private OnToolBoxListener mFaceListener;
    private SoftKeyboardStateHelper mKeyboardHelper;
    private Typeface emojitf;

    /**
     * 语音会话
     *
     * @param context
     */
    public KJChatKeyboard(Context context) {
        super(context);
        init(context);
    }

    public KJChatKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KJChatKeyboard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        View root = View.inflate(context, R.layout.chat_tool_box, null);
        this.addView(root);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initData();
        this.initWidget();
    }

    private void initData() {
        getGroupMembers(GroupId);
        mKeyboardHelper = new SoftKeyboardStateHelper(((Activity) getContext())
                .getWindow().getDecorView());
        mKeyboardHelper.addSoftKeyboardStateListener(this);
        if (!"smartisan".equals(Build.MANUFACTURER)) {
            try {
                emojitf = Typeface.createFromAsset(context.getAssets(), "fonts/emoji.ttf");
            } catch (Exception e) {
            }
        }
    }

    /**
     * 切换按钮状态
     */
    private void switchSendStatus() {
        isTextStatus = !isTextStatus;
        iv_sendStatus.setImageResource(isTextStatus ? R.mipmap.ic_switch_sound : R.mipmap.ic_switch_keyboard);
        mEtMsg.setVisibility(isTextStatus ? View.VISIBLE : View.GONE);
        btn_sendSound.setVisibility(isTextStatus ? View.GONE : View.VISIBLE);
        if (!isTextStatus) {//不是编辑状态
            setSendVisible(false);
            hideKeyboard(context);
        } else if (!mEtMsg.getText().toString().isEmpty()) {
            setSendVisible(true);
        }
    }

    /**
     * 设置发送按钮是否可见
     */
    private void setSendVisible(boolean isVisible) {
        mBtnSend.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mBtnMore.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        if (isVisible) {
            mBtnSend.requestFocus();
        }
    }

    private void initWidget() {
        mEtMsg = (EditText) findViewById(R.id.toolbox_et_message);
        if (emojitf != null) {
            mEtMsg.setTypeface(emojitf);
        }
        mEtMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override //CharSequence不能直接做比较，要转换为string
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String s1 = s.toString();
                int length = s1.length();
                if (length != 0) {
                    char c = s1.charAt(length - 1);
                    Log.e(TAG, "afterTextChanged: " + s1 + "  " + length + "   " + c);
                    if (c == '@') {
//                    Toast.makeText(context, "输入了@", Toast.LENGTH_SHORT).show();
                        showCTNLayout();
                    }
                }

                setSendVisible(!s.toString().isEmpty());
            }
        });
        iv_sendStatus = (ImageView) findViewById(R.id.iv_sendStatus);
        iv_sendStatus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSendStatus();
            }
        });
        /**
         * 语音
         */
        btn_sendSound = (SpeakButton) findViewById(R.id.btn_sendSound);
        btn_sendSound.setOnSendListener(new SpeakButton.OnSendListener() {
            @Override
            public void send(String filePath) {
                if (listener != null) {
                    listener.sendVoice(filePath);
                }

            }
        });


        mBtnSend = (Button) findViewById(R.id.toolbox_btn_send);
        mBtnFace = (CheckBox) findViewById(R.id.toolbox_btn_face);
        mBtnMore = (CheckBox) findViewById(R.id.toolbox_btn_more);
        mRlFace = (RelativeLayout) findViewById(R.id.toolbox_layout_face);//emoji  和  照片的layout

        mRlChatAtName = (RelativeLayout) findViewById(R.id.chat_at_name_layout);//@用户layout

        mRlMsgToolBox = (RelativeLayout) findViewById(R.id.messageToolBox);//输入框layout

        /**
         * 取消
         */
        cancel_atname = (TextView) findViewById(R.id.cancel_atname);
        cancel_atname.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "nnn",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onClick: " + "返回了");
                mRlChatAtName.setVisibility(View.GONE);
                mRlMsgToolBox.setVisibility(View.VISIBLE);
                mEtMsg.setFocusable(true);
                mEtMsg.setFocusableInTouchMode(true);
                mEtMsg.requestFocus();
                mEtMsg.findFocus();
                mEtMsg.setSelection(mEtMsg.getText().length());
                int visibility = mRlChatAtName.getVisibility();
                Log.e(TAG, "onKeyDown:cancel " + visibility);
            }
        });

        /**
         * 用户列表
         */
        mListView = (ListView) findViewById(R.id.atname_listview);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatAtName atName = mAtNameList.get(position);
//                Toast.makeText(context, position + atName.getName(), Toast.LENGTH_SHORT).show();
                String s = mEtMsg.getText().toString();
                AtNickName = atName.getNickname().toString();
                AtName=atName.getName().toString();
                Log.e(TAG, "onItemClick: @用户名"+AtNickName );
                mEtMsg.setText(s+AtNickName);
                mRlChatAtName.setVisibility(View.GONE);
                mRlMsgToolBox.setVisibility(View.VISIBLE);
                mEtMsg.setFocusable(true);
                mEtMsg.setFocusableInTouchMode(true);
                mEtMsg.requestFocus();
                mEtMsg.findFocus();
                mEtMsg.setSelection(mEtMsg.getText().length());
            }
        });
        mPagerFaceCagetory = (ViewPager) findViewById(R.id.toolbox_pagers_face);
        mFaceTabs = (PagerSlidingTabStrip) findViewById(R.id.toolbox_tabs);
        adapter = new FaceCategroyAdapter(((FragmentActivity) getContext())
                .getSupportFragmentManager(), LAYOUT_TYPE_FACE);

        /**
         * 发送消息
         */
        mBtnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    String content = mEtMsg.getText().toString();
                    listener.send(content);
                    mEtMsg.setText(null);  //输入框置为空
                }
            }
        });
        // 点击表情按钮
        mBtnFace.setOnClickListener(getFunctionBtnListener(LAYOUT_TYPE_FACE));
        // 点击表情按钮旁边的加号
        mBtnMore.setOnClickListener(getFunctionBtnListener(LAYOUT_TYPE_MORE));
        // 点击消息输入框
        mEtMsg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLayout();
            }
        });
    }

    /*************************
     * 内部方法 start
     ************************/

    private OnClickListener getFunctionBtnListener(final int which) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow() && which == layoutType) {
                    hideLayout();
                    showKeyboard(context);
                } else {
                    changeLayout(which);
                    showLayout();
                    mBtnFace.setChecked(layoutType == LAYOUT_TYPE_FACE);
                    mBtnMore.setChecked(layoutType == LAYOUT_TYPE_MORE);
                }
            }
        };
    }


    private void changeLayout(int mode) {
        adapter = new FaceCategroyAdapter(((FragmentActivity) getContext())
                .getSupportFragmentManager(), mode);
        adapter.setOnOperationListener(listener);
        layoutType = mode;
        setFaceData(mFaceData);
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        hideLayout();
    }

    @Override
    public void onSoftKeyboardClosed() {
    }

    /***************************** 内部方法 end ******************************/

    /**************************
     * 可选调用的方法 start
     **************************/

    public void setFaceData(List<String> faceData) {
        mFaceData = faceData;
        adapter.refresh(faceData);
        mPagerFaceCagetory.setAdapter(adapter);
        mFaceTabs.setViewPager(mPagerFaceCagetory);
        if (layoutType == LAYOUT_TYPE_MORE) {
            mFaceTabs.setVisibility(GONE);
        } else {
            //加1是表示第一个分类为默认的emoji表情分类，这个分类是固定不可更改的
            if (faceData.size() + 1 < 2) {
                mFaceTabs.setVisibility(GONE);
            } else {
                mFaceTabs.setVisibility(VISIBLE);
            }
        }
    }

    public EditText getEditTextBox() {
        return mEtMsg;
    }

    public void showCTNLayout() {

        mRlChatAtName.setVisibility(View.VISIBLE);
        mRlMsgToolBox.setVisibility(View.GONE);
        int visibility = mRlChatAtName.getVisibility();
        Log.e(TAG, "onKeyDown: " + visibility);
    }

    public void showLayout() {
        hideKeyboard(this.context);
        // 延迟一会，让键盘先隐藏再显示表情键盘，否则会有一瞬间表情键盘和软键盘同时显示
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mRlFace.setVisibility(View.VISIBLE);
                if (mFaceListener != null) {
                    mFaceListener.onShowFace();
                }
            }
        }, 50);
    }


    public boolean isShow() {
        return mRlFace.getVisibility() == VISIBLE;
    }

    public void hideLayout() {
        mRlFace.setVisibility(View.GONE);
        if (mBtnFace.isChecked()) {
            mBtnFace.setChecked(false);
        }
        if (mBtnMore.isChecked()) {
            mBtnMore.setChecked(false);
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard(Context context) {
        Activity activity = (Activity) context;
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive() && activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }
    }

    /**
     * 显示软键盘
     */
    public static void showKeyboard(Context context) {
        Activity activity = (Activity) context;
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInputFromInputMethod(activity.getCurrentFocus()
                    .getWindowToken(), 0);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public OnOperationListener getOnOperationListener() {
        return listener;
    }

    public void setOnOperationListener(OnOperationListener onOperationListener) {
        this.listener = onOperationListener;
        adapter.setOnOperationListener(onOperationListener);
    }

    public void setOnToolBoxListener(OnToolBoxListener mFaceListener) {
        this.mFaceListener = mFaceListener;
    }

    /*********************** 可选调用的方法 end ******************************/

    /**
     * 返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            int visibility = mRlChatAtName.getVisibility();
            Log.e(TAG, "onKeyDown: 返回键" + visibility);
            if (visibility == 0) {
                mRlChatAtName.setVisibility(View.GONE);
                mRlMsgToolBox.setVisibility(View.VISIBLE);
                mEtMsg.setFocusable(true);
                mEtMsg.setFocusableInTouchMode(true);
                mEtMsg.requestFocus();
                mEtMsg.findFocus();
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取群组用户列表
     *
     * @param groupId
     */
    private void getGroupMembers(String groupId) {
        if (TextUtils.isEmpty(groupId)) {
            Log.e(TAG, "getGroupMembers: " + "请输入群组ID");
            return;
        }
        final long groupID = Long.parseLong(groupId);
        JMessageClient.getGroupMembers(groupID, new GetGroupMembersCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                if (i == 0) {
                    StringBuilder sb = new StringBuilder();
                    for (UserInfo info : list) {
                        sb.append(info.getNickname());//昵称
                        ChatAtName chatAtName = new ChatAtName(info.getNickname(),info.getUserName(), R.mipmap.ic_user_header_img_example);
                        mAtNameList.add(chatAtName);
                        mAtNameAdapter = new AtNameAdapter(mAtNameList, context);
                        mListView.setAdapter(mAtNameAdapter);
                    }
                } else {
                    Log.e(TAG, "gotResult: " + "获取失败");
                    Log.e(TAG, "JMessageClient.getGroupMembers " + ", responseCode = " + i + " ; Desc = " + s);
                }
            }
        });
    }
}
