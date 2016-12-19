package com.qtd.weatherforecast.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 12/19/2016.
 */

public class ChartView extends View {
    private Paint paint;
    private Path path;
    private int[] temperatureArr;

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#CECECE"));
        paint.setStyle(Paint.Style.STROKE);

        path = new Path();
    }

    public int[] getTemperatureArr() {
        return temperatureArr;
    }

    public void setTemperatureArr(int[] temperatureArr) {
        this.temperatureArr = temperatureArr;
    }

    public void setupWithRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter) {

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        path.moveTo(0, getHeight() - 30);
        path.lineTo(30, getHeight() - 20);
        path.lineTo(60, getHeight() - 20);
        path.lineTo(90, getHeight() - 20);
        path.lineTo(90, getHeight() - 20);

        path.lineTo(150, getHeight() - 25);
        path.lineTo(180, getHeight() - 60);
        path.lineTo(210, getHeight() - 10);
        path.lineTo(240, getHeight() - 80);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw chart
        canvas.drawPath(path, paint);

        //draw bottom line
        canvas.drawLine(0, getHeight() - 5, getWidth(), getHeight() - 5, paint);
    }
}
