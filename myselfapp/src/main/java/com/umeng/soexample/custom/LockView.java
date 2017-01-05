package com.umeng.soexample.custom;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.umeng.soexample.R;

public class LockView extends View {
	private boolean inited = false;
	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);// 抗锯齿
	Paint pressPaint = new Paint(); //按下时的画笔
	Paint errorPaint = new Paint(); //错误时的画笔
	private Bitmap bitmapPointError;
	private Bitmap bitmapPointPress;
	private Bitmap bitmapPointNormal;
	private float bitmapR; // 点的半径
	private boolean isDraw = false; //是否正在绘制
	float mouseX, mouseY;
	private Point [][] points = new Point[3][3];
	private ArrayList<Point> pointList = new ArrayList<Point>(); //保存经过的点
	private ArrayList<Integer> passList = new ArrayList<Integer>();
	private OnDrawFinishedListener listener;

	public LockView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public LockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public LockView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mouseX = event.getX();
		mouseY = event.getY();
		int [] ij;
		int i , j;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			resetPoints();
			ij = getSelectedPoint();
			if(ij != null){
				isDraw = true;
				i = ij[0];
				j = ij[1];
				points[i][j].state = Point.STATE_PRESS;
				pointList.add(points[i][j]);
				passList.add(i * 3 + j);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(isDraw){
				ij = getSelectedPoint();
				if(ij != null){
					i = ij[0];
					j = ij[1];
					if(!pointList.contains(points[i][j])){
						points[i][j].state = Point.STATE_PRESS;
						pointList.add(points[i][j]);
						passList.add(i * 3 + j);
					}				
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			boolean valid = false;
			if(listener != null && isDraw){
				valid = listener.OnDrawFinished(passList);
			}
			if(!valid){
				for (Point p:pointList) {
					p.state = Point.STATE_ERROR;
				}
			}
			isDraw = false;
			break;

		default:
			break;
		}
		this.postInvalidate();
		return true;
	}

	private int[] getSelectedPoint() {
		Point pMouse = new Point(mouseX,mouseY);
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points[i].length; j++) {
				if(points[i][j].distance(pMouse) < bitmapR){
					int [] result = new int [2];
					result[0] = i;
					result[1] = j;
					return result;
				}
			}
			
		}
		return null;
	}

	public void resetPoints() {
		passList.clear();
		pointList.clear();
		for (int i = 0; i < points.length; i++)
        {
            for (int j = 0; j < points[i].length; j++)
            {
                points[i][j].state = Point.STATE_NORMOL;
            }
        }
		this.postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		super.onDraw(canvas);
		if(!inited){
			init();
		}
		drawPoints(canvas);
		
		if(pointList.size() > 0){ //画线 2->3,3->5 ...
			Point a = pointList.get(0);
			for (int i = 1;i < pointList.size(); i++)
            {
                Point b = pointList.get(i);
                drawLine(canvas, a, b);
                a = b;
            }
			if(isDraw){ // 不理解
				drawLine(canvas, a, new Point(mouseX, mouseY));
			}
		}
	}

	private void drawLine(Canvas canvas, Point a, Point b) {
		if(a.state == Point.STATE_ERROR){
			canvas.drawLine(a.x, a.y, b.x, b.y, errorPaint);
		}
		else if(a.state == Point.STATE_PRESS){
			canvas.drawLine(a.x, a.y, b.x, b.y, pressPaint);
		}
	}

	private void drawPoints(Canvas canvas) {
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points[i].length; j++) {
				if(points[i][j].state == Point.STATE_NORMOL){
					// normol
					canvas.drawBitmap(bitmapPointNormal, points[i][j].x - bitmapR, points[i][j].y - bitmapR, paint);
				}
				else if(points[i][j].state == Point.STATE_PRESS){
					// press
					canvas.drawBitmap(bitmapPointPress, points[i][j].x - bitmapR, points[i][j].y - bitmapR, paint);
				}
				else{
					// error
					canvas.drawBitmap(bitmapPointError, points[i][j].x - bitmapR, points[i][j].y - bitmapR, paint);
				}
			}
			
		}
	}

	private void init() {
		pressPaint.setColor(Color.YELLOW);
		pressPaint.setStrokeWidth(4);
		errorPaint.setColor(Color.RED);
		errorPaint.setStrokeWidth(4);
		
		bitmapPointError = BitmapFactory.decodeResource(getResources(), R.mipmap.error);
		bitmapPointNormal = BitmapFactory.decodeResource(getResources(), R.mipmap.normal);
		bitmapPointPress = BitmapFactory.decodeResource(getResources(), R.mipmap.press);
		
		bitmapR = bitmapPointError.getWidth() / 2; //三种点的大小一样
		int width = getWidth();
		int height = getHeight();
		int offset = Math.abs(width - height) / 2;
		int offsetX, offsetY;
		int space;
		if(width > height){
			space = height / 4;
			offsetX = offset;
			offsetY = 0;
		}
		else{
			space = width / 4;
			offsetX = 0;
			offsetY = offset;
		}
		points[0][0] = new Point(offsetX +space , offsetY + space);
		points[0][1] = new Point(offsetX + 2*space , offsetY + space);
		points[0][2] = new Point(offsetX + 3 * space , offsetY + space);
		points[1][0] = new Point(offsetX +space, offsetY + 2 * space);
		points[1][1] = new Point(offsetX + 2 * space , offsetY + 2 * space);
		points[1][2] = new Point(offsetX + 3 * space , offsetY + 2 * space);
		points[2][0] = new Point(offsetX +space, offsetY + 3 * space);
		points[2][1] = new Point(offsetX + 2 * space , offsetY + 3 * space);
		points[2][2] = new Point(offsetX + 3 * space , offsetY + 3 * space);
		
		inited = true; //初始化完成
	}
	
	public interface OnDrawFinishedListener{
		boolean OnDrawFinished(List<Integer> passList);
	}
	
	public void setOnDrawFinishedListener(OnDrawFinishedListener listener){
		this.listener = listener;
	}
	

}
