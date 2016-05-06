package com.qtd.weatherforecast.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qtd.weatherforecast.AppController;
import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.adapter.MainPagerAdapter;
import com.qtd.weatherforecast.adapter.viewholder.CityViewHolder;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.custom.CustomViewPager;
import com.qtd.weatherforecast.fragment.CurrentWeatherFragment;
import com.qtd.weatherforecast.fragment.SearchFragment;
import com.qtd.weatherforecast.fragment.WeatherDayFragment;
import com.qtd.weatherforecast.fragment.WeatherHourFragment;
import com.qtd.weatherforecast.service.WeatherForecastService;
import com.qtd.weatherforecast.utility.NetworkUtil;
import com.qtd.weatherforecast.utility.SharedPreUtils;
import com.qtd.weatherforecast.utility.StringUtils;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements CityViewHolder.DeleteItemCallback, SearchFragment.UpdateChosingCityCallback {
    @Bind(R.id.toolbar_home)
    Toolbar toolbar;

    @Bind(R.id.viewPager)
    CustomViewPager viewPager;

    @Bind(R.id.indicator)
    CirclePageIndicator indicator;

    @Bind(R.id.tv_location)
    TextView tvLocation;

    @Bind(R.id.tv_time)
    TextView tvTime;

    @Bind(R.id.imv_renew)
    ImageView imvRenew;

    private BroadcastReceiver broadcastReceiver;
    private boolean isReceiverRegistered;
    MainPagerAdapter adapter;
    PopupMenu popupMenu;
    ArrayList<Fragment> fragments;
    AlertDialog alertDialog;
    Intent intent;
    boolean isPlus;
    public static final int REQUEST_CODE = 113;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initComponent();
    }

    public void setPlus(boolean plus) {
        isPlus = plus;
    }

    public ImageView getImvRenew() {
        return imvRenew;
    }

    public TextView getTvLocation() {
        return tvLocation;
    }

    public TextView getTvTime() {
        return tvTime;
    }

    private void initComponent() {
        setSupportActionBar(toolbar);
        setupViewPager();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateDatabase();
            }
        };
        registerBroadcast();
        if (intent == null) {
            intent = new Intent(MainActivity.this, WeatherForecastService.class);
            startService(intent);
        }
        alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage("Đã có lỗi trong quá trình xử lý, xin thử lại")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    private void updateDatabase() {
        ((SearchFragment) adapter.getItem(0)).getDataFromDatabase();
        ((CurrentWeatherFragment) adapter.getItem(1)).getDataFromDatabase();
        ((WeatherHourFragment) adapter.getItem(2)).getDataFromDatabase();
        ((WeatherDayFragment) adapter.getItem(3)).getDataFromDatabase();
        Log.d("Update", "Ok");
    }

    private void setupViewPager() {
        fragments = new ArrayList<>();
        fragments.add(new SearchFragment());
        fragments.add(new CurrentWeatherFragment());
        fragments.add(new WeatherHourFragment());
        fragments.add(new WeatherDayFragment());
        adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(4);

        int id = SharedPreUtils.getInt("ID", -1);
        if (id == -1) {
            viewPager.setPagingEnabled(false);
            indicator.setVisibility(View.INVISIBLE);
        }

    }

    private void registerBroadcast() {
        if (!isReceiverRegistered) {
            registerReceiver(broadcastReceiver, new IntentFilter(WeatherForecastService.BROADCAST_ACTION));
            isReceiverRegistered = true;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcast();

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    @OnClick(R.id.imv_renew)
    void renewOnClick() {
        if (isPlus) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            if (NetworkUtil.getInstance().isNetworkAvailable(MainActivity.this)) {
                updateData();
            } else {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Không có kết nối Internet, xin thử lại sau")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            }
        }
    }

    @OnClick(R.id.imv_more)
    void imvMoreOnClick() {
        popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.view_anchor));
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tv_setting:
                        Log.d("setting", "");
                        break;
                    case R.id.tv_info:
                        Log.d("info", "");
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == SearchActivity.RESULT_CODE) {
            String conditions = data.getStringExtra("conditions");
            int idCity = ((SearchFragment) adapter.getItem(0)).updateDataAndGetID(conditions, true);
            sendDataToFragment(conditions, 1, idCity, true);
            String hourly = data.getStringExtra("hourly");
            sendDataToFragment(hourly, 2, idCity, true);
            String forecast = data.getStringExtra("forecast10days");
            sendDataToFragment(forecast, 3, idCity, true);
            viewPager.setPagingEnabled(true);
            indicator.setVisibility(View.VISIBLE);
        }
    }

    private void sendDataToFragment(String s, int id, int idCity, boolean isInsert) {
        switch (id) {
            case 1:
                ((CurrentWeatherFragment) adapter.getItem(id)).updateData(s, idCity, isInsert);
                break;
            case 2:
                ((WeatherHourFragment) adapter.getItem(id)).updateData(s, idCity, isInsert);
                break;
            case 3:
                ((WeatherDayFragment) adapter.getItem(id)).updateData(s, idCity, isInsert);
                break;
        }

    }

    private void updateData() {
        final String coordinate = SharedPreUtils.getString(ApiConstant.COORDINATE, "-1");
        final String[] data = new String[3];
        String urlConditions = StringUtils.getURL("conditions", coordinate);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlConditions, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                data[0] = response.toString();
                requestHourly(coordinate, data);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }

    private void requestHourly(final String coordinate, final String[] data) {
        String urlHourly = StringUtils.getURL("hourly", coordinate);
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, urlHourly, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("hourly", response.toString());
                data[1] = response.toString();
                requestForecast10day(coordinate, data);
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

    private void requestForecast10day(String coordinate, final String[] data) {
        String urlForecast10day = StringUtils.getURL("forecast10day", coordinate);
        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, urlForecast10day, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("forecast10day", response.toString());
                data[2] = response.toString();
                int idCity = ((SearchFragment) adapter.getItem(0)).updateDataAndGetID(data[0], false);
                sendDataToFragment(data[0], 1, idCity, false);
                sendDataToFragment(data[1], 2, idCity, false);
                sendDataToFragment(data[2], 3, idCity, false);
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

    @Override
    public void deleteItemCity(int idCity) {
        ((SearchFragment) adapter.getItem(0)).deleteItem(idCity);
    }

    @Override
    public void choseItemCity(int idCity, String name, String coordinate) {
        SharedPreUtils.putInt("ID", idCity);
        SharedPreUtils.putString(DatabaseConstant.NAME, name);
        SharedPreUtils.putString(ApiConstant.COORDINATE, coordinate);
        ((SearchFragment) adapter.getItem(0)).chooseItem(idCity);
        ((CurrentWeatherFragment) adapter.getItem(1)).chooseItem(idCity);
        ((WeatherHourFragment) adapter.getItem(2)).chooseItem(idCity);
        ((WeatherDayFragment) adapter.getItem(3)).chooseItem(idCity);
    }

    @Override
    public void checkCitySizeToEnableViewPagerSwipe(int idCity) {
        if (idCity == -1) {
            viewPager.setPagingEnabled(false);
            indicator.setVisibility(View.INVISIBLE);
        }
    }
}

