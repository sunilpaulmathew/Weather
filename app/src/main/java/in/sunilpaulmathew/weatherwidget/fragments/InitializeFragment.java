package in.sunilpaulmathew.weatherwidget.fragments;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

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
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.adapters.LocationsAdapter;
import in.sunilpaulmathew.weatherwidget.interfaces.LocationListener;
import in.sunilpaulmathew.weatherwidget.utils.LocationItems;
import in.sunilpaulmathew.weatherwidget.utils.Utils;
import in.sunilpaulmathew.weatherwidget.utils.Weather;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 28, 2023
 */
public class InitializeFragment extends Fragment {

    private TextInputEditText mLatitude, mLocation, mLongitude;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_intialize, container, false);

        RelativeLayout mMainLayout = mRootView.findViewById(R.id.main);
        mLatitude = mRootView.findViewById(R.id.latitude);
        mLocation = mRootView.findViewById(R.id.location);
        mLongitude = mRootView.findViewById(R.id.longitude);
        MaterialCardView mApplyCard = mRootView.findViewById(R.id.apply_card);
        RecyclerView mRecyclerView = mRootView.findViewById(R.id.recycler_view);

        if (Utils.isAmoledBlackEnabled(requireActivity())) {
            mMainLayout.setBackgroundColor(Utils.getColor(R.color.color_black, requireActivity()));
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(R.string.location_access_request), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.request_access, v -> locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }));
        if (Utils.isLocationAccessDenied(requireActivity())) {
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
                            new Handler(Looper.getMainLooper()).post(() -> Utils.toast(getString(R.string.location_data_failed), requireActivity()).show());
                        }
                        if (!executors.isShutdown()) executors.shutdown();
                    });
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                }
            }
        });

        mApplyCard.setOnClickListener(v -> {
            if (Objects.requireNonNull(mLatitude.getText()).toString().trim().isEmpty() || Objects.requireNonNull(mLongitude.getText()).toString().trim().isEmpty()) {
                return;
            }
            apply(Objects.requireNonNull(mLocation.getText()).toString().trim(), mLatitude.getText().toString().trim(), mLongitude.getText().toString().trim());
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (Weather.getLatitude(requireActivity()) != null && Weather.getLongitude(requireActivity()) != null) {
                    Utils.restartApp(requireActivity());
                } else {
                    requireActivity().finish();
                }
            }
        });

        return mRootView;
    }

    private void apply(String city, String latitude, String longitude) {
        ExecutorService executors = Executors.newSingleThreadExecutor();
        executors.execute(() -> {
            Utils.saveBoolean("reAcquire", true, requireActivity());
            Utils.saveString("latitude", latitude, requireActivity());
            Utils.saveString("longitude", longitude, requireActivity());
            Utils.saveString("location", city, requireActivity());
            Utils.saveLong("lastUVAlert", Long.MIN_VALUE, requireActivity());
            Utils.saveLong("lastWeatherAlert", Long.MIN_VALUE, requireActivity());
            new Handler(Looper.getMainLooper()).post(() -> Utils.restartApp(requireActivity()));
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
                            new LocationListener(requireActivity()) {
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

}