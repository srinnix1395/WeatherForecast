package com.qtd.weatherforecast.model;

/**
 * Created by Dell on 5/6/2016.
 */
public class CityPlus extends City {
    private String icon;

    public CityPlus(int id, String name, int temp, String weather, String coordinate, boolean isChosen, String fullName, String icon) {
        super(id, name, temp, weather, coordinate, isChosen, fullName);
        this.icon = icon;
    }

    public CityPlus() {
        this.id = -1;
        this.name = "";
        this.coordinate = "";
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
