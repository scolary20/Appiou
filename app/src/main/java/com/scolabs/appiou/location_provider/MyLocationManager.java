package com.scolabs.appiou.location_provider;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Scolary on 2020/11/05.
 */

public class MyLocationManager {

    protected static final long UPDATE_INTERVAL = 1000 * 10;
    protected static final long FASTEST_INTERVAL = 10;
    private static final int LOCATION_SERVICE_ENABLE = 1001;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "LocationUtils";
    protected GoogleApiClient googleApiClient;
    private Context mContext;
    private Location location;
    private String locationUrl = null;

    public MyLocationManager(Context mContext) {
        this.mContext = mContext;
    }

    public String getCountryCode(Context context, Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getCountryCode();
            }
            return null;
        } catch (IOException ignored) {
            return "";
        }
    }

    public String getCity(Context context, Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getLocality();
            }
            return null;
        } catch (IOException ignored) {
            return "";
        }
    }

    public String getAddress(Context context, Location loc) {
        try {
            if (context != null) {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(loc.getLatitude(),
                            loc.getLongitude(), 3);
                } catch (IOException e1) {
                    Log.e(TAG, "IO Exception in getFromLocation()");
                    e1.printStackTrace();
                    return null;
                } catch (IllegalArgumentException e2) {
                    // Error message to post in the log
                    String errorString = "Illegal arguments " +
                            Double.toString(loc.getLatitude()) +
                            " , " +
                            Double.toString(loc.getLongitude()) +
                            " passed to address service";
                    Log.e(TAG, errorString);
                    e2.printStackTrace();
                    return null;
                }
                // If the reverse geocode returned an address
                if (addresses != null && addresses.size() > 0) {
                    // Get the first address
                    Address address = addresses.get(0);
                    String street = address.getMaxAddressLineIndex() > 0 ?
                            address.getAddressLine(0) : address.getAddressLine(0);
                    return String.format(
                            "%s, %s, %s",
                            // If there's a street address, add it
                            street,
                            // Locality is usually a city
                            address.getLocality(),
                            // The country of the address
                            address.getCountryName());
                } else {
                    return null;
                }
            }

        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
