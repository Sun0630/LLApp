package com.example.myselfapp.custom;

public class Point {
	public static int STATE_NORMOL = 0;
	public static int STATE_PRESS = 1;
	public static int STATE_ERROR = 2;
	
	float x,y;
	int state = STATE_NORMOL;
	
	public Point(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public float distance(Point a){
		float distance = (float)Math.sqrt((x - a.x)*(x - a.x)+(y - a.y)*(y - a.y));
		return distance;
	}

}
