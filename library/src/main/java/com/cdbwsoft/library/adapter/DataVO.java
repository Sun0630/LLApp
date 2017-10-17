package com.cdbwsoft.library.adapter;

import android.view.View;
import android.widget.TextView;

import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.R;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 列表数据对象
 * Created by DDL on 2016/5/25.
 */

public class DataVO {

	private int mPosition;//数据索引位置

	public long getId(){
		return mPosition;
	}
	public void setPosition(int position){
		mPosition = position;
	}

	@SuppressWarnings("unchecked")
	public void bindData(View view) {
		Field[] fields = getClass().getDeclaredFields();

		Map<Field,View> dataMap;
		Object holder = view.getTag(R.id.data_holder_tag);
		if (holder != null) {
			dataMap = (Map<Field,View>)holder;
		}else{
			dataMap = new HashMap<>();
			if(fields != null && fields.length >0) {
				for (Field field : fields) {
					FieldBind fieldBind = field.getAnnotation(FieldBind.class);
					if(fieldBind == null){
						continue;
					}
					View v = view.findViewById(fieldBind.value());
					if(v == null){
						continue;
					}
					dataMap.put(field,v);
				}
			}
		}

		for(Field field : dataMap.keySet()){
			if(field == null){
				continue;
			}
			View v = dataMap.get(field);
			if(v == null){
				continue;
			}
			field.setAccessible(true);
			try {
				Object value = field.get(this);
				if(v instanceof TextView){
					((TextView) v).setText(String.valueOf(value));
				}
			} catch (IllegalAccessException e) {
				if(AppConfig.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}
}
