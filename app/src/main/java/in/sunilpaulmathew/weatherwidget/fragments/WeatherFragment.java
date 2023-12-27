package in.sunilpaulmathew.weatherwidget.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
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
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 27, 2023
 */
public class WeatherFragment extends Fragment {

    private boolean mExit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_weather, container, false);

        AppCompatImageView mSplash = mRootView.findViewById(R.id.splash_screen);
        RecyclerView mRecyclerView = mRootView.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        new AcquireWeatherData(Weather.getLatitude(requireActivity()), Weather.getLongitude(requireActivity()), requireActivity()) {

            @Override
            public void successLister(List<WeatherItems> weatherItems) {
                mSplash.setVisibility(View.GONE);
                mRecyclerView.setAdapter(new WeatherAdapter(weatherItems, requireActivity()));
            }
        }.acquire();

        // Update widgets on app launch
        Utils.updateWidgets(requireActivity());

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mExit) {
                    mExit = false;
                    requireActivity().finish();
                } else {
                    Utils.toast(getString(R.string.exit_confirmation_message), requireActivity()).show();
                    mExit = true;
                    new Handler().postDelayed(() -> mExit = false, 2000);
                }
            }
        });

        return mRootView;
    }

}