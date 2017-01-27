package com.qtd.weatherforecast.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.callback.BackgroundCallback;
import com.qtd.weatherforecast.model.Background;
import com.qtd.weatherforecast.widget.SquareImageView;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by DELL on 1/18/2017.
 */

public class BackgroundViewHolder extends RecyclerView.ViewHolder {
	@Bind(R.id.imvImage)
	SquareImageView imageView;
	
	@Bind(R.id.viewFrame)
	View viewBorder;
	
	private String name;
	
	public BackgroundViewHolder(View itemView, final BackgroundCallback callback) {
		super(itemView);
		ButterKnife.bind(this, itemView);
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (callback != null) {
					callback.onSelectedBackground(name);
				}
			}
		});
	}
	
	public void bindData(Background background) {
		this.name = background.getName();
		Picasso.with(itemView.getContext())
				.load("file:///android_asset/" + name)
				.resize(300, 300)
				.centerCrop()
				.into(imageView);
		
		if (background.isChosen()) {
			viewBorder.setVisibility(View.VISIBLE);
		} else {
			viewBorder.setVisibility(View.INVISIBLE);
		}
	}
}
