package com.qtd.weatherforecast.utility;

import com.qtd.weatherforecast.constant.ApiConstant;

/**
 * Created by Dell on 4/27/2016.
 */
public class StringUtils {

    public static String getURL(String method, String national, String city) {
        String url = ApiConstant.URL + ApiConstant.API_KEY + "/" + method + "/lang:VU/q/" + national + "/" + city + ".json";
        return url;
    }

    public static String getURL(String method, String tz) {
        String url = ApiConstant.URL + ApiConstant.API_KEY + "/" + method + "/lang:VU/q/" + tz + ".json";
        return url;
    }

    public static String getWeekday(String day) {
        switch (day) {
            case "Mon":
                return "Thứ hai";
            case "Tue":
                return "Thứ ba";
            case "Wed":
                return "Thứ tư";
            case "Thu":
                return "Thứ năm";
            case "Fri":
                return "Thứ sáu";
            case "Sat":
                return "Thứ bảy";
            case "sun":
                return "Chủ nhật";
            default:
                return "";
        }
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
}
