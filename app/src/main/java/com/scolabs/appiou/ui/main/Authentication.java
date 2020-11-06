package com.scolabs.appiou.ui.main;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public interface Authentication {
    String TAG = Authentication.class.getName();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    default LiveData<FirebaseUser> createAccount(String email, String password, Activity context) {
        MutableLiveData<FirebaseUser> trigger = new MutableLiveData<>();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        trigger.setValue(user);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        trigger.setValue(null);
                    }
                });
        return trigger;
    }

    default LiveData<FirebaseUser> signIn(String email, String password, Activity context) {
        MutableLiveData<FirebaseUser> trigger = new MutableLiveData<>();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        trigger.setValue(user);
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        trigger.setValue(null);
                    }
                });
        return trigger;
    }

    default FirebaseUser getSignInUser() {
        return mAuth.getCurrentUser();
    }

    default GoogleSignInClient getGoogleSignInClient(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(context, gso);
    }
}
