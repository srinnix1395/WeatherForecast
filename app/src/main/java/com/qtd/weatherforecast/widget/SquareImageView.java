package com.qtd.weatherforecast.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by DELL on 1/18/2017.
 */

public class SquareImageView extends android.support.v7.widget.AppCompatImageView {
	public SquareImageView(Context context) {
		super(context);
	}
	
	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
		int childHeightSize = getMeasuredHeight();
		heightMeasureSpec =
				widthMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
