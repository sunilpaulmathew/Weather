package in.sunilpaulmathew.weatherwidget.activities;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.adapters.LocationsAdapter;
import in.sunilpaulmathew.weatherwidget.interfaces.LocationListener;
import in.sunilpaulmathew.weatherwidget.utils.LocationItems;
import in.sunilpaulmathew.weatherwidget.utils.Utils;
import in.sunilpaulmathew.weatherwidget.utils.Weather;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class InitializeActivity extends AppCompatActivity {

    private AppCompatAutoCompleteTextView mLatitude, mLocation, mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intialize);

        mLatitude = findViewById(R.id.latitude);
        mLocation = findViewById(R.id.location);
        mLongitude = findViewById(R.id.longitude);
        MaterialCardView mApplyCard = findViewById(R.id.apply_card);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.location_access_request), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.request_access, v -> locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }));
        if (Utils.isLocationAccessDenied(this)) {
            snackbar.show();
        }

        mLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    ExecutorService executors = Executors.newSingleThreadExecutor();
                    executors.execute(() -> {
                        try (InputStream is = new URL("https://geocoding-api.open-meteo.com/v1/search?name=" + s.toString().trim() + "&count=10&language=en&format=json").openStream()) {
                            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                            StringBuilder sb = new StringBuilder();
                            int cp;
                            while ((cp = rd.read()) != -1) {
                                sb.append((char) cp);
                            }
                            if (!sb.toString().startsWith("{\"generationtime_ms\":")) {
                                JSONObject mMainObject = new JSONObject(sb.toString());
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    try {
                                        JSONArray mResults = mMainObject.getJSONArray("results");
                                        List<LocationItems> mData = new ArrayList<>();
                                        for (int i=0; i<mResults.length(); i++) {
                                            mData.add(new LocationItems(
                                                    mResults.getJSONObject(i).getString("name"),
                                                    mResults.getJSONObject(i).getString("country"),
                                                    mResults.getJSONObject(i).getString("latitude"),
                                                    mResults.getJSONObject(i).getString("longitude"))
                                            );
                                        }
                                        LocationsAdapter mLocationsAdapter = new LocationsAdapter(mData);
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                        mRecyclerView.setAdapter(mLocationsAdapter);

                                        mLocationsAdapter.setOnItemClickListener((position, v) -> apply(mData.get(position).getCity(),
                                                mData.get(position).getLatitude(), mData.get(position).getLongitude()));

                                    } catch (JSONException ignored) {
                                    }
                                });
                            }
                        } catch (JSONException | IOException ignored) {
                            new Handler(Looper.getMainLooper()).post(() -> Utils.toast(getString(R.string.location_data_failed), InitializeActivity.this).show());
                        }
                        if (!executors.isShutdown()) executors.shutdown();
                    });
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                }
            }
        });

        mApplyCard.setOnClickListener(v -> {
            if (mLatitude.getText().toString().trim().isEmpty() || mLongitude.getText().toString().trim().isEmpty()) {
                return;
            }
            apply(mLocation.getText().toString().trim(), mLatitude.getText().toString().trim(), mLongitude.getText().toString().trim());
        });

    }

    private void apply(String city, String latitude, String longitude) {
        ExecutorService executors = Executors.newSingleThreadExecutor();
        executors.execute(() -> {
            Utils.saveBoolean("reAcquire", true, this);
            Utils.saveString("latitude", latitude, this);
            Utils.saveString("longitude", longitude, this);
            Utils.saveString("location", city, this);
            new Handler(Looper.getMainLooper()).post(() -> Utils.restartApp(this));
            if (!executors.isShutdown()) executors.shutdown();
        });
    }

    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted != null && fineLocationGranted || coarseLocationGranted != null && coarseLocationGranted) {
                            new LocationListener(this) {
                                @Override
                                public void onLocationInitialized(String latitude, String longitude, String address) {
                                    apply(address, latitude, longitude);
                                }
                            }.initialize();
                        } else {
                            mLocation.requestFocus();
                        }
                    }
            );



    @Override
    public void onBackPressed() {
        if (Weather.getLatitude(this) != null && Weather.getLongitude(this) != null) {
            Utils.restartApp(this);
        } else {
            finish();
        }
    }

}