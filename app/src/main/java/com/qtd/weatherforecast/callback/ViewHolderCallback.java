package com.qtd.weatherforecast.callback;

/**
 * Created by DELL on 8/19/2016.
 */
public interface ViewHolderCallback {
    void deleteItemCity(int idCity);

    void choseItemCity(int idCity, String name, String coordinate, String timeZone);
}
