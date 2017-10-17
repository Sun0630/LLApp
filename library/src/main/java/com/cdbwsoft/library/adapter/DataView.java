package com.cdbwsoft.library.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;

import com.cdbwsoft.library.R;

/**
 * 列表视图
 * Created by DDL on 2016/5/25.
 */

public class DataView<D extends DataVO> extends FrameLayout implements Checkable {
	private int       mItemId;
	private Checkable mItemView;

	private static final int[] STATE_FIRST = {R.attr.state_first};
	private static final int[] STATE_LAST  = {R.attr.state_last};

	private boolean mIsFirst = false;
	private boolean mIsLast  = false;


	public DataView(Context context) {
		super(context);
	}

	public DataView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public DataView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckableItemView);
		mItemId = a.getResourceId(R.styleable.CheckableItemView_check_id, -1);
		a.recycle();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		View view = findViewById(mItemId);
		if (view != null && view instanceof Checkable) {
			view.setClickable(false);
			mItemView = (Checkable) view;
		}
	}

	@Override
	public void setChecked(boolean checked) {
		if (mItemView != null) {
			mItemView.setChecked(checked);
		}
	}

	@Override
	public boolean isChecked() {
		return mItemView != null && mItemView.isChecked();
	}

	@Override
	public void toggle() {
		if (mItemView != null) {
			mItemView.toggle();
		}
	}

	public void bindData(D data) {
		if (data == null) {
			return;
		}
		data.bindData(this);
	}

	public void setFirst(boolean isFirst) {
		if (mIsFirst != isFirst) {
			mIsFirst = isFirst;
			drawableStateChanged();
		}
	}

	public void setLast(boolean isLast) {
		if (mIsLast != isLast) {
			mIsLast = isLast;
			drawableStateChanged();
		}
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
		if (mIsFirst) {
			mergeDrawableStates(drawableState, STATE_FIRST);
		}
		if (mIsLast) {
			mergeDrawableStates(drawableState, STATE_LAST);
		}
		return drawableState;
	}
}
