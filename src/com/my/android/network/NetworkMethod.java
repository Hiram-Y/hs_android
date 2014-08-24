package com.my.android.network;

import com.android.volley.Request.Method;
/**
 * 网络请求方式
 * @author hushuai
 *
 */
public enum NetworkMethod {	
	 DEPRECATED_GET_OR_POST (Method.DEPRECATED_GET_OR_POST),
     GET (Method.GET),
     POST (Method.POST),
     PUT (Method.PUT),
     DELETE (Method.DELETE),
     HEAD (Method.HEAD),
     OPTIONS (Method.OPTIONS),
     TRACE (Method.TRACE),
     PATCH (Method.PATCH);
	
	private final int value;
    public int getValue() {
        return value;
    }
    //构造器默认也只能是private, 从而保证构造函数只能在内部使用
    NetworkMethod(int value) {
        this.value = value;
    }
	public static NetworkMethod valueOf(int method) {
		switch (method) {
		case -1:
			return DEPRECATED_GET_OR_POST;
		case 0:
			return GET;
		case 1:
			return POST;
		case 2:
			return PUT;
		case 3:
			return DELETE;
		case 4:
			return HEAD;
		case 5:
			return OPTIONS;
		case 6:
			return TRACE;
		case 7:
			return PATCH;
		default:
			break;
		}
		return POST;
	}
}
