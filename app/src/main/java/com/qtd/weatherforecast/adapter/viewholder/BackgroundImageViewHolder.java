package com.qtd.weatherforecast.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.qtd.weatherforecast.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 7/6/2016.
 */
public class BackgroundImageViewHolder extends RecyclerView.ViewHolder {
    private View view;

    @Bind(R.id.imvBackground)
    ImageView imvBackground;

    public BackgroundImageViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        ButterKnife.bind(this, view);
    }

    public void setupViewHolder(int resID) {
        imvBackground.setImageResource(resID);
    }

    @OnClick(R.id.imvBackground)
    void onClickImage(){

    }
}
