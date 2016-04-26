package com.qtd.weatherforecast.model;

/**
 * Created by Dell on 4/26/2016.
 */
public class WeatherDay {
    String day;
    String weather;
    int highTemp;
    int lowTemp;

    public WeatherDay(String day, String weather, int highTemp, int lowTemp) {
        this.day = day;
        this.weather = weather;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
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
}
