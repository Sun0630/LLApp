package com.cdbwsoft.library.adapter;

import android.content.Context;
import android.view.View;

/**
 * 绑定数据对象
 * Created by DDL on 2016/5/25.
 */

public class HolderAdapter<D extends DataVO> extends SuperAdapter<D,View> {

	public HolderAdapter(Context context, int layout) {
		super(context, layout);
	}

	@Override
	protected void bindData(View view, D data) {
		super.bindData(view, data);
		data.bindData(view);
	}
}
