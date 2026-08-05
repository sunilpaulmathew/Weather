package in.sunilpaulmathew.weatherwidget.activities;

import android.os.Bundle;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.fragments.InitializeFragment;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class InitializeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main, R.id.fragment_container);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new InitializeFragment()).commit();
    }

}