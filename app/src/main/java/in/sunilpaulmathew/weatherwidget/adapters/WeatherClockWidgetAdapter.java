package in.sunilpaulmathew.weatherwidget.adapters;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.providers.WidgetProviderWeatherClock;
import in.sunilpaulmathew.weatherwidget.utils.Weather;
import in.sunilpaulmathew.weatherwidget.utils.WeatherItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 25, 2023
 */
public class WeatherClockWidgetAdapter extends RecyclerView.Adapter<WeatherClockWidgetAdapter.ViewHolder> {

    private final Activity mActivity;
    private final int mAppWidgetId;
    private final List<WeatherItems> mData;

    public WeatherClockWidgetAdapter(List<WeatherItems> data, int appWidgetId, Activity activity) {
        this.mData = data;
        this.mAppWidgetId = appWidgetId;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public WeatherClockWidgetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_weatherclockwidget, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherClockWidgetAdapter.ViewHolder holder, int position) {
        if (this.mData.get(position).isSuccess()) {
            holder.mTemperature.setText(this.mData.get(position).getTemperatureStatus());
            holder.mTemperature.setTextColor(this.mData.get(position).getAccentColor(holder.mTemperature.getContext()));
            holder.mTempUnit.setText(Weather.getTemperatureUnit(holder.mTempUnit.getContext()));
        } else {
            holder.mTemperature.setVisibility(View.GONE);
            holder.mTempUnit.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MaterialTextView mTemperature, mTempUnit;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mTemperature = view.findViewById(R.id.temperature_status);
            this.mTempUnit = view.findViewById(R.id.temperature_unit);
        }

        @Override
        public void onClick(View view) {
            if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                return;
            }

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mActivity);
            WidgetProviderWeatherClock.update(appWidgetManager, mAppWidgetId, mActivity);
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            mActivity.setResult(Activity.RESULT_OK, resultValue);
            mActivity.finish();
        }
    }

}