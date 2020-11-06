package com.scolabs.appiou.repo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.scolabs.appiou.models.Location;
import com.scolabs.appiou.models.PaymentInformation;
import com.scolabs.appiou.models.User;
import com.scolabs.appiou.models.UserLocationData;

public class DatabaseManager {

    public final static String USERS_PATH = "USERS";
    public final static String TRANSACTION_PATH = "TRANSACTIONS";
    public final static String USER_LOCATION = "USER_LOCATION";
    public final static String USER_DATA = "USER_DATA";
    private static final FirebaseDatabase db = FirebaseDatabase.getInstance();

    public static void writeUser(User user) {
        db.getReference().child(USERS_PATH).child(user.getUui()).child(USER_DATA).setValue(user);
    }

    public static void writeCurrentLocation(String userId, Location location) {
        db.getReference().child(USERS_PATH).child(userId).child(USER_LOCATION).setValue(location);
    }

    public static MutableLiveData<Boolean> writeTransaction(PaymentInformation paymentInformation) {
        MutableLiveData<Boolean> isSuccessfulBool = new MutableLiveData<>();
        db.getReference().child(TRANSACTION_PATH).child(paymentInformation.getTransactionUUID()).setValue(paymentInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                isSuccessfulBool.setValue(task.isSuccessful());
            }
        });
        return isSuccessfulBool;
    }

    public static void getUsersLocation(MutableLiveData<UserLocationData> userLocationDataMutableLiveData) {
        db.getReference().child(USERS_PATH).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Location location = snapshot.child(USER_LOCATION).getValue(Location.class);
                User aUser = snapshot.child(USER_DATA).getValue(User.class);
                userLocationDataMutableLiveData.setValue(new UserLocationData(aUser, location));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
