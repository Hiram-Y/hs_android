package com.my.android.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageLoader.MyImageListener;
import com.my.android.network.MyRequestQueue;

/**
 * 图片工具类
 * @author hushuai
 */
public class ImageUtil {
	
	public static StateListDrawable getStateDrawable(Drawable defDrawable,Drawable pressedDrawable){
		StateListDrawable bg = null;
		if(pressedDrawable!=null && defDrawable!=null){
			bg = new StateListDrawable();
			bg.addState(new int[]{android.R.attr.state_pressed,android.R.attr.state_enabled}, pressedDrawable);
			bg.addState(new int[]{android.R.attr.state_enabled,android.R.attr.state_focused}, pressedDrawable);
			bg.addState(new int[]{android.R.attr.state_enabled,android.R.attr.state_selected}, pressedDrawable);
	        bg.addState(new int[]{android.R.attr.state_enabled}, defDrawable);
		}
		return bg;
	}
	
	/**
	 * 请求网络图片,自带缓存,会优先从缓存获取
	 * @param imageView
	 * @param url 图片url
	 * @param defImageId 默认显示图片的资源id，没有传0
	 */
	public static void requestImage(ImageView imageView, String url,
			int defImageId) {
		requestImage(imageView, url, defImageId, null, null);
	}

	public static void requestImage(ImageView imageView, String url,
			int defImageId, ImageListener listener) {
		requestImage(imageView, url, defImageId, listener, null);
	}

	/**
	 * 请求网络图片,自带缓存,会优先从缓存获取
	 * @param imageView
	 * @param url  图片url
	 * @param defImageId 默认显示的图片资源id
	 * @param listener  图片请求监听类
	 * @param tag 请求图片的tag， 可用于手动取消请求
	 */
	public static void requestImage(ImageView imageView, String url,
			int defaultImageResId, ImageListener listener, String tag) {
		if (imageView == null || url == null || imageView.getContext() == null) {
			return;
		}
		ImageLoader imageLoader = new ImageLoader(
				MyRequestQueue.getRequestQueue(imageView.getContext()),
				MyImageCache.getInstance());

		if (listener == null) {
			listener = getImageListener(imageView, defaultImageResId, tag);
		}
		imageLoader.get(url, listener);
	}

	/**
	 * 获取图片请求监听 
	 * @param view
	 * @param defaultImageResId 默认显示图片
	 * @param 请求tag
	 * @return
	 */
	public static ImageListener getImageListener(final ImageView view,
			final int defaultImageResId, String tag) {
		MyImageListener listener = new MyImageListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (defaultImageResId != 0) {
					view.setImageResource(defaultImageResId);
				}
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if(view == null){
					return;
				}
				if (response.getBitmap() != null) {
					view.setImageBitmap(response.getBitmap());
				} else if (defaultImageResId != 0) {
					view.setImageResource(defaultImageResId);
				}
			}
		};
		if (tag != null) {
			listener.setRequestTag(tag);
		}
		return listener;
	}
	/**
	 * 把Uri对应的图片转化成Bitmap
	 */
	public static Bitmap getBitmapFromUri(Context context,Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
		} catch (Exception e) {
			LogUtil.log(e);
			return null;
		}
		return bitmap;
	}
	 
    /**
     * 通过uri获取相册图片的绝对路径
     */
    @SuppressWarnings("deprecation")
	public static String getImagePathFromUri(Activity context,Uri uri) {
        String imagePath = "";
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.managedQuery(uri, proj, null,null,null); 
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                imagePath = cursor.getString(column_index);
            }
        }
        return imagePath;
    }
	
	/**
	 * 将给定的bitmap压缩到指定size大小(KB)
	 * <p>因为Bitmap 已经在内存中了，所以该方法只能减少内存消耗，不能直接避免OOM
	 */
	public static Bitmap compressBitmap(Bitmap bitmap,int size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ( baos.toByteArray().length / 1024>size) {	//循环判断如果压缩后图片是否大于size kb,大于继续压缩		
			baos.reset();//重置baos即清空baos
			options -= 10;//每次都减少10
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap newBitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		bitmap.recycle();
		return newBitmap;
	}
	
	/**将给定路径的图片文件,按照宽高等比压缩到指定的分辨率大小
	 * 可以防止OOM 
	 */
	public static Bitmap compressBitmap(String path,int width,int height) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;//表示只读边界不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(path,newOpts);//此时返回bm为空
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		if(w <= width && h <= height){
			return BitmapFactory.decodeFile(path, newOpts);
		}
		int be = 1;//be=1表示不缩放
		if((w - width) > (h - height)){
			be = w/width;
		}else{
			be = h/height;
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		newOpts.inPurgeable = true;// 同时设置才会有效  
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(path, newOpts);
		return compressBitmap(bitmap,100);//压缩好比例大小后再进行质量压缩
	}
	
	/*
	 * 读取图片旋转的角度
	 * @param path 图片绝对路径
	 */
	public static int getImageAngle(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (Exception e) {
			LogUtil.log(e);
		}
		return degree;
	}

	/*
	 * 旋转图片
	 * @param angle 旋转的角度
	 */
	public static Bitmap rotateImageView(int angle, Bitmap bitmap) {
		if(angle == 0){
			return bitmap;
		}
		// 旋转图片 动作
		Matrix matrix = new Matrix();;
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 图片内存缓存类
	 * @author hushuai
	 */
	public static class MyImageCache implements ImageCache {
		private LruCache<String, Bitmap> mCache;
		private static MyImageCache imageCache;

		private MyImageCache() {
			int maxSize = 1 * 1024 * 1024;
			mCache = new LruCache<String, Bitmap>(maxSize) {
				@Override
				protected int sizeOf(String key, Bitmap value) {
					return value.getRowBytes() * value.getHeight();
				}
			};
		}

		public static MyImageCache getInstance() {
			if (imageCache == null) {
				imageCache = new MyImageCache();
			}
			return imageCache;
		}

		@Override
		public Bitmap getBitmap(String url) {
			return mCache.get(url);
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			mCache.put(url, bitmap);
		}
	}
}
