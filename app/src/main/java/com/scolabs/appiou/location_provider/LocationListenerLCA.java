package com.scolabs.appiou.location_provider;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

import com.scolabs.appiou.models.LocationInformation;

import static android.content.Context.LOCATION_SERVICE;

public class LocationListenerLCA extends AndroidViewModel implements LifecycleObserver, LocationListener {

    private final String APP_ID = "AIzaSyCJXTo_fMTC7llHQxzz9-NUyZCXnex5lTA";
    private final String PLACE_BASE_URL = "http://maps.googleapis.com/maps/api/geocode/";
    private final long LOCATION_REFRESH_TIME = 5000L;
    private final float LOCATION_REFRESH_DISTANCE = 1.0f;
    private LocationManager mLocationManager;
    private Context ctx;
    private MyLocationManager myLocationAddress;
    private DialogInterface.OnClickListener listener;
    private MutableLiveData<LocationInformation> locationInformationLiveData;

    public LocationListenerLCA(@NonNull Application application, DialogInterface.OnClickListener listener, MutableLiveData<LocationInformation> locationInformationLiveData) {
        super(application);
        this.ctx = application;
        this.listener = listener;
        this.locationInformationLiveData = locationInformationLiveData;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void initConnection() {
        myLocationAddress = new MyLocationManager(ctx);
        mLocationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void connectListener() throws SecurityException {
        mLocationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void disconnectListener() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            String city = myLocationAddress.getCity(ctx, location);
            locationInformationLiveData.setValue(new LocationInformation(location, city));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(ctx, "provider status updated...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(ctx, provider + " enabled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(ctx, provider + " disabled", Toast.LENGTH_LONG).show();
    }
}
