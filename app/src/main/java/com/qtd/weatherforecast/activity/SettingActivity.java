package com.qtd.weatherforecast.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.utils.NotificationUtils;
import com.qtd.weatherforecast.utils.SharedPreUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.qtd.weatherforecast.constant.AppConstant.C;
import static com.qtd.weatherforecast.constant.AppConstant.DEGREE;
import static com.qtd.weatherforecast.constant.AppConstant.F;

/**
 * Created by Dell on 5/8/2016.
 */
public class SettingActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.switch_notification)
    Switch aSwitch;

    @Bind(R.id.tvC)
    TextView tvC;

    @Bind(R.id.tvF)
    TextView tvF;

    private int typeDegree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        getData();
        initComponent();
    }

    private void getData() {
        typeDegree = SharedPreUtils.getInt(DEGREE, AppConstant.C);
    }

    private void initComponent() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.setting);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });

        aSwitch.setChecked(SharedPreUtils.getBoolean(AppConstant.STATE_NOTIFICATION, true));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreUtils.putBoolean(AppConstant.STATE_NOTIFICATION, true);
                    NotificationUtils.createOrUpdateNotification(SettingActivity.this);
                } else {
                    SharedPreUtils.putBoolean(AppConstant.STATE_NOTIFICATION, false);
                    NotificationUtils.clearNotification(SettingActivity.this);
                }
            }
        });

        updateTextColorDegree();
    }

    private void updateTextColorDegree() {
        if (typeDegree == C) {
            tvC.setTextColor(Color.WHITE);
            tvF.setTextColor(ContextCompat.getColor(this, R.color.colorGrayDark));
        } else {
            tvF.setTextColor(Color.WHITE);
            tvC.setTextColor(ContextCompat.getColor(this, R.color.colorGrayDark));
        }
    }

    @OnClick(R.id.imv_logo)
    void imvLogoOnClick() {
        Uri uri = Uri.parse(getString(R.string.wunderground_com));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick({R.id.tvF, R.id.tvC})
    void onClickChangeDegree(View v) {
        switch (v.getId()) {
            case R.id.tvF: {
                if (typeDegree != F) {
                    typeDegree = F;
                }
                break;
            }
            case R.id.tvC: {
                if (typeDegree != C) {
                    typeDegree = C;
                }
                break;
            }
        }
        updateTextColorDegree();
        SharedPreUtils.putInt(DEGREE, typeDegree);
    }
}
