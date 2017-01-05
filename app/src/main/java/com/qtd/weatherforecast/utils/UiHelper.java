package com.qtd.weatherforecast.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.qtd.weatherforecast.R;

/**
 * Created by Dell on 5/4/2016.
 */
public class UiHelper {
    public static int getImageResource(String url) {
        String[] s = url.split("/");
        String[] s2 = s[6].split("\\.");
        switch (s2[0]) {
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

    public static int getImageResourceCurrentWeather(String url) {
        if (url == null) {
            return R.drawable.sun_500;
        }
        String[] s = url.split("/");
        String[] s2 = s[6].split("\\.");
        switch (s2[0]) {
            case "chanceflurries":
                return R.drawable.cloud_drizzle_alt_500;
            case "nt_chanceflurries":
                return R.drawable.cloud_rain_moon_alt_500;
            case "nt_flurries":
                return R.drawable.cloud_drizzle_moon_500;
            case "chancesnow":
                return R.drawable.cloud_snow_500;
            case "nt_chancesnow":
                return R.drawable.cloud_snow_moon_500;
            case "nt_snow":
                return R.drawable.cloud_snow_moon_alt_500;
            case "snow":
                return R.drawable.cloud_snow_alt_500;
            case "chancerain":
                return R.drawable.cloud_rain_alt_500;
            case "nt_chancerain":
                return R.drawable.cloud_rain_moon_alt_500;
            case "nt_rain":
                return R.drawable.cloud_rain_moon_500;
            case "rain":
                return R.drawable.cloud_rain_500;
            case "chancesleet":
                return R.drawable.cloud_hail_500;
            case "nt_chancesleet":
                return R.drawable.cloud_hail_moon_500;
            case "sleet":
                return R.drawable.cloud_hail_alt_500;
            case "nt_sleet":
                return R.drawable.cloud_hail_moon_alt_500;
            case "chancetstorms":
            case "tstorms":
                return R.drawable.cloud_lightning_500;
            case "nt_chancetstorms":
            case "nt_tstorms":
                return R.drawable.cloud_light_moon_500;
            case "clear":
            case "sunny":
                return R.drawable.sun_500;
            case "cloudy":
            case "nt_cloudy":
                return R.drawable.cloud_500;
            case "fog":
                return R.drawable.cloud_fog_alt_500;
            case "nt_fog":
                return R.drawable.cloud_fog_moon_alt_500;
            case "hazy":
                return R.drawable.cloud_fog_500;
            case "nt_hazy":
                return R.drawable.cloud_fog_moon_500;
            case "mostlysunny":
            case "partlysunny":
            case "mostlycloudy":
            case "partlycloudy":
                return R.drawable.cloud_sun_500;
            case "nt_clear":
            case "nt_sunny":
                return R.drawable.moon_500;
            case "nt_mostlycloudy":
            case "nt_mostlysunny":
            case "nt_partlycloudy":
            case "nt_partlysunny":
                return R.drawable.cloud_moon_500;
        }
        return 0;
    }

    public static int getImageResourceNotification(String url) {
        if (url != null) {
            String[] s = url.split("/");
            String[] s2 = s[6].split("\\.");
            switch (s2[0]) {
                case "chanceflurries":
                    return R.drawable.cloud_drizzle_alt_black;
                case "nt_chanceflurries":
                    return R.drawable.cloud_rain_moon_alt_black;
                case "nt_flurries":
                    return R.drawable.cloud_drizzle_moon_black;
                case "chancesnow":
                    return R.drawable.cloud_snow_black;
                case "nt_chancesnow":
                    return R.drawable.cloud_snow_moon_black;
                case "nt_snow":
                    return R.drawable.cloud_snow_moon_alt_black;
                case "snow":
                    return R.drawable.cloud_snow_alt_black;
                case "chancerain":
                    return R.drawable.cloud_rain_alt_black;
                case "nt_chancerain":
                    return R.drawable.cloud_rain_moon_alt_black;
                case "nt_rain":
                    return R.drawable.cloud_rain_moon_black;
                case "rain":
                    return R.drawable.cloud_rain_black;
                case "chancesleet":
                    return R.drawable.cloud_hail_black;
                case "nt_chancesleet":
                    return R.drawable.cloud_hail_moon_black;
                case "sleet":
                    return R.drawable.cloud_hail_alt_black;
                case "nt_sleet":
                    return R.drawable.cloud_hail_moon_alt_black;
                case "chancetstorms":
                case "tstorms":
                    return R.drawable.cloud_lightning_black;
                case "nt_chancetstorms":
                case "nt_tstorms":
                    return R.drawable.cloud_light_moon_black;
                case "clear":
                case "sunny":
                    return R.drawable.sun_black;
                case "cloudy":
                case "nt_cloudy":
                    return R.drawable.cloud_black;
                case "fog":
                    return R.drawable.cloud_fog_alt_black;
                case "nt_fog":
                    return R.drawable.cloud_fog_moon_alt_black;
                case "hazy":
                    return R.drawable.cloud_fog_black;
                case "nt_hazy":
                    return R.drawable.cloud_fog_moon_black;
                case "mostlysunny":
                case "partlysunny":
                case "mostlycloudy":
                case "partlycloudy":
                    return R.drawable.cloud_sun_black;
                case "nt_clear":
                case "nt_sunny":
                    return R.drawable.moon_black;
                case "nt_mostlycloudy":
                case "nt_mostlysunny":
                case "nt_partlycloudy":
                case "nt_partlysunny":
                    return R.drawable.cloud_moon_black;
            }
        }
        return R.drawable.sun_black;
    }

    public static void showDialogFail(Context context) {
        new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.errorOnProcessing))
                .setPositiveButton(context.getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    public static int getImageTemp(Context context, int temp) {
        StringBuilder s = new StringBuilder("status_");
        if (temp < 0) {
            s.append("_").append(temp).append("_honeycomb");
        } else {
            s.append(temp).append("_honeycomb");
        }
        return context.getResources().getIdentifier(s.toString(), "drawable", context.getPackageName());
    }
}
