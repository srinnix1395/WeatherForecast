package com.qtd.weatherforecast.model;

/**
 * Created by Dell on 4/26/2016.
 */
public class WeatherDay {
    private String day;
    private String weather;
    private int highTemp;
    private int lowTemp;
    private String url;

    public WeatherDay(String day, String weather, int highTemp, int lowTemp,String url) {
        this.day = day;
        this.weather = weather;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.url = url;
    }

    public String getDay() {
        return day;
    }

    public String getWeather() {
        return weather;
    }

    public int getHighTemp() {
        return highTemp;
    }

    public int getLowTemp() {
        return lowTemp;
    }

    public String getIcon() {
        return url;
    }
}
