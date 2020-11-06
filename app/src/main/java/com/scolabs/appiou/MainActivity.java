package com.scolabs.appiou;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.scolabs.appiou.location_provider.LocationListenerLCA;
import com.scolabs.appiou.models.Location;
import com.scolabs.appiou.models.LocationInformation;
import com.scolabs.appiou.repo.DatabaseManager;
import com.scolabs.appiou.ui.main.MainFragment;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class MainActivity extends AppCompatActivity {

    public static String ACCOUNT_EXTRA = "ACCOUNT_DETAILS";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int LOCATION_SERVICE_ENABLE = 1001;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private MutableLiveData<LocationInformation> liveData;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoadingDialog();
        liveData = new MutableLiveData<>();
        getLifecycle().addObserver(new LocationListenerLCA(getApplication(), getSuccessDialog(), liveData));
        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(ACCOUNT_EXTRA);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MainFragment.newInstance(googleSignInAccount, liveData))
                .commitNow();
        liveData.observe(this, locationInformation -> progressDialog.dismiss());
        setContentView(R.layout.main_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkSettings();
        checkLocationPermission();
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                showAlertDialog();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.location_permission)
                .setMessage(R.string.grant_location_permission)
                .setPositiveButton(R.string.ok_text, (dialogInterface, i) -> ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION))
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this.getApplicationContext(), getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this.getApplicationContext(), getString(R.string.location_permission_required), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public DialogInterface.OnClickListener getSuccessDialog() {
        return (dialog, id) -> {
            dialog.dismiss();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, LOCATION_SERVICE_ENABLE);
        };
    }

    public void showLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.wait_while_loading));
        progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressDialog.show();
    }

    private void checkSettings() {
        if (!((LocationManager) getSystemService(LOCATION_SERVICE))
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Change your location settings")
                    .setMessage("Location service disabled")
                    .setCancelable(false)
                    .setPositiveButton("Location service settings", getSuccessDialog())
                    .setNegativeButton(android.R.string.cancel, (dialog, id) -> dialog.cancel());
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void saveLocationData(GoogleSignInAccount googleSignInAccount, LocationInformation locationInfo) {
        Location currentLocation = new Location(locationInfo.getLocation().getLatitude(), locationInfo.getLocation().getLongitude());
        DatabaseManager.writeCurrentLocation(
                googleSignInAccount.getId(), currentLocation);
    }

    public interface FragmentLocationUpdate {
        void locationUpdate(LocationInformation locationInformation);
    }
}