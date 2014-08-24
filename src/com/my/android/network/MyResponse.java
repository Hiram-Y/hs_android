package com.my.android.network;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.my.android.bean.RequestBean;
import com.my.android.utils.IOUtil;
import com.my.android.utils.LogUtil;

public class MyResponse extends NetworkResponse {
	public final String stringData;
	Map<String,Object> tagMap;
	private VolleyError error;
	private RequestBean requestBean;

	public MyResponse(int statusCode, byte[] data, Map<String, String> headers,boolean notModified) {
		super(statusCode, data, headers, notModified);
		this.stringData = getRealString(data);
	}

	private int getShort(byte[] data) {
		return (int) ((data[0] << 8) | data[1] & 0xFF);
	}

	/**数据经过gzip 压缩要特殊处理*/
	private String getRealString(byte[] data) {
		if(data == null){
			return null;
		}
		try {
			byte[] h = new byte[2];
			h[0] = data[0];
			h[1] = data[1];
			int head = getShort(h);
			boolean t = head == 0x1f8b;
			InputStream in;
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			if (t) {
				in = new GZIPInputStream(bis);
			} else {
				in = bis;
			}
			return IOUtil.inputStreamToString(in);
		} catch (Exception e) {
			LogUtil.log(e);
		}
		return null;
	}
	
	@Override
	public String toString() {
		return stringData;
	}

	public void setCustomTag(String key,Object value) {
		if(tagMap == null){
			tagMap = new HashMap<String, Object>();
		}
		tagMap.put(key, value);
	}

	public Object getCustomTag(String key) {
		if(tagMap == null){
			return null;
		}
		return tagMap.get(key);
	}

	public VolleyError getError() {
		return error;
	}

	public void setError(VolleyError error) {
		this.error = error;
	}

	public RequestBean getRequestBean() {
		return requestBean;
	}

	public void setRequestBean(RequestBean requestBean) {
		this.requestBean = requestBean;
	}
}
