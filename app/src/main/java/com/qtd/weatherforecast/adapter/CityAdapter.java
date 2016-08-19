package com.qtd.weatherforecast.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.viewholder.CityViewHolder;
import com.qtd.weatherforecast.model.City;

import java.util.ArrayList;

/**
 * Created by Dell on 4/26/2016.
 */
public class CityAdapter extends RecyclerView.Adapter<CityViewHolder> {
    private ArrayList<City> cities;

    public CityAdapter(ArrayList<City> cities) {
        this.cities = cities;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        holder.setupViewHolder(cities.get(position));
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }
}
