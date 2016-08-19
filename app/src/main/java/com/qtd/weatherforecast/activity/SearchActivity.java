package com.qtd.weatherforecast.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
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
import com.qtd.weatherforecast.utils.NetworkUtil;
import com.qtd.weatherforecast.utils.StringUtils;

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
    EditText etLocation;
//    @Bind(R.id.actvLocation)
//    AutoCompleteTextView autocompleteLocation;

    private PopupMenu popupMenu;
    private ArrayList<String> tzs = new ArrayList<>();
    private String urlConditions = "";
    private String urlForecast10day = "";
    private String urlHourly = "";

    private ProgressDialog loading;
    private AlertDialog alertDialog;
//    private ArrayList<City> cities;
//    private AutoCompleteTextViewLocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.TOP);
        setFinishOnTouchOutside(false);
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

        popupMenu = new PopupMenu(SearchActivity.this, etLocation);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                loading.show();
                final Intent intent = new Intent(SearchActivity.this, MainActivity.class);

                urlConditions = StringUtils.getURL(ApiConstant.CONDITIONS, tzs.get(item.getItemId()));
                urlForecast10day = StringUtils.getURL(ApiConstant.FORECAST10DAY, tzs.get(item.getItemId()));
                urlHourly = StringUtils.getURL(ApiConstant.HOURLY, tzs.get(item.getItemId()));

                Log.d("search", tzs.get(item.getItemId()));
                alertDialog = new AlertDialog.Builder(SearchActivity.this)
                        .setMessage(getString(R.string.errorOnProcessing))
                        .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();


                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlConditions, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("conditions", response.toString());
                        intent.putExtra(ApiConstant.CONDITIONS, response.toString());
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

        etLocation.addTextChangedListener(new TextWatcher() {
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
//        cities = new ArrayList<>();
//        adapter = new AutoCompleteTextViewLocationAdapter(this, android.R.layout.select_dialog_item, cities);
//        autocompleteLocation.setAdapter(adapter);

        loading = new ProgressDialog(this);
        loading.setIndeterminate(true);
        loading.setTitle(getString(R.string.loading));
        loading.setCanceledOnTouchOutside(false);
    }

    private void requestHourly(final Intent intent) {
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, urlHourly, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("hourly", response.toString());
                intent.putExtra(ApiConstant.HOURLY, response.toString());
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
                intent.putExtra(ApiConstant.FORECAST10DAY, response.toString());
                setResult(Activity.RESULT_OK, intent);
                SearchActivity.this.finish();
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
        if (!NetworkUtil.isNetworkAvailable(this)) {
            new AlertDialog.Builder(SearchActivity.this, R.style.DialogTheme)
                    .setMessage(getString(R.string.pleaseConnectInternet))
                    .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else if (s.length() >= 3) {
            requestAutoComplete();
        } else {
            tzs.clear();
            popupMenu.getMenu().clear();
        }
    }

    private void requestAutoComplete() {
        String keyWord = etLocation.getText().toString();
        if (keyWord.contains(" ")) {
            keyWord = keyWord.replace(" ", "%20");
        }

        String url = ApiConstant.AUTOCOMPLETE_API;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url + keyWord,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            tzs.clear();
                            popupMenu.getMenu().clear();
                            Log.d("response", response.toString());

                            JSONArray array = response.getJSONArray(ApiConstant.RESULTS);
                            int j = 0;
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                if ((object.length() == 10 || object.length() == 9) && object.getString(ApiConstant.TYPE).equals(ApiConstant.CITY)) {
                                    tzs.add(object.getString(ApiConstant.LAT) + "," + object.getString(ApiConstant.LON));
                                    popupMenu.getMenu().add(Menu.NONE, j, j, object.getString(ApiConstant.NAME));
//                                    City city = new City(0, object.getString("name"), object.getString("lat") + "," + object.getString("lon"));
//                                    cities.add(city);
                                    j++;
                                }
                            }
//                            adapter.notifyDataSetChanged();
//                            autocompleteLocation.setAdapter(adapter);
                            popupMenu.dismiss();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupMenu != null) popupMenu.dismiss();
        if (loading != null) loading.dismiss();
        if (alertDialog != null) alertDialog.dismiss();
    }
}
