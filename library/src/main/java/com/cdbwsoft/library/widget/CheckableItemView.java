package com.cdbwsoft.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;

import com.cdbwsoft.library.R;


/**
 * 可选框列表
 * Created by DDL on 2016/5/18.
 */
public class CheckableItemView extends FrameLayout implements Checkable{

	private int  mItemId;
	private Checkable mItemView;

	public CheckableItemView(Context context) {
		super(context);
	}

	public CheckableItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CheckableItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}
	private void init(Context context, AttributeSet attrs){
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckableItemView);
		mItemId = a.getResourceId(R.styleable.CheckableItemView_check_id, -1);
		a.recycle();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		View view = findViewById(mItemId);
		if(view != null && view instanceof Checkable){
			mItemView = (Checkable)view;
		}
	}

	@Override
	public void setChecked(boolean checked) {
		if(mItemView != null && isEnabled()){
			mItemView.setChecked(checked);
		}
	}
	@Override
	public boolean isChecked() {
		return mItemView != null && mItemView.isChecked();
	}
	@Override
	public void toggle() {
		if(mItemView != null && isEnabled()){
			mItemView.toggle();
		}
	}
}
