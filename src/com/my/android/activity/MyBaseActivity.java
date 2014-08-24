package com.my.android.activity;

import java.util.ArrayList;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.my.android.R;
import com.my.android.bean.RequestBean;
import com.my.android.fragment.MyActionSheetFragment;
import com.my.android.fragment.MyActionSheetFragment.ActionSheetListener;
import com.my.android.fragment.MyDialogFragment;
import com.my.android.fragment.MyDialogFragment.MyDialogClickListener;
import com.my.android.network.MyRequest;
import com.my.android.network.MyRequestListener;
import com.my.android.network.MyRequestQueue;
import com.my.android.network.MyResponse;
import com.my.android.network.NetworkMethod;
import com.my.android.utils.LogUtil;
import com.my.android.utils.ToastUtil;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 所有Activity的父类，如无特殊需求，项目中的Activity都应该是该类的直接或间接子类
 * @author hushuai
 */
public class MyBaseActivity extends FragmentActivity implements MyRequestListener,OnClickListener,MyDialogClickListener{	
	private View loadingView;
	private static String loadingText;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	}

	/**
	 * 请求数据，默认显示加载框，默认以POST方式
	 * <p>请求成功回调本类以RequestBean.response_methodName的值为方法名的方法,请求出错回调本类的onErrorRequest 方法
	 * @param bean 网络数据封装类  {@link RequestBean}
	 */
	public MyRequest requestUrl(RequestBean bean){
		return requestUrl(bean,true);
	}
	
	/**
	 * 请求数据，默认以POST方式
	 * <p>请求成功回调本类以RequestBean.response_methodName的值为方法名的方法,请求出错回调本类的onErrorRequest 方法
	 * @param bean 网络数据封装类  {@link RequestBean}
	 * @param showLoading 是否显示默认loading框
	 */
	public MyRequest requestUrl(RequestBean bean,boolean showLoading){
		return requestUrl(bean,NetworkMethod.POST,showLoading,this);
	}
	
	/**请求数据，	 
	 * <p>请求成功回调listenter中以RequestBean.response_methodName的值为方法名的方法,请求出错回调listenter的onErrorRequest 方法
	 * @param bean 网络数据封装类  {@link RequestBean}
	 * @param method 请求方式
	 * @param showLoading 是否显示默认loading框
	 * @param listenter 请求回调监听类
	 */
	public MyRequest requestUrl(RequestBean bean,NetworkMethod method,boolean showLoading,MyRequestListener listenter){
		if(showLoading){
			showLoading(bean.request_id);
		}
		MyRequest request = new MyRequest(bean.request_id,method, bean.request_url, listenter);
		request.setRequestBean(bean);
		return MyRequestQueue.request(request);
	}
	
	/**请求数据,默认显示加载框, 默认以POST方式, 
	 * <p>请求成功回调本类的onSuccessRequest 方法， 请求出错回调本类的onErrorRequest方法
	 *@param url 请求url
	 */
	public MyRequest requestUrl(String url){
		return requestUrl(MyRequest.def_identify,url);
	}

	/**请求数据,默认显示加载框, 默认以POST方式, 
	 * <p>请求成功回调本类的onSuccessRequest 方法， 请求出错回调本类的onErrorRequest方法
	 *@param id 请求id，用来标识本次请求，可在请求回调里进行判断，做相应的处理
	 *@param url 请求url
	 */
	public MyRequest requestUrl(int id, String url){
		return requestUrl(id, url, true);
	}
	
	/**请求数据,默认以POST方式, 
	 * <p>请求成功回调本类的onSuccessRequest 方法， 请求出错回调本类的onErrorRequest方法
	 * @param id
	 * @param url
	 * @param showLoading 是否显示默认loading框
	 * @return
	 */
	public MyRequest requestUrl(int id, String url,boolean showLoading){
		return requestUrl(id, url, NetworkMethod.POST,showLoading,this);
	}
	/**请求数据
	 * @param id
	 * @param url
	 * @param method
	 * @param showLoading
	 * @param listenter 请求回调监听类
	 * @return
	 */
	public MyRequest requestUrl(int id, String url,NetworkMethod method,boolean showLoading,MyRequestListener listenter){
		if(showLoading){
			showLoading(id);
		}
		return MyRequestQueue.requestUrl(id, url, method,listenter);
	}

	@Override
	public void onSuccessRequest(int id,MyResponse response) {
		
	}
	
	/**
	 * 请求出错的回调方法，本方法已有默认的处理方式，一般情况下不需要处理，除非有特殊的业务需求
	 * <p>默认处理方式为：对相关的错误进行Toast 提示
	 */
	@Override
	public void onErrorRequest(int id,MyResponse response) {
		String content = "获取数据失败!";
		VolleyError error = response.getError();
		if(error instanceof NoConnectionError){//当前设备无网络连接
			content = "当前无网络连接，请检查网络";
		}else if(error instanceof NetworkError){//网络错误
			content = "网络出错,请稍后再试!";
		}else if(error instanceof TimeoutError){//连接或读取数据超时
			content = "连接超时,请稍后再试!";
		}else if(error instanceof ParseError){//解析数据时错误
			content = "解析数据出错了!";
		}else if(error instanceof ServerError){//服务器错误
			content = "服务器错误，请稍后再试!";
		}else{//其他错误
			
		}
		showToast(content);
		LogUtil.log(error);
	}
	
	/**
	 * 显示loading视图
	 * @param loadingId 与取消loading时调用dismissLoading(id)的参数对应
	 * <p>调用了多少次showLoading(id) 就必须对应的调用多少次 dismissLoding(id)。
	 * <p>不同的耗时操作应有不同的loadingId
	 */
	@SuppressWarnings("unchecked")
	public final void showLoading(int loadingId) {
		if(loadingView == null){
			loadingView = initLoadingView();
		}
		ArrayList<Integer> list = (ArrayList<Integer>) loadingView.getTag();
		list.add(loadingId);
		if(loadingView.getParent() != null){
			return;
		}
		ViewGroup vg = (ViewGroup) getWindow().getDecorView();
		vg.addView(loadingView);
	}
	
	/**初始化loading，可自定义重写*/
	@SuppressLint("InflateParams")
	protected View initLoadingView(){
		View loadingView = getLayoutInflater().inflate(R.layout.my_loading_view, null);
		if(loadingText != null){
			TextView tv = (TextView) loadingView.findViewById(R.id.tv_loadingHint);
			tv.setText(loadingText);
		}
		ArrayList<Integer> list = new ArrayList<Integer>();
		loadingView.setTag(list);
		return loadingView;
	}
	
	/**
	 * 取消默认网络请求loading框
	 * @param loadingId 和调用showLoading传的id对应
	 */
	@SuppressWarnings("unchecked")
	public final void dismissLoading(int loadingId) {
		if(loadingView != null && loadingView.getParent() != null){
			ArrayList<Integer> list = (ArrayList<Integer>) loadingView.getTag();
			list.remove(Integer.valueOf(loadingId));
			if(list.isEmpty()){
				((ViewGroup)loadingView.getParent()).removeView(loadingView);
			}
		}
	}
	
	/**设置加载框默认的文字*/
	public void setLoadingText(String text){
		loadingText = text;
	}
	
	/**
	 * 重写返回按键，返回时取消‘同步’请求
	 */
	@Override
	public void onBackPressed() {
		if(loadingView != null && loadingView.getParent() != null){
			((ViewGroup)loadingView.getParent()).removeView(loadingView);
		}
		/*取消‘同步’ 网络请求
		 *这里的‘同步’是指业务需求和流程上的同步。
		 */
		MyRequestQueue.canceAll(new RequestFilter() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean apply(Request<?> request) {
				if(request instanceof MyRequest){
					MyRequest mr = (MyRequest) request;
					ArrayList<Integer> list = (ArrayList<Integer>) loadingView.getTag();
					if(list.contains(mr.getIdentify())){
						return true;
					}
				}
				return false;
			}
		});
		super.onBackPressed();
	}
	
	/**activity 销毁时取消当前activity监听的所有请求*/
	@Override
	protected void onDestroy() {
		MyRequestQueue.cance(this);
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		
	}
	
	public TextView findTextView(int id){
		 return (TextView) findViewById(id);
	}
	
	public Button findButton(int id){
		return (Button) findViewById(id);
	}
	
	public ImageView findImageView(int id){
		return (ImageView) findViewById(id);
	}
	
	public EditText findEditText(int id){
		return (EditText) findViewById(id);
	}
	
	public CheckBox findCheckBox(int id){
		return (CheckBox) findViewById(id);
	}
	
	public RadioButton findRadioButton(int id){
		return (RadioButton) findViewById(id);
	}
	
	public RadioGroup findRadioGroup(int id){
		return (RadioGroup) findViewById(id);
	}
	
	public LinearLayout findLinearLayout(int id){
		return (LinearLayout) findViewById(id);
	}
	
	public FrameLayout findFrameLayout(int id){
		return (FrameLayout) findViewById(id);
	}
	
	public RelativeLayout findRelativeLayout(int id){
		return (RelativeLayout) findViewById(id);
	}
	
	public ListView findListView(int id){
		return (ListView)findViewById(id);
	}
	
	
	/**
	 * 显示Toast提示
	 * @param content 要提示的内容
	 */
	public void showToast(String content){
		showToast(content,1);
	}
	
	/**
	 * 显示Toast提示
	 * @param content 要提示的内容
	 */
	public void showToast(String content,int length){
		ToastUtil.showToast(this, content,length);
	}
	
	/**显示最简单的提示对话框，
	 * <p>只能做单纯的信息提示，message为要提示的信息
	 * <p>标题显示“提示”，只有一个“确定”按钮,点击按钮对话框消失，不做回调
	 */
	public MyDialogFragment showMyDialog(String message){
		MyDialogFragment dialog = MyDialogFragment.newInstance(); 
		dialog.setMessage(message).singleButton();
		dialog.show(getSupportFragmentManager(), "dialog_0"); 
		return dialog;
	}
	
	/**显示需要选择操作的提示对话框，
	 * @param dialogID 对话框标识，回调操作时以dialogID区分是那个对话框，默认为0
	 * @param message 提示的内容
	 * <p>标题显示“提示”，有一个“取消”按钮(左边)，和一个 “确定”按钮(右边)
	 * <p>点击按钮后回调本类的onDialogLeftBtnClick()、onDialogRightBtnClick()方法，
	 * <p>按需求覆写这两个方法做处理
	 */
	public MyDialogFragment showMyDialog(int dialogID, String message){
		return showMyDialog(dialogID, null, message, null, null,this);
	}
	/**
	 * 显示对话框
	 * @param dialogID 对话框id，回调操作时区分是那个对话框，默认为0
	 * @param title  对话框标题显示文字
	 * @param message 对话框提示内容
	 * @param leftBtnText 左边按钮文字
	 * @param rightBtnText 右边按钮文字
	 * <p>点击按钮后回调本类的onDialogLeftBtnClick()、onDialogRightBtnClick()方法，
	 * <p>按需求覆写这两个方法做处理
	 */
	public MyDialogFragment showMyDialog(int dialogID, String title,String message,String leftBtnText,String rightBtnText){
		return showMyDialog(dialogID, title, message, leftBtnText, rightBtnText, this); 
	}
	/**
	 * 同上，按钮点击回调时调用listener的方法
	 */
	public MyDialogFragment showMyDialog(int dialogID, String title,String message,String leftBtnText,String rightBtnText,MyDialogClickListener listener){
		MyDialogFragment dialog = MyDialogFragment.newInstance(); 
		dialog.setDialogID(dialogID).setDialogClickListener(listener).setTitle(title)
		.setMessage(message).setLeftButtonText(leftBtnText).setRightButtonText(rightBtnText);
		dialog.show(getSupportFragmentManager(), "dialog_"+dialogID); 
		return dialog;
	}
	
	/**
	 * 显示选项卡控件
	 * <p>效果见：<a href="https://raw.githubusercontent.com/baoyongzhang/ActionSheetForAndroid/master/screenshot-2.png">UI效果演示图</a>
	 * @param items 选择项的文字
	 * @param listener 选择之后的监听
	 */
	public void showActionSheet(String[] items, ActionSheetListener listener){
		showActionSheet(items, listener, -1);
	}
	
	/**
	 * 
	 * @param items
	 * @param listener
	 * @param textColor 选项文字的颜色, 不能直接传R.color.xx, 要这样传 getResources.getColor(R.color.xx)
	 */
	public void showActionSheet(String[] items, ActionSheetListener listener, int textColor){
		MyActionSheetFragment.newInstance().setItems(items).setItemTextColor(textColor)
		.setActionSheetListener(listener)
		.show(getSupportFragmentManager(), "actionSheet");
	}

	@Override
	public void onDialogLeftBtnClick(MyDialogFragment dialog) {
		dialog.dismiss();
	}

	@Override
	public void onDialogRightBtnClick(MyDialogFragment dialog) {
		dialog.dismiss();
	}
}
