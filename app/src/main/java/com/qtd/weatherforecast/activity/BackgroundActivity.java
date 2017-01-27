package com.qtd.weatherforecast.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.adapter.BackgroundAdapter;
import com.qtd.weatherforecast.callback.BackgroundCallback;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.model.Background;
import com.qtd.weatherforecast.utils.SharedPreUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by DELL on 1/18/2017.
 */

public class BackgroundActivity extends AppCompatActivity {
	@Bind(R.id.imvBackground)
	ImageView imvBackground;
	
	@Bind(R.id.recycleView)
	RecyclerView recyclerView;
	
	@Bind(R.id.toolbar)
	Toolbar toolbar;
	
	private ArrayList<Background> arrayList;
	
	private int[] nameBackground = {
			R.string.background_blue,
			R.string.background_green,
			R.string.background_pink,
			R.string.background_purple,
			R.string.background_wood,
	};
	private String background;
	private BackgroundAdapter adapter;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_background);
		ButterKnife.bind(this);
		initData();
		initViews();
	}
	
	private void initData() {
		arrayList = new ArrayList<>();
		
		arrayList.add(new Background("bg_blue.jpg", false));
		arrayList.add(new Background("bg_green.jpg", false));
		arrayList.add(new Background("bg_pink.jpg", false));
		arrayList.add(new Background("bg_purple.jpg", false));
		arrayList.add(new Background("bg_wood.jpg", false));
	}
	
	private void initViews() {
		setSupportActionBar(toolbar);
		toolbar.setTitleTextColor(Color.WHITE);
		toolbar.setNavigationIcon(R.drawable.arrow_left);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.putExtra(AppConstant.BACKGROUND, background);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		background = SharedPreUtils.getBackground();
		setImageBackground();
		
		recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		
		adapter = new BackgroundAdapter(arrayList, new BackgroundCallback() {
			@Override
			public void onSelectedBackground(String name) {
				if (!background.equals(name)) {
					background = name;
					setTitleToolbar();
					setImageBackground();
				}
			}
		});
		recyclerView.setAdapter(adapter);
		setTitleToolbar();
	}
	
	private void setTitleToolbar() {
		for (int i = 0, size = arrayList.size(); i < size; i++) {
			if (arrayList.get(i).getName().equals(background)) {
				getSupportActionBar().setTitle(nameBackground[i]);
				arrayList.get(i).setChosen(true);
			} else {
				arrayList.get(i).setChosen(false);
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	private void setImageBackground() {
		Picasso.with(this)
				.load("file:///android_asset/" + background)
				.resize(1024, 1024)
				.into(imvBackground);
	}
}