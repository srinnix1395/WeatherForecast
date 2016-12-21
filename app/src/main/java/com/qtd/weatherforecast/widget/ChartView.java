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
	private int[] temperatureArr = {
			6, 5, 4, 7
			, 3, 1, 9, 10
			, 12, 8, 3, 12
			, 8, 9, 10, 7
			, 4, 5, 6, 8
			, 9, 4, 5, 6
	};
	private RecyclerView recyclerView;
	
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
		if (this.recyclerView.equals(recyclerView)) {
			return;
		}
		if (recyclerView.getAdapter() == null) {
			throw new IllegalStateException();
		}
		this.recyclerView = recyclerView;
		this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
			}
		});
		invalidate();
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		int d = getWidth() / 24;
		int h = getHeight();
		for (int i = 0; i < temperatureArr.length; i++) {
			if (i == 0) {
				path.moveTo(d * i, h - 100 - (temperatureArr[i] * 3));
			} else if (i < temperatureArr.length - 1) {
				path.quadTo(d * (i - 1), h - 100 - temperatureArr[i - 1]
						, d * i, h - 100 - (temperatureArr[i] * 3));
			} else {
				path.lineTo(d * i, h - 100 - (temperatureArr[i] * 3));
			}
		}
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
