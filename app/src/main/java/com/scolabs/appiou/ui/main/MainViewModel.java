package com.scolabs.appiou.ui.main;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.scolabs.appiou.models.LocationInformation;

public class MainViewModel extends ViewModel {
    public GoogleSignInAccount googleSignInAccount;
    public LocationInformation locationInformation;
}