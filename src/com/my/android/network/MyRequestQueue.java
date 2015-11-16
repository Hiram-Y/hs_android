package com.my.android.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.toolbox.Volley;
import com.my.android.utils.LogUtil;

/**
 * 本类是Volley RequestQueue的封装类, 采用单例模式，在MyApplication里初始化，
 * 所以 Manifest 文件里的Application标签的name 属性一定要是MyApplication及其子类。 
 * 当然也可以自己在适当的地方调用本类的init方法进行初始化，已经初始化将不做任何操作，
 * <p>对google原生Volley 进行了修改，修改如下
 * <p>增加 https处理支持
 * <p>增加 session、cookie 支持
 * <p>增加 支持无网络时总是使用磁盘缓存数据（如果数据有缓存）
 * <p>可加入全局自定义头字段
 * <p>可加入全局自定义user-Agent
 * <p>可设置数据缓存目录
 * <p>可客户端控制数据缓存时间（默认图片永久缓存，普通接口数据不缓存），不依赖服务器头字段（服务器设置了缓存信息则以服务器的为准） 
 * @author hushuai
 */
public class MyRequestQueue {
    private static RequestQueue mRequestQueue;
//    private static ImageLoader mImageLoader;
    
    public static void initRequestQueue(Context context){
    	if(mRequestQueue == null){
			mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    	}
    }
    
	public static RequestQueue getRequestQueue(Context context) {
		initRequestQueue(context);
		return mRequestQueue;
	}
	
	public static MyRequest request(MyRequest request){
		if(!isInit()){
			return request;
		}
		mRequestQueue.add(request);
		return request;
	}
	
	/**
	 * 以POST方式请求url， 请求结果返回String
	 * @param url 请求的url 
	 * @param listener 请求监听（成功或失败）
	 */
	public static MyRequest requestUrl(String url,MyRequestListener listener){
		return requestUrl(MyRequest.def_identify, url, listener);
	}
	
	/**
	 * 以POST方式请求url， 请求结果返回String
	 * @param id 请求id
	 * @param url 请求的url 
	 * @param listener 请求监听（成功或失败）
	 */
	public static MyRequest requestUrl(int id,String url,MyRequestListener listener){
		return requestUrl(id, url, NetworkMethod.POST, listener);
	}
	
	/**
	 * 请求url， 请求结果返回String
	 * @param url 请求的url 
	 * @param id 请求id
	 * @param method 请求方式 {@link NetworkMethod}
	 * @param listener 请求监听（成功或失败）
	 */
	public static MyRequest requestUrl(int id,String url,NetworkMethod method,MyRequestListener listener){
		if(!isInit()){
			return null;
		}
		MyRequest request = new MyRequest(id,method,url,listener);
		mRequestQueue.add(request);
		return request;
	}

	private static boolean isInit() {
		if(mRequestQueue == null){
			LogUtil.log("MyRequestQueue 没有初始化， 请调用init 方法进行初始化");
			return false;
		}
		return true;
	}

//	public static ImageLoader getImageLoader(Context context) {
//		if(mImageLoader == null){
//			mInstance = new MyRequest(context);
//		}
//		return mImageLoader;
//	}
	/**
	 *把当前请求队列中所有未完成的请求都终止掉。
	 */
	public static void canceAll() {
		if(mRequestQueue == null){
			return;
		}
		MyRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
			@Override
			public boolean apply(Request<?> request) {
				return true;
			}
		});
//		mRequestQueue = null;
//		mImageLoader = null;
	}
	public static void cancelAll(RequestFilter filter) {
		if(mRequestQueue == null || filter == null){
			return;
		}
		mRequestQueue.cancelAll(filter);
	}
	/**
	 * 终止给定tag 的所有请求
	 * @param tag
	 */
	public static void cancelAll(final String tag) {
		if(mRequestQueue == null || tag == null){
			return;
		}
		MyRequestQueue.cancelAll(new RequestFilter() {
			@Override
			public boolean apply(Request<?> request) {
				if(tag.equals(request.getTag())){
					return true;
				}
				return false;
			}
		});
	}
	
	public static void destroy(){
		canceAll();
		mRequestQueue = null;
	}
}
