package com.qtd.weatherforecast.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.qtd.weatherforecast.adapter.viewholder.BackgroundImageViewHolder;

import java.util.ArrayList;

/**
 * Created by Dell on 7/6/2016.
 */
public class BackgroundImageAdapter extends RecyclerView.Adapter<BackgroundImageViewHolder> {
    ArrayList<Integer> arrImage;

    public BackgroundImageAdapter(ArrayList<Integer> arrImage) {
        this.arrImage = arrImage;
    }

    @Override
    public BackgroundImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(BackgroundImageViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return arrImage.size();
    }
}
