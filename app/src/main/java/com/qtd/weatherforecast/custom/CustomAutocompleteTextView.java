package com.qtd.weatherforecast.custom;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by DELL on 1/27/2017.
 */

public class CustomAutocompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
	public CustomAutocompleteTextView(Context context) {
		super(context);
	}
	
	public CustomAutocompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CustomAutocompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	@Override
	protected void replaceText(CharSequence text) {
		//do nothing
	}
}
