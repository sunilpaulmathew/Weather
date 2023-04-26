package in.sunilpaulmathew.weatherwidget.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.adapters.WeatherAdapter;
import in.sunilpaulmathew.weatherwidget.interfaces.AcquireWeatherData;
import in.sunilpaulmathew.weatherwidget.utils.Utils;
import in.sunilpaulmathew.weatherwidget.utils.Weather;
import in.sunilpaulmathew.weatherwidget.utils.WeatherItems;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class MainActivity extends AppCompatActivity {

    private boolean mExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatImageView mSplash = findViewById(R.id.splash_screen);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (Weather.getLatitude(this) == null || Weather.getLongitude(this) == null) {
            Intent initialize = new Intent(this, InitializeActivity.class);
            startActivity(initialize);
            finish();
        }

        new AcquireWeatherData(Weather.getLatitude(this), Weather.getLongitude(this), this) {

            @Override
            public void successLister(List<WeatherItems> weatherItems) {
                mSplash.setVisibility(View.GONE);
                mRecyclerView.setAdapter(new WeatherAdapter(weatherItems, MainActivity.this));
            }
        }.acquire();

        // Update widgets on app launch
        Utils.updateWidgets(this);
    }

    @Override
    public void onBackPressed() {
        if (mExit) {
            mExit = false;
            finish();
        } else {
            Utils.toast(getString(R.string.exit_confirmation_message), this).show();
            mExit = true;
            new Handler().postDelayed(() -> mExit = false, 2000);
        }
    }

}