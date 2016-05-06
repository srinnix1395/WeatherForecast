package com.qtd.weatherforecast.model;

/**
 * Created by Dell on 4/26/2016.
 */
public class City {
    protected int id;
    protected String name;
    protected int temp;
    protected String weather;
    protected String coordinate;
    protected boolean isChosen;

    public City(int id, String name, int temp, String weather, String coordinate, boolean isChosen) {
        this.id = id;
        this.name = name;
        this.temp = temp;
        this.weather = weather;
        this.coordinate = coordinate;
        this.isChosen = isChosen;
    }

    public City(int id, String name, String coordinate) {
        this.id = id;
        this.name = name;
        this.coordinate = coordinate;
    }

    public City() {
        this.id = -1;
        this.name = "";
        this.coordinate = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTemp() {
        return temp;
    }


    public String getWeather() {
        return weather;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}
