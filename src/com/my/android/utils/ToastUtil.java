package com.my.android.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	
	/**
	 * 显示Toast提示
	 * @param content 要提示的内容
	 */
	public static void showToast(Context context,String content){
		showToast(context, content,1);
	}
	
	/**
	 * 显示Toast提示
	 * @param content 要提示的内容
	 */
	public static void showToast(Context context,String content,int length){
		Toast.makeText(context, content, length).show();
	}
}
