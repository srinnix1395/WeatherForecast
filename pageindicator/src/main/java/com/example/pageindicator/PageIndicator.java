package com.example.pageindicator;

import android.support.v4.view.ViewPager;

/**
 * Created by Administrator on 12/20/2016.
 */

public interface PageIndicator {
	void setupWithViewPager(ViewPager viewPager) throws AdapterNotFoundException;
	void setupWithViewPager(ViewPager view, int initialPosition) throws AdapterNotFoundException;
	
	void setCurrentItem(int position);
}
