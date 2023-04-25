package in.sunilpaulmathew.weatherwidget.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import in.sunilpaulmathew.weatherwidget.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class Weather {

    public static boolean deleteDataFile(Context context) {
        return getDataFile(context).delete();
    }

    /*
     * Credits for icons: https://github.com/basmilius/weather-icons
     */
    public static Drawable getWeatherIcon(int isDay, int weatherCode, Context context) {
        return Utils.getDrawable(getWeatherIcon(isDay, weatherCode), context);
    }

    public static File getDataFile(Context context) {
        return new File(context.getExternalFilesDir("location"), "locationData");
    }

    static int getWeatherIcon(int isDay, int weatherCode) {
        switch (weatherCode) {
            case 1:
            case 2:
                return isDay == 1 ? R.drawable.ic_partly_cloudy_day : R.drawable.ic_partly_cloudy_night;
            case 3:
                return isDay == 1 ? R.drawable.ic_overcast_day : R.drawable.ic_overcast_night;
            case 51:
            case 53:
            case 55:
            case 56:
            case 57:
                return isDay == 1 ? R.drawable.ic_drizzle_day : R.drawable.ic_drizzle_night;
            case 45:
            case 48:
                return isDay == 1 ? R.drawable.ic_fog_day : R.drawable.ic_fog_night;
            case 61:
            case 63:
            case 65:
            case 66:
            case 67:
            case 80:
            case 81:
            case 82:
                return isDay == 1 ? R.drawable.ic_rain_day : R.drawable.ic_rain_night;
            case 71:
            case 73:
            case 75:
            case 77:
            case 85:
            case 86:
                return R.drawable.ic_snow;
            case 95:
            case 96:
            case 99:
                return isDay == 1 ? R.drawable.ic_thunderstorms_day : R.drawable.ic_thunderstorms_night;
            default:
                return isDay == 1 ? R.drawable.ic_clear_day : R.drawable.ic_clear_night;
        }
    }

    public static String getFormattedDay(int day) {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.getDefault());
        List<String> days = new ArrayList<>();
        Collections.addAll(days, dfs.getWeekdays());
        if (day > 21) {
            day = day - 21;
        } else if (day > 14) {
            day = day - 14;
        } else if (day > 7) {
            day = day - 7;
        }
        return days.get(day);
    }

    public static String getFormattedDate(String time) {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.getDefault());
        List<String> months = new ArrayList<>();
        Collections.addAll(months, dfs.getMonths());
        String[] timeSplit = time.split("-");
        switch (timeSplit[1]) {
            case "02":
                return months.get(1) + " " + timeSplit[2];
            case "03":
                return months.get(2) + " " + timeSplit[2];
            case "04":
                return months.get(3) + " " + timeSplit[2];
            case "05":
                return months.get(4) + " " + timeSplit[2];
            case "06":
                return months.get(5) + " " + timeSplit[2];
            case "07":
                return months.get(6) + " " + timeSplit[2];
            case "08":
                return months.get(7) + " " + timeSplit[2];
            case "09":
                return months.get(8) + " " + timeSplit[2];
            case "10":
                return months.get(9) + " " + timeSplit[2];
            case "11":
                return months.get(10) + " " + timeSplit[2];
            case "12":
                return months.get(11) + " " + timeSplit[2];
            default:
                return months.get(0) + " " + timeSplit[2];
        }
    }

    public static String getFormattedTime(String time) {
        String[] timeSplit = time.split("T");
        int newTime;
        if (timeSplit[1].replace(":00","").equals("00")) {
            newTime = 0;
        } else if (timeSplit[1].replace(":00","").startsWith("0")) {
            newTime = Integer.parseInt(timeSplit[1].replace(":00","").replace("0",""));
        } else {
            newTime = Integer.parseInt(timeSplit[1].replace(":00",""));
        }
        if (newTime == 0) {
            return 12 + " AM";
        } else if (newTime == 12) {
            return newTime + " PM";
        } else if (newTime > 12) {
            return newTime - 12 + " PM";
        } else {
            return newTime + " AM";
        }
    }

    public static String getLatitude(Context context) {
        return Utils.getString("latitude", null, context);
    }

    public static String getLocation(Context context) {
        return Utils.getString("location", null, context);
    }

    public static String getLongitude(Context context) {
        return Utils.getString("longitude", null, context);
    }

    public static String getTemperatureUnit(Context context) {
        if (Utils.getString("temperatureUnit", "", context).equals("&temperature_unit=fahrenheit")) {
            return context.getString(R.string.temperature_unit_fahrenheit);
        } else {
            return context.getString(R.string.temperature_unit_centigrade);
        }
    }

    public static String getWeatherMode(int weatherCode, Context context) {
        switch (weatherCode) {
            case 1:
                return context.getString(R.string.weather_code_clear_mostly);
            case 2:
                return context.getString(R.string.weather_code_cloudy_partly);
            case 3:
                return context.getString(R.string.weather_code_overcast);
            case 45:
                return context.getString(R.string.weather_code_fog);
            case 48:
                return context.getString(R.string.weather_code_rim_fog);
            case 51:
                return context.getString(R.string.weather_code_drizzle_light);
            case 53:
                return context.getString(R.string.weather_code_drizzle_moderate);
            case 55:
                return context.getString(R.string.weather_code_drizzle_dense);
            case 56:
                return context.getString(R.string.weather_code_drizzle_freezing_light);
            case 57:
                return context.getString(R.string.weather_code_drizzle_freezing_dense);
            case 61:
                return context.getString(R.string.weather_code_rain_light);
            case 63:
                return context.getString(R.string.weather_code_rain_moderate);
            case 65:
                return context.getString(R.string.weather_code_rain_heavy);
            case 66:
                return context.getString(R.string.weather_code_rain_freezing_light);
            case 67:
                return context.getString(R.string.weather_code_rain_freezing_heavy);
            case 71:
                return context.getString(R.string.weather_code_snowfalls_light);
            case 73:
                return context.getString(R.string.weather_code_snowfalls_moderate);
            case 75:
                return context.getString(R.string.weather_code_snowfalls_heavy);
            case 77:
                return context.getString(R.string.weather_code_snow_grains);
            case 80:
                return context.getString(R.string.weather_code_rain_showers_light);
            case 81:
                return context.getString(R.string.weather_code_rain_showers_moderate);
            case 82:
                return context.getString(R.string.weather_code_rain_showers_heavy);
            case 85:
                return context.getString(R.string.weather_code_snow_showers_light);
            case 86:
                return context.getString(R.string.weather_code_snow_showers_heavy);
            case 95:
            case 96:
            case 99:
                return context.getString(R.string.weather_code_thunderstorm);
            default:
                return context.getString(R.string.weather_code_clear);
        }
    }

}