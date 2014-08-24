package com.my.android.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 字符串操作相关工具类
 * @author hushuai
 *
 */
public class StringUtil {
	
	/**
	 * <p>把http url请求参数转换成有序map
	 * <p>例如： http://www.baidu.com/xxx/xxx?key1=123&key2=456&key3=987
	 * <p>转换成： 
	 * <p>map.put("key1","123");
	 * <p>map.put("key2","456");
	 * <p>map.put("key3","987");
	 * @param url
	 * @return 转换后的有序 map；
	 */
	public static LinkedHashMap<String,String> convertUrlToMap(String url){
		if(isEmpty(url)){
			return null;
		}
		int index = url.indexOf("?");
		if(index != -1){
			url = url.substring(index + 1);
		}
		
		String[] arr = url.split("&");
		if(arr == null || arr.length==0){
			return null;
		}
		
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for(String para : arr){
			String[] p = para.split("=");
			if(p != null && p.length == 2){
				map.put(p[0], p[1]);
			}else{
				map.put(para, "");
			}
		}
		return map;
	}
	
	/**判断字符串是否为null 或取消首尾空格后 是否为空字符串*/
	public static boolean isEmpty(String str){
		if(str == null){
			return true;
		}
		
		if("".equals(str.trim())){
			return true;
		}
		return false;
	}
	
	/** 判断字符串是否不为空*/
	public static boolean isNotEmpty(String str){
		return !isEmpty(str);
	}
	
	
	/** 判断字符串是否全是数字 */
	public static boolean isAllNumer(String str) {
		if (str.trim().matches("\\d*")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**是否是手机号码(验证长度等于11，首位数字为1，只能为数字)*/
	public static boolean isPhoneNum(String str) {
		if(TextUtils.isEmpty(str)){
			return false;
		}
		int length = str.trim().length();
		
		if(length !=11 || !str.startsWith("1")){
			return false;
		}	
		return isAllNumer(str);
	}
	
	/**得到md5后的字符串*/
	public static String getMd5(String pInput) {
		try {
			MessageDigest lDigest = MessageDigest.getInstance("MD5");
			lDigest.update(pInput.getBytes());
			BigInteger lHashInt = new BigInteger(1, lDigest.digest());
			return String.format("%1$032X", lHashInt);
		} catch (NoSuchAlgorithmException lException) {
			throw new RuntimeException(lException);
		}
	}
	
	/** 将字符串的首字符转化成小写*/
	public static String tofirstLowerCase(String str) {
		if (str != null && str.length() > 0) {
			StringBuilder sb = new StringBuilder(str);
			sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
			return sb.toString();
		} 
		return str;
	}

	/** 将字符串的首字符转化成大写*/
	public static String tofirstUpperCase(String str) {
		if (str != null && str.length() > 0) {
			StringBuilder sb = new StringBuilder(str);
			sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
			return sb.toString();
		} 
		return str;

	}
	
	/**创建json 对象*/
	public static JSONObject stringToJson(String jsonStr) {
		if (jsonStr == null) {
			return null;
		}
		JSONObject json = null;
		try {
			json = new JSONObject(jsonStr);
		} catch (JSONException e) {
			LogUtil.log(e);
		}
		return json;
	}
}
