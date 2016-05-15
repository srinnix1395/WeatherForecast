package com.qtd.weatherforecast.utils;

import com.qtd.weatherforecast.constant.ApiConstant;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Dell on 4/27/2016.
 */
public class StringUtils {
    public static String getURL(String method, String tz) {
        String url = ApiConstant.URL + ApiConstant.API_KEY + "/" + method + "/lang:VU/q/" + tz + ".json";
        return url;
    }

    public static String getWeekday(int i) {
        switch (i) {
            case 2:
                return "Thứ hai";
            case 3:
                return "Thứ ba";
            case 4:
                return "Thứ tư";
            case 5:
                return "Thứ năm";
            case 6:
                return "Thứ sáu";
            case 7:
                return "Thứ bảy";
            case 1:
                return "Chủ nhật";
            default:
                return "";
        }
    }

    public static String getCurrentDateTime(String timeZone) {
        String temp = "GMT" + timeZone.substring(0, 3) + ":" + timeZone.substring(3);
        TimeZone tz = TimeZone.getTimeZone(temp);
        Calendar cal = Calendar.getInstance(tz);
        String day = getWeekday(cal.get(Calendar.DAY_OF_WEEK));
        String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        String minute = String.valueOf(cal.get(Calendar.MINUTE));
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        return day + ", " + hour + ":" + minute;
    }
}
