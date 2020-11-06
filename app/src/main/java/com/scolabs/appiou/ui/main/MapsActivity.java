package com.scolabs.appiou.ui.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.scolabs.appiou.R;
import com.scolabs.appiou.models.LocationInformation;
import com.scolabs.appiou.models.User;
import com.scolabs.appiou.models.UserLocationData;
import com.scolabs.appiou.repo.DatabaseManager;

import static com.scolabs.appiou.util.Util.getBitmapFromVectorDrawable;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final int SELECTION_RESULT_CODE = 1500;
    private static final int DEFAULT_PAYMENT_RADIUS_METERS = 500;
    public static String MY_LOCATION_EXTRA = "MY_LOCATION_EXTRA";
    public static String CURRENT_USER = "CURRENT_USER";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        LocationInformation locationInfo = getIntent().getParcelableExtra(MY_LOCATION_EXTRA);
        User user = getIntent().getParcelableExtra(CURRENT_USER);
        if (locationInfo != null && locationInfo.getLocation() != null && user != null) {
            double latitude = locationInfo.getLocation().getLatitude();
            double longitude = locationInfo.getLocation().getLongitude();
            LatLng myPosition = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            addCircle(myPosition);
            addNearbyUserToMap(user.getUui());
            buildMarker(latitude, longitude, user, R.drawable.ic_baseline_sentiment_very_dissatisfied_32);
        }
    }

    private void addCircle(LatLng myPosition) {
        mMap.addCircle(new CircleOptions()
                .center(myPosition)
                .radius(DEFAULT_PAYMENT_RADIUS_METERS)
                .strokeColor(Color.GREEN));
    }

    private void addNearbyUserToMap(String currentUserId) {
        MutableLiveData<UserLocationData> userLocation = new MutableLiveData<>();
        DatabaseManager.getUsersLocation(userLocation);
        userLocation.observe(this, userLocationData -> {
            try {
                if (!currentUserId.equalsIgnoreCase(userLocationData.getUser().getUui())) {
                    double latitude = userLocationData.getLocation().getLatitude();
                    double longitude = userLocationData.getLocation().getLongitude();
                    buildMarker(latitude, longitude, userLocationData.getUser(), R.drawable.ic_baseline_sentiment_very_satisfied_32);
                }
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void buildMarker(double latitude, double longitude, User user, @DrawableRes int drawableId) {
        LatLng myPosition = new LatLng(latitude, longitude);
        Marker marker = mMap.addMarker(new MarkerOptions().position(myPosition).title(user.getName()).snippet(user.getEmail()));
        marker.showInfoWindow();
        marker.setTag(user);
        Bitmap icon = getBitmapFromVectorDrawable(this, drawableId);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = getIntent();
        User selectedUser = (User) marker.getTag();
        intent.putExtra(CURRENT_USER, selectedUser);
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }
}