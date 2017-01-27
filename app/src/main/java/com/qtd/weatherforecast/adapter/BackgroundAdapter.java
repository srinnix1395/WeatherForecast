package com.qtd.weatherforecast.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.callback.BackgroundCallback;
import com.qtd.weatherforecast.model.Background;
import com.qtd.weatherforecast.viewholder.BackgroundViewHolder;

import java.util.ArrayList;

/**
 * Created by DELL on 1/18/2017.
 */

public class BackgroundAdapter extends RecyclerView.Adapter<BackgroundViewHolder> {
	private ArrayList<Background> list;
	private BackgroundCallback callback;
	
	public BackgroundAdapter(ArrayList<Background> list, BackgroundCallback backgroundCallback) {
		this.list = list;
		callback = backgroundCallback;
	}
	
	@Override
	public BackgroundViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_background, parent, false);
		return new BackgroundViewHolder(view, callback);
	}
	
	@Override
	public void onBindViewHolder(BackgroundViewHolder holder, int position) {
		holder.bindData(list.get(position));
	}
	
	@Override
	public int getItemCount() {
		return list.size();
	}
}
