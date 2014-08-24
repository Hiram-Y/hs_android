package com.my.android.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;
/**
 * 操作javaBean 的工具类
 * @author hushuai
 *
 */
public class BeanUtil {
	
	/**
	 * <p>把json字符串转化成javaBean
	 * @param jsonStr  json字符串
	 * @param beanclass 要转化的javabean的class
	 * @return 封装好数据之后的javabean
	 */
	public static <T> T jsonToBean(String jsonStr, Class<T> beanclass) {
		JSONObject json = StringUtil.stringToJson(jsonStr);
		return jsonToBean(json, beanclass);
	}

	/**
	 * 把json 对象封装换成javaBean 对象
	 * @param json
	 * @param beanclass
	 * @return
	 */
	public static <T> T jsonToBean(JSONObject json, Class<T> beanclass) {
		T bb = null;
		try {
			bb = beanclass.newInstance();
			bb = jsonToBean(json, bb);
		} catch (Exception e) {
			LogUtil.log(e);
		}
		return bb;
	}
	/**
	 * 把json里的数据填充进给定的javaBean 对象
	 * @param json
	 * @param 给定的javaBean对象
	 * @return
	 */
	public static <T> T jsonToBean(String jsonStr, T bean) {
		JSONObject json = StringUtil.stringToJson(jsonStr);
		return jsonToBean(json, bean);
	}

	/**
	 * 把json里的数据填充进给定的javaBean 对象
	 * @param json
	 * @param bean
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T jsonToBean(JSONObject json, T bean) {
		T bb = bean;
		try {
			HashMap<String, Method> methodNames = new HashMap<String, Method>();
			Method[] methods = bean.getClass().getMethods();
			for (int j = 0; j < methods.length; j++) {
				String methodname = methods[j].getName();
				if (methodname.contains("set")) {
					methodNames.put(StringUtil.tofirstLowerCase(methodname.substring(3)), methods[j]);
				}
			}
			
			for (Entry<String, Method> entry : methodNames.entrySet()) {
				String fildname = entry.getKey();
				try {
					if (!json.has(fildname) && !json.has(StringUtil.tofirstUpperCase(fildname))) {
						continue;
					}
					Object obje = json.opt(fildname);
					if (obje == null) {
						obje = json.opt(StringUtil.tofirstUpperCase(fildname));
						fildname = StringUtil.tofirstUpperCase(fildname);
					}
					
					if (obje == null || obje == JSONObject.NULL) {
						continue;
					}
					if (obje instanceof JSONArray) {
						JSONArray arr = (JSONArray) obje;
						Field field = bean.getClass().getDeclaredField(fildname);
						ParameterizedType ptype = (ParameterizedType) field
								.getGenericType();
						Type[] type = ptype.getActualTypeArguments();
						Class subbeanclass = (Class) type[0];
						ArrayList sublist = new ArrayList();
						for (int i = 0; i < arr.length(); i++) {
							Object subobj = arr.get(i);
							if (subobj instanceof JSONObject) {
								Object subbean = jsonToBean(
										(JSONObject) subobj, subbeanclass);
								sublist.add(subbean);
							} else {
								sublist.add(subobj);
							}
						}
						entry.getValue().invoke(bb, sublist);
					} else if (obje instanceof JSONObject) {
						Field field = bean.getClass().getDeclaredField(fildname);
						Type type = field.getGenericType();
						Class subbeanclass = (Class) type;
						Object subobj = jsonToBean((JSONObject) obje,
								subbeanclass);
						entry.getValue().invoke(bb, subobj);
					} else {
						try {
							entry.getValue().invoke(bb, obje);
						} catch (Exception e) {
							try {
								Field field = bean.getClass().getDeclaredField(fildname);
								Class clazz = field.getType();
								if(clazz == int.class || clazz == Integer.class){
									entry.getValue().invoke(bb, Integer.parseInt(obje.toString()));
								}else if(clazz == float.class || clazz == Float.class){
									entry.getValue().invoke(bb, Float.parseFloat(obje.toString()));
								}else if(clazz == double.class || clazz == Double.class){
									entry.getValue().invoke(bb, Double.parseDouble(obje.toString()));
								}else if(clazz == boolean.class || clazz == Boolean.class){
									entry.getValue().invoke(bb, Boolean.parseBoolean(obje.toString()));
								}else if(clazz == short.class || clazz == Short.class){
									entry.getValue().invoke(bb, Short.parseShort(obje.toString()));
								}else{
									entry.getValue().invoke(bb, String.valueOf(obje));
								}
							} catch (Exception e1) {
								LogUtil.log(fildname + "字段填充数据失败，请检查参数类型是否相符");
								LogUtil.log(e1);
							}
						}
					}
				} catch (Exception e) {
					LogUtil.log(fildname + "字段填充数据失败");
					LogUtil.log(e);
					continue;
				}
			}
		} catch (Exception e) {
			LogUtil.log("jsonToBean 调用失败");
			LogUtil.log(e);
		}
		return bb;
	}
}
