package com.example.pageindicator;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by Administrator on 12/20/2016.
 */

public class Icon extends Indicator {
	private int resourceId;
	private float x;
	private float y;
	private final float size;
	
	public Icon(int resourceId, float x, float y, float size, int alpha) {
		this.resourceId = resourceId;
		this.x = x;
		this.y = y;
		this.size = size;
		this.alpha = alpha;
	}
	
	public int getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}
	
	public float getX() {
		return x;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getSize() {
		return size;
	}
	@Override
	public void draw(Canvas canvas, Paint paint) {
		paint.setAlpha(alpha);
		try {
			canvas.drawBitmap(BitmapFactory.decodeResource(Resources.getSystem(), resourceId), null
					, new RectF(x, y, size, size), paint);
		} catch (NullPointerException ne) {
			ne.printStackTrace();
			canvas.drawCircle((x + size) / 2, (y + size) / 2, size, paint);
		}
	}
}
