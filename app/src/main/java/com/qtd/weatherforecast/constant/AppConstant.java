package com.qtd.weatherforecast.constant;

/**
 * Created by DELL on 8/19/2016.
 */
public class AppConstant {
    public static final int REQUEST_CODE_SETTING = 115;
    public static final int REQUEST_CODE_PERMISSION_LOCATION = 116;
	public static final int BACKGROUND_ACTIVITY_REQUEST_CODE = 117;

    public static final String CONNECTIVITY_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";

    //service
    public static final String ACTION_DATABASE_CHANGED = "com.qtd.weatherforecast.activity.MainActivity";
    public static final int NOTIFICATION_ID = 1012;
    public static final String STATE_UPDATE_CHANGED = "state_update_changed";
    public static final String STATE_START = "state_start";
    public static final String STATE_END = "state_end";
    public static final String STATE = "state";
    public static final String STATE_NOTIFICATION = "state_noti";
    public static final int TIME_DELAY = 180000;

    public static final String HAS_CITY = "has_city";
    public static final String DEGREE = "degree";
    public static final int C = 0;
    public static final int F = 1;
    public static final String IS_OPEN_GUIDE = "IS_OPEN_GUIDE";
	public static final String BACKGROUND = "Background";
	public static final String DEFAULT_BACKGROUND = "bg_blue.jpg";
	public static String _ID = "ID";
    public static int ERROR_ID = -18;
}
