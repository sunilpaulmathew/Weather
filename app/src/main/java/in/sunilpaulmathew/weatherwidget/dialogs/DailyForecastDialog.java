package in.sunilpaulmathew.weatherwidget.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.serializable.ForecastItems;
import in.sunilpaulmathew.weatherwidget.utils.Weather;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on August 05, 2026
 */
public class DailyForecastDialog extends BottomSheetDialog {

    public DailyForecastDialog(ForecastItems forecastItem, int dailyPosition, Context context) {
        super(context);

        View detailsLayout = View.inflate(context, R.layout.layout_weather_details, null);
        AppCompatImageButton mStatusIcon = detailsLayout.findViewById(R.id.weather_button);
        MaterialButton mCancelIcon = detailsLayout.findViewById(R.id.cancel_button);
        MaterialTextView mLocation = detailsLayout.findViewById(R.id.location);
        MaterialTextView mDay = detailsLayout.findViewById(R.id.time_zone);
        MaterialTextView mPrecipitation = detailsLayout.findViewById(R.id.precipitation);
        MaterialTextView mAirPressure = detailsLayout.findViewById(R.id.air_pressure);
        MaterialTextView mSunrise = detailsLayout.findViewById(R.id.sunrise);
        MaterialTextView mSunset = detailsLayout.findViewById(R.id.sunset);
        MaterialTextView mTemperature = detailsLayout.findViewById(R.id.temperature_status);
        MaterialTextView mTempUnit = detailsLayout.findViewById(R.id.temperature_unit);
        MaterialTextView mTempApparent = detailsLayout.findViewById(R.id.temperature_apparent);
        MaterialTextView mWeatherStatus = detailsLayout.findViewById(R.id.weather_status);
        MaterialTextView mWindSpeed = detailsLayout.findViewById(R.id.wind_speed);
        MaterialTextView mHumidity = detailsLayout.findViewById(R.id.humidity);
        MaterialTextView mVisibility = detailsLayout.findViewById(R.id.visibility);
        mStatusIcon.setImageDrawable(forecastItem.getWeatherIcon(forecastItem.getDayOrNight(), context));
        mDay.setText(context.getString(R.string.weather_expected_title, Weather.getFormattedDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                + dailyPosition) + " (" + (dailyPosition == 0 ? context.getString(R.string.today) : forecastItem.getDate()) + ")"));
        mDay.setGravity(Gravity.CENTER);
        if (Weather.getLocation(context).contains(",")) {
            mLocation.setText(Weather.getLocation(context).split(",")[0]);
        } else {
            mLocation.setText(Weather.getLocation(context));
        }
        mLocation.setTextColor(forecastItem.getAccentColor(true, context));
        mSunrise.setText(forecastItem.getSunriseTime());
        mSunrise.setTextColor(forecastItem.getAccentColor(true, context));
        mSunset.setText(forecastItem.getSunsetTime());
        mSunset.setTextColor(forecastItem.getAccentColor(true, context));
        mTemperature.setText(forecastItem.getDailyTemp());
        mTemperature.setTextColor(forecastItem.getAccentColor(true, context));
        mTemperature.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        mTempUnit.setText(Weather.getTemperatureUnit(context));
        mTempUnit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        mWeatherStatus.setText(forecastItem.getWeatherStatus(context));
        mPrecipitation.setText(forecastItem.getUVIndex(context));
        mAirPressure.setTypeface(Typeface.DEFAULT_BOLD);
        getUVIndexText(forecastItem, mAirPressure, context);

        mTempApparent.setVisibility(View.GONE);
        mWindSpeed.setVisibility(View.GONE);
        mHumidity.setVisibility(View.GONE);
        mVisibility.setVisibility(View.GONE);
        mCancelIcon.setVisibility(View.VISIBLE);

        setContentView(detailsLayout);

        mCancelIcon.setOnClickListener(v -> dismiss());

        show();
    }

    @SuppressLint("SetTextI18n")
    private static void getUVIndexText(ForecastItems forecastItems, MaterialTextView alertText, Context context) {
        int value;
        String mUVIndex = forecastItems.getUVIndex(context).replace("UV Index: ", "");
        if (mUVIndex.contains(".")) {
            value = Integer.parseInt(mUVIndex.split("\\.")[0]);
        } else {
            value = Integer.parseInt(mUVIndex);
        }
        if (value >= 8) {
            alertText.setText("(" + context.getString(R.string.uv_index_alert_high) + ")");
            alertText.setTextColor(Color.RED);
        } else if (value >= 3) {
            alertText.setText("(" + context.getString(R.string.uv_index_alert_medium) + ")");
            alertText.setTextColor(Color.YELLOW);
        } else {
            alertText.setText("(" + context.getString(R.string.uv_index_alert_safe) + ")");
            alertText.setTextColor(Color.GREEN);
        }
        alertText.setGravity(Gravity.CENTER);
    }

}