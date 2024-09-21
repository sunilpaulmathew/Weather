package in.sunilpaulmathew.weatherwidget.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.fragments.WeatherFragment;
import in.sunilpaulmathew.weatherwidget.utils.Utils;
import in.sunilpaulmathew.weatherwidget.utils.Weather;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Utils.isAmoledBlackEnabled(this)? R.style.AppTheme_Amoled : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Weather.getLatitude(this) == null || Weather.getLongitude(this) == null) {
            Intent initialize = new Intent(this, InitializeActivity.class);
            startActivity(initialize);
            finish();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new WeatherFragment()).commit();
    }

}