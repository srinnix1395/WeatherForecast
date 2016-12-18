package com.qtd.weatherforecast.callback;

import android.os.Bundle;

import com.android.volley.VolleyError;

/**
 * Created by DELL on 12/19/2016.
 */

public interface RequestCallback {
	void onSuccess(Bundle bundle);
	void onFail(VolleyError error);
}
