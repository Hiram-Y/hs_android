package com.my.android.utils;

import java.util.List;

public class ArrayUtil {

	public static boolean isEmpty(Object[] arr) {
		if (arr == null || arr.length == 0) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmpty(Object[] arr) {
		return !isEmpty(arr);
	}

	public static boolean isEmpty(List<?> list) {
		if (list == null || list.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmpty(List<?> list) {
		return !isEmpty(list);
	}

}
