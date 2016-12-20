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

public class IconCirclePageIndicator extends View implements ViewPager.OnPageChangeListener {
    public static final int DEFAULT_RADIUS = 15;
    public static final int DEFAULT_INIT_POSITION = 0;
    public static final int DEFAULT_SELECTED_COLOR = Color.WHITE;
    public static final int DEFAULT_UNSELECTED_COLOR = Color.WHITE;
    public static final int DISTANCE = 40;
    public static final int ALPHA_SELECTED = 255;
    public static final int ALPHA_UNSELECTED = 70 * 255 / 100;

    private int radius;
    private int currentPosition;
    private int beforePosition;
    private int selectedColor;
    private int unselectedColor;
    private int selectedRes;
    private int unselectedRes;
    private boolean hasIcon;

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

        if (selectedRes != -1 && unselectedRes != -1) {
            hasIcon = true;
        }
        typedArray.recycle();
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Indicator indicator : indicatorList) {
            indicator.draw(canvas, paint);
        }
    }

    public void setupWithViewPager(ViewPager viewPager) throws AdapterNotFoundException {
        if (this.viewPager.equals(viewPager)) {
            return;
        }
        if (viewPager.getAdapter() == null) {
            throw new AdapterNotFoundException();
        }
        this.viewPager = viewPager;
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

        if (selectedRes != -1) {
            indicatorList.add(new Icon(selectedRes, firstX, centerY - (radius / 2)
                    , currentPosition == 0 ? ALPHA_SELECTED : ALPHA_UNSELECTED));
        } else {
            indicatorList.add(new Circle(firstX + radius / 2, centerY, radius
                    , currentPosition == 0 ? ALPHA_SELECTED : ALPHA_UNSELECTED));
        }

        for (int i = 1; i < count; i++) {
            Circle circle = new Circle();
            circle.setCenterX(firstX + (i + 0.5f) * radius + i * DISTANCE);
            circle.setCenterY(centerY);
            circle.setRadius(radius);
            circle.setColor(i == currentPosition ? selectedColor : unselectedColor);
            circle.setAlpha(i == currentPosition ? ALPHA_SELECTED : ALPHA_UNSELECTED);
            indicatorList.add(circle);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        beforePosition = currentPosition;
        currentPosition = position;

        if (hasIcon) {

        } else {
            ((Circle) indicatorList.get(currentPosition)).setColor(selectedColor);
            ((Circle) indicatorList.get(beforePosition)).setColor(unselectedColor);
        }

        indicatorList.get(currentPosition).setAlpha(ALPHA_SELECTED);
        indicatorList.get(beforePosition).setAlpha(ALPHA_UNSELECTED);
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
