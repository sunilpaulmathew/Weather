package in.sunilpaulmathew.weatherwidget.interfaces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.utils.ForecastItems;
import in.sunilpaulmathew.weatherwidget.utils.Utils;
import in.sunilpaulmathew.weatherwidget.utils.Weather;
import in.sunilpaulmathew.weatherwidget.utils.WeatherAlerts;
import in.sunilpaulmathew.weatherwidget.utils.WeatherItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public abstract class AcquireWeatherData {

    private final Context mContext;
    private final String mLatitude, mLongitude;

    public AcquireWeatherData(String latitude, String longitude, Context context) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mContext = context;
    }

    @SuppressLint("StringFormatMatches")
    public void acquire() {
        ExecutorService executors = Executors.newSingleThreadExecutor();
        executors.execute(() -> {
            // Initialize LocationListener to find out current location
            new LocationListener(mContext) {
                @Override
                public void onLocationInitialized(String latitude, String longitude, String address) {
                    Utils.saveBoolean("reAcquire", true, mContext);
                    Utils.saveString("latitude", latitude, mContext);
                    Utils.saveString("longitude", longitude, mContext);
                    Utils.saveString("location", address, mContext);
                }
            }.initialize();

            List<WeatherItems> mWeatherItems = new ArrayList<>();
            JSONObject jsonObject;
            try {
                if (Utils.getBoolean("reAcquire", false, mContext) || Weather.getDataFile(mContext).lastModified() + 1800000 < System.currentTimeMillis()
                        && !Utils.isNetworkUnavailable(mContext)) {
                    InputStream is = new URL("https://api.open-meteo.com/v1/forecast?latitude=" + mLatitude + "&longitude=" + mLongitude +
                            "&current_weather=true&daily=weathercode,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min," +
                            "uv_index_max,sunrise,sunset&hourly=temperature_2m,weathercode,precipitation_probability,visibility,relativehumidity_2m," +
                            "pressure_msl,apparent_temperature,is_day&timezone=auto" + Utils.getString("temperatureUnit", "", mContext) +
                            Utils.getString("forecastDays", "", mContext)).openStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    int cp;
                    while ((cp = rd.read()) != -1) {
                        sb.append((char) cp);
                    }
                    FileWriter writer = new FileWriter(Weather.getDataFile(mContext));
                    writer.write(sb.toString());
                    writer.close();
                    jsonObject = new JSONObject(sb.toString());
                    if (Utils.getBoolean("reAcquire", false, mContext)) {
                        Utils.saveBoolean("reAcquire", false, mContext);
                    }
                } else {
                    jsonObject = new JSONObject(Utils.read(Weather.getDataFile(mContext)));
                }

                JSONObject mCurrentWeather = jsonObject.getJSONObject("current_weather");

                JSONObject mHourly = jsonObject.getJSONObject("hourly");
                JSONArray mHourlyTime = mHourly.getJSONArray("time");
                JSONArray mHourlyWeatherCode = mHourly.getJSONArray("weathercode");
                JSONArray mHourlyTemp = mHourly.getJSONArray("temperature_2m");
                JSONArray mHourlyPre = mHourly.getJSONArray("precipitation_probability");
                JSONArray mAirPressure = mHourly.getJSONArray("pressure_msl");
                JSONArray mVisibility = mHourly.getJSONArray("visibility");
                JSONArray mHumidity = mHourly.getJSONArray("relativehumidity_2m");
                JSONArray mTempApparent = mHourly.getJSONArray("apparent_temperature");
                JSONArray mDayOrNight = mHourly.getJSONArray("is_day");

                List<ForecastItems> mHourlyForecastItems = new ArrayList<>();
                int hour;
                String timeHour = mCurrentWeather.getString("time").split("T")[1].split(":")[0];
                if (timeHour.equals("00")) {
                    hour = 0;
                } else if (timeHour.startsWith("0")) {
                    hour = Integer.parseInt(timeHour.replace("0",""));
                } else {
                    hour = Integer.parseInt(timeHour);
                }
                for (int i = hour; i < hour + 24; i++) {
                    mHourlyForecastItems.add(
                            new ForecastItems(
                                    mHourlyTime.getString(i),
                                    Utils.valueOfInt(mHourlyTemp.getString(i)),
                                    null,
                                    mHourlyWeatherCode.getInt(i),
                                    null,
                                    mDayOrNight.getInt(i)
                            )
                    );
                }

                JSONObject mDaily = jsonObject.getJSONObject("daily");
                JSONArray mDailyTime = mDaily.getJSONArray("time");
                JSONArray mDailyWeatherCode = mDaily.getJSONArray("weathercode");
                JSONArray mDailyTempMax = mDaily.getJSONArray("temperature_2m_max");
                JSONArray mDailyTempMin = mDaily.getJSONArray("temperature_2m_min");
                JSONArray mUVIndex = mDaily.getJSONArray("uv_index_max");
                JSONArray mSunrise = mDaily.getJSONArray("sunrise");
                JSONArray mSunset = mDaily.getJSONArray("sunset");

                // Send weather alerts if enabled
                if (Utils.getBoolean("weatherAlerts", false, mContext)) {
                    if (mUVIndex.getInt(0) >= 3 && Weather.getFormattedHour(mSunrise.getString(0)) + 1 <= hour && Weather.getFormattedHour(
                            mSunrise.getString(0)) + 3 >= hour && Utils.getLong("lastUVAlert", Long.MIN_VALUE, mContext) +
                            5 * 60 * 60 * 1000 < System.currentTimeMillis()) {
                        new WeatherAlerts(true, mUVIndex.getInt(0), Integer.MIN_VALUE, mContext).alert();
                    }

                    if (Utils.getLong("lastWeatherAlert", Long.MIN_VALUE, mContext) + 3 * 60 * 60 * 1000 < System.currentTimeMillis()) {
                        int alertCode = mHourlyWeatherCode.getInt(hour + 2);
                        Integer[] weatherCodes = new Integer[]{
                                45, 48, 55, 57, 65, 67, 75, 82, 86, 95, 96, 99
                        };
                        for (Integer weatherCode : weatherCodes) {
                            if (alertCode == weatherCode) {
                                new WeatherAlerts(false, alertCode, mDayOrNight.getInt(hour + 2), mContext).alert();
                            }
                        }
                    }
                }

                List<ForecastItems> mDailyForecastItems = new ArrayList<>();
                for (int i = 0; i < mDailyTime.length(); i++) {
                    mDailyForecastItems.add(
                            new ForecastItems(
                                    mDailyTime.getString(i),
                                    Utils.valueOfInt(mDailyTempMax.getString(i)),
                                    Utils.valueOfInt(mDailyTempMin.getString(i)),
                                    mDailyWeatherCode.getInt(i),
                                    mUVIndex.getString(i),
                                    1
                            )
                    );
                }

                mWeatherItems.add(
                        new WeatherItems(
                                Weather.getWeatherIcon(mCurrentWeather.getInt("is_day"), mCurrentWeather.getInt("weathercode"), mContext),
                                mHourlyForecastItems,
                                mDailyForecastItems,
                                Weather.getLocation(mContext),
                                mHourlyPre.getString(hour),
                                Utils.valueOfInt(mCurrentWeather.getString("temperature")),
                                "(" + mContext.getString(R.string.temperature_feels_like, Utils.valueOfInt(mTempApparent.getString(hour))) +
                                        Weather.getTemperatureUnit(mContext) + ")",
                                "(" + jsonObject.getString("timezone") + ")",
                                Weather.getWeatherMode(mCurrentWeather.getInt("weathercode"), mContext),
                                mSunrise.getString(0),
                                mSunset.getString(0),
                                mContext.getString(R.string.wind_speed, mCurrentWeather.getInt("windspeed")),
                                mContext.getString(R.string.air_pressure, mAirPressure.getString(hour)),
                                mContext.getString(R.string.visibility, mVisibility.getString(hour)),
                                mContext.getString(R.string.humidity, mHumidity.getInt(hour)),
                                mCurrentWeather.getInt("is_day"),
                                true
                        )
                );
            } catch (JSONException | IOException ignored) {
                new Handler(Looper.getMainLooper()).post(() -> Utils.toast(mContext.getString(R.string.weather_status_failed), mContext).show());
                mWeatherItems.add(
                        new WeatherItems(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                Integer.MIN_VALUE,
                                false
                        )
                );
            }

            new Handler(Looper.getMainLooper()).post(() -> successLister(mWeatherItems));

            if (!executors.isShutdown()) executors.shutdown();
        });
    }

    public abstract void successLister(List<WeatherItems> weatherItems);

}