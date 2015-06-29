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
import com.my.android.fragment.MyImageChooseFragment.ChooseCompleteListener;
import com.my.android.fragment.MyImageChooseFragment;
import com.my.android.listener.MyAsyncListener;
import com.my.android.network.MyRequest;
import com.my.android.network.MyRequestListener;
import com.my.android.network.MyRequestQueue;
import com.my.android.network.MyResponse;
import com.my.android.network.NetworkMethod;
import com.my.android.utils.ImageUtil;
import com.my.android.utils.StringUtil;
import com.my.android.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import android.widget.Toast;

/**
 * 所有Activity的父类，如无特殊需求，项目中的Activity都应该是该类的直接或间接子类
 * @author hushuai
 */
public class MyBaseActivity extends FragmentActivity implements MyRequestListener,OnClickListener,MyDialogClickListener,MyAsyncListener{	
	private View loadingView;
	private static String loadingText;
	
	private HandlerThread my_handlerThread;
	private Handler my_uiHandler,my_asyncHandler;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	}

	public void startActivity(Class<?extends Activity> clazz){
		startActivity(new Intent(this, clazz));
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
	
	/**
	 * 请求网络图片
	 * @param imageView
	 * @param url
	 * @param defaultId 默认图片资源id
	 */
	public void requestImage(ImageView imageView,String url,int defaultId){
		ImageUtil.requestImage(imageView, url, defaultId, null, this.getClass().getName());
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
		String content = null;
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
			content = error.getMessage();
		}
		if(StringUtil.isEmpty(content)){
			content = "获取数据失败";
		}
		showToast(content);
	}
	
	/**
	 * 开启异步操作, 多个异步按顺序执行， 不支持并发。
	 * @param id 异步操作的id， 
	 * 异步耗时操作对应在doInBackground方法里完成，耗时操作完成后对应在doInUI方法里操作界面。
	 */
	public void startAsync(final int id){
		startAsync(id,this);
	}
	/**
	 * 开启异步操作,操作完回调UI, 多个异步按顺序执行， 不支持并发。
	 * <pre>
	 * 		void doSomething(){
	 * 			//开启异步
	 * 			startAsync(1);
	 * 		}
	 * 		public Object doInBackground(int id){
	 * 			if(id == 1){
	 * 				xxxxxxxxx你要做的是事情xxxxxxxxxx
	 * 				return  xxx 你要返回给UI方法的数据
	 * 			}
	 * 		}
	 * 		public void doInUI(int id,Object result){
	 * 			if(id == 1){
	 * 				你要进行UI 操作的代码
	 * 			}
	 * 		}
	 * </pre>
	 * @param id 异步操作的id， 
	 * @param listener 异步监听
	 * 异步耗时操作对应在doInBackground方法里完成，耗时操作完成后对应在doInUI方法里操作界面。
	 */
	public void startAsync(final int id, final MyAsyncListener listener){
		if(listener == null){
			return;
		}
		initAsyncHandler(true);
		my_asyncHandler.post(new Runnable() {
			@Override
			public void run() {
				final Object obj = listener.doInBackground(id);
				if(my_uiHandler == null){
					return;
				}
				my_uiHandler.post(new Runnable() {
					@Override
					public void run() {
						listener.doInUI(id, obj);
					}
				});
			}
		});
	}
	
	/**在线程中执行任务, 纯后台执行，可直接调用， 执行完不回调UI*/
	public void doInBackground(Runnable run){
		initAsyncHandler(false);
		my_asyncHandler.post(run);
	}

	private void initAsyncHandler(boolean isInitUIHandler) {
		if(my_handlerThread == null || !my_handlerThread.isAlive()){
			my_handlerThread = null;
			my_handlerThread = new HandlerThread(this.getClass().getName());
			my_handlerThread.start();
			my_asyncHandler = new Handler(my_handlerThread.getLooper());
		}	
		if(my_uiHandler == null && isInitUIHandler){
			my_uiHandler = new Handler();
		}
		if(my_asyncHandler == null){
			my_asyncHandler = new Handler(my_handlerThread.getLooper());
		}
	}
	
	protected void stopAllAsync(){
		if(my_handlerThread != null && my_handlerThread.isAlive()){
			my_handlerThread.quit();
		}
		my_handlerThread = null;
		my_asyncHandler = null;
	}
	
	/**
	 * 此方法在子线程里执行，不阻塞UI，不能直接调用此方法，必须用startAsync(id)开启异步，
	 * @param id 异步耗时操作的id， 回调完成后回调doInUI()方法，id相对应
	 */
	public Object doInBackground(int id){
		return null;
	}
	
	/**
	 * doInBackground 对应的回调方法，此方法在主线程中执行，用来操作UI
	 * @param id
	 */
	public void doInUI(int id,Object result){
		
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
		if(loadingText != null){
			TextView tv = (TextView) loadingView.findViewById(R.id.tv_loadingHint);
			tv.setText(loadingText);
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
		ArrayList<Integer> list = new ArrayList<Integer>();
		loadingView.setTag(list);
		loadingView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
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
		if(loadingView == null || loadingView.getParent() == null){
			super.onBackPressed();
			return;
		}
		((ViewGroup)loadingView.getParent()).removeView(loadingView);
		cancelSyncRequest();
	}
	
	@Override
	public void finish() {
		release();
		super.finish();
	}
	
	private void release(){
		stopAllAsync();
		my_uiHandler = null;
		MyRequestQueue.cancelAll(this.getClass().getName());
	}
	
	/**取消‘同步’ 网络请求
	 * 这里的‘同步’是指业务需求和流程上的同步。
	 */
	private void cancelSyncRequest() {
		MyRequestQueue.cancelAll(new RequestFilter() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean apply(Request<?> request) {
				if(loadingView == null){
					return false;
				}
				if(request instanceof MyRequest){
					MyRequest mr = (MyRequest) request;
					ArrayList<Integer> list = (ArrayList<Integer>) loadingView.getTag();
					if(list != null && list.contains(mr.getIdentify())){
						list.remove(Integer.valueOf(mr.getIdentify()));
						return true;
					}
				}
				return false;
			}
		});
	}
	
	/**activity销毁时取消当前activity监听的所有未完成的请求*/
	@Override
	protected void onDestroy() {
		release();
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
		showToast(content,Toast.LENGTH_SHORT);
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
	 * @param textColor 选项文字的颜色, 不能直接传R.color.xx, 要这样传 getResources.getColor(R.color.xx), 或者直接传16进制色值
	 */
	public void showActionSheet(String[] items, ActionSheetListener listener, int textColor){
		MyActionSheetFragment.newInstance().setItems(items).setItemTextColor(textColor)
		.setActionSheetListener(listener)
		.show(getSupportFragmentManager(), "actionSheet");
	}
	
	/**调用图片选择、裁剪
	 *@param isCrop 选择图片后是否裁剪
	 *@param listener 选择、裁剪后的回调监听
	 */
	public MyImageChooseFragment showImageChooseActionSheet(boolean isCrop,ChooseCompleteListener listener){
		MyImageChooseFragment icf = MyImageChooseFragment.newInstance(isCrop,0,0,listener);
		icf.show(getSupportFragmentManager(), "imageChoose");
		return icf;
	}
	/**
	 * @param isCrop 是否需要裁剪
	 * @param scale_width、scale_height 裁剪比例 1：1表示裁剪正方形
	 * @param listener 选择裁剪完成后的回调
	 * <p>isCrop为true时scale_width、scale_height参数才有效
	 */
	public MyImageChooseFragment showImageChooseActionSheet(boolean isCrop,int scale_width,int scale_height, ChooseCompleteListener listener){
		MyImageChooseFragment icf = MyImageChooseFragment.newInstance(isCrop,0,0,listener);
		icf.show(getSupportFragmentManager(), "imageChoose");
		return icf;
	}

	@Override
	public void onDialogLeftBtnClick(MyDialogFragment dialog) {
		dialog.dismiss();
	}

	@Override
	public void onDialogRightBtnClick(MyDialogFragment dialog) {
		dialog.dismiss();
	}
	
	public String getEditString(int id){
		return getEditString(findEditText(id));
	}
	
	public String getEditString(EditText et) {
		return et.getText().toString().trim();
	}
}
