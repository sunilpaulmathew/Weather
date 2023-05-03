package in.sunilpaulmathew.weatherwidget.utils;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import in.sunilpaulmathew.weatherwidget.activities.MainActivity;
import in.sunilpaulmathew.weatherwidget.providers.WidgetProviderFull;
import in.sunilpaulmathew.weatherwidget.providers.WidgetProviderLight;
import in.sunilpaulmathew.weatherwidget.providers.WidgetProviderWeatherClock;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class Utils {

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static boolean getBoolean(String name, boolean defaults, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(name, defaults);
    }

    public static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isLocationAccessDenied(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isNetworkUnavailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            return cm.getActiveNetworkInfo() == null || !cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static boolean isNotificationAccessDenied(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED;
    }

    public static Drawable getDrawable(int drawable, Context context) {
        return ContextCompat.getDrawable(context, drawable);
    }

    public static int getColor(int color, Context context) {
        return ContextCompat.getColor(context, color);
    }

    public static int getOrientation(Activity activity) {
        return activity.isInMultiWindowMode() ? Configuration.ORIENTATION_PORTRAIT : activity.getResources().getConfiguration().orientation;
    }

    public static long getLong(String name, long defaults, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(name, defaults);
    }

    public static String valueOfInt(String string) {
        String[] strings = string.split("\\.");
        if (Integer.parseInt(strings[1]) > 5) {
            return String.valueOf(Integer.parseInt(strings[0]) + 1);
        } else {
            return String.valueOf(Integer.parseInt(strings[0]));
        }
    }

    public static String getString(String name, String defaults, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(name, defaults);
    }

    public static String read(File file) throws IOException {
        BufferedReader buf = new BufferedReader(new FileReader(file));

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = buf.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString().trim();
    }

    public static Toast toast(String message, Context context) {
        return Toast.makeText(context, message, Toast.LENGTH_LONG);
    }

    public static void launchUrl(String url, Activity activity) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            activity.startActivity(i);
        } catch (ActivityNotFoundException ignored) {
        }
    }

    public static void remove(String name, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(name).apply();
    }

    public static void restartApp(Activity activity) {
        Intent mainActivity = new Intent(activity, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(mainActivity);
        activity.finish();
    }

    public static void saveBoolean(String name, boolean value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(name, value).apply();
    }

    public static void saveLong(String name, long value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(name, value).apply();
    }

    public static void saveString(String name, String value, Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(name, value).apply();
    }

    public static void updateWidgets(Activity activity) {
        int[] idsFull = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(
                activity.getApplication(), WidgetProviderFull.class));
        int[] idsLight = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(
                activity.getApplication(), WidgetProviderLight.class));
        int[] idsWeatherClock = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(
                activity.getApplication(), WidgetProviderWeatherClock.class));
        WidgetProviderFull mFullWidgetProvider = new WidgetProviderFull();
        mFullWidgetProvider.onUpdate(activity, AppWidgetManager.getInstance(activity), idsFull);
        WidgetProviderLight mLightWidgetProvider = new WidgetProviderLight();
        mLightWidgetProvider.onUpdate(activity, AppWidgetManager.getInstance(activity), idsLight);
        WidgetProviderWeatherClock mWeatherClockWidgetProvider = new WidgetProviderWeatherClock();
        mWeatherClockWidgetProvider.onUpdate(activity, AppWidgetManager.getInstance(activity), idsWeatherClock);
    }

}