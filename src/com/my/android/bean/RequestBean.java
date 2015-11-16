package com.my.android.bean;

import com.my.android.network.MyRequest;
import com.my.android.utils.BeanUtil;
import com.my.android.utils.StringUtil;

/**
 * 网络请求数据封装类 （对请求参数，返回数据的封装）
 * <p><h3>* 只对json格式的返回数据做解析， 不支持xml, 不支持Date自动转换</h3>
 * <p>json 和 封装类的规则如下：
 * <p>1. json 数据中的JsonObject类型对应封装类中的 RequestBean 类型
 * <p>2. json 数据中得JsonArray类型对应封装类中的ArrayList<T> 类型
 * <p>3. json 中字符串 对应String
 * <p>4. 基本类型相互对应
 * <p>解析方法见 {@link BeanUtil}
 * <h3>使用封装类请求数据注意事项</h3>
 * <p>1. 子类必须在默认构造方法或其他构造方法中为url 赋值
 * <p>2. 请求成功后会在回调监听器中回调一个方法，回调方法的方法名就是 initResponseMethodName方法返回的，
 * 该方法默认返回的方法名为：子类的类名去掉后缀“Bean” + “Response” 
 * 所以务必要在回调监听器 MyRequestListener 的实现类中加入对应方法名的方法, 方法参数为封装类。
 * 当然如果使用插件会自动生成
 * @author hushuai
 */
public class RequestBean implements BaseBean {
	private static final String RESPONSE_METHOD_SUFFIX = "Response";	
	/**请求的url ，需在子类中赋值*/
	public String request_url;		
	/**可赋值来标识请求*/
	public int request_id = MyRequest.def_identify;	
	/**请求完成后，回调监听类中回调方法的方法名, 可自己赋值，但要requestListener 中要有此方法*/
	public String response_methodName = initResponseMethodName();
	
	private String initResponseMethodName(){
		String className = getClass().getSimpleName();
		className = StringUtil.tofirstLowerCase(className);
		StringBuilder sb = new StringBuilder();
		if(className.endsWith("Bean")){
			sb.append(className.substring(0, className.length() - 4));
		}
		sb.append(RESPONSE_METHOD_SUFFIX);
		return sb.toString();
	}
}
