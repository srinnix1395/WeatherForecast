package com.qtd.weatherforecast.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.callback.SearchCallback;
import com.qtd.weatherforecast.callback.WeatherRequestCallback;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.request.WeatherRequest;
import com.qtd.weatherforecast.utils.ServiceUtil;
import com.qtd.weatherforecast.utils.StringUtils;
import com.qtd.weatherforecast.utils.UiHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DELL on 1/16/2017.
 */

public class SearchFragment extends Fragment implements WeatherRequestCallback {
	@Bind(R.id.toolbar)
	Toolbar toolbar;
	
	@Bind(R.id.actvLocation)
	AutoCompleteTextView autoCompleteTextView;
	
	private SearchCallback searchCallback;
	private ProgressDialog dialog;
	
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			searchCallback = (SearchCallback) context;
		} catch (ClassCastException cce) {
			cce.printStackTrace();
		}
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_search, container, false);
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		initViews();
	}
	
	private void initViews() {
		toolbar.setNavigationIcon(R.drawable.arrow_left);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager().beginTransaction()
						.remove(SearchFragment.this).commit();
			}
		});
		
		Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in);
		toolbar.startAnimation(animation);
		
		autoCompleteTextView.requestFocus();
		UiHelper.openSoftKeyboard(getContext(), autoCompleteTextView);
		
		dialog = new ProgressDialog(getContext());
		dialog.setIndeterminate(true);
		dialog.setTitle(R.string.loading);
		dialog.setCanceledOnTouchOutside(false);
	}
	
	
	private void requestData(String location) {
		String urlConditions = StringUtils.getURL(ApiConstant.CONDITIONS, location);
		String urlForecast10day = StringUtils.getURL(ApiConstant.FORECAST10DAY, location);
		String urlHourly = StringUtils.getURL(ApiConstant.HOURLY, location);
		
		WeatherRequest request = new WeatherRequest.Builder(getContext(), AppConstant.ERROR_ID)
				.withType(WeatherRequest.TYPE_INSERT)
				.withUrlCurrentWeather(urlConditions)
				.withUrlHourly(urlHourly)
				.withUrlForecast10Days(urlForecast10day)
				.withCallback(this)
				.build();
		request.request();
	}
	
	@OnClick(R.id.imvLocation)
	void onClickImvLocation() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, AppConstant.REQUEST_CODE_PERMISSION_LOCATION);
				return;
			}
		}
		getCurrentLocation();
	}
	
	
	private void getCurrentLocation() {
		if (!ServiceUtil.isLocationServiceEnabled(getContext())) {
			Toast.makeText(getContext(), "Hãy bật dịch vụ vị trí", Toast.LENGTH_SHORT).show();
			Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(callGPSSettingIntent, 113);
			return;
		}
		
		String location = ServiceUtil.getLocation(getContext());
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
	public void onDestroy() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		super.onDestroy();
	}
	
	@Override
	public void onSuccess(Bundle bundle) {
		if (searchCallback != null) {
			searchCallback.onSearchFinish(bundle);
		}
	}
	
	@Override
	public void onFail(String error) {
		UiHelper.showDialogFail(getContext());
	}
}
