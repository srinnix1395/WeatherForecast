package com.qtd.weatherforecast.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.adapter.CityAdapter;
import com.qtd.weatherforecast.model.City;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/25/2016.
 */
public class SearchFragment extends Fragment {
    @Bind(R.id.recycleView_search_fragment)
    RecyclerView recyclerView;

    CityAdapter adapter;
    ArrayList<City> cities;
    LinearLayoutManager layoutManager;

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        initComponent();
        initData();
        return view;

    }

    private void initComponent() {
        layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        cities = new ArrayList<>();
        adapter = new CityAdapter(cities);
        recyclerView.setAdapter(adapter);
    }

    public void initData() {

    }
}
