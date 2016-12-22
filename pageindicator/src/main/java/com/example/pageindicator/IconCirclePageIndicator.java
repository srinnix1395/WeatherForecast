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
    public static final String TAG = "indicatorCircle";

    public static final int DEFAULT_RADIUS = 10;
    public static final int DEFAULT_INIT_POSITION = 0;
    public static final int DEFAULT_SELECTED_COLOR = Color.WHITE;
    public static final int DEFAULT_UNSELECTED_COLOR = Color.WHITE;
    public static final int DISTANCE = 30;
    public static final int ALPHA_SELECTED = 255;
    public static final int ALPHA_UNSELECTED = 50 * 255 / 100;
    private static final int DEFAULT_POSITION_ICON = 0;

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

    public IconCirclePageIndicator(Context context) {
        super(context);
        init();
    }

    public IconCirclePageIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IconCirclePageIndicator);

        currentPosition = DEFAULT_INIT_POSITION;
        radius = typedArray.getDimensionPixelSize(R.styleable.IconCirclePageIndicator_radius, DEFAULT_RADIUS);
        selectedColor = typedArray.getColor(R.styleable.IconCirclePageIndicator_colorSelected, DEFAULT_SELECTED_COLOR);
        unselectedColor = typedArray.getColor(R.styleable.IconCirclePageIndicator_colorUnselected, DEFAULT_UNSELECTED_COLOR);
        selectedRes = typedArray.getResourceId(R.styleable.IconCirclePageIndicator_selectedResource, -1);
        unselectedRes = typedArray.getResourceId(R.styleable.IconCirclePageIndicator_unselectedResource, -1);
        positionIcon = typedArray.getInteger(R.styleable.IconCirclePageIndicator_positionIcon, 0);

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
        initIconCircle();
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
        this.viewPager.setCurrentItem(initialPosition, true);
        this.viewPager.addOnPageChangeListener(this);
    }

    private void initIconCircle() {
        int count = viewPager.getAdapter().getCount();

        int sizeAll = count * radius + (count - 1) * DISTANCE;
        float firstX = getWidth() / 2 - (sizeAll / 2);
        float centerY = getHeight() / 2;

        for (int i = 0; i < count; i++) {
            if (i == positionIcon) {
                indicatorList.add(new Icon(positionIcon == currentPosition ? selectedRes : unselectedRes
                        , firstX + (i + 0.5f) * radius + i * DISTANCE, centerY - (radius / 2)
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
        if (indicatorList.size() == 0) {
            return;
        }

        currentPosition = position;

        postInvalidate();
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
