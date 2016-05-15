package com.qtd.weatherforecast.activity;

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

    PopupMenu popupMenu;
    ArrayList<String> tzs = new ArrayList<>();
    String url = ApiConstant.AUTOCOMPLETE_API;
    String urlConditions = "";
    String urlForecast10day = "";
    String urlHourly = "";

    ProgressDialog loading;
    public static final int RESULT_CODE = 114;
    AlertDialog alertDialog;

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

                urlConditions = StringUtils.getURL("conditions", tzs.get(item.getItemId()));
                urlForecast10day = StringUtils.getURL("forecast10day",tzs.get(item.getItemId()));
                urlHourly = StringUtils.getURL("hourly", tzs.get(item.getItemId()));

                Log.d("search", tzs.get(item.getItemId()));
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

        etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getAutoComplete(s);
                Log.d("On", "ok");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loading = new ProgressDialog(this);
        loading.setIndeterminate(true);
        loading.setTitle("Đang xử lý...");
        loading.setCanceledOnTouchOutside(false);
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
        }else if (s.length() >= 3) {
            requestAutoComplete();
        } else {
            tzs.clear();
            popupMenu.getMenu().clear();
        }
    }

    private void requestAutoComplete() {
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url + etLocation.getText(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            tzs.clear();
                            popupMenu.getMenu().clear();
                            Log.d("response", response.toString());

                            JSONArray array = response.getJSONArray("RESULTS");
                            int j = 0;
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                if ((object.length() == 10 || object.length() == 9) && object.getString("type").equals("city") ) {
                                    tzs.add(object.getString("lat") + "," + object.getString("lon"));
                                    popupMenu.getMenu().add(Menu.NONE, j, j, object.getString("name"));
                                    j++;
                                }
                            }
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
}
