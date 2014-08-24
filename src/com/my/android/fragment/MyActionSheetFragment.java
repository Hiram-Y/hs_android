package com.my.android.fragment;
import com.my.android.R;
import com.my.android.utils.ScreenUtil;
import com.my.android.view.MyAnimator;
import com.my.android.view.MyAnimator.AnimType;
import com.my.android.view.MyAnimator.MyAnimatorListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * 选项卡控件
 *<p>效果见：<a href="https://raw.githubusercontent.com/baoyongzhang/ActionSheetForAndroid/master/screenshot-2.png">UI效果演示图</a>
 */
public class MyActionSheetFragment extends DialogFragment {
	private static final AnimType animIn = AnimType.FlipInX , animOut = AnimType.FlipOutX;
	private static final Integer CANCEL_BTN_TAG = -1;
	private ActionSheetListener mListener;
	private String cancelBtnText;
	private String[] items;
	private int itemTextColor = -1;
	
	private OnClickListener btnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dismiss();
			if(mListener == null){
				return;
			}
			int position = (Integer) v.getTag();
			if(position == CANCEL_BTN_TAG){
				mListener.onCancel(MyActionSheetFragment.this);
			}else{
				mListener.onItemClick(MyActionSheetFragment.this, position);
			}
			
		}
	};
	
	public static MyActionSheetFragment newInstance() {
		MyActionSheetFragment dialog = new MyActionSheetFragment();
		return dialog;
	}

	@Override
	// 在onCreate中设置对话框的风格、属性等
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);
		int style = DialogFragment.STYLE_NO_TITLE;
		setStyle(style, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mview = inflater.inflate(R.layout.my_action_sheet, container);
		LinearLayout ll_content = (LinearLayout) mview.findViewById(R.id.ll_action_sheet_content);
		Button btn_cancel = (Button) mview.findViewById(R.id.btn_action_sheet_cancel);
		btn_cancel.setOnClickListener(btnClickListener);
		btn_cancel.setTag(CANCEL_BTN_TAG);
		if(cancelBtnText != null){
			btn_cancel.setText(cancelBtnText);
		}
		if(items != null){
			for(int i=0;i<items.length;i++){
				String item = items[i];
				Button btn = new Button(getActivity());
				btn.setTag(Integer.valueOf(i));
				btn.setText(item);
				btn.setOnClickListener(btnClickListener);
				if(itemTextColor != -1){
					btn.setTextColor(itemTextColor);
				}else{
					btn.setTextColor(0xFF666666);
				}
				int padding = ScreenUtil.dip2px(12);
				btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
				btn.setPadding(padding,padding,padding,padding);
				btn.setGravity(Gravity.CENTER);
				if(items.length == 1){
					btn.setBackgroundResource(R.drawable.my_action_sheet_item_bg);
				}else if(i == 0){
					btn.setBackgroundResource(R.drawable.my_action_sheet_item_frist_bg);
				}else if(i == items.length-1){
					btn.setBackgroundResource(R.drawable.my_action_sheet_item_last_bg);
				}else{
					btn.setBackgroundResource(R.drawable.my_action_sheet_item_middle_bg);
				}
				ll_content.addView(btn);
				if( i!=items.length-1 ){
					ImageView iv = new ImageView(getActivity());
					LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					lp.height = ScreenUtil.dip2px(1);
					iv.setBackgroundColor(0xFFDCDADD);
					ll_content.addView(iv,lp);
				}
			}
		}
		return mview;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Window w = getDialog().getWindow();
		w.setBackgroundDrawableResource(android.R.color.transparent);
		WindowManager.LayoutParams wlp = w.getAttributes();
		wlp.gravity = Gravity.BOTTOM;
//		wlp.windowAnimations = R.style.MyActionSheetAnim;
		getView().getLayoutParams().width = ScreenUtil.screenWidth;
	}
	
	@Override
	public void show(FragmentManager manager, String tag) {
		show(manager.beginTransaction(), tag);
	}
	
	@Override
	public int show(FragmentTransaction transaction, String tag) {
		int i = super.show(transaction, tag);
		getFragmentManager().executePendingTransactions();
		MyAnimator.newInstance(animIn).playOn(getView());
		return i;
	}
	
	public ActionSheetListener getActionSheetListener() {
		return mListener;
	}
	/**设置按钮点击监听*/
	public MyActionSheetFragment setActionSheetListener(ActionSheetListener mListener) {
		this.mListener = mListener;
		return this;
	}

	/**设置取消按钮的文字*/
	public MyActionSheetFragment setCancelBtnText(String cancelBtnText) {
		this.cancelBtnText = cancelBtnText;
		return this;
	}
	/**设置选项卡文字颜色, 不要直接传 R.color.xx ， 应该传 getResources().getColor(R.color.xx)
	 * 或者直接传 android.graphics.Color.xx
	 * */
	public MyActionSheetFragment setItemTextColor(int color) {
		this.itemTextColor = color;
		return this;
	}
	
	/**设置选项卡要显示的选项文字*/
	public MyActionSheetFragment setItems(String[] items) {
		this.items = items;
		return this;
	}

	/**
	 * 点击监听
	 * @author hushuai
	 *
	 */
	public interface ActionSheetListener {
		/**
		 *点击选择条目回调
		 */
		public void onItemClick(MyActionSheetFragment actionSheet,int position);
		/**
		 * 取消回调
		 */
		public void onCancel(MyActionSheetFragment actionSheet);
	}
	
	@Override
	public void dismiss() {
		MyAnimator.newInstance(animOut).setDuration(300).setListener(new MyAnimatorListener() {
			@Override
			public void onAnimationStart() {
			}
			@Override
			public void onAnimationEnd() {
				MyActionSheetFragment.super.dismiss();
			}
		}).playOn(getView());
	}
	
}
