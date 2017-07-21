package com.niwj.control;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


import com.niwj.instantmessaging.ChatActivity;
import com.niwj.instantmessaging.MainActivity;
import com.niwj.instantmessaging.R;

import java.lang.reflect.Method;
import java.util.Locale;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by ${chenyn} on 16/3/23.
 *
 * @desc : 注册和登陆界面
 */
public class
RegisterAndLoginActivity extends Activity {

    private String UserId;
//    private String UserName_IM = null;
    private String cut_string = "-";
    private String IMRegisterPwd;


    private static String KEY_APP_KEY = "JPUSH_APPKEY";
    private static String APP_KEY;
    public static EditText mEd_userName;
    public static EditText mEd_password;
    private Button mBt_login;
    private Button mBt_gotoRegister;
    private ProgressDialog mProgressDialog = null;
    private CheckBox mCb_isTest;
    private boolean isTestVisibility = false;
    private static final String TAG = "RegisterAndLoginActivit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LoadRegisterPwd();
        initView();
        initData();
    }


/*    private String GetUserId() {
        UserId = LoginUtils.getUserId(this);
        for (int i = 0; i < 4; i++) {//UserId是需要删除某个子串的字符串s是需要删除的子串
            boolean ai = UserId.contains(cut_string);
            while (ai) {
                int positon = UserId.indexOf(cut_string);
                int length = cut_string.length();
                int Length = UserId.length();
                UserName_IM = UserId.substring(0, positon) + UserId.substring(positon + length, Length);
                UserId = UserName_IM;
                boolean ai1 = UserName_IM.contains(cut_string);
                ai = ai1;
                i++;//
            }
        }
        return UserName_IM;
    }*/

    /**
     * #################    应用入口,登陆或者是注册    #################
     */
    private void initData() {
        /**=================     获取个人信息不是null，说明已经登陆，无需再次登陆，则直接进入聊天界面    =================*/
        UserInfo myInfo = JMessageClient.getMyInfo();
        if (myInfo != null) {
//            Intent intent = new Intent(RegisterAndLoginActivity.this, TypeActivity.class);
            Intent intent = new Intent(RegisterAndLoginActivity.this, ChatActivity.class);
            startActivity(intent);
        }
        /**=================     调用注册接口    =================*/
        mBt_gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        /**
         * 登录
         */
        mBt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = ProgressDialog.show(RegisterAndLoginActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                final String userName = mEd_userName.getText().toString();
                final String password = mEd_password.getText().toString();
//                final String userName = "wis1234";
//                final String password = "123321";
                /**=================     调用SDk登陆接口    =================*/
                JMessageClient.login(userName, password, new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String LoginDesc) {
                        if (responseCode == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "登录成功" + ", responseCode = " + responseCode + " ; LoginDesc = " + LoginDesc);
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), ChatActivity.class);
//                            intent.setClass(getApplicationContext(), ChatActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "登录失败" + ", responseCode = " + responseCode + " ; LoginDesc = " + LoginDesc);
                        }
                    }
                });
            }
        });
/**=================     是否设置测试环境    =================*/
        mCb_isTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swapEnvironment(RegisterAndLoginActivity.this.getApplicationContext(), true);
                    buttonView.setText("测试环境");
                } else {
                    buttonView.setText("生产环境");
                    swapEnvironment(RegisterAndLoginActivity.this.getApplicationContext(), false);
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.imactivity_login);
        mEd_userName = (EditText) findViewById(R.id.ed_login_username);
        mEd_password = (EditText) findViewById(R.id.ed_login_password);
        mBt_login = (Button) findViewById(R.id.bt_login);
        mBt_gotoRegister = (Button) findViewById(R.id.bt_goto_regester);
        mCb_isTest = (CheckBox) findViewById(R.id.cb_is_test);
//        GetUserId();//拿到登录的userid  作为用户名
        //记录注册的账号密码
//        mEd_userName.setText(UserName_IM);
//        Log.e(TAG, "获取注册的IM账号"+UserName_IM);
        mEd_password.setText(IMRegisterPwd);

        if (!isTestVisibility) {
            mCb_isTest.setVisibility(View.GONE);
        } else {
            //供jmessage sdk测试使用，开发者无需关心。
            Boolean isTestEvn = invokeIsTestEvn();
            if (isTestEvn) {
                mCb_isTest.setText("测试环境");
            } else {
                mCb_isTest.setText("生产环境");
            }
            mCb_isTest.setChecked(isTestEvn);
        }
    }

    public static String getAppKey(Context context) {
        Bundle metaData = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            } else {
                return null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        if (null != metaData) {
            APP_KEY = metaData.getString(KEY_APP_KEY);
            if (TextUtils.isEmpty(APP_KEY)) {
                return null;
            } else if (APP_KEY.length() != 24) {
                return null;
            }
            APP_KEY = APP_KEY.toLowerCase(Locale.getDefault());
        }
        return APP_KEY;
    }

    public static Boolean invokeIsTestEvn() {
        try {
            Method method = JMessageClient.class.getDeclaredMethod("isTestEnvironment");
            Object result = method.invoke(null);
            return (Boolean) result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void swapEnvironment(Context context, boolean isTest) {
        try {
            Method method = JMessageClient.class.getDeclaredMethod("swapEnvironment", Context.class, Boolean.class);
            method.invoke(null, context, isTest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void LoadRegisterPwd() {
//        SharePreferenceUtil sp = SharePreferenceUtil.getInstance(RegisterAndLoginActivity.this);
//        IMRegisterPwd = sp.getString("IMRegisterPwd", "");
//        Log.e("获取注册IM密码", IMRegisterPwd);
////        Log.e("获取注册IM账户", UserName_IM);
//    }
}