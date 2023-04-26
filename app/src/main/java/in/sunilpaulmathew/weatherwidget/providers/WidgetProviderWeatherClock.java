package in.sunilpaulmathew.weatherwidget.providers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import java.util.List;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.activities.MainActivity;
import in.sunilpaulmathew.weatherwidget.interfaces.AcquireWeatherData;
import in.sunilpaulmathew.weatherwidget.utils.Utils;
import in.sunilpaulmathew.weatherwidget.utils.Weather;
import in.sunilpaulmathew.weatherwidget.utils.WeatherItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 25, 2023
 */
public class WidgetProviderWeatherClock extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            update(appWidgetManager, appWidgetId, context);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Utils.remove("appwidget" + appWidgetId, context);
        }
    }

    public static void update(AppWidgetManager appWidgetManager, int appWidgetId, Context context) {
        RemoteViews mViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout_weather_clock);
        Intent mIntent = new Intent(context, MainActivity.class);

        new AcquireWeatherData(Weather.getLatitude(context), Weather.getLongitude(context), context) {

            @Override
            public void successLister(List<WeatherItems> weatherItems) {

                for (WeatherItems items : weatherItems) {
                    if (items.isSuccess()) {
                        mViews.setTextViewText(R.id.temperature_status, items.getTemperatureStatus());
                        mViews.setTextColor(R.id.temperature_status, items.getAccentColor(context));
                        mViews.setTextViewText(R.id.temperature_unit, Weather.getTemperatureUnit(context));
                        PendingIntent mPendingIntent = PendingIntent.getActivity(context, appWidgetId, mIntent, PendingIntent.FLAG_IMMUTABLE);
                        mViews.setOnClickPendingIntent(R.id.layout, mPendingIntent);
                        appWidgetManager.updateAppWidget(appWidgetId, mViews);
                    } else {
                        mViews.setViewVisibility(R.id.temperature_status, View.GONE);
                        mViews.setViewVisibility(R.id.temperature_unit, View.GONE);
                    }
                }
            }
        }.acquire();
    }

}