package com.example.pageindicator;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Administrator on 12/20/2016.
 */

public class Icon extends Indicator {
    private int resourceId;
    private float x;
    private float y;

    public Icon(int resourceId, float x, float y, int alpha) {
        this.resourceId = resourceId;
        this.x = x;
        this.y = y;
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

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setAlpha(alpha);
        canvas.drawBitmap(BitmapFactory.decodeResource(Resources.getSystem(), resourceId), x, y, paint);
    }
}
