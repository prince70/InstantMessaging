package com.niwj.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.niwj.instantmessaging.R;


public class LoadingDialog extends MyDialog{
	
	private ImageView iv_loading;

	private Animation rotate;		//旋转的动画
	private OnBackListener onBackListener;

	public LoadingDialog(Context context) {
		super(context, R.layout.dialog_loading);

		rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(1000);
		rotate.setRepeatCount(-1);	//无限重复

		iv_loading = getView(R.id.iv_loading);
		//不能被取消
		setCancelable(true);
		//监听返回键
		setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
					if (onBackListener != null){
						onBackListener.onBack();
					}
				}
				return true;
			}
		});
	}

	@Override
	public void show() {
		super.show();
		iv_loading.startAnimation(rotate);
	}

	/**
	 * @param msg 提示文字
     */
	public void show(String msg){
		((TextView) getView(R.id.tv_msg)).setText(msg);
		show();
	}

	public void show(int stringId){
		((TextView) getView(R.id.tv_msg)).setText(stringId);
		show();
	}

	/**
	 * 设置提示文字
	 */
	public void setText(String msg){
		((TextView) getView(R.id.tv_msg)).setText(msg);
	}

	/**
	 * 返回键的监听事件
	 */
	public void setOnBackListener(OnBackListener l){
		this.onBackListener = l;
	}

	@Override
	public void dismiss() {
		if (isShowing()) {
			super.dismiss();
			iv_loading.clearAnimation();
		}
	}

	/**
	 * 按返回键的回调
	 */
	public interface OnBackListener {
		void onBack();
	}
}
