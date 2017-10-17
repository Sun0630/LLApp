package com.cdbwsoft.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表数据适配器
 * Created by DDL on 2016/5/25.
 */

public class SuperAdapter<D extends DataVO,V extends View> extends BaseAdapter {
	private final List<D> mList = new ArrayList<>();
	private Context mContext;
	private V       mContentView;
	private int     mLayout;

	public SuperAdapter(Context context, int layout) {
		mContext = context;
		mLayout = layout;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public D getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mList.get(position).getId();
	}

	protected void bindData(View view, D data) {}

	public void setData(List<D> data){
		synchronized (mList){
			mList.clear();
			if(data != null) {
				mList.addAll(data);
			}
		}
		notifyDataSetChanged();
	}
	public void addData(D data){
		synchronized (mList){
			if(data != null) {
				mList.add(data);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	@SuppressWarnings("unchecked")
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mContentView == null) {
			mContentView = (V) LayoutInflater.from(mContext).inflate(mLayout, parent, false);
		}
		D item = getItem(position);
		if(item != null) {
			item.setPosition(position);
			if (mContentView instanceof DataView) {
				((DataView<D>) mContentView).bindData(item);
				((DataView<D>) mContentView).setFirst(position == 0);
				((DataView<D>) mContentView).setLast(position == mList.size());
			} else {
				bindData(mContentView, item);
			}
		}
		return mContentView;
	}
}
