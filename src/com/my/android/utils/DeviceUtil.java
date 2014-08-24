package com.my.android.utils;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class DeviceUtil {
	/**
	 * 获取设备唯一标识
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		try {
			//IMEI（imei）
			String imei = getIMEI(context);
			if(!TextUtils.isEmpty(imei)){
				return imei;
			}
			//wifi mac地址
			String macAddress = getMacAddress(context);
			if(!TextUtils.isEmpty(macAddress)){
				return macAddress;
			}
			//序列号（sn）
			String sn = getSN(context);
			if(!TextUtils.isEmpty(sn)){
				return sn;
			}
			
			//如果上面都没有， 则生成一个id：随机码
			String uuid = getUUID(context);
			if(!TextUtils.isEmpty(uuid)){
				return uuid;
			}
		} catch (Exception e) {
			return getUUID(context);
		}
		return "";
	}
	public static String getSN(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String sn = tm.getSimSerialNumber();
		if(sn == null){
			return "";
		}
		return sn;
	}
	public static String getIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId(); 
		if(imsi == null){
			return "";
		}
		return imsi;
	}
	
	
	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		if(imei==null){
			return "";
		}
		return imei;
	}
	
	public static String getMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String wifiMac = info.getMacAddress();
		if(wifiMac == null){
			return "";
		}
		return wifiMac;
	}

	/**
	* 得到一个全局唯一UUID
	*/
	public static String getUUID(Context context){
		SharedPreferences mShare = PreferenceManager.getDefaultSharedPreferences(context);
		String uuid = null;
		if(mShare != null){
			uuid = mShare.getString("uuid", "");
		}
		if(TextUtils.isEmpty(uuid)){
			uuid = UUID.randomUUID().toString();
			mShare.edit().putString("uuid", uuid).commit();
		}
		return uuid;
	}
}
