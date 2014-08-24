package com.my.android.utils;

import java.util.ArrayList;
import java.util.Arrays;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SPUtil {
	private static final String SPLIT_KEY = "|_@_@_|";
	
	public static SharedPreferences getDefaultSP(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public static Editor getEditor(Context context){
		return getDefaultSP(context).edit();
	}
	
    public static boolean putString(Context context, String key, String value) {
        Editor editor = getEditor(context);
        editor.putString(key, value);
        return editor.commit();
    }
    /**不存在返回null*/
    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    public static String getString(Context context, String key, String defaultValue) {
        return getDefaultSP(context).getString(key, defaultValue);
    }

    public static boolean putInt(Context context, String key, int value) {
        Editor editor = getEditor(context);
        editor.putInt(key, value);
        return editor.commit();
    }
    /**不存在返回-1*/
    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return getDefaultSP(context).getInt(key, defaultValue);
    }

    public static boolean putLong(Context context, String key, long value) {
        Editor editor = getEditor(context);
        editor.putLong(key, value);
        return editor.commit();
    }
    
    /**不存在返回-1*/
    public static long getLong(Context context, String key) {
        return getLong(context, key, -1);
    }

    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sp = getDefaultSP(context);
        return sp.getLong(key, defaultValue);
    }

    public static boolean putFloat(Context context, String key, float value) {
        Editor editor = getEditor(context);
        editor.putFloat(key, value);
        return editor.commit();
    }
    /**不存在返回-1*/
    public static float getFloat(Context context, String key) {
    	return getFloat(context, key, -1);
    }
    
    public static float getFloat(Context context, String key, float defaultValue) {
    	return getDefaultSP(context).getFloat(key, defaultValue);
    }

    public static boolean putBoolean(Context context, String key, boolean value) {
        Editor editor = getEditor(context);
        editor.putBoolean(key, value);
        return editor.commit();
    }
    
    /**不存在返回false*/
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getDefaultSP(context).getBoolean(key, defaultValue);
    }
    
    public static boolean putStringArray(Context context,String key ,String[] value){
    	if(ArrayUtil.isEmpty(value)){
    		return false;
    	}
    	StringBuilder sb = new StringBuilder();
    	int lenght = value.length;
    	for(int i=0;i<lenght;i++){
    		sb.append(value[i]);
    		if(i != lenght -1){
    			sb.append(SPLIT_KEY);
    		}
    	}
    	return putString(context, key, sb.toString());
    }
    
    public static String[] getStringArray(Context context,String key){
    	String str = getString(context, key);
    	if(str == null){
    		return null;
    	}
    	return str.split(SPLIT_KEY);
    }
    
    public static boolean putStringList(Context context,String key ,ArrayList<String> list){
    	if(ArrayUtil.isEmpty(list)){
    		return false;
    	}
    	StringBuilder sb = new StringBuilder();
    	int lenght = list.size();
    	for(int i=0;i<lenght;i++){
    		sb.append(list.get(i));
    		if(i != lenght -1){
    			sb.append(SPLIT_KEY);
    		}
    	}
    	return putString(context, key, sb.toString());
    }
    
    public static ArrayList<String> getStringList(Context context,String key){
    	String str = getString(context, key);
    	if(str == null){
    		return null;
    	}
    	return (ArrayList<String>) Arrays.asList(str.split(SPLIT_KEY));
    }
}