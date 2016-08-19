package com.qtd.weatherforecast.model;

/**
 * Created by Dell on 4/28/2016.
 */
public class CurrentWeather {
    private String icon;
    private int temp;
    private String weather;
    private String humidity;
    private int wind;
    private int UV;
    private int feelsLike;
    private String time;
    private long lastUpdate;

    public CurrentWeather() {

    }

    public CurrentWeather(String icon, int temp, String weather, String humidity, int wind, int UV, int feelsLike, String time, long lastUpdate) {
        this.icon = icon;
        this.temp = temp;
        this.weather = weather;
        this.humidity = humidity;
        this.wind = wind;
        this.UV = UV;
        this.feelsLike = feelsLike;
        this.time = time;
        this.lastUpdate = lastUpdate;
    }

    public String getIcon() {
        return icon;
    }

    public int getTemp() {
        return temp;
    }

    public String getWeather() {
        return weather;
    }

    public String getHumidity() {
        return humidity;
    }

    public int getWind() {
        return wind;
    }

    public String getTime() {
        return time;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setWind(int wind) {
        this.wind = wind;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUV() {
        return UV;
    }

    public void setUV(int UV) {
        this.UV = UV;
    }

    public int getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(int feelslike) {
        this.feelsLike = feelslike;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
