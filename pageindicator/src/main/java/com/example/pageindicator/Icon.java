package com.example.pageindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

/**
 * Created by Administrator on 12/20/2016.
 */

public class Icon extends Indicator {
	private int x;
	private int y;
	private int size;
	private Drawable drawableSelected;
	private Drawable drawableUnselected;
	private boolean selected;
	
	public Icon(Context context, int selectedRes, int unselectedRes, int x, int y
			, int size, boolean selected) {
		this.x = x;
		this.y = y;
		this.size = size;
		
		drawableSelected = ContextCompat.getDrawable(context, selectedRes);
		drawableSelected.setBounds(x, y, x + 4 * size, y + 4 * size);
		
		drawableUnselected = ContextCompat.getDrawable(context, unselectedRes);
		drawableUnselected.setBounds(x, y, x + 4 * size, y + 4 * size);
		
		this.selected = selected;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getSize() {
		return size;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (selected) {
			drawableSelected.draw(canvas);
		} else {
			drawableUnselected.draw(canvas);
		}
	}
}
