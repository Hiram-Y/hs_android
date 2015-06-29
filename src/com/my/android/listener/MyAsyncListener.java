package com.my.android.listener;

/**异步处理监听*/
public interface MyAsyncListener {
	/**子线程调用方法*/
	public Object doInBackground(int id);
	/**UI回调方法*/
	public void doInUI(int id,Object result);
}
