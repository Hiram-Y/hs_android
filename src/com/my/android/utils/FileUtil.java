package com.my.android.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * 文件操作工具类
 * @author hushuai
 *
 */
public class FileUtil {
	
	/**获取文件，如果文件或目录不存在则创建*/
	public static File getFile(String fileName) {
		File file = new File(fileName);
		return getFile(file);
	}
	
	/**获取文件，如果文件或目录不存在则创建*/
	public static File getFile(File file) {
		if (!file.exists() && !file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		return file;
	}
	
	/** 
	 * 删除文件;
	 * 如果是目录,删除目录下所有文件，目录本身不删除;
	 * 如果是文件则删除文件
	 * @param file 
	 **/
	public static void deleteFile(File file) {
		if (file != null && file.exists()) {
			if(file.isDirectory()){
				for (File item : file.listFiles()) {
					item.delete();
				}
			}else{
				file.delete();
			}
		}
	}
	/** 
	 * 删除文件;
	 * 如果是目录,删除目录下所有文件，目录本身不删除;
	 * 如果是文件则删除文件
	 * @param filepath  文件路径 
	 **/
	public static void deleteFile(String filepath) {
		deleteFile(new File(filepath));
	}
	
	/**
	 * 通过路径复制文件
	 * @param sourcePath 源文件路径  源文件不存在不做任何操作
	 * @param targetPath 目标文件路径 目标路径不存在会自己创建
	 * @throws IOException
	 */
	public static void copyFile(String sourcePath, String targetPath) throws IOException {
		copyFile(new File(sourcePath), new File(targetPath));
	}
	
	/**
	 * 复制文件
	 * @param sourceFile 源文件 源文件不存在不做任何操作
	 * @param targetFile 目标文件 目标文件不存在会自动创建
	 * @throws IOException
	 */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
    	if(!sourceFile.exists()){
    		return;
    	}
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
        	if(!targetFile.getParentFile().exists()){
        		targetFile.getParentFile().mkdirs();
        	}
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            IOUtil.inputStreamToOutputStream(inBuff, outBuff);
        }catch(Exception e){
        	LogUtil.log(e);
        }
    }
    
	
	/**
	 * 往文件里写内容,注意：该方法执行完后会自动关闭输入流。
	 * @param is 要写的内容
	 * @param fileName 要写入的文件（全路径+文件名）
	 * @param isappend 文件已存在时，是否添加内容到文件末尾， false 直接覆盖
	 * @return 是否写入成功
	 */
	public static boolean writeFile(String fileName, InputStream is,boolean isappend) {
		FileOutputStream fos = null;
		try {
			File saveFile = getFile(fileName);
			fos = new FileOutputStream(saveFile,isappend);
			return IOUtil.inputStreamToOutputStream(is, fos);
		} catch (Exception e) {
			LogUtil.log(e);
		} 
		return false;
	}
	/**
	 * 往文件里写内容
	 * @param str 要写的内容
	 * @param fileName 要写入的文件（全路径+文件名）
	 * @param isappend 文件已存在时，是否添加内容到文件末尾， false 直接覆盖
	 * @return 是否写入成功
	 */
	public static boolean writeFile(String fileName, String str,boolean isappend) {
		return writeFile(fileName, str.getBytes(), isappend);
	}
	/**
	 * 往文件里写内容
	 * @param bytes 要写的内容
	 * @param fileName 要写入的文件（全路径+文件名）
	 * @param isappend 文件已存在时，是否添加内容到文件末尾， false 直接覆盖
	 * @return 是否写入成功
	 */
	public static boolean writeFile(String fileName, byte[] bytes,boolean isappend) {
		FileOutputStream fos = null;
		try {
			File saveFile = getFile(fileName);
			fos = new FileOutputStream(saveFile,isappend);
			fos.write(bytes);
			fos.flush();
			return true;
		} catch (Exception e) {
			LogUtil.log(e);
		} finally{
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
		return false;
	}
}
