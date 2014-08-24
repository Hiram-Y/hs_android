package com.my.android.network;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.android.volley.NetworkError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.my.android.utils.IOUtil;
import com.my.android.utils.LogUtil;
import com.my.android.utils.StringUtil;

import android.content.Context;

/**
 * <p>此类是最简单的网络请求类， 采用的是最基础的 java.net.HttpURLConnection。
 * <p>只推荐在子线程中同步调用网络请求时使用此类，主线程中请使用Volley
 * <p>请注意 此类不支持wap 接入点连网方式
 * <p>此类支持https
 * <p>此类支持 session (无需自己手动设置，本类会自动记录cookie并回传给服务器)
 * <p>此类支持自定义头字段  有需要请调用setProperty（）方法设置
 * <p>此类支持自定义连接超时时间和读取超时时间  默认时间分别为 30秒，60秒， 调用setConnect_outTime ，setRead_outTime 方法设置
 * @author hushuai
 */
public class MyUrlConnection {
	public static HashMap<String, String> sessionMap =  new HashMap<String, String>();
	private static String mt="";
	private static HashMap<String, String> propertyMap = new HashMap<String, String>();
	private static int connect_outTime = 30*1000;
	private static int read_outTime = 1000*60*1;
	private final static int TIME_OUT_CODE = 504;
	private final static int SUCCEED_CODE = 200;
	
	public static void setProperty(String propertyKey,String propertyValue) {
		propertyMap.put(propertyKey, propertyValue);
	}
	
	public static void setConnect_outTime(int connect_outTime) {
		MyUrlConnection.connect_outTime = connect_outTime;
	}
	public static void setRead_outTime(int read_outTime) {
		MyUrlConnection.read_outTime = read_outTime;
	}

	static{
//		mt = ("&os="+android.os.Build.BRAND+","+android.os.Build.PRODUCT+","+android.os.Build.VERSION.RELEASE);	
	}   
    /**
     * 设置公共参数mt, 此参数是全局的，设置后所有请求都会加上，请酌情使用。
     * @param mtStr
     */
	public static void setMt(String mtStr){
		mt = mtStr;
	}
	
	/**
	 * 获取给定链接的数据，默认使用post方法。
	 * @param context
	 * @param path 
	 * @return 获取的数据
	 */
	public static String getConnectionStr(String path) throws TimeoutError, VolleyError{
			String responseStr = getConnectionStr(path, NetworkMethod.POST);
			return responseStr;
	}
	
    /**
     * 获取给定链接的数据
     * @param context
     * @param path 
     * @param method 请求方法
     * 
     * @return 获取的数据
     */
    public static String getConnectionStr(String path, NetworkMethod method) throws TimeoutError, VolleyError{
    	InputStream is = getInputStream(path, method);
		String responseStr = IOUtil.inputStreamToString(is);
		return responseStr;
	}
    
    /**
     * 获取给定链接的数据,默认使用post方法
     * @param context
     * @param path 
     * 
     * @return 获取的输入流
     */
    public static InputStream getInputStream(Context context, String path) throws TimeoutError, VolleyError {
    	return getInputStream(path, NetworkMethod.POST);
    }
    
    /**
     * 根据给定的path从本地获取离线文件数据
     */
//    public static InputStream getLocalInputStream(Context context,String path){
//    	String[] arr = path.split("&");
//    	String localPaht = arr[0];
//    	int start = localPaht.lastIndexOf("/")+1;
//		int end = localPaht.lastIndexOf("?");
//		String localName = localPaht.substring(start,end);
//		String localCode = localPaht.substring(localPaht.lastIndexOf("=")+1, localPaht.length());
//		String localFile = localName+localCode+MyConst.LOCAL_POSTFIX;
//		InputStream is = null;
//		try {
//			is = context.getAssets().open(localFile);
//		} catch (IOException e) {
//			LogUtil.log(e);
//		}
//		return is;
//    }
    /**
     * 获取给定链接的数据
     * @param context
     * @param path 
     * @param method 请求方法
     * 
     * @return 获取的输入流
     */
    public static InputStream getInputStream(String path, NetworkMethod method) throws TimeoutError,VolleyError{
//    	if(MyTools.local && path.contains(MyConst.LOCALHOST_CODE)){	
//    		return getLocalInputStream(context,path);
//    	}
    	InputStream  is = null;
    	URLConnection connection = getConnection(path, method);
    	if(connection != null){
    		try {
				is = connection.getInputStream();
			}catch (SocketTimeoutException timeOut){
				throw new TimeoutError();
			}catch (IOException e) {
				throw new VolleyError("向服务器读取数据异常:"+e.getMessage());
			}
    		Map<String,List<String>> map = connection.getHeaderFields();
            List<String> cookies = map.get("Set-Cookie");
            if(cookies != null && !cookies.isEmpty()){
                String sessionId = "";
                int size = cookies.size();
                for(int i=0;i<cookies.size();i++){
                    try {
						String cookie = cookies.get(i);
						sessionId = sessionId + cookie.substring(0, cookie.indexOf(";"));
						if(i != size-1){
						    sessionId = sessionId + "; ";
						}
					} catch (Exception e) {
						LogUtil.log(e);
						continue;
					}
                }
                String url = path;
                int i = path.indexOf("/",path.indexOf("."));
                if(i<0){
                    i = path.indexOf("?");
                }
                if(i>0){
                    url = path.substring(0, i);
                }
                sessionMap.put(url, sessionId);
            }
    	}
    	return is;
    }
    
