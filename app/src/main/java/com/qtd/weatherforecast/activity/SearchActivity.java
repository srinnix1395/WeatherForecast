package com.qtd.weatherforecast.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.adapter.AutoCompleteTextViewLocationAdapter;
import com.qtd.weatherforecast.callback.AutoCompleteRequestCallback;
import com.qtd.weatherforecast.callback.WeatherRequestCallback;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.model.Location;
import com.qtd.weatherforecast.request.AutoCompleteRequest;
import com.qtd.weatherforecast.request.WeatherRequest;
import com.qtd.weatherforecast.utils.ServiceUtil;
import com.qtd.weatherforecast.utils.StringUtils;
import com.qtd.weatherforecast.utils.UiHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 4/27/2016.
 */
public class SearchActivity extends AppCompatActivity implements WeatherRequestCallback {
	@Bind(R.id.toolbar)
	Toolbar toolbar;
	
	@Bind(R.id.actvLocation)
	AutoCompleteTextView autocompleteLocation;
	
	private ProgressDialog loading;
	
	private ArrayList<Location> arrayList;
	private AutoCompleteTextViewLocationAdapter adapter;
	
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
		
		initAutocompleteTextView();
		
		loading = new ProgressDialog(this);
		loading.setIndeterminate(true);
		loading.setTitle(getString(R.string.loading));
		loading.setCanceledOnTouchOutside(false);
	}
	
	private void initAutocompleteTextView() {
		arrayList = new ArrayList<>();
		adapter = new AutoCompleteTextViewLocationAdapter(this, android.R.layout.select_dialog_item, arrayList);
		autocompleteLocation.setAdapter(adapter);
		autocompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				requestData(arrayList.get(i).getCoordinate());
			}
		});
		autocompleteLocation.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				
			}
			
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				getAutoComplete(charSequence);
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				
			}
		});
	}
	
	private void getAutoComplete(CharSequence s) {
		if (!ServiceUtil.isNetworkAvailable(this)) {
			Toast.makeText(this, R.string.pleaseConnectInternet, Toast.LENGTH_SHORT).show();
		} else if (s.length() >= 3) {
			requestAutoComplete();
		}
	}
	
	private void requestAutoComplete() {
		String keyWord = "";
		if (keyWord.contains(" ")) {
			keyWord = keyWord.replace(" ", "%20");
		}
		
		AutoCompleteRequest request = new AutoCompleteRequest.Builder()
				.withKeyword(keyWord)
				.withCallback(new AutoCompleteRequestCallback() {
					@Override
					public void onSuccess(ArrayList<Location> result) {
						arrayList.clear();
						arrayList.addAll(result);
						
						adapter.notifyDataSetChanged();
					}
					
					@Override
					public void onFail(String error) {
						UiHelper.showDialogFail(SearchActivity.this);
					}
				})
				.build();
		request.request();
	}
	
	private void requestData(String location) {
		String urlConditions = StringUtils.getURL(ApiConstant.CONDITIONS, location);
		String urlForecast10day = StringUtils.getURL(ApiConstant.FORECAST10DAY, location);
		String urlHourly = StringUtils.getURL(ApiConstant.HOURLY, location);
		
		WeatherRequest request = new WeatherRequest.Builder(SearchActivity.this, AppConstant.ERROR_ID)
				.withUrlCurrentWeather(urlConditions)
				.withUrlHourly(urlHourly)
				.withUrlForecast10Days(urlForecast10day)
				.withCallback(SearchActivity.this)
				.build();
		request.request();
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
		
		String location = ServiceUtil.getLocation(this);
		requestData(location);
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
	
	
	@Override
	protected void onDestroy() {
		if (loading != null) loading.dismiss();
		super.onDestroy();
	}
	
	@Override
	public void onSuccess(Bundle value) {
		if (value == WeatherRequest.RESULT_OK) {
			Intent intent = new Intent();
//			intent.putExtras(bundle);
			
			setResult(Activity.RESULT_OK, intent);
			SearchActivity.this.finish();
		} else {
			UiHelper.showDialogFail(this);
		}
	}
	
	@Override
	public void onFail(String error) {
		UiHelper.showDialogFail(this);
	}
}
