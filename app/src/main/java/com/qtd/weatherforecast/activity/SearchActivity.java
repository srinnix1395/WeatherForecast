package com.qtd.weatherforecast.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.callback.RequestCallback;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.request.AutoCompleteRequest;
import com.qtd.weatherforecast.request.WeatherRequest;
import com.qtd.weatherforecast.utils.ServiceUtil;
import com.qtd.weatherforecast.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 4/27/2016.
 */
public class SearchActivity extends AppCompatActivity implements RequestCallback {
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
	private boolean isEnabledSearch;
	
	private ProgressDialog loading;
	private AlertDialog alertDialog;
	private boolean isOpenDialogWarning;

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
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		toolbar.setNavigationIcon(R.drawable.ic_arrow_left_white_24dp);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		alertDialog = new AlertDialog.Builder(SearchActivity.this)
				.setMessage(getString(R.string.errorOnProcessing))
				.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
		
		popupMenu = new PopupMenu(SearchActivity.this, etLocation);
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				loading.show();
				final Intent intent = new Intent(SearchActivity.this, MainActivity.class);
				
				urlConditions = StringUtils.getURL(ApiConstant.CONDITIONS, tzs.get(item.getItemId()));
				urlForecast10day = StringUtils.getURL(ApiConstant.FORECAST10DAY, tzs.get(item.getItemId()));
				urlHourly = StringUtils.getURL(ApiConstant.HOURLY, tzs.get(item.getItemId()));
				
				WeatherRequest request = new WeatherRequest.Builder()
						.withUrlCurrentWeather(urlConditions)
						.withUrlHourly(urlHourly)
						.withUrlForecast10Days(urlForecast10day)
						.withCallback(SearchActivity.this)
						.build();
				request.request();
				
				return true;
			}
		});
		
		etLocation.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				if (isEnabledSearch) {
//					getAutoComplete(s);
//				}
//				isEnabledSearch = false;
//				new android.os.Handler().postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						isEnabledSearch = true;
//					}
//				}, 500);
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
	
	@OnClick(R.id.imvLocation)
	void onClickImvLocation() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AppConstant.REQUEST_CODE_PERMISSION_LOCATION);
				return;
			}
		}
		getCurrentLocation();
	}
	
	private void getCurrentLocation() {
		if (!ServiceUtil.isLocationServiceEnabled(this)) {
			Toast.makeText(this, "Hãy bật dịch vụ vị trí", Toast.LENGTH_SHORT).show();
			Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(callGPSSettingIntent, 113);
			return;
		}
		
		double[] location = ServiceUtil.getLocation(this);
		
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
		switch (requestCode) {
			case AppConstant.REQUEST_CODE_PERMISSION_LOCATION: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					getCurrentLocation();
				}
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 113) {
			
		}
	}
	
	private void getAutoComplete(CharSequence s) {
		if (!ServiceUtil.isNetworkAvailable(this)) {
			Toast.makeText(this, R.string.pleaseConnectInternet, Toast.LENGTH_SHORT).show();
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
		
		AutoCompleteRequest request = new AutoCompleteRequest.Builder()
				.withKeyword(keyWord)
				.withCallback(new RequestCallback() {
					@Override
					public void onSuccess(Bundle bundle) {
						try {
							JSONObject response = new JSONObject(bundle.getString(ApiConstant.RESULTS));
							
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
					
					@Override
					public void onFail(VolleyError error) {
						
					}
				})
				.build();
		request.request();
	}
	
	@Override
	protected void onDestroy() {
		if (popupMenu != null) popupMenu.dismiss();
		if (loading != null) loading.dismiss();
		if (alertDialog != null) alertDialog.dismiss();
		super.onDestroy();
	}
	
	@Override
	public void onSuccess(Bundle bundle) {
		Intent intent = new Intent();
		intent.putExtras(bundle);
		
		setResult(Activity.RESULT_OK, intent);
		SearchActivity.this.finish();
	}
	
	@Override
	public void onFail(VolleyError error) {
		alertDialog.show();
	}
}
