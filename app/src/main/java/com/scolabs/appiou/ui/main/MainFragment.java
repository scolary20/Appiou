package com.scolabs.appiou.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.scolabs.appiou.R;
import com.scolabs.appiou.models.LocationInformation;
import com.scolabs.appiou.models.PaymentInformation;
import com.scolabs.appiou.models.User;
import com.scolabs.appiou.repo.DatabaseManager;
import com.scolabs.appiou.util.Util;

import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment implements View.OnClickListener {

    public static String ACCOUNT_EXTRA = "ACCOUNT_DETAILS";
    public static int SELECTION_REQUEST_CODE = 2000;
    public static MutableLiveData<LocationInformation> locationLiveDataTrigger;
    private MainViewModel mViewModel;
    private User selectedUser;
    private EditText sender;
    private EditText receiver;
    private EditText amountText;

    public static Fragment newInstance(GoogleSignInAccount googleSignInAccount, MutableLiveData<LocationInformation> locationInformation) {
        locationLiveDataTrigger = locationInformation;
        Fragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ACCOUNT_EXTRA, googleSignInAccount);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        sender = getActivity().findViewById(R.id.name_textview);
        receiver = getActivity().findViewById(R.id.recipientName);
        amountText = getActivity().findViewById(R.id.amount_text);
        locationLiveDataTrigger.observeForever(locationInformation -> {
            if (locationInformation != null) {
                GoogleSignInAccount googleSignInAccount = getArguments().getParcelable(ACCOUNT_EXTRA);
                if (googleSignInAccount != null & locationInformation != null) {
                    mViewModel.googleSignInAccount = googleSignInAccount;
                    mViewModel.locationInformation = locationInformation;
                    updateUI();
                } else {
                    Toast.makeText(getContext(), "Required Information is missing", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Button button = getActivity().findViewById(R.id.payment_button);
        EditText name = getActivity().findViewById(R.id.recipientName);
        name.setInputType(InputType.TYPE_NULL | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        if (resultCode == RESULT_OK && requestCode == SELECTION_REQUEST_CODE) {
            selectedUser = data.getParcelableExtra(MapsActivity.CURRENT_USER);
            if (selectedUser != null) {
                name.setText(selectedUser.getName());
                button.setEnabled(true);
                button.setOnClickListener(this);
                return;
            }
        }
        Snackbar.make(name, getString(R.string.no_recipient), Snackbar.LENGTH_LONG)
                .setAction("CLOSE", view -> {
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    private void updateUI() {
        EditText name = getActivity().findViewById(R.id.name_textview);
        name.setText(mViewModel.googleSignInAccount.getDisplayName());
        name.setInputType(InputType.TYPE_NULL | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        Intent mapIntent = new Intent(getContext(), MapsActivity.class);
        User aUser = new User(mViewModel.googleSignInAccount.getDisplayName(), mViewModel.googleSignInAccount.getFamilyName(), mViewModel.googleSignInAccount.getId(), mViewModel.googleSignInAccount.getEmail());
        mapIntent.putExtra(MapsActivity.MY_LOCATION_EXTRA, mViewModel.locationInformation);
        mapIntent.putExtra(MapsActivity.CURRENT_USER, aUser);
        getActivity().findViewById(R.id.recipientName).setOnFocusChangeListener((view, b) -> {
            if (b) {
                startActivityForResult(mapIntent, SELECTION_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        EditText sender = getActivity().findViewById(R.id.name_textview);
        EditText receiver = getActivity().findViewById(R.id.recipientName);
        EditText amountText = getActivity().findViewById(R.id.amount_text);
        if (checkValidation(sender, receiver, amountText)) {
            Util.vibrate(getContext());
            String timestamp = new Date(System.currentTimeMillis()).toString();
            String uuid = mViewModel.googleSignInAccount.getId();
            double amount = Double.parseDouble(amountText.getText().toString());
            PaymentInformation paymentInformation = new PaymentInformation(uuid, selectedUser.getUui(), amount, timestamp);
            handleTransaction(paymentInformation);
        } else {
            final Snackbar snackBar = Snackbar.make(sender, getString(R.string.form_validation), Snackbar.LENGTH_INDEFINITE);
            snackBar.setAction("CLOSE", _view -> snackBar.dismiss())
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();
        }
    }

    private boolean checkValidation(EditText... editText) {
        for (EditText text : editText) {
            if (text.getText().toString().length() < 1) {
                text.setError("Required");
                text.animate();
                return false;
            }
        }
        return true;
    }

    private void clearFields(EditText... editText) {
        for (EditText text : editText) {
            if (text != null) {
                text.getText().clear();
            }
        }
    }

    private void handleTransaction(PaymentInformation paymentInformation) {
        DatabaseManager.writeTransaction(paymentInformation).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccessful) {
                if (isSuccessful) {
                    clearFields(sender, receiver, amountText);
                    showAlertDialog(paymentInformation);
                } else {
                    EditText sender = getActivity().findViewById(R.id.name_textview);
                    final Snackbar snackBar = Snackbar.make(sender, "Transaction was unsuccessful... \uD83D\uDE14 ", Snackbar.LENGTH_INDEFINITE);
                    snackBar.setAction("CLOSE", _view -> snackBar.dismiss())
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                }
            }
        });
    }

    private void showAlertDialog(PaymentInformation info) {
        new AlertDialog.Builder(getContext())
                .setTitle("Transaction Successful")
                .setMessage("Your transaction of " + info.getAmount() + " on " + info.getTimestamp() + " has been successfully received.")
                .setPositiveButton(R.string.ok_text, (dialogInterface, i) -> dialogInterface.dismiss())
                .create()
                .show();
    }
}