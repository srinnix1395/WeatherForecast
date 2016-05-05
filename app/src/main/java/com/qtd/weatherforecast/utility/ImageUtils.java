package com.qtd.weatherforecast.utility;

import com.qtd.weatherforecast.R;

/**
 * Created by Dell on 5/4/2016.
 */
public class ImageUtils {
    public static int getImageResource(String icon) {
//        String[] s = url.split("/");
//        String[] s2 = s[6].split("\\.");
        switch (icon) {
            case "chanceflurries":
                return R.drawable.cloud_drizzle_alt;
            case "nt_chanceflurries":
                return R.drawable.cloud_rain_moon_alt;
            case "nt_flurries":
                return R.drawable.cloud_drizzle_moon;
            case "chancesnow":
                return R.drawable.cloud_snow;
            case "nt_chancesnow":
                return R.drawable.cloud_snow_moon;
            case "nt_snow":
                return R.drawable.cloud_snow_moon_alt;
            case "snow":
                return R.drawable.cloud_snow_alt;
            case "chancerain":
                return R.drawable.cloud_rain_alt;
            case "nt_chancerain":
                return R.drawable.cloud_rain_moon_alt;
            case "nt_rain":
                return R.drawable.cloud_rain_moon;
            case "rain":
                return R.drawable.cloud_rain;
            case "chancesleet":
                return R.drawable.cloud_hail;
            case "nt_chancesleet":
                return R.drawable.cloud_hail_moon;
            case "sleet":
                return R.drawable.cloud_hail_alt;
            case "nt_sleet":
                return R.drawable.cloud_hail_moon_alt;
            case "chancetstorms":
            case "tstorms":
                return R.drawable.cloud_lightning;
            case "nt_chancetstorms":
            case "nt_tstorms":
                return R.drawable.cloud_light_moon;
            case "clear":
            case "sunny":
                return R.drawable.sun;
            case "cloudy":
            case "nt_cloudy":
                return R.drawable.cloud;
            case "fog":
                return R.drawable.cloud_fog_alt;
            case "nt_fog":
                return R.drawable.cloud_fog_moon_alt;
            case "hazy":
                return R.drawable.cloud_fog;
            case "nt_hazy":
                return R.drawable.cloud_fog_moon;
            case "mostlysunny":
            case "partlysunny":
            case "mostlycloudy":
            case "partlycloudy":
                return R.drawable.cloud_sun;
            case "nt_clear":
            case "nt_sunny":
                return R.drawable.moon;
            case "nt_mostlycloudy":
            case "nt_mostlysunny":
            case "nt_partlycloudy":
            case "nt_partlysunny":
                return R.drawable.cloud_moon;
        }
        return R.drawable.shades;
    }
}
