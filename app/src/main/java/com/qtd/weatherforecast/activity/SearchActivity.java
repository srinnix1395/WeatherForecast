package com.qtd.weatherforecast.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qtd.weatherforecast.AppController;
import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.utility.NetworkUtil;
import com.qtd.weatherforecast.utility.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/27/2016.
 */
public class SearchActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.et_location)
    EditText completeTextViewLocation;

    PopupMenu popupMenu;
    ArrayList<String> tzs = new ArrayList<>();
    String url = ApiConstant.AUTOCOMPLETE_API;
    String urlConditions = "";
    String urlForecast10day = "";
    String urlHourly = "";

    ProgressDialog loading;
    public static final int RESULT_CODE = 114;
    AlertDialog alertDialog;
    ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.TOP);
        ButterKnife.bind(this);
        initComponent();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void initComponent() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        popupMenu = new PopupMenu(SearchActivity.this, completeTextViewLocation);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                loading.show();
                final Intent intent = new Intent(SearchActivity.this, MainActivity.class);

                urlConditions = StringUtils.getURL("conditions", tzs.get(item.getItemId()));
                urlForecast10day = StringUtils.getURL("forecast10day",tzs.get(item.getItemId()));
                urlHourly = StringUtils.getURL("hourly", tzs.get(item.getItemId()));

                alertDialog = new AlertDialog.Builder(SearchActivity.this)
                        .setMessage("Đã có lỗi trong quá trình xử lý, xin thử lại")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                return;
                            }
                        }).create();


                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlConditions, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("conditions", response.toString());
                        intent.putExtra("conditions", response.toString());
                        requestHourly(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", error.toString());
                        alertDialog.show();
                    }
                });
                AppController.getInstance().addToRequestQueue(request);

                return true;
            }
        });

        completeTextViewLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getAutoComplete(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loading = new ProgressDialog(this);
        loading.setIndeterminate(true);
        loading.setTitle("Đang xử lý...");
    }

    private void requestHourly(final Intent intent) {
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, urlHourly, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("hourly", response.toString());
                intent.putExtra("hourly", response.toString());
                requestForecast10day(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                alertDialog.show();
            }
        });
        AppController.getInstance().addToRequestQueue(request1);
    }

    private void requestForecast10day(final Intent intent) {
        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, urlForecast10day, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("forecast10day", response.toString());
                intent.putExtra("forecast10days", response.toString());
                setResult(RESULT_CODE, intent);
                try{
                    SearchActivity.this.finish();
                } finally {
                    Log.d("finish", "finally");
                    popupMenu.dismiss();
                    loading.dismiss();
                    alertDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                alertDialog.show();
            }
        });
        AppController.getInstance().addToRequestQueue(request2);
    }

    private void getAutoComplete(CharSequence s) {
        if (!NetworkUtil.getInstance().isNetworkAvailable(this)) {
            new AlertDialog.Builder(SearchActivity.this, R.style.DialogTheme)
                    .setMessage("Hãy kết nối internet để thêm địa điểm")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else if (s.length() >= 2) {
            tzs.clear();
            popupMenu.getMenu().clear();
            requestAutoComplete();
        } else {
            tzs.clear();
            popupMenu.getMenu().clear();
        }
    }

    private void requestAutoComplete() {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url + completeTextViewLocation.getText(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("RESULTS");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                if (object.length() == 10) {
                                    tzs.add(object.getString("lat") + "," + object.getString("lon"));
                                    popupMenu.getMenu().add(1,i,i,object.getString("name"));
                                }
                            }
                            popupMenu.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
            }
        });
        AppController.getInstance().addToRequestQueue(objectRequest);
    }
}
