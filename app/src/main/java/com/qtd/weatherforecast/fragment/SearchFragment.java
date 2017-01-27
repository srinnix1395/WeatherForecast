package com.qtd.weatherforecast.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Toast;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.adapter.AutoCompleteTextViewLocationAdapter;
import com.qtd.weatherforecast.callback.AutoCompleteRequestCallback;
import com.qtd.weatherforecast.callback.SearchCallback;
import com.qtd.weatherforecast.callback.WeatherRequestCallback;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.custom.CustomAutocompleteTextView;
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
 * Created by DELL on 1/16/2017.
 */

public class SearchFragment extends Fragment {
	@Bind(R.id.toolbar)
	Toolbar toolbar;
	
	@Bind(R.id.actvLocation)
	CustomAutocompleteTextView autoCompleteTextView;
	
	private SearchCallback searchCallback;
	private ProgressDialog dialog;
	
	private ArrayList<Location> arrayList;
	private AutoCompleteTextViewLocationAdapter adapter;
	private Handler handlerDelay = new Handler();
	
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
				UiHelper.closeSoftKeyboard(SearchFragment.this.getActivity());
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
		
		arrayList = new ArrayList<>();
		adapter = new AutoCompleteTextViewLocationAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
		autoCompleteTextView.setAdapter(adapter);
		
		autoCompleteTextView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				
			}
			
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				requestAutocomplete(charSequence.toString());
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				
			}
		});
		autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				requestData(arrayList.get(i).getCoordinate());
			}
		});
		
	}
	
	private void requestAutocomplete(String keyWord) {
		if (!ServiceUtil.isNetworkAvailable(getContext())) {
			Toast.makeText(getContext(), R.string.noInternetConnection, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (keyWord.length() > 2) {
			if (keyWord.contains(" ")) {
				keyWord = keyWord.replace(" ", "%20");
			}
			final String finalKeyWord = keyWord;
			
			handlerDelay.removeMessages(0);
			handlerDelay.postDelayed(new Runnable() {
				@Override
				public void run() {
					final AutoCompleteRequest request = new AutoCompleteRequest.Builder()
							.withKeyword(finalKeyWord)
							.withCallback(new AutoCompleteRequestCallback() {
								@Override
								public void onSuccess(ArrayList<Location> result) {
									arrayList.clear();
									arrayList.addAll(result);
									
									adapter.notifyDataSetChanged();
									if (autoCompleteTextView != null) {
										autoCompleteTextView.showDropDown();
									}
								}
								
								@Override
								public void onFail(String error) {
									UiHelper.showDialogFail(getActivity());
								}
							})
							.build();
					request.request();
				}
			}, 500);
		}
	}
	
	
	private void requestData(String location) {
		String urlConditions = StringUtils.getURL(ApiConstant.CONDITIONS, location);
		String urlForecast10day = StringUtils.getURL(ApiConstant.FORECAST10DAY, location);
		String urlHourly = StringUtils.getURL(ApiConstant.HOURLY, location);
		
		dialog.show();
		WeatherRequest request = new WeatherRequest.Builder(getContext(), AppConstant.ERROR_ID)
				.withType(WeatherRequest.TYPE_INSERT)
				.withUrlCurrentWeather(urlConditions)
				.withUrlHourly(urlHourly)
				.withUrlForecast10Days(urlForecast10day)
				.withCallback(new WeatherRequestCallback() {
					@Override
					public void onSuccess(Bundle result) {
						dialog.dismiss();
						if (searchCallback != null) {
							searchCallback.onSearchFinish(result);
						}
						UiHelper.closeSoftKeyboard(getActivity());
					}
					
					@Override
					public void onFail(String error) {
						dialog.dismiss();
						UiHelper.showDialogFail(getContext());
					}
				})
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
		if (autoCompleteTextView.isPopupShowing()) {
			autoCompleteTextView.dismissDropDown();
		}
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		super.onDestroy();
	}
}
