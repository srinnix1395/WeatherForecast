package com.qtd.weatherforecast.model;

/**
 * Created by Dell on 4/26/2016.
 */
public class WeatherHour {
    String hour;
    String rain;
    int temp;
    String url;


    public WeatherHour(String hour, String rain, int temp, String url) {
        this.hour = hour;
        this.rain = rain;
        this.temp = temp;
        this.url = url;
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

    public String getIcon() {
        return url;
    }

}
