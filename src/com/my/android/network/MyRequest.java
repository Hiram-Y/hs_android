package com.my.android.network;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.my.android.activity.MyBaseActivity;
import com.my.android.bean.RequestBean;
import com.my.android.fragment.MyBaseFragment;
import com.my.android.utils.BeanUtil;
import com.my.android.utils.LogUtil;
import com.my.android.utils.StringUtil;
/**请求封装类*/
public class MyRequest extends Request<MyResponse>{
	public static final int def_identify = -1;
	private int identify ;
	private Map<String, String> paramsMap;
	private MyRequestListener listener;
	//其他一些自定义数据对象，可根据实际需要使用。
	//请求时填充的是什么对象，请求完成后通过MyResopnse.getCustomTag 获取到的就是什么。
	private Map<String,Object> tagMap;
	private RequestBean requestBean;
	
	public MyRequest(String url) {
		this(def_identify,url);
	}
	
	public MyRequest(int id,String url) {
		this(id,NetworkMethod.POST,url,null);
	}
	
	public MyRequest(String url, MyRequestListener listener) {
		this(def_identify,NetworkMethod.POST,url,listener);
	}
	
	public MyRequest(int id,String url, MyRequestListener listener) {
		this(id,NetworkMethod.POST,url,listener);
	}

	public MyRequest(final int id,NetworkMethod method, String url,final MyRequestListener listener) {
		super(method.getValue(), handleUrl(method,url), new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if(listener != null){
					dismissLoading(listener, id);
					MyResponse response = new MyResponse(-1,null,null,false);
					response.setError(error);
					listener.onErrorRequest(id,response);
				}else{
					LogUtil.log(error);
				}
			}
		});
		LogUtil.log("请求url:"+url);
		if(NetworkMethod.POST == method){
			paramsMap = StringUtil.convertUrlToMap(url);
		}
		this.listener = listener;
		this.identify = id;
		setTag(listener);
		setShouldCache(false);
	}
	
	private static String handleUrl(NetworkMethod method,String url){
		if(method != NetworkMethod.POST){
			return url;
		}
		if(url != null){
			int index = url.indexOf("?");
			if(index != -1){
				return url.substring(0, index);
			}
		}
		return url;
	}
	
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		if(paramsMap != null){
			return paramsMap;
		}
		return super.getParams();	
	}
	
	@Override
	protected Response<MyResponse> parseNetworkResponse(NetworkResponse response) {
		try {
			MyResponse myResponse = new MyResponse(response.statusCode, response.data, response.headers, response.notModified);
			myResponse.tagMap = tagMap;
			myResponse.setRequestBean(requestBean);
			return Response.success(myResponse,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception e) {
			return Response.error(new ParseError(e));
		} 
	}

	@Override
	protected void deliverResponse(MyResponse response) {
		LogUtil.log("返回数据："+response);
		//回调为空，请求成功后不做任何处理
		if(listener == null){
			return;
		}
		dismissLoading(listener,identify);
		//返回数据为空
		if(response == null){
			response = new MyResponse(-1, null, null, false);
			response.setError(new VolleyError("返回数据为空"));
			response.tagMap = tagMap;
			response.setRequestBean(requestBean);
			listener.onErrorRequest(identify, response);
			return;
		}

		if(requestBean == null){
			listener.onSuccessRequest(identify,response);
			return;
		}
		
		String callbackName = requestBean.response_methodName;
		if(StringUtil.isEmpty(callbackName)){
			listener.onSuccessRequest(identify,response);
			return;
		}
		
		//使用了RequestBean， 回调listener中的方法名为：RequestBean.response_methodName 的方法
		//参数为把返回数据封装后的RequestBean
		Exception error = null;
		try {
			Class<? extends RequestBean> clazz = requestBean.getClass();
			RequestBean bean = BeanUtil.jsonToBean(response.stringData, requestBean);
			java.lang.reflect.Method callback = listener.getClass().getDeclaredMethod(callbackName, clazz);
			callback.setAccessible(true);
			callback.invoke(listener, new Object[]{bean});
			return;
		} catch (NoSuchMethodException e) {
			error = e;
			LogUtil.log("没有在"+listener.getClass().getName()+"中找到"+callbackName+"方法，请检查");
		} catch (Exception e) {
			error = e;
			LogUtil.log("调用"+callbackName+"方法失败，请检查相关信息是否正确，如：方法是否存在，参数是否正确等");
		}
		response.setError(new VolleyError(error));
		listener.onErrorRequest(identify, response);
		
	}

	private static void dismissLoading(MyRequestListener listener,int identify) {
		if(listener instanceof MyBaseActivity){
			MyBaseActivity activity = (MyBaseActivity) listener;
			activity.dismissLoading(identify);
		}else if(listener instanceof MyBaseFragment){
			MyBaseFragment fragment = (MyBaseFragment) listener;
			MyBaseActivity activity = (MyBaseActivity) fragment.getActivity();
			activity.dismissLoading(identify);
		}
	}
	
	public int getIdentify() {
		return identify;
	}

	public void setCustomTag(String key,Object value) {
		if(tagMap == null){
			tagMap = new HashMap<String, Object>();
		}
		tagMap.put(key, value);
	}

	public Object getCustomTag(String key) {
		if(tagMap == null){
			return null;
		}
		return tagMap.get(key);
	}

	public RequestBean getRequestBean() {
		return requestBean;
	}

	public void setRequestBean(RequestBean requestBean) {
		this.requestBean = requestBean;
	}

}
