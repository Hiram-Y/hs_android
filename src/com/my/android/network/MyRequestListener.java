package com.my.android.network;

/**
 * 网络请求回调接口
 * @author hushuai
 *
 */
public interface MyRequestListener{
	/**
	 * 请求成功回调方法
	 * @param response
	 */
	public void onSuccessRequest(int id,MyResponse response);
	/**
	 * 请求出错回调方法（无网络连接、连接超时，网络错误，服务器错误，数据解析以及其他异常）
	 * @param response
	 */
	public void onErrorRequest(int id,MyResponse response);
}
