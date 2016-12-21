package com.example.pageindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Administrator on 12/20/2016.
 */

public class IconCirclePageIndicator extends View implements PageIndicator, ViewPager.OnPageChangeListener {
	public static final int DEFAULT_RADIUS = 10;
	public static final int DEFAULT_INIT_POSITION = 0;
	public static final int DEFAULT_SELECTED_COLOR = Color.WHITE;
	public static final int DEFAULT_UNSELECTED_COLOR = Color.WHITE;
	public static final int DISTANCE = 30;
	public static final int ALPHA_SELECTED = 255;
	public static final int ALPHA_UNSELECTED = 70 * 255 / 100;
	private static final int DEFAULT_POSITION_ICON = 0;
	
	private int radius;
	private int currentPosition;
	private int beforePosition;
	private int selectedColor;
	private int unselectedColor;
	private int selectedRes;
	private int unselectedRes;
	private int positionIcon = DEFAULT_POSITION_ICON;
	
	private ViewPager viewPager;
	private ArrayList<Indicator> indicatorList;
	private Paint paint;
	
	public IconCirclePageIndicator(Context context) {
		super(context);
		init();
	}
	
	public IconCirclePageIndicator(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IconCirclePageIndicator);
		
		beforePosition = DEFAULT_INIT_POSITION;
		currentPosition = DEFAULT_INIT_POSITION;
		radius = typedArray.getDimensionPixelSize(R.styleable.IconCirclePageIndicator_radius, DEFAULT_RADIUS);
		selectedColor = typedArray.getColor(R.styleable.IconCirclePageIndicator_colorSelected, DEFAULT_SELECTED_COLOR);
		unselectedColor = typedArray.getColor(R.styleable.IconCirclePageIndicator_colorUnselected, DEFAULT_UNSELECTED_COLOR);
		selectedRes = typedArray.getResourceId(R.styleable.IconCirclePageIndicator_selectedResource, -1);
		unselectedRes = typedArray.getResourceId(R.styleable.IconCirclePageIndicator_unselectedResource, -1);
		
		typedArray.recycle();
		init();
	}
	
	private void init() {
		paint = new Paint();
		paint.setAntiAlias(true);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int desiredHeight = 2 * radius;
		
		int width;
		int height;
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			width = widthSize;
		} else {
			width = 0;
		}
		
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			height = Math.min(desiredHeight, heightSize);
		} else {
			height = desiredHeight;
		}
		
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (viewPager == null) {
			return;
		}
		
		for (Indicator indicator : indicatorList) {
			indicator.draw(canvas, paint);
		}
	}
	
	@Override
	public void setupWithViewPager(ViewPager viewPager) throws AdapterNotFoundException {
		setupWithViewPager(viewPager, DEFAULT_INIT_POSITION);
	}
	
	@Override
	public void setupWithViewPager(ViewPager viewPager, int initialPosition) throws AdapterNotFoundException {
		if (this.viewPager == viewPager) {
			return;
		}
		if (viewPager.getAdapter() == null) {
			throw new AdapterNotFoundException();
		}
		this.viewPager = viewPager;
		this.viewPager.setCurrentItem(initialPosition, true);
		this.viewPager.addOnPageChangeListener(this);
		initIconCircle();
		invalidate();
	}
	
	private void initIconCircle() {
		int count = viewPager.getAdapter().getCount();
		indicatorList = new ArrayList<>();
		
		int sizeAll = count * radius + (count - 1) * DISTANCE;
		float firstX = getWidth() / 2 - (sizeAll / 2);
		float centerY = getHeight() / 2;
		
		for (int i = 0; i < count; i++) {
			if (i == positionIcon) {
				indicatorList.add(new Icon(positionIcon == currentPosition ? selectedRes : unselectedRes
						, firstX, centerY - (radius / 2)
						, radius, currentPosition == 0 ? ALPHA_SELECTED : ALPHA_UNSELECTED));
			} else {
				indicatorList.add(new Circle(firstX + (i + 0.5f) * radius + i * DISTANCE,
						centerY, radius, i == currentPosition ? selectedColor : unselectedColor,
						i == currentPosition ? ALPHA_SELECTED : ALPHA_UNSELECTED));
			}
		}
	}
	
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
	}
	
	@Override
	public void onPageSelected(int position) {
		beforePosition = currentPosition;
		currentPosition = position;
		
		indicatorList.get(beforePosition).setAlpha(ALPHA_UNSELECTED);
		indicatorList.get(currentPosition).setAlpha(ALPHA_SELECTED);
		
		if (beforePosition == positionIcon) {
			((Icon) indicatorList.get(beforePosition)).setResourceId(unselectedRes);
		} else {
			((Circle) indicatorList.get(beforePosition)).setColor(unselectedColor);
		}
		
		if (currentPosition == positionIcon) {
			((Icon) indicatorList.get(beforePosition)).setResourceId(selectedRes);
		} else {
			((Circle) indicatorList.get(currentPosition)).setColor(selectedColor);
		}
		
		invalidate();
	}
	
	@Override
	public void onPageScrollStateChanged(int state) {
		
	}
	
	public int getRadius() {
		return radius;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public int getSelectedColor() {
		return selectedColor;
	}
	
	public void setSelectedColor(int selectedColor) {
		this.selectedColor = selectedColor;
	}
	
	public int getUnselectedColor() {
		return unselectedColor;
	}
	
	public void setUnselectedColor(int unselectedColor) {
		this.unselectedColor = unselectedColor;
	}
}
