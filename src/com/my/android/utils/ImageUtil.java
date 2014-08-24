package com.my.android.utils;

import android.graphics.Bitmap;
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
		if (listener instanceof MyImageListener) {
			if (tag != null) {
				((MyImageListener) listener).setRequestTag(tag);
			}
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
