package com.example.pageindicator;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Administrator on 12/20/2016.
 */

public abstract class Indicator {
    protected int alpha;

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public abstract void draw(Canvas canvas, Paint paint);
}
