package com.qtd.weatherforecast.callback;

import com.qtd.weatherforecast.model.Location;

import java.util.ArrayList;

/**
 * Created by DELL on 1/5/2017.
 */

public interface AutoCompleteRequestCallback {
	void onSuccess(ArrayList<Location> locations);
	
	void onFail(String error);
}
