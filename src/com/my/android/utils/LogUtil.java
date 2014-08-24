package com.my.android.utils;

import android.util.Log;

/**
 * 调试信息相关工具类。
 * <p>由于现在第三方统计SDK已经非常完善，基本上都有异常收集、错误分析功能，
 * <p>所以本类不做信息收集和上传信息到服务器功能，只做简单的控制台打印。
 * @author hushuai
 */
public class LogUtil {
	private static final String LOG_TAG = "-------------------";
	public static void log(String log){
		if(AppUtil.isDebug){
			Log.e(AppUtil.packageName,log+LOG_TAG);
		}
	}
	
	public static void log(Exception e){
		if(AppUtil.isDebug){
			e.printStackTrace();
		}
	}
}
