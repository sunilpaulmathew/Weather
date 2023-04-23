package in.sunilpaulmathew.weatherwidget.interfaces;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.Locale;

import in.sunilpaulmathew.weatherwidget.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public abstract class LocationListener {

    private final Context mContext;

    public LocationListener(Context context) {
        this.mContext = context;
    }

    public void initialize() {
        if (!Utils.getBoolean("useGPS", true, mContext) || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Utils.saveBoolean("gpsAllowed", false, mContext);
            return;
        }
        Utils.saveBoolean("gpsAllowed", true, mContext);
        LocationManager mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location mLocationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location mLocationNetwork = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location mLocation = null;

        if (mLocationGPS != null) {
            mLocation = mLocationGPS;
        } else if (mLocationNetwork != null) {
            mLocation = mLocationNetwork;
        }

        if (mLocation != null) {
            try {
                Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                String address = null;
                if (addresses.get(0).getAddressLine(0).contains(",")) {
                    address = addresses.get(0).getAddressLine(0).split(",")[0];
                }
                onLocationInitialized(String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()), address);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void onLocationInitialized(String latitude, String longitude, String address);

}