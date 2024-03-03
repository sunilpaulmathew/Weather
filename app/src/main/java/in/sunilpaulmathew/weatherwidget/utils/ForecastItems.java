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
    private final String mTempMax, mTempMin, mTempApp, mTime, mUVIndex, mSunrise, mSunset, mHumidity, mPrecipitation, mAirPressure, mVisibility;

    public ForecastItems(String time, String tempMax, String tempMin, String tempApp, int weatherCode, String uvIndex, int day, String sunRise,
                         String sunSet, String humidity, String precipitation, String airPressure, String visibility) {
        this.mTime = time;
        this.mTempMax = tempMax;
        this.mTempMin = tempMin;
        this.mTempApp = tempApp;
        this.mWeatherCode = weatherCode;
        this.mUVIndex = uvIndex;
        this.mDay = day;
        this.mSunrise = sunRise;
        this.mSunset = sunSet;
        this.mHumidity = humidity;
        this.mPrecipitation = precipitation;
        this.mAirPressure = airPressure;
        this.mVisibility = visibility;
    }

    public Drawable getWeatherIcon(int mode, Context context) {
        return Weather.getWeatherIcon(mode, mWeatherCode, context);
    }

    public int getAccentColor(boolean day, Context context) {
        return Utils.getColor(!day || mDay == 1 ? R.color.color_accent_day : R.color.color_accent_night, context);
    }

    public int getDayOrNight() {
        return mDay;
    }

    public String getAirPressure() {
        return mAirPressure;
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

    public String getHumidity() {
        return mHumidity;
    }

    public String getPrecipitation(Context context) {
        return context.getString(R.string.precipitation, mPrecipitation);
    }

    public String getSunriseTime() {
        return Weather.getFormattedTime(true, mSunrise);
    }

    public String getSunsetTime() {
        return Weather.getFormattedTime(true, mSunset);
    }

    public String getApparentTemperature() {
        return mTempApp;
    }

    public String getTime() {
        return Weather.getFormattedTime(false, mTime);
    }

    public String getUVIndex(Context context) {
        return context.getString(R.string.uv_index, mUVIndex);
    }

    public String getVisibility() {
        return mVisibility;
    }

    public String getWeatherStatus(Context context) {
        return Weather.getWeatherMode(mWeatherCode, context);
    }

}