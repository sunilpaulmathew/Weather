package in.sunilpaulmathew.weatherwidget.activities;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.adapters.FullWidgetAdapter;
import in.sunilpaulmathew.weatherwidget.interfaces.AcquireWeatherData;
import in.sunilpaulmathew.weatherwidget.utils.Weather;
import in.sunilpaulmathew.weatherwidget.utils.WeatherItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class FullWidgetActivity extends AppCompatActivity {

    private static int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recyclerview);

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (Weather.getLatitude(this) == null || Weather.getLongitude(this) == null) {
            Intent initialize = new Intent(this, InitializeActivity.class);
            startActivity(initialize);
            finish();
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        new AcquireWeatherData(Weather.getLatitude(this), Weather.getLongitude(this), this) {

            @Override
            public void successLister(List<WeatherItems> weatherItems) {
                mRecyclerView.setAdapter(new FullWidgetAdapter(weatherItems, mAppWidgetId, FullWidgetActivity.this));
            }
        }.acquire();
    }

}