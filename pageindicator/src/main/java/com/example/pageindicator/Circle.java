package com.example.pageindicator;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Administrator on 12/20/2016.
 */

public class Circle extends Indicator {
	private float centerX;
	private float centerY;
	private int radius;
	private int color;
	private int alpha;
	
	public Circle(float centerX, float centerY, int radius, int color, int alpha) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
		this.color = color;
		this.alpha = alpha;
	}
	
	public int getAlpha() {
		return alpha;
	}
	
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public float getCenterX() {
		return centerX;
	}
	
	public void setCenterX(float centerX) {
		this.centerX = centerX;
	}
	
	public float getCenterY() {
		return centerY;
	}
	
	public void setCenterY(float centerY) {
		this.centerY = centerY;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	@Override
	public void draw(Canvas canvas, Paint paint) {
		paint.setColor(color);
		paint.setAlpha(alpha);
		canvas.drawCircle(centerX, centerY, radius, paint);
	}
}
