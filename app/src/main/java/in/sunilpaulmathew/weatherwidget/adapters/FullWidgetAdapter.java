package in.sunilpaulmathew.weatherwidget.adapters;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.providers.WidgetProviderFull;
import in.sunilpaulmathew.weatherwidget.utils.Weather;
import in.sunilpaulmathew.weatherwidget.utils.WeatherItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class FullWidgetAdapter extends RecyclerView.Adapter<FullWidgetAdapter.ViewHolder> {

    private final Activity mActivity;
    private final int mAppWidgetId;
    private final List<WeatherItems> mData;

    public FullWidgetAdapter(List<WeatherItems> data, int appWidgetId, Activity activity) {
        this.mData = data;
        this.mAppWidgetId = appWidgetId;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public FullWidgetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_fullwidget, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FullWidgetAdapter.ViewHolder holder, int position) {
        if (this.mData.get(position).isSuccess()) {
            holder.mStatusIcon.setImageDrawable(this.mData.get(position).getWeatherIcon());
            holder.mTemperature.setText(this.mData.get(position).getTemperatureStatus());
            holder.mTemperature.setTextColor(this.mData.get(position).getAccentColor(holder.mTemperature.getContext()));
            holder.mWeatherStatus.setText(this.mData.get(position).getWeatherStatus());
            holder.mTempUnit.setText(Weather.getTemperatureUnit(holder.mTempUnit.getContext()));
            holder.mErrorStatus.setVisibility(View.GONE);
        } else {
            holder.mErrorStatus.setText(holder.mErrorStatus.getContext().getString(R.string.weather_status_failed));
            holder.mErrorStatus.setTextColor(ContextCompat.getColor(holder.mErrorStatus.getContext(), R.color.color_red));
            holder.mTemperature.setVisibility(View.GONE);
            holder.mWeatherStatus.setVisibility(View.GONE);
            holder.mTempUnit.setVisibility(View.GONE);
            holder.mStatusIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AppCompatImageButton mStatusIcon;
        private final MaterialTextView mErrorStatus, mTemperature, mWeatherStatus, mTempUnit;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.mStatusIcon = view.findViewById(R.id.weather_button);
            this.mTemperature = view.findViewById(R.id.temperature_status);
            this.mTempUnit = view.findViewById(R.id.temperature_unit);
            this.mErrorStatus = view.findViewById(R.id.error_status);
            this.mWeatherStatus = view.findViewById(R.id.weather_status);
        }

        @Override
        public void onClick(View view) {
            if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                return;
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mActivity);
            try {
                WidgetProviderFull.update(appWidgetManager, mAppWidgetId, mActivity);
            } catch (JSONException | IOException ignored) {
            }
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            mActivity.setResult(Activity.RESULT_OK, resultValue);
            mActivity.finish();
        }
    }

}