package in.sunilpaulmathew.weatherwidget.dialogs;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.serializable.ForecastItems;
import in.sunilpaulmathew.weatherwidget.utils.Weather;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on August 05, 2026
 */
public class HourlyForecastDialog extends BottomSheetDialog {

    public HourlyForecastDialog(ForecastItems forecastItem, Context context) {
        super(context);

        View detailsLayout = View.inflate(context, R.layout.layout_weather_details, null);
        AppCompatImageButton mSunriseIcon = detailsLayout.findViewById(R.id.sunrise_button);
        AppCompatImageButton mSunsetIcon = detailsLayout.findViewById(R.id.sunset_button);
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
        mDay.setText(context.getString(R.string.weather_expected_title, forecastItem.getTime()));
        mDay.setGravity(Gravity.CENTER);
        if (Weather.getLocation(context).contains(",")) {
            mLocation.setText(Weather.getLocation(context).split(",")[0]);
        } else {
            mLocation.setText(Weather.getLocation(context));
        }
        mLocation.setTextColor(forecastItem.getAccentColor(true, context));
        mTemperature.setText(forecastItem.getHourlyTemp());
        mTemperature.setTextColor(forecastItem.getAccentColor(true, context));
        mTemperature.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        mTempUnit.setText(Weather.getTemperatureUnit(context));
        mTempUnit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        mTempApparent.setText(forecastItem.getApparentTemperature());
        mWeatherStatus.setText(forecastItem.getWeatherStatus(context));
        mPrecipitation.setText(forecastItem.getPrecipitation(context));
        mAirPressure.setText(forecastItem.getAirPressure());
        mHumidity.setText(forecastItem.getHumidity());
        mVisibility.setText(forecastItem.getVisibility());

        mSunriseIcon.setVisibility(View.GONE);
        mSunsetIcon.setVisibility(View.GONE);
        mSunrise.setVisibility(View.GONE);
        mSunset.setVisibility(View.GONE);
        mWindSpeed.setVisibility(View.GONE);
        mCancelIcon.setVisibility(View.VISIBLE);

        setContentView(detailsLayout);

        mCancelIcon.setOnClickListener(v -> dismiss());

        show();
    }

}