	private static HttpURLConnection getConnection(String path, NetworkMethod method) throws TimeoutError,ServerError,VolleyError{
		if(path == null || !path.startsWith("http")){
			throw new VolleyError("请求连接"+path+"为空或者不是http请求");
		}
		HttpURLConnection conn = getcon(path, method);
		int responseCode = 0;
		try {
			responseCode = conn.getResponseCode();
		} catch (IOException e) {
			throw new NetworkError(e);
		}
		if(responseCode!= SUCCEED_CODE ){
			if(responseCode == TIME_OUT_CODE){
				throw new TimeoutError();
			}else{
				throw new ServerError();
			}
		}
		return conn;
	}

	private static HttpURLConnection getcon(String path, NetworkMethod method) throws TimeoutError, VolleyError{
		HttpURLConnection conn = null;
		String postParam = "";	
		URL url = null;
		if (path.indexOf("?", 10) < 0){
			path = path + "?";
		}
		if(StringUtil.isNotEmpty(mt)){
			path = path + mt;
		}
		
		if (NetworkMethod.POST == method) {			
			int end = path.indexOf("?") + 1;
			postParam = path.substring(end, path.length());
			path = path.substring(0, end);
		}

		LogUtil.log(path+"\n请求参数:"+postParam);
		
		try {
			url = new URL(path);
		} catch (MalformedURLException e1) {
			throw new VolleyError(e1);
		}
		try {
			conn = (HttpURLConnection) url.openConnection();
		}catch (SocketTimeoutException timeOut){
			throw new TimeoutError();
		}catch (IOException e) {
			throw new VolleyError(e); 
		}
		conn.setConnectTimeout(connect_outTime);
		if(conn instanceof HttpsURLConnection){
			TrustManager[] tm = {new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}
				
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					
				}
			}};	
			
		    HostnameVerifier hv = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					LogUtil.log("hostname:"+hostname);
					LogUtil.log("SSLSession:"+session);
					return true;
				}
			}; 

		    try {
				SSLContext sslc = SSLContext.getInstance("TLS");    
				sslc.init(new KeyManager[0], tm, new SecureRandom());    
				SSLSocketFactory socketFactory = sslc.getSocketFactory();    
				((HttpsURLConnection)conn).setSSLSocketFactory(socketFactory);
				((HttpsURLConnection)conn).setHostnameVerifier(hv);
			} catch (Exception e) {
				LogUtil.log(e);
			}
	
		}
		
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setReadTimeout(read_outTime);
		if(propertyMap.size()>0){
			for(Entry<String, String> entry : propertyMap.entrySet()){
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		String host = path;
		int i = path.indexOf("/",path.indexOf("."));
		if(i<0){
			i = path.indexOf("?");
		}
		if(i>0){
			host = path.substring(0, i);
		}
		String session = sessionMap.get(host);
		if(session != null){ 
			conn.setRequestProperty("cookie", session);
		} 
		if(NetworkMethod.POST == method){
			try {
				conn.setRequestMethod("POST");
			} catch (ProtocolException e) {
			}
			if(!"".equals(postParam)){
				conn.setDoOutput(true);
				try {
					OutputStream outputStream = conn.getOutputStream();
					outputStream.write(postParam.getBytes());
				} catch (IOException e) {
					throw new VolleyError("往服务器传输数据失败："+ e.getMessage());
				}
			}
		}
		return conn;
	}
}
