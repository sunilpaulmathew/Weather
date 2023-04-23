package in.sunilpaulmathew.weatherwidget.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

import in.sunilpaulmathew.weatherwidget.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class ForecastItems implements Serializable {

    private final int mDay, mWeatherCode;
    private final String mTempMax, mTempMin, mTime, mUVIndex;

    public ForecastItems(String time, String tempMax, String tempMin, int weatherCode, String uvIndex, int day) {
        this.mTime = time;
        this.mTempMax = tempMax;
        this.mTempMin = tempMin;
        this.mWeatherCode = weatherCode;
        this.mUVIndex = uvIndex;
        this.mDay = day;
    }

    public Drawable getWeatherIcon(int mode, Context context) {
        return Weather.getWeatherIcon(mode, mWeatherCode, context);
    }

    public int getAccentColor(Context context) {
        return Utils.getColor(mDay == 1 ? R.color.color_accent_day : R.color.color_accent_night, context);
    }

    public int getDayOrNight() {
        return mDay;
    }

    public String getDate() {
        return Weather.getFormattedDate(mTime);
    }

    public String getDailyTemp() {
        return mTempMax + "/" + mTempMin;
    }

    public String getHourlyTemp() {
        return mTempMax;
    }

    public String getTime() {
        return Weather.getFormattedTime(mTime);
    }

    public String getUVIndex(Context context) {
        return context.getString(R.string.uv_index, mUVIndex);
    }

    public String getWeatherStatus(Context context) {
        return Weather.getWeatherMode(mWeatherCode, context);
    }

}