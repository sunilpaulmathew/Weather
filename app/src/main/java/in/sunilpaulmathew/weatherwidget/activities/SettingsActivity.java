package in.sunilpaulmathew.weatherwidget.activities;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.sunilpaulmathew.weatherwidget.BuildConfig;
import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.adapters.SettingsAdapter;
import in.sunilpaulmathew.weatherwidget.interfaces.SingleChoiceDialog;
import in.sunilpaulmathew.weatherwidget.utils.SettingsItems;
import in.sunilpaulmathew.weatherwidget.utils.Utils;
import in.sunilpaulmathew.weatherwidget.utils.Weather;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayoutCompat mMainLayout = findViewById(R.id.layout_main);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);

        if (Utils.isAmoledBlackEnabled(this)) {
            mMainLayout.setBackgroundColor(Utils.getColor(R.color.color_black, this));
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, Utils.getOrientation(
                this)  == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1));
        SettingsAdapter mAdapter = new SettingsAdapter(getData());
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((position, v) -> {
            if (getData().get(position).getUrl() != null) {
                Utils.launchUrl(getData().get(position).getUrl(), this);
            } else if (position == 0) {
                Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                settings.setData(uri);
                startActivity(settings);
                finish();
            } else if (position == 1) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || !Utils.isDarkTheme(this)) {
                    Utils.toast(getString(R.string.amoled_black_message), this).show();
                    return;
                }
                Utils.saveBoolean("amoledTheme", !Utils.getBoolean("amoledTheme", false, this), this);
                Utils.restartApp(this);
            } else if (position == 2) {
                if (Utils.isLocationAccessDenied(this)) {
                    locationPermissionRequest.launch(new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    });
                } else {
                    Utils.saveBoolean("useGPS", !Utils.getBoolean("useGPS", true, this), this);
                    Utils.saveBoolean("gpsAllowed", !Utils.getBoolean("gpsAllowed", false, this), this);
                    recreate();
                }
            } else if (position == 3) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && Utils.isNotificationAccessDenied(this)) {
                    notificationPermissionRequest.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                    );
                } else {
                    Utils.saveBoolean("weatherAlerts", !Utils.getBoolean("weatherAlerts", false, this), this);
                    recreate();
                }
            } else if (position == 4) {
                new SingleChoiceDialog(R.drawable.ic_thermostat, getString(R.string.temperature_unit),
                        new String[] {
                                getString(R.string.centigrade),
                                getString(R.string.fahrenheit)
                        }, temperatureUnitPosition(), SettingsActivity.this) {
                    @Override
                    public void onItemSelected(int itemPosition) {
                        if (itemPosition == temperatureUnitPosition()) return;
                        if (Utils.isNetworkUnavailable(SettingsActivity.this)) {
                            Utils.toast(getString(R.string.network_connection_failed), SettingsActivity.this).show();
                            return;
                        }
                        if (itemPosition == 1) {
                            Utils.saveString("temperatureUnit", "&temperature_unit=fahrenheit", SettingsActivity.this);
                        } else {
                            Utils.saveString("temperatureUnit", "", SettingsActivity.this);
                        }
                        Weather.deleteDataFile(SettingsActivity.this);
                        Utils.restartApp(SettingsActivity.this);
                    }
                }.show();
            } else if (position == 5) {
                new SingleChoiceDialog(R.drawable.ic_days, getString(R.string.forecast_days),
                        new String[] {
                                getString(R.string.days, "3"),
                                getString(R.string.days, "7"),
                                getString(R.string.days, "14")
                        }, forecastDaysPosition(), SettingsActivity.this) {
                    @Override
                    public void onItemSelected(int itemPosition) {
                        if (itemPosition == forecastDaysPosition()) return;
                        if (Utils.isNetworkUnavailable(SettingsActivity.this)) {
                            Utils.toast(getString(R.string.network_connection_failed), SettingsActivity.this).show();
                            return;
                        }
                        if (itemPosition == 2) {
                            Utils.saveString("forecastDays", "&forecast_days=14", SettingsActivity.this);
                        } else if (itemPosition == 1) {
                            Utils.saveString("forecastDays", "", SettingsActivity.this);
                        } else {
                            Utils.saveString("forecastDays", "&forecast_days=3", SettingsActivity.this);
                        }
                        Weather.deleteDataFile(SettingsActivity.this);
                        Utils.restartApp(SettingsActivity.this);
                    }
                }.show();
            } else if (position == 6) {
                Utils.saveBoolean("transparentBackground", !Utils.getBoolean("transparentBackground", false, this), this);
                Utils.updateWidgets(this);
                recreate();
            }
        });
    }

    private ArrayList<SettingsItems> getData() {
        ArrayList<SettingsItems> mData = new ArrayList<>();
        mData.add(new SettingsItems(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + " ("
                + BuildConfig.VERSION_CODE + ")", "Copyright: ©2023-2024, sunilpaulmathew",
                Utils.getDrawable(R.drawable.ic_info, this), null));
        mData.add(new SettingsItems(getString(R.string.amoled_black), getString(R.string.amoled_black_summary),
                Utils.getDrawable(R.drawable.ic_theme, this), null));
        mData.add(new SettingsItems(getString(R.string.location_service), getString(R.string.location_service_summary),
                Utils.getDrawable(R.drawable.ic_gps, this), null));
        mData.add(new SettingsItems(getString(R.string.weather_alert), getString(R.string.weather_alert_summary),
                Utils.getDrawable(R.drawable.ic_notifications, this), null));
        mData.add(new SettingsItems(getString(R.string.temperature_unit), getTemperatureUnit(),
                Utils.getDrawable(R.drawable.ic_thermostat, this), null));
        mData.add(new SettingsItems(getString(R.string.forecast_days), getForecastDays(),
                Utils.getDrawable(R.drawable.ic_days, this), null));
        mData.add(new SettingsItems(getString(R.string.transparent_background), getString(R.string.transparent_background_summary),
                Utils.getDrawable(R.drawable.ic_eye, this), null));
        mData.add(new SettingsItems(getString(R.string.source_code), getString(R.string.source_code_summary),
                Utils.getDrawable(R.drawable.ic_github, this), "https://github.com/sunilpaulmathew/Weather"));
        mData.add(new SettingsItems(getString(R.string.report_issue), getString(R.string.report_issue_summary),
                Utils.getDrawable(R.drawable.ic_issue, this), "https://github.com/sunilpaulmathew/Weather/issues/new/choose"));
        mData.add(new SettingsItems(getString(R.string.translations), getString(R.string.translations_summary),
                Utils.getDrawable(R.drawable.ic_translate, this), "https://poeditor.com/join/project/DV7W7CTUV0"));
        return mData;
    }

    private int temperatureUnitPosition() {
        if (Utils.getString("temperatureUnit", "", this).equals("&temperature_unit=fahrenheit")) {
            return 1;
        } else {
            return 0;
        }
    }

    private int forecastDaysPosition() {
        String days = Utils.getString("forecastDays", "", this);
        if (days.equals("&forecast_days=14")) {
            return 2;
        } else if (days.equals("&forecast_days=3")) {
            return 0;
        } else {
            return 1;
        }
    }

    private String getTemperatureUnit() {
        if (Utils.getString("temperatureUnit", "", this).equals("&temperature_unit=fahrenheit")) {
            return getString(R.string.fahrenheit);
        } else {
            return getString(R.string.centigrade);
        }
    }

    private String getForecastDays() {
        String days = Utils.getString("forecastDays", "", this);
        if (days.equals("&forecast_days=14")) {
            return getString(R.string.days, "14");
        } else if (days.equals("&forecast_days=3")) {
            return getString(R.string.days, "3");
        } else {
            return getString(R.string.days, "7");
        }
    }

    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                Utils.saveBoolean("useGPS", fineLocationGranted != null && fineLocationGranted
                                || coarseLocationGranted != null && coarseLocationGranted, this);
                        recreate();
                    }
            );

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private final ActivityResultLauncher<String> notificationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestPermission(), isGranted -> {
                        Utils.saveBoolean("weatherAlerts", isGranted, this);
                        recreate();
                    }
            );

}