package com.my.android.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * IO流操作工具类
 * @author hushuai
 *
 */
public class IOUtil {
    /**
	 * 将输入流转换为字符串，注意：该方法会自动关闭输入流
	 * @param is 要转换的输入流
	 * @return 转换之后的字符串
	 */
	public static String inputStreamToString(InputStream is){
		if (is == null) {
			return null;
		}
		byte[] data = inputStreamTobyte(is);
		return data == null ? null : new String(data);
	}
	
	/**
	 * 将输入流转化成字节数组，注意：该方法会自动关闭输入流
	 * @param is 要转化的输入流
	 * @return 转化之后的字节数组
	 */
	public static byte[] inputStreamTobyte(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		boolean b = inputStreamToOutputStream(is, bos);
		return b ? bos.toByteArray() : null;
	}
	
	/**
	 * 将输入流写到输出流，注意：该方法会自动关闭输入、输出流
	 * @return 是否写入成功
	 */
	public static boolean inputStreamToOutputStream(InputStream is, OutputStream os) {
		try {
			if (is != null && os != null) {
				byte[] b = new byte[1024 * 10];
				int i = 0;
				while ((i = is.read(b)) != -1) {
					os.write(b, 0, i);
				}
				return true;
			}
		} catch (IOException e) {
			LogUtil.log(e);
		} finally {
			try {
				os.flush();
				is.close();
				os.close();
			} catch (IOException e) {
				LogUtil.log(e);
			}
		}
		return false;
	}
}
