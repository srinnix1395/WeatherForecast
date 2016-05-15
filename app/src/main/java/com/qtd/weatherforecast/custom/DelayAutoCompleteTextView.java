package com.qtd.weatherforecast.custom;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

/**
 * Created by Dell on 5/13/2016.
 */
public class DelayAutoCompleteTextView extends AutoCompleteTextView{

    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;
    private static final int MESSAGE_TEXT_CHANGED = 100;
    private int mAutoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;
    private ProgressBar mLoadingIndicator;

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            DelayAutoCompleteTextView.super.performFiltering((CharSequence) msg.obj, msg.arg1);
        }
    };

    public DelayAutoCompleteTextView(Context context) {
        super(context);
    }

    public int getmAutoCompleteDelay() {
        return mAutoCompleteDelay;
    }

    public void setmAutoCompleteDelay(int mAutoCompleteDelay) {
        this.mAutoCompleteDelay = mAutoCompleteDelay;
    }

    public void setmLoadingIndicator(ProgressBar mLoadingIndicator) {
        this.mLoadingIndicator = mLoadingIndicator;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(VISIBLE);
        }
        handler.removeMessages(MESSAGE_TEXT_CHANGED);

    }
}
