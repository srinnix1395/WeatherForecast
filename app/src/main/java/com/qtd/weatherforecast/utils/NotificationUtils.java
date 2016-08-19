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

/**
 * Created by Dell on 7/6/2016.
 */
public class NotificationUtils {
    public static void createNotification(Context context) {
        int id = SharedPreUtils.getInt("ID", -1);
        if (id != -1) {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(context);
            CityPlus city = databaseHelper.getCityByID(id);
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

    public static void updateNotification(Context context) {
        int id = SharedPreUtils.getInt("ID", -1);
        if (id != -1) {
            MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(context);
            CityPlus cityPlus = databaseHelper.getCityByID(id);
            databaseHelper.close();
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
            remoteViews.setImageViewResource(R.id.imv_icon, ImageUtils.getImageResourceNotification(cityPlus.getIcon()));
            remoteViews.setTextViewText(R.id.tv_weather, cityPlus.getWeather());
            remoteViews.setTextViewText(R.id.tv_temp, String.valueOf(cityPlus.getTemp()) + "°");
            remoteViews.setTextViewText(R.id.tv_location, cityPlus.getFullName());
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(ImageUtils.getImageResourceCurrentWeather(cityPlus.getIcon()))
                    .setContent(remoteViews)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(cityPlus.getTemp() + "°");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(AppConstant.NOTIFICATION_ID, notiBuilder.build());
        }
    }

    public static void clearNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(AppConstant.NOTIFICATION_ID);
    }
}
