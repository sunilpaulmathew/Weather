package in.sunilpaulmathew.weatherwidget.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.List;

import in.sunilpaulmathew.weatherwidget.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class WeatherItems implements Serializable {

    private final boolean mSuccess;
    private final Drawable mWeatherIcon;

    private final int mDay;
    private final List<ForecastItems> mForecastItemsHourly, mForecastItemsDaily;
    private final String mAirPressure, mCity, mHumidity, mPrecipitation, mSunrise, mSunset, mTemperature, mTempApp,
            mTimeZone, mVisibility, mWeatherStatus, mWinsSpeed;

    public WeatherItems(Drawable icon, List<ForecastItems> forecastItemsHourly, List<ForecastItems> forecastItemsDaily,
                        String city, String precipitation, String temperature, String tempApp, String timeZone, String weatherStatus,
                        String sunrise, String sunset, String windSpeed, String airPressure, String visibility,
                        String humidity, int day, boolean success) {
        this.mWeatherIcon = icon;
        this.mForecastItemsHourly = forecastItemsHourly;
        this.mForecastItemsDaily = forecastItemsDaily;
        this.mCity = city;
        this.mPrecipitation = precipitation;
        this.mTemperature = temperature;
        this.mTempApp = tempApp;
        this.mTimeZone = timeZone;
        this.mWeatherStatus = weatherStatus;
        this.mSunrise = sunrise;
        this.mSunset = sunset;
        this.mWinsSpeed = windSpeed;
        this.mAirPressure = airPressure;
        this.mVisibility = visibility;
        this.mHumidity = humidity;
        this.mDay = day;
        this.mSuccess = success;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public Drawable getWeatherIcon() {
        return mWeatherIcon;
    }

    public int getAccentColor(Context context) {
        return Utils.getColor(mDay == 1 ? R.color.color_accent_day : R.color.color_accent_night, context);
    }

    public List<ForecastItems> getHourlyForecastItems() {
        return mForecastItemsHourly;
    }

    public List<ForecastItems> getDailyForecastItems() {
        return mForecastItemsDaily;
    }

    public String getCity() {
        return mCity;
    }

    public String getPrecipitation(Context context) {
        return context.getString(R.string.precipitation, mPrecipitation);
    }

    public String getTemperatureStatus() {
        return mTemperature;
    }

    public String getApparentTemperature() {
        return mTempApp;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public String getSunriseTime() {
        return getFormattedTime(mSunrise);
    }

    public String getSunsetTime() {
        return getFormattedTime(mSunset);
    }

    public String getWeatherStatus() {
        return mWeatherStatus;
    }

    public String getWindSpeed() {
        return mWinsSpeed;
    }

    public String getAirPressure() {
        return mAirPressure;
    }

    public String getVisibility() {
        return mVisibility;
    }

    public String getHumidity() {
        return mHumidity;
    }

    private static String getFormattedTime(String time) {
        String[] timeSplit = time.split("T");
        String hour = timeSplit[1].split(":")[0];
        String min = timeSplit[1].split(":")[1];
        int newHour;
        if (hour.equals("00")) {
            newHour = 0;
        } else if (hour.startsWith("0")) {
            newHour = Integer.parseInt(hour.replace("0",""));
        } else {
            newHour = Integer.parseInt(hour);
        }
        if (newHour == 0) {
            return 12 + ":" + min + " AM";
        } else if (newHour == 12) {
            return newHour + ":" + min + " PM";
        } else if (newHour > 12) {
            return newHour - 12 + ":" + min + " PM";
        } else {
            return newHour + ":" + min + " AM";
        }
    }

}