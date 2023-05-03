package in.sunilpaulmathew.weatherwidget.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.activities.MainActivity;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 24, 2023
 */
public class WeatherAlerts {

    private final boolean mUVAlert;
    private final Context mContext;
    private final int mDay, mWeatherCode;

    public WeatherAlerts(boolean uvAlert, int weatherCode, int day, Context context) {
        this.mUVAlert = uvAlert;
        this.mWeatherCode = weatherCode;
        this.mDay = day;
        this.mContext = context;
    }

    public void alert() {
        Uri mAlarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Intent mIntent = new Intent(mContext, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, 0, mIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationChannel mNotificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel("channel", mContext.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, "channel");
        Notification mNotification = mBuilder.setContentTitle(mUVAlert ? mContext.getString(R.string.uv_index_alert_title,
                        Weather.getLocation(mContext)) : mContext.getString(R.string.weather_alert_title, Weather.getLocation(mContext)))
                .setContentText(mUVAlert ? mContext.getString(mWeatherCode >= 8 ? R.string.uv_index_alert_high :
                        R.string.uv_index_alert_medium) : mContext.getString(R.string.weather_alert_message,
                        Weather.getWeatherMode(mWeatherCode, mContext)))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setSound(mAlarmSound)
                .setSmallIcon(mUVAlert ? R.drawable.ic_uv_index : Weather.getWeatherIcon(mDay, mWeatherCode))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentIntent(mPendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mNotificationChannel);
        }
        try {
            notificationManager.notify(0, mNotification);
            Utils.saveLong(mUVAlert ? "lastUVAlert" : "lastWeatherAlert", System.currentTimeMillis(), mContext);
        } catch (NullPointerException ignored) {
        }
    }

}