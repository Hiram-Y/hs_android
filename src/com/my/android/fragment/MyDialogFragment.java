package com.my.android.fragment;

import com.my.android.R;
import com.my.android.view.MyAnimator;
import com.my.android.view.MyAnimator.AnimType;
import com.my.android.view.MyAnimator.MyAnimatorListener;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class MyDialogFragment extends DialogFragment {
	private static final AnimType animIn = AnimType.Bounce , animOut = AnimType.FadeOut;
	private int dialogID = 0;
	private MyDialogClickListener mListener;
	private View contentView;
	private String title,leftButtonText,rightButtonText,message;
	private boolean isSigleButton = false;
	
	private OnClickListener btnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mListener == null){
				dismiss();
				return;
			}
			if(v.getId() == R.id.my_dialog_left_btn){
				mListener.onDialogLeftBtnClick(MyDialogFragment.this);
			}else if(v.getId() == R.id.my_dialog_right_btn){
				mListener.onDialogRightBtnClick(MyDialogFragment.this);
			}
		}
	};
	
	public static MyDialogFragment newInstance() {
		MyDialogFragment dialog = new MyDialogFragment();
		return dialog;
	}

	@Override
	// 在onCreate中设置对话框的风格、属性等
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false);
		int style = DialogFragment.STYLE_NO_TITLE;
		setStyle(style, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		View mview = inflater.inflate(R.layout.my_dialog, container);
		TextView tv_title = (TextView) mview.findViewById(R.id.my_dialog_title_tv);
		if(title == null){
			title = getString(R.string.hint);
		}
		tv_title.setText(title);
		TextView tv_message = (TextView) mview.findViewById(R.id.my_dialog_content_tv);
		tv_message.setText(message);
		Button btn_left = (Button) mview.findViewById(R.id.my_dialog_left_btn);
		if(leftButtonText == null){
			leftButtonText = getString(R.string.cancel);
		}
		btn_left.setText(leftButtonText);
		btn_left.setOnClickListener(btnClickListener);
		Button btn_right = (Button) mview.findViewById(R.id.my_dialog_right_btn);
		if(rightButtonText == null){
			rightButtonText = getString(R.string.confirm);
		}
		btn_right.setText(rightButtonText);
		btn_right.setOnClickListener(btnClickListener);
		if(contentView != null){
			ScrollView sv = (ScrollView) mview.findViewById(R.id.my_dialog_content_sv);
			sv.removeAllViews();
			sv.addView(contentView);
		}
		if(isSigleButton){
			mview.findViewById(R.id.my_dialog_line).setVisibility(View.GONE);
			btn_left.setVisibility(View.GONE);
			btn_right.setBackgroundResource(R.drawable.my_dialog_bottom_btn);
		}
		return mview;
	}
	
	@Override
	public void show(FragmentManager manager, String tag) {
		show(manager.beginTransaction(), tag);
	}
	
	@Override
	public int show(FragmentTransaction transaction, String tag) {
		int i = super.show(transaction, tag);
		getFragmentManager().executePendingTransactions();
		MyAnimator.newInstance(animIn).setDuration(500).playOn(getView());
		return i;
	}
	
	/**设置标题文字*/
	public MyDialogFragment setTitle(String title){
		this.title = title;
		return this;
	}
	/**设置左边按钮文字*/
	public MyDialogFragment setLeftButtonText(String text){
		leftButtonText = text;
		return this;
	}
	/**设置右边按钮文字*/
	public MyDialogFragment setRightButtonText(String text){
		rightButtonText = text;
		return this;
	}
	/**获取对话框标识ID，用来区分后续操作*/
	public int getDialogID() {
		return dialogID;
	}
	/**设置对话标识ID*/
	public MyDialogFragment setDialogID(int dialogID) {
		this.dialogID = dialogID;
		return this;
	}
	
	/**设置要显示的文字内容*/
	public MyDialogFragment setMessage(String message){
		this.message = message;
		return this;
	}
	
	
	public MyDialogClickListener getDialogClickListener() {
		return mListener;
	}
	/**设置按钮点击监听*/
	public MyDialogFragment setDialogClickListener(MyDialogClickListener mListener) {
		this.mListener = mListener;
		return this;
	}
	/**获取自定义的内容视图*/
	public View getContentView() {
		return contentView;
	}
	/**设置自定义视图， 标题和底部按钮之间的内容*/
	public MyDialogFragment setContentView(View contentView) {
		this.contentView = contentView;
		return this;
	}

	/**
	 * 调用此方法后只显示一个按钮
	 * <p>显示单个按钮（右边按钮），点击后回调mListener的onDialogRightBtnClick()
	 */
	public MyDialogFragment singleButton() {
		isSigleButton = true;
		return this;
	}

	@Override
	public void dismiss() {
		MyAnimator.newInstance(animOut).setDuration(300).setListener(new MyAnimatorListener() {
			@Override
			public void onAnimationStart() {
			}
			@Override
			public void onAnimationEnd() {
				MyDialogFragment.super.dismiss();
			}
		}).playOn(getView());
	}
	
	/**
	 * 对话框按钮点击监听
	 * @author hushuai
	 *
	 */
	public interface MyDialogClickListener {
		public void onDialogLeftBtnClick(MyDialogFragment dialog);

		public void onDialogRightBtnClick(MyDialogFragment dialog);
	}
}
