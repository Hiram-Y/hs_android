package com.my.android;

import com.android.volley.VolleyLog;
import com.my.android.network.MyRequestQueue;
import com.my.android.utils.AppUtil;
import com.my.android.utils.LogUtil;
import com.my.android.utils.ScreenUtil;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			//初始化网络请求队列
			MyRequestQueue.initRequestQueue(this);
			
			//读取应用相关配置、信息
			ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(),PackageManager.GET_META_DATA);
			if(appInfo != null){
				AppUtil.isDebug = appInfo.metaData.getBoolean("isDebug");
				AppUtil.isLocal = appInfo.metaData.getBoolean("isLocal");
				VolleyLog.DEBUG = AppUtil.isDebug;
			}
			//读取应用信息
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			AppUtil.appVersion = info.versionCode;
			AppUtil.appVersionName = info.versionName;
			AppUtil.packageName = getPackageName();
			//读取屏幕相关信息
			DisplayMetrics dm = getResources().getDisplayMetrics();
			ScreenUtil.screenWidth = dm.widthPixels;
			ScreenUtil.screenHeight = dm.heightPixels;
			ScreenUtil.density = dm.density;
			ScreenUtil.densityDpi = dm.densityDpi;
		} catch (Exception e) {
			LogUtil.log("初始化应用数据失败，请检查相关配置");
			LogUtil.log(e);
		}
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
