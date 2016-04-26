package com.qtd.weatherforecast.model;

/**
 * Created by Dell on 4/26/2016.
 */
public class City {
    String name;
    String national;
    int temp;
    String weather;

    public City(String name, String national, int temp, String weather) {
        this.name = name;
        this.national = national;
        this.temp = temp;
        this.weather = weather;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNational() {
        return national;
    }

    public void setNational(String national) {
        this.national = national;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}
