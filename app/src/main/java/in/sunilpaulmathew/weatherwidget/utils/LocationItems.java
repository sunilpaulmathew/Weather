package in.sunilpaulmathew.weatherwidget.utils;

import java.io.Serializable;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public class LocationItems implements Serializable {

    private final String mCity, mCountry, mLatitude, mLongitude;

    public LocationItems(String city, String country, String latitude, String longitude) {
        this.mCity = city;
        this.mCountry = country;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    public String getCity() {
        return mCity;
    }

    public String getLocation() {
        return mCity + ", " + mCountry;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

}