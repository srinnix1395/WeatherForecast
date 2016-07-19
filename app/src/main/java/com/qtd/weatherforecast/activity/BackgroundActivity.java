package com.qtd.weatherforecast.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.qtd.weatherforecast.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 7/6/2016.
 */
public class BackgroundActivity extends AppCompatActivity {

    @Bind(R.id.background_app)
    LinearLayout linearLayout;

    @Bind(R.id.recycleView)
    RecyclerView recyclerView;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        ButterKnife.bind(this);
    }
}
