package com.my.android.utils;

import java.io.File;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

/**
 * 清除本应用数据工具类
 */
public class DataCleanUtil {
	@SuppressLint("SdCardPath")
	public static final String dataDic = "/data/data/";
	/** 
	 * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) 
	 * @param context 
	 */
	public static void cleanInternalCache(Context context) {
		FileUtil.deleteFile(context.getCacheDir());
	}

	/** 
	 * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) 
	 * @param context 
	 */
	public static void cleanDatabases(Context context) {
		FileUtil.deleteFile(new File(dataDic+ context.getPackageName() + "/databases"));
	}

	/**
	 * 清除本应用所有SharedPreference(/data/data/com.xxx.xxx/shared_prefs) 
	 * @param
	 * context
	 */
	public static void cleanSharedPreference(Context context) {
		FileUtil.deleteFile(new File(dataDic + context.getPackageName() + "/shared_prefs"));
	}

	/**
	 * 按名字清除本应用数据库 
	 * @param context 
	 * @param dbName 
	 */
	public static void cleanDatabaseByName(Context context, String dbName) {
		context.deleteDatabase(dbName);
	}

	/**
	 * 清除本应用所有文件 /data/data/com.xxx.xxx/files下的内容 
	 * @param context 
	 */
	public static void cleanFiles(Context context) {
		FileUtil.deleteFile(context.getFilesDir());
	}

	/**
	 * 清除外部cache下的内容(/mnt/sdcard/Android/data/com.xxx.xxx/cache) 
	 * @param context
	 */
	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			FileUtil.deleteFile(context.getExternalCacheDir());
		}
	}

	/** 
	 ** 清除自定义路径下的文件
	 *** @param filePath 
	 **/
	public static void cleanCustomCache(String filePath) {
		FileUtil.deleteFile(new File(filePath));
	}

	/** 
	 * 清除本应用所有的数据
	 * @param context 
	 * @param filepath 自定义其他文件路径
	 */
	public static void cleanApplicationData(Context context, String[] filepath) {
		cleanInternalCache(context);
		cleanExternalCache(context);
		cleanDatabases(context);
		cleanSharedPreference(context);
		cleanFiles(context);
		if(filepath != null){
			for (String filePath : filepath) {
				cleanCustomCache(filePath);
			}
		}
	}

}
