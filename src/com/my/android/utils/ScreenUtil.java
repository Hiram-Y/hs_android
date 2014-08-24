package com.my.android.utils;

/**
 * 屏幕信息相关
 * @author hushuai
 *
 */
public class ScreenUtil {
	public static int screenWidth;
	public static int screenHeight;
	public static float density;
	public static float densityDpi;
	
	public static int hpx2otherpx(float pxValue) {
		return (int) ((pxValue / 1.5) * density);
	}

	public static int dip2px(float dipValue) {
		return (int) (dipValue * density);
	}

	public static int px2dip(float pxValue) {

		return (int) (pxValue /density);
	}

}
