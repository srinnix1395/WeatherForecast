package com.qtd.weatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qtd.weatherforecast.model.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 6/2/2016.
 */
public class AutoCompleteTextViewLocationAdapter extends ArrayAdapter<City> {
    List<City> mCities = new ArrayList<>();
    Context mContext;
    int resID;

    public AutoCompleteTextViewLocationAdapter(Context context, int resource, List<City> cities) {
        super(context, resource, cities);
        mCities = cities;
        mContext = context;
        resID = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resID, parent, false);
        }

        TextView tvLocation = (TextView) convertView.findViewById(android.R.id.text1);
        tvLocation.setText(mCities.get(position).getFullName());

        return convertView;
    }

    @Override
    public int getCount() {
        return mCities.size();
    }

    @Override
    public City getItem(int position) {
        return mCities.get(position);
    }
}
