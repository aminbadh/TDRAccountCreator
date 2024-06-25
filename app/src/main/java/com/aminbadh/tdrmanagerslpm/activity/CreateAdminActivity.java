package com.aminbadh.tdrmanagerslpm.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aminbadh.tdrmanagerslpm.R;
import com.aminbadh.tdrmanagerslpm.custom.Admin;
import com.aminbadh.tdrmanagerslpm.databinding.ActivityCreateAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import static com.aminbadh.tdrmanagerslpm.custom.Constants.ROLE_ADMIN;
import static com.aminbadh.tdrmanagerslpm.custom.Constants.USERS_COLLECTION;
import static com.aminbadh.tdrmanagerslpm.custom.Functions.displaySnackbarRef;
import static com.aminbadh.tdrmanagerslpm.custom.Functions.displaySnackbarStr;
import static com.aminbadh.tdrmanagerslpm.custom.Functions.getInternetConnectionStatus;

public class CreateAdminActivity extends AppCompatActivity {

    private static final String TAG = CreateAdminActivity.class.getSimpleName();
    private ActivityCreateAdminBinding binding;
    private FirebaseAuth auth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set Activity's title.
        setTitle(R.string.create_admin);
        // Set the orientation to locked.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        // Initialise the binding object.
        binding = ActivityCreateAdminBinding.inflate(getLayoutInflater());
        // Set the Activity's content view.
        setContentView(binding.getRoot());
        // Initialise the auth object.
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!getInternetConnectionStatus(this)) {
            // If the user is offline, show the offline feedback.
            binding.textViewOffline.setVisibility(View.VISIBLE);
            binding.nestedScrollView.setVisibility(View.GONE);
        }
    }

    public void signUp(View view) {
        if (!getInternetConnectionStatus(this)) {
            // If the user is offline, show the offline feedback.
            binding.textViewOffline.setVisibility(View.VISIBLE);
            binding.nestedScrollView.setVisibility(View.GONE);
        } else {
            // Get data.
            String email = binding.editTextEmail.getText().toString();
            String password = binding.editTextPassword.getText().toString();
            String firstName = binding.editTextNameFirst.getText().toString();
            String lastName = binding.editTextNameLast.getText().toString();
            // Do check.
            if (email.trim().isEmpty() || password.trim().isEmpty()
                    || firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
                // Inform the user that he must fill all required inputs.
                displaySnackbarRef(binding.getRoot(), R.string.fill_inputs);
            } else {
                // Request the focus for the root.
                binding.getRoot().requestFocus();
                // Show a message.
                displaySnackbarRef(binding.getRoot(), R.string.creating);
                // Create the account.
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Log the user ID.
                                Log.i(TAG, "onSuccess: User successfully created => " +
                                        Objects.requireNonNull(auth.getCurrentUser()).getUid());
                                // Create a Professor object using the given data.
                                Admin admin = new Admin(firstName, lastName, ROLE_ADMIN);
                                // Add this user data to the database.
                                db.collection(USERS_COLLECTION).document(auth.getCurrentUser().getUid())
                                        .set(admin).addOnSuccessListener(aVoid -> {
                                    // If the task was successful, log a message.
                                    Log.i(TAG, "onSuccess: Document created successfully!");
                                    // Sign out.
                                    auth.signOut();
                                    // Finish the activity.
                                    finish();
                                }).addOnFailureListener(e -> {
                                    // If the task fails, log the exception.
                                    Log.e(TAG, "onFailure: Document wasn't created", e);
                                    // Display the exception's message.
                                    displaySnackbarStr(binding.getRoot(), e.getMessage());
                                });
                            } else {
                                // Make sure there is an exception.
                                assert task.getException() != null;
                                // Log the exception.
                                Log.e(TAG, "onFailure: User was not created", task.getException());
                                // Display the exception's message.
                                displaySnackbarStr(binding.getRoot(), task.getException().getMessage());
                            }
                        });
            }
        }
    }
}