package com.qtd.weatherforecast.request;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qtd.weatherforecast.AppController;
import com.qtd.weatherforecast.callback.RequestCallback;
import com.qtd.weatherforecast.constant.ApiConstant;

import org.json.JSONObject;

/**
 * Created by Administrator on 12/19/2016.
 */

public class AutoCompleteRequest {
    private RequestCallback requestCallback;
    private String keyWord;

    public AutoCompleteRequest(Builder builder) {
        keyWord = builder.keyWord;
        requestCallback = builder.requestCallback;
    }

    public void request() {
        String url = ApiConstant.AUTOCOMPLETE_API;

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url + keyWord,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (requestCallback != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString(ApiConstant.RESULTS, response.toString());

                            requestCallback.onSuccess(bundle);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
                if (requestCallback != null) {
                    requestCallback.onFail(error);
                }
            }
        });
        AppController.getInstance().addToRequestQueue(objectRequest);
    }

    public static class Builder{
        private RequestCallback requestCallback;
        private String keyWord;

        public Builder withKeyword(String keyWord) {
            this.keyWord = keyWord;
            return this;
        }

        public Builder withCallback(RequestCallback requestCallback) {
            this.requestCallback = requestCallback;
            return this;
        }

        public AutoCompleteRequest build(){
            return new AutoCompleteRequest(this);
        }
    }
}
