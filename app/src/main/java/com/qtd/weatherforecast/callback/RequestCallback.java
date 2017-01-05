package com.qtd.weatherforecast.callback;

/**
 * Created by DELL on 12/19/2016.
 */

public interface RequestCallback {
	void onSuccess(Integer integer);
	void onFail(String error);
}
