package in.sunilpaulmathew.weatherwidget.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.weatherwidget.BuildConfig;
import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.activities.InitializeActivity;
import in.sunilpaulmathew.weatherwidget.activities.SettingsActivity;
import in.sunilpaulmathew.weatherwidget.utils.Utils;
import in.sunilpaulmathew.weatherwidget.utils.Weather;
import in.sunilpaulmathew.weatherwidget.utils.WeatherItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private final Activity mActivity;
    private final List<WeatherItems> mData;

    public WeatherAdapter(List<WeatherItems> data, Activity activity) {
        this.mData = data;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_weather, parent, false);
        return new ViewHolder(rowItem);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        if (this.mData.get(position).isSuccess()) {
            holder.mStatusIcon.setImageDrawable(this.mData.get(position).getWeatherIcon());
            if (this.mData.get(position).getCity() != null) {
                if (this.mData.get(position).getCity().contains(",")) {
                    holder.mLocation.setText(this.mData.get(position).getCity().split(",")[0]);
                } else {
                    holder.mLocation.setText(this.mData.get(position).getCity());
                }
                holder.mLocation.setTextColor(this.mData.get(position).getAccentColor(holder.mLocation.getContext()));
                holder.mTimeZone.setText(this.mData.get(position).getTimeZone());
            } else {
                holder.mLocation.setVisibility(View.GONE);
                holder.mTimeZone.setVisibility(View.GONE);
            }
            holder.mPrecipitation.setText(this.mData.get(position).getPrecipitation(holder.mPrecipitation.getContext()));
            holder.mSunrise.setText(this.mData.get(position).getSunriseTime());
            holder.mSunrise.setTextColor(this.mData.get(position).getAccentColor(holder.mSunrise.getContext()));
            holder.mSunset.setText(this.mData.get(position).getSunsetTime());
            holder.mSunset.setTextColor(this.mData.get(position).getAccentColor(holder.mSunset.getContext()));
            holder.mTemperature.setText(this.mData.get(position).getTemperatureStatus());
            holder.mTemperature.setTextColor(this.mData.get(position).getAccentColor(holder.mTemperature.getContext()));
            holder.mTempUnit.setText(Weather.getTemperatureUnit(holder.mTempUnit.getContext()));
            holder.mTempApparent.setText(this.mData.get(position).getApparentTemperature());
            holder.mWeatherStatus.setText(this.mData.get(position).getWeatherStatus());
            holder.mWindSpeed.setText(this.mData.get(position).getWindSpeed());
            holder.mAirPressure.setText(this.mData.get(position).getAirPressure());
            holder.mHumidity.setText(this.mData.get(position).getHumidity());
            holder.mVisibility.setText(this.mData.get(position).getVisibility());
            holder.mRecyclerViewDaily.setLayoutManager(new LinearLayoutManager(holder.mRecyclerViewHourly.getContext()));
            holder.mRecyclerViewDaily.addItemDecoration(new DividerItemDecoration(holder.mRecyclerViewDaily.getContext(), DividerItemDecoration.VERTICAL));
            holder.mRecyclerViewDaily.setAdapter(new DailyForecastAdapter(this.mData.get(position).getDailyForecastItems()));
            holder.mRecyclerViewHourly.setLayoutManager(new LinearLayoutManager(holder.mRecyclerViewHourly.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.mRecyclerViewHourly.setAdapter(new HourlyForecastAdapter(this.mData.get(position).getHourlyForecastItems()));
            holder.mMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.mMenu);
                Menu menu = popupMenu.getMenu();
                menu.add(Menu.NONE, 0, Menu.NONE, v.getContext().getString(R.string.edit_location)).setIcon(R.drawable.ic_edit);
                menu.add(Menu.NONE, 1, Menu.NONE, v.getContext().getString(R.string.preferences)).setIcon(R.drawable.ic_settings);
                menu.add(Menu.NONE, 2, Menu.NONE, v.getContext().getString(R.string.about)).setIcon(R.drawable.ic_info);
                popupMenu.setForceShowIcon(true);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case 0:
                            if (Utils.getBoolean("gpsAllowed", false, v.getContext()) && Utils.isGPSEnabled(v.getContext())) {
                                Utils.toast(v.getContext().getString(R.string.edit_location_warning), v.getContext()).show();
                            } else {
                                Intent initialize = new Intent(v.getContext(), InitializeActivity.class);
                                initialize.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                v.getContext().startActivity(initialize);
                                mActivity.finish();
                            }
                            break;
                        case 1:
                            Intent settings = new Intent(v.getContext(), SettingsActivity.class);
                            v.getContext().startActivity(settings);
                            break;
                        case 2:
                            LayoutInflater mLayoutInflator = LayoutInflater.from(v.getContext());
                            View aboutLayout = mLayoutInflator.inflate(R.layout.layout_about, null);
                            MaterialTextView mAppTile = aboutLayout.findViewById(R.id.title);
                            mAppTile.setText(v.getContext().getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);

                            new MaterialAlertDialogBuilder(v.getContext())
                                    .setView(aboutLayout)
                                    .setPositiveButton(R.string.cancel, (dialogInterface, i) -> {
                                    }).show();
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            });
        } else {
            holder.mLocation.setText(holder.mLocation.getContext().getString(R.string.weather_status_failed));
            holder.mLocation.setTextColor(ContextCompat.getColor(holder.mLocation.getContext(), R.color.color_red));
            holder.mForecastTitleCard.setVisibility(View.GONE);
            holder.mSunrise.setVisibility(View.GONE);
            holder.mSunset.setVisibility(View.GONE);
            holder.mTemperature.setVisibility(View.GONE);
            holder.mTempApparent.setVisibility(View.GONE);
            holder.mTimeZone.setVisibility(View.GONE);
            holder.mWeatherStatus.setVisibility(View.GONE);
            holder.mTempUnit.setVisibility(View.GONE);
            holder.mWindSpeed.setVisibility(View.GONE);
            holder.mAirPressure.setVisibility(View.GONE);
            holder.mHumidity.setVisibility(View.GONE);
            holder.mVisibility.setVisibility(View.GONE);
            holder.mStatusIcon.setVisibility(View.GONE);
            holder.mSunriseIcon.setVisibility(View.GONE);
            holder.mSunsetIcon.setVisibility(View.GONE);
            holder.mMenu.setVisibility(View.GONE);
            holder.mRecyclerViewDaily.setVisibility(View.GONE);
            holder.mRecyclerViewHourly.setVisibility(View.GONE);
            holder.mPrecipitation.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton mMenu, mStatusIcon, mSunriseIcon, mSunsetIcon;
        private final MaterialCardView mForecastTitleCard;
        private final MaterialTextView mLocation, mPrecipitation, mSunrise, mSunset, mTemperature, mTempApparent, mTimeZone,
                mWeatherStatus, mTempUnit, mWindSpeed, mAirPressure, mHumidity, mVisibility;
        private final RecyclerView mRecyclerViewHourly, mRecyclerViewDaily;

        public ViewHolder(View view) {
            super(view);
            this.mMenu = view.findViewById(R.id.menu_button);
            this.mStatusIcon = view.findViewById(R.id.weather_button);
            this.mSunriseIcon = view.findViewById(R.id.sunrise_button);
            this.mSunsetIcon = view.findViewById(R.id.sunset_button);
            this.mForecastTitleCard = view.findViewById(R.id.forecast_title_card);
            this.mLocation = view.findViewById(R.id.location);
            this.mPrecipitation = view.findViewById(R.id.precipitation);
            this.mSunrise = view.findViewById(R.id.sunrise);
            this.mSunset = view.findViewById(R.id.sunset);
            this.mTemperature = view.findViewById(R.id.temperature_status);
            this.mTempApparent = view.findViewById(R.id.temperature_apparent);
            this.mTempUnit = view.findViewById(R.id.temperature_unit);
            this.mTimeZone = view.findViewById(R.id.time_zone);
            this.mWeatherStatus = view.findViewById(R.id.weather_status);
            this.mWindSpeed = view.findViewById(R.id.wind_speed);
            this.mAirPressure = view.findViewById(R.id.air_pressure);
            this.mHumidity = view.findViewById(R.id.humidity);
            this.mVisibility = view.findViewById(R.id.visibility);
            this.mRecyclerViewDaily = view.findViewById(R.id.recycler_view_daily);
            this.mRecyclerViewHourly = view.findViewById(R.id.recycler_view_hourly);
        }
    }

}