package com.qtd.weatherforecast.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.utils.UiHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by DELL on 1/16/2017.
 */

public class SearchFragment extends Fragment {
	@Bind(R.id.toolbar)
	Toolbar toolbar;
	
	@Bind(R.id.actvLocation)
	AutoCompleteTextView autoCompleteTextView;
	
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
		UiHelper.openSoftKeyboard(getContext(),autoCompleteTextView);
	}
}
