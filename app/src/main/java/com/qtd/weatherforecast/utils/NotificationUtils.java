package com.qtd.weatherforecast.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.activity.MainActivity;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.model.CityPlus;

import static com.qtd.weatherforecast.constant.AppConstant.HAS_CITY;

/**
 * Created by Dell on 7/6/2016.
 */
public class NotificationUtils {
    public static void createOrUpdateNotification(Context context) {
        //// TODO: 1/3/2017 fix bug bad noti
        if (!SharedPreUtils.getBoolean(HAS_CITY, false)) {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(context);
            CityPlus city = databaseHelper.getCityByID(SharedPreUtils.getInt(AppConstant._ID, -1));
            databaseHelper.close();

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
            remoteViews.setImageViewResource(R.id.imv_icon, ImageUtils.getImageResourceNotification(city.getIcon()));
            remoteViews.setTextViewText(R.id.tv_location, city.getFullName());
            remoteViews.setTextViewText(R.id.tv_weather, city.getWeather());
            remoteViews.setTextViewText(R.id.tv_temp, String.valueOf(city.getTemp()) + "°");
            NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(ImageUtils.getImageResourceCurrentWeather(city.getIcon()))
                    .setContent(remoteViews)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(city.getTemp() + "°");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(AppConstant.NOTIFICATION_ID, notiBuilder.build());
        }
    }

    public static void clearNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(AppConstant.NOTIFICATION_ID);
    }
}
