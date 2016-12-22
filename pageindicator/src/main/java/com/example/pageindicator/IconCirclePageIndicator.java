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
	public static final int DISTANCE = 15;
	public static final int ALPHA_SELECTED = 255;
	public static final int ALPHA_UNSELECTED = 50 * 255 / 100;
	public static final int DEFAULT_POSITION_ICON = 0;
	
	private int radius;
	private int currentPosition;
	private int selectedColor;
	private int unselectedColor;
	private int selectedRes;
	private int unselectedRes;
	private int positionIcon = DEFAULT_POSITION_ICON;
	
	private ViewPager viewPager;
	private ArrayList<Indicator> indicatorList;
	private Paint paint;
	private boolean isFirst = true;
	private int beforePosition;
	
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
		positionIcon = typedArray.getInteger(R.styleable.IconCirclePageIndicator_positionIcon, DEFAULT_POSITION_ICON);
		
		typedArray.recycle();
		init();
	}
	
	private void init() {
		paint = new Paint();
		paint.setAntiAlias(true);
		
		indicatorList = new ArrayList<>();
	}
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (isFirst) {
			initIconCircle();
			isFirst = false;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (viewPager == null) {
			return;
		}
		
		if (viewPager.getAdapter().getCount() <= 0) {
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
		this.viewPager.setCurrentItem(initialPosition);
		this.viewPager.addOnPageChangeListener(this);
		this.currentPosition = initialPosition;
	}
	
	@Override
	public void setCurrentItem(int position) {
		if (viewPager == null || viewPager.getAdapter() == null) {
			return;
		}
		viewPager.setCurrentItem(position, true);
		invalidate();
	}
	
	private void initIconCircle() {
		int count = viewPager.getAdapter().getCount();
		
		int sizeAll = (count - 1) * radius * 2 + radius * 4 + (count - 1) * DISTANCE;
		float firstX = getWidth() / 2 - (sizeAll / 2);
		float centerY = getHeight() / 2;
		
		int radiusBefore = positionIcon * 2 + 4;
		for (int i = 0; i < count; i++) {
			if (i == positionIcon) {
				indicatorList.add(new Icon(getContext(), selectedRes, unselectedRes
						, (int) (i == 0 ? firstX : firstX + 2 * i * radius + (i + 1) * DISTANCE)
						, (int) (centerY - (radius * 2)), radius, currentPosition == positionIcon));
			} else if (i > positionIcon) {
				indicatorList.add(new Circle(firstX + (radiusBefore + (i - positionIcon - 1) * 2 + 1) * radius + i * DISTANCE,
						centerY, radius, i == currentPosition ? selectedColor : unselectedColor,
						i == currentPosition ? ALPHA_SELECTED : ALPHA_UNSELECTED));
			} else {
				indicatorList.add(new Circle(firstX + (2 * i + 1) * radius + i * DISTANCE,
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
		if (indicatorList.size() == 0) {
			return;
		}
		
		beforePosition = currentPosition;
		currentPosition = position;
		
		if (beforePosition == positionIcon) {
			((Icon) indicatorList.get(beforePosition)).setSelected(false);
		} else {
			((Circle) indicatorList.get(beforePosition)).setColor(unselectedColor);
			((Circle) indicatorList.get(beforePosition)).setAlpha(ALPHA_UNSELECTED);
		}
		
		if (currentPosition == positionIcon) {
			((Icon) indicatorList.get(currentPosition)).setSelected(true);
		} else {
			((Circle) indicatorList.get(currentPosition)).setColor(selectedColor);
			((Circle) indicatorList.get(currentPosition)).setAlpha(ALPHA_SELECTED);
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
