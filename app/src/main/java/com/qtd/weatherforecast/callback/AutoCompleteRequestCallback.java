package com.qtd.weatherforecast.callback;

import android.os.Bundle;

/**
 * Created by DELL on 1/5/2017.
 */

public interface AutoCompleteRequestCallback {
	void onSuccess(Bundle bundle);
	
	void onFail(String error);
}
