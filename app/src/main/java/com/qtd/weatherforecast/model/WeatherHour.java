package com.qtd.weatherforecast.model;

/**
 * Created by Dell on 4/26/2016.
 */
public class WeatherHour {
    String hour;
    String rain;
    int temp;

    public WeatherHour(String hour, String rain, int temp) {
        this.hour = hour;
        this.rain = rain;
        this.temp = temp;
    }

    public String getHour() {
        return hour;
    }

    public String getRain() {
        return rain;
    }

    public int getTemp() {
        return temp;
    }
}
