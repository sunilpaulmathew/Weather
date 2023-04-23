package in.sunilpaulmathew.weatherwidget.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.utils.ForecastItems;
import in.sunilpaulmathew.weatherwidget.utils.Weather;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder> {

    private final List<ForecastItems> mData;

    public HourlyForecastAdapter(List<ForecastItems> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public HourlyForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_forecast_hourly, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyForecastAdapter.ViewHolder holder, int position) {
        holder.mStatusIocn.setImageDrawable(this.mData.get(position).getWeatherIcon(this.mData.get(position).getDayOrNight(), holder.mStatusIocn.getContext()));
        holder.mDate.setText(this.mData.get(position).getTime());
        holder.mTemperature.setText(this.mData.get(position).getHourlyTemp());
        holder.mTemperature.setTextColor(this.mData.get(position).getAccentColor(holder.mTemperature.getContext()));
        holder.mTemperatureUnit.setText(Weather.getTemperatureUnit(holder.mTemperatureUnit.getContext()));
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageButton mStatusIocn;
        private final MaterialTextView mDate, mTemperature, mTemperatureUnit;

        public ViewHolder(View view) {
            super(view);
            this.mStatusIocn = view.findViewById(R.id.weather_button);
            this.mDate = view.findViewById(R.id.date);
            this.mTemperature = view.findViewById(R.id.temperature_status);
            this.mTemperatureUnit = view.findViewById(R.id.temperature_unit);
        }
    }

}