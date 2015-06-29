package com.my.android.fragment;

import com.my.android.activity.MyBaseActivity;
import com.my.android.bean.RequestBean;
import com.my.android.network.MyRequest;
import com.my.android.network.MyRequestListener;
import com.my.android.network.MyRequestQueue;
import com.my.android.network.MyResponse;
import com.my.android.network.NetworkMethod;
import com.my.android.utils.ToastUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
/**
 * 只能在MyBaseActivity及其子类中使用 MyBaseFragment及其子类
 * @author hushuai
 *
 */
public class MyBaseFragment extends Fragment implements MyRequestListener,OnClickListener{
	protected MyBaseActivity mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		mActivity = (MyBaseActivity) getActivity();
		return v;
	};
	
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
		return mActivity.requestUrl(bean, NetworkMethod.POST, showLoading,listenter);

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
	 * @param method 请求方法
	 * @param showLoading
	 * @param listenter 请求回调监听类
	 * @return
	 */
	public MyRequest requestUrl(int id, String url,NetworkMethod method,boolean showLoading,MyRequestListener listenter){
		return mActivity.requestUrl(id, url, method, showLoading, listenter);
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
		mActivity.onErrorRequest(id, response);
	}
	
	/**fragment 关联的视图销毁时取消当前fragment监听的所有请求*/
	@Override
	public void onDestroyView() {
		MyRequestQueue.cancelAll(this.getClass().getName());
		super.onDestroyView();
	}
	
	/**fragment 销毁时取消当前fragment监听的所有请求*/
	@Override
	public void onDestroy() {
		MyRequestQueue.cancelAll(this.getClass().getName());
		super.onDestroy();
	}
	/**fragment 从activity中移除时取消当前fragment监听的所有请求*/
	@Override
	public void onDetach() {
		MyRequestQueue.cancelAll(this.getClass().getName());
		super.onDetach();
	}
	
	@Override
	public void onClick(View v) {
		
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
		ToastUtil.showToast(getActivity(), content,length);
	}
}
