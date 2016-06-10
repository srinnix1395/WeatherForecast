package com.qtd.weatherforecast.utils;

import com.qtd.weatherforecast.constant.ApiConstant;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Dell on 4/27/2016.
 */
public class StringUtils {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getURL(String method, String tz) {
        String url = ApiConstant.URL + ApiConstant.API_KEY_WU + "/" + method + "/lang:VU/q/" + tz + ".json";
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

    public static String getTimeAgo() {
        long now = System.currentTimeMillis();
        long time = SharedPreUtils.getLong("LastUpdate", 0);

        long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "vừa xong";
        }
        if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " phút trước";
        }
        if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " giờ trước";
        }
        if (diff < 48 * HOUR_MILLIS) {
            return "ngày hôm qua";
        }
        return diff / DAY_MILLIS + " ngày trước";
    }
}
