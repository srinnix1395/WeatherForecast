package com.qtd.weatherforecast.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.adapter.MainPagerAdapter;
import com.qtd.weatherforecast.service.BackgroundService;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.toolbar_home)
    Toolbar toolbar;

    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Bind(R.id.indicator)
    CirclePageIndicator indicator;

    private BroadcastReceiver broadcastReceiver;
    private boolean isReceiverRegistered;
    MainPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initComponent();
    }

    private void initComponent() {
        setSupportActionBar(toolbar);
        setupViewPager();
//        Intent intent = new Intent(MainActivity.this, BackgroundService.class);
//        startActivity(intent);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }
        };
//        registerBroadcast();
    }

    private void setupViewPager() {
        adapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
    }

    private void registerBroadcast() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(BackgroundService.BROADCAST_ACTION));
            isReceiverRegistered = true;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //registerBroadcast();
    }

    @Override
    protected void onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
//        isReceiverRegistered = false;
        super.onPause();
    }
}
