package in.sunilpaulmathew.weatherwidget.interfaces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.serializable.ForecastItems;
import in.sunilpaulmathew.weatherwidget.utils.Utils;
import in.sunilpaulmathew.weatherwidget.utils.Weather;
import in.sunilpaulmathew.weatherwidget.utils.WeatherAlerts;
import in.sunilpaulmathew.weatherwidget.serializable.WeatherItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public abstract class AcquireWeatherData {

    private final Context mContext;
    private final String mLatitude, mLongitude;

    public AcquireWeatherData(String latitude, String longitude, Context context) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mContext = context.getApplicationContext();
    }

    @SuppressLint("StringFormatMatches")
    public void acquire() {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            executor.execute(() -> {
                try {
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
                        File dataFile = Weather.getDataFile(mContext);
                        boolean shouldFetch = Utils.getBoolean("reAcquire", false, mContext)
                                || !dataFile.exists()
                                || (dataFile.lastModified() + 1800000 < System.currentTimeMillis() && !Utils.isNetworkUnavailable(mContext));

                        if (shouldFetch) {
                            String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + mLatitude
                                    + "&longitude=" + mLongitude
                                    + "&current_weather=true"
                                    + "&daily=weathercode,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,uv_index_max,sunrise,sunset"
                                    + "&hourly=temperature_2m,weathercode,precipitation_probability,visibility,relativehumidity_2m,pressure_msl,apparent_temperature,is_day"
                                    + "&timezone=auto"
                                    + Utils.getString("temperatureUnit", "", mContext)
                                    + Utils.getString("forecastDays", "", mContext);

                            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
                            conn.setConnectTimeout(10000); // 10s connect timeout
                            conn.setReadTimeout(15000);    // 15s read timeout
                            conn.setRequestProperty("User-Agent", "AndroidWeatherApp/1.0");

                            int responseCode = conn.getResponseCode();
                            if (responseCode != HttpURLConnection.HTTP_OK) {
                                throw new IOException("HTTP error code: " + responseCode + " for URL: " + urlString);
                            }

                            StringBuilder sb = new StringBuilder();
                            try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                                String line;
                                while ((line = rd.readLine()) != null) {
                                    sb.append(line);
                                }
                            } finally {
                                conn.disconnect();
                            }

                            try (FileWriter writer = new FileWriter(dataFile)) {
                                writer.write(sb.toString());
                            }

                            jsonObject = new JSONObject(sb.toString());
                            if (Utils.getBoolean("reAcquire", false, mContext)) {
                                Utils.saveBoolean("reAcquire", false, mContext);
                            }
                        } else {
                            jsonObject = new JSONObject(Utils.read(dataFile));
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
                        String timeHour = mCurrentWeather.getString("time").split("T")[1].split(":")[0];
                        int hour = Integer.parseInt(timeHour);

                        int maxHourlyIndex = Math.min(hour + 24, mHourlyTime.length());
                        for (int i = hour; i < maxHourlyIndex; i++) {
                            mHourlyForecastItems.add(
                                    new ForecastItems(
                                            mHourlyTime.optString(i, ""),
                                            Utils.valueOfInt(safeNumberString(mHourlyTemp, i)),
                                            null,
                                            "(" + mContext.getString(R.string.temperature_feels_like, Utils.valueOfInt(safeNumberString(mTempApparent, i)))
                                                    + Weather.getTemperatureUnit(mContext) + ")",
                                            mHourlyWeatherCode.optInt(i, 0),
                                            null,
                                            mDayOrNight.optInt(i, 1),
                                            null,
                                            null,
                                            mContext.getString(R.string.humidity, mHumidity.optInt(i, 0)),
                                            safeNumberString(mHourlyPre, i),
                                            mContext.getString(R.string.air_pressure, safeNumberString(mAirPressure, i)),
                                            mContext.getString(R.string.visibility, safeNumberString(mVisibility, i))
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
                            int uvToday = (int) Math.round(mUVIndex.optDouble(0, 0));
                            if (uvToday >= 3 && Weather.getFormattedHour(mSunrise.optString(0, "06:00")) + 1 <= hour
                                    && Weather.getFormattedHour(mSunrise.optString(0, "06:00")) + 3 >= hour
                                    && Utils.getLong("lastUVAlert", Long.MIN_VALUE, mContext) + 5 * 60 * 60 * 1000 < System.currentTimeMillis()) {
                                new WeatherAlerts(true, uvToday, Integer.MIN_VALUE, mContext).alert();
                            }

                            if (hour + 2 < mHourlyWeatherCode.length()
                                    && Utils.getLong("lastWeatherAlert", Long.MIN_VALUE, mContext) + 3 * 60 * 60 * 1000 < System.currentTimeMillis()) {
                                int alertCode = mHourlyWeatherCode.optInt(hour + 2, 0);
                                int[] weatherCodes = new int[]{45, 48, 55, 57, 65, 67, 75, 82, 86, 95, 96, 99};
                                for (int weatherCode : weatherCodes) {
                                    if (alertCode == weatherCode) {
                                        new WeatherAlerts(false, alertCode, mDayOrNight.optInt(hour + 2, 1), mContext).alert();
                                    }
                                }
                            }
                        }

                        List<ForecastItems> mDailyForecastItems = new ArrayList<>();
                        for (int i = 0; i < mDailyTime.length(); i++) {
                            mDailyForecastItems.add(
                                    new ForecastItems(
                                            mDailyTime.optString(i, ""),
                                            Utils.valueOfInt(safeNumberString(mDailyTempMax, i)),
                                            Utils.valueOfInt(safeNumberString(mDailyTempMin, i)),
                                            null,
                                            mDailyWeatherCode.optInt(i, 0),
                                            safeNumberString(mUVIndex, i),
                                            mDayOrNight.optInt(i, 1),
                                            mSunrise.optString(i, ""),
                                            mSunset.optString(i, ""),
                                            null,
                                            null,
                                            null,
                                            null
                                    )
                            );
                        }

                        mWeatherItems.add(
                                new WeatherItems(
                                        Weather.getWeatherIcon(mCurrentWeather.optInt("is_day", 1), mCurrentWeather.optInt("weathercode", 0), mContext),
                                        mHourlyForecastItems,
                                        mDailyForecastItems,
                                        Weather.getLocation(mContext),
                                        safeNumberString(mHourlyPre, hour),
                                        Utils.valueOfInt(String.valueOf(mCurrentWeather.optDouble("temperature", 0))),
                                        "(" + mContext.getString(R.string.temperature_feels_like, Utils.valueOfInt(safeNumberString(mTempApparent, hour)))
                                                + Weather.getTemperatureUnit(mContext) + ")",
                                        "(" + jsonObject.optString("timezone", "") + ")",
                                        Weather.getWeatherMode(mCurrentWeather.optInt("weathercode", 0), mContext),
                                        mSunrise.optString(0, ""),
                                        mSunset.optString(0, ""),
                                        mContext.getString(R.string.wind_speed, (int) Math.round(mCurrentWeather.optDouble("windspeed", 0))),
                                        mContext.getString(R.string.air_pressure, safeNumberString(mAirPressure, hour)),
                                        mContext.getString(R.string.visibility, safeNumberString(mVisibility, hour)),
                                        mContext.getString(R.string.humidity, mHumidity.optInt(hour, 0)),
                                        mCurrentWeather.optInt("is_day", 1),
                                        true
                                )
                        );
                    } catch (Exception e) {
                        Log.e("Weather", "Failed to acquire weather data", e);
                        new Handler(Looper.getMainLooper()).post(() -> Utils.toast(mContext.getString(R.string.weather_status_failed), mContext).show());
                        mWeatherItems.add(
                                new WeatherItems(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, Integer.MIN_VALUE, false)
                        );
                    }

                    new Handler(Looper.getMainLooper()).post(() -> successLister(mWeatherItems));
                } finally {
                    executor.shutdown();
                }
            });
        }
    }

    private String safeNumberString(JSONArray array, int index) {
        if (array == null || index < 0 || index >= array.length() || array.isNull(index)) {
            return "0";
        }
        return array.optString(index, "0");
    }

    public abstract void successLister(List<WeatherItems> weatherItems);
}