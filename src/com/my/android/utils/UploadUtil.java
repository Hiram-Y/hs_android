package com.my.android.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.graphics.Bitmap;

/**
 * 上传文件到服务器， 采用的是HttpURLConnection 模拟表单形式
<p>也可以使用apache 开源的httpmime， 需要导入jar包，使用代码如下：
<p>   public static String uploadFile(String serviceUrl,String name,String filePath){
<p>    	try {
<p>			HttpPost httppost = new HttpPost(serviceUrl);  
<p>			File f= new File(filePath);
<p>			MultipartEntity mpEntity = new MultipartEntity();  
<p>			FileBody  file= new FileBody(f);  
<p>			mpEntity.addPart(name, file);
<p>			//如链接上带有其他参数需要把下面注射的代码打开
<p>//			String[] params = serviceUrl.substring(serviceUrl.indexOf("?")+1).split("&");
<p>//			for(int i=0;i<params.length;i++){
<p>//				String param = params[i];
<p>//				String key = param.split("=")[0];
<p>//				String value = param.split("=").length>1?param.split("=")[1]:"";
<p>//				mpEntity.addPart(key, new StringBody(value));
<p>//			}
<p>//		httppost.setEntity(mpEntity);
<p>			HttpClient hc = new DefaultHttpClient();
<p>			hc.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
<p>			HttpResponse response = hc.execute(httppost);
<p>			HttpEntity resEntity = response.getEntity();
<p>			String res = EntityUtils.toString(resEntity);
<p>			System.out.println(res);
<p>			return res;
<p>		} catch (ClientProtocolException e) {
<p>			e.printStackTrace();
<p>		} catch (IOException e) {
<p>			e.printStackTrace();
<p>		}
<p>		return "上传失败";
<p>    }

 * @author hushuai
 *
 */
public class UploadUtil {
	/**
	 * 上传文件到服务器, 模拟表单提交
	 * @param serviceUrl 上传到服务器的路径
	 * @param name  服务器接受的参数名
	 * @param obj 上传的内容（输入流或者字节数组）
	 */
	private static String uploadFile(String serviceUrl, String name,Object obj) {
		if(!(obj instanceof InputStream) && !(obj instanceof byte[])){
			return null;
		}
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		String response = null;
		try {
			URL url = new URL(serviceUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			/*内容类型*/
			con.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition:form-data;name=\"" + name
					+ "\";filename=\"" + name + "\"" + end);
			ds.writeBytes("Content-Type:application/octet-stream" + end);
			ds.writeBytes(end);
			InputStream is = null;
			if(obj instanceof InputStream){
				is = (InputStream) obj;
				/* 设置每次写入1024 * 10bytes */
				int bufferSize = 1024 *10;
				byte[] buffer = new byte[bufferSize];
				int length = -1;
				/* 从文件读取数据至缓冲区 */
				while ((length = is.read(buffer)) != -1) {
					/* 将资料写入DataOutputStream中 */
					ds.write(buffer, 0, length);
				}
			}else if(obj instanceof byte[]){
				ds.write((byte[])obj);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			ds.flush();
			/* 取得Response内容 */
			response = IOUtil.inputStreamToString(con.getInputStream());
			/* 关闭DataOutputStream */
			ds.close();
			/* close streams */
			if(is != null){
				is.close();
			}
		} catch (Exception e) {
			LogUtil.log(e);
		}
		return response;
	}

	/**
	 * 上传文件到服务器, 模拟表单提交
	 * @param serviceUrl  上传到服务器的路径
	 * @param name  服务器接受的参数名
	 * @param filePath  文件在本地的路径
	 */
	public static String uploadFile(String serviceUrl, String name,String filePath) {
		try {
			FileInputStream fileIs = new FileInputStream(filePath);
			return uploadFile(serviceUrl, name, fileIs);
		} catch (FileNotFoundException e) {
			LogUtil.log(e);
		}
		return null;
	}
	/**
	 * 上传图片到服务器, 模拟表单提交
	 * @param serviceUrl  上传到服务器的路径
	 * @param name  服务器接受的参数名
	 * @param Bitmap
	 */
	public static String uploadImage(String serviceUrl,String name,Bitmap bm) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			// 将Bitmap压缩成PNG编码，质量为100%
			bm.compress(Bitmap.CompressFormat.PNG, 100, os);
			return uploadFile(serviceUrl, name, os.toByteArray());
		} catch (Exception e) {
			LogUtil.log(e);
		}
		return null;
	}
	
	/**
	 * 上传图片到服务器, 模拟表单提交
	 * @param serviceUrl  上传到服务器的路径
	 * @param name  服务器接受的参数名
	 * @param byte[] 图片的字节数组
	 */
	public static String uploadImage(String serviceUrl,String name,byte[] byteArr) {
		return uploadFile(serviceUrl,name,byteArr);
	}
}
