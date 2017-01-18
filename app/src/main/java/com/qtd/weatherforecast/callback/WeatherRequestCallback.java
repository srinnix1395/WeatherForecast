package com.qtd.weatherforecast.callback;

import android.os.Bundle;

/**
 * Created by DELL on 12/19/2016.
 */

public interface WeatherRequestCallback {
	void onSuccess(Bundle result);
	void onFail(String error);
}
