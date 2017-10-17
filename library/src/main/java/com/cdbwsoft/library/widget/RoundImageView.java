package com.cdbwsoft.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.cdbwsoft.library.R;

public class RoundImageView extends ImageView {

	private static final int   DEFAULT_BORDER_COLOR = Color.LTGRAY;
	private static final float RECT_DEFAULT         = 10;
	private static final float BORDER_DEFAULT       = 1;
	private final        Paint mBorderPaint         = new Paint();
	private final        Paint mMaskPaint           = new Paint();
	private final        RectF mRoundRect           = new RectF();
	private final        Paint mZonePaint           = new Paint();
	private int   mBorderColor;
	private int   mBorderWidth;
	private float mRadius;

	public RoundImageView(Context context) {
		super(context);
		init(context, null);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.saveLayer(mRoundRect, mZonePaint, Canvas.ALL_SAVE_FLAG);
		canvas.drawRoundRect(mRoundRect, mRadius, mRadius, mZonePaint);
		canvas.saveLayer(mRoundRect, mMaskPaint, Canvas.ALL_SAVE_FLAG);
		super.draw(canvas);
		canvas.drawRoundRect(mRoundRect, mRadius, mRadius, mBorderPaint);
		canvas.restore();
	}

	private void init(Context context, AttributeSet attrs) {
		float density = getResources().getDisplayMetrics().density;
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
			mBorderWidth = a.getDimensionPixelSize(R.styleable.RoundImageView_border_width, (int) (BORDER_DEFAULT * density));
			mBorderColor = a.getColor(R.styleable.RoundImageView_border_color, DEFAULT_BORDER_COLOR);
			mRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_round_radius, (int) (RECT_DEFAULT * density));
			a.recycle();
		} else {
			mBorderWidth = (int) (BORDER_DEFAULT * density);
			mBorderColor = DEFAULT_BORDER_COLOR;
			mRadius = RECT_DEFAULT * density;
		}

		mMaskPaint.setAntiAlias(true);
		mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

		/**
		 * 边框刷
		 */
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setColor(mBorderColor);
		mBorderPaint.setStrokeWidth(mBorderWidth);

		//
		mZonePaint.setAntiAlias(true);
		mZonePaint.setColor(Color.WHITE);
		//
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int w = getWidth();
		int h = getHeight();
		mRoundRect.set(0, 0, w, h);
	}

	/**
	 * 设置半径
	 *
	 * @param radius 半径
	 */
	public void setRectRadius(float radius) {
		mRadius = radius;
		invalidate();
	}

}
