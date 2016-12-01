package com.heaton.liulei.utils.utils;



import com.heaton.liulei.utils.AppConfig;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 格式化工具
 * Created by DDL on 2016/9/2.
 */
public class BeanUtils {

	public static Object parseObject(Object object){
		if (object == null) {
			return null;
		}
		if(object instanceof Number || object instanceof String || object instanceof Boolean || object instanceof CharSequence){
			return object;
		}
		return parseJSONObjectWithoutNull(object);
	}
	public static JSONObject parseJSONObjectWithNull(Object object) {
		return parseJSONObject(object, true);
	}

	public static JSONObject parseJSONObjectWithoutNull(Object object) {
		return parseJSONObject(object, false);
	}

	public static JSONObject parseJSONObject(Object object, boolean writeNull) {
		if (object == null) {
			return null;
		}
		JSONObject jsonObject = new JSONObject();
		try {
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				if ((field.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC && (field.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
					field.setAccessible(true);
					Object value = field.get(object);
					if (value == null && !writeNull) {
						continue;
					}
					jsonObject.put(field.getName(), value);
				}
			}
		} catch (Exception e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}

	public static <T> T toJSONObject(Class<T> cls, JSONObject jsonObject) {
		if (jsonObject == null || cls == null) {
			return null;
		}
		try {
			T obj = cls.getConstructor().newInstance();
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String name = field.getName();
				if (!jsonObject.has(name)) {
					continue;
				}
				Object val = jsonObject.get(name);
				if(val != null && !val.equals(JSONObject.NULL)){
					field.set(obj, val);
				}
			}
			return obj;
		} catch (Exception e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
		return null;
	}


}
