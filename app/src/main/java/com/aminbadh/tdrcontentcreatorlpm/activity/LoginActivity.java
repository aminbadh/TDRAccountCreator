package com.aminbadh.tdrcontentcreatorlpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aminbadh.tdrcontentcreatorlpm.R;
import com.aminbadh.tdrcontentcreatorlpm.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static com.aminbadh.tdrcontentcreatorlpm.custom.Functions.DisplaySnackbarRef;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Functions.DisplaySnackbarStr;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set activity's title.
        setTitle(R.string.login);
        // Initialise the binding object.
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        // Set Activity's content view.
        setContentView(binding.getRoot());
        // Initialise the auth object.
        auth = FirebaseAuth.getInstance();
        // Set the wait feedback visibility to gone.
        setWaitFeedbackVisibility(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            // If the currentUser isn't null, disable the login button.
            enableLoginButton(false);
            // Set the wait feedback visibility to visible.
            setWaitFeedbackVisibility(true);
            // Update the UI.
            updateUI();
        }
    }

    public void login(View view) {
        // Retrieve texts from EditTexts.
        String email = binding.editTextEmailAddress.getText().toString();
        String password = binding.editTextPassword.getText().toString();
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            // If the retrieved data is empty, inform the user
            // that he should fill all the required inputs.
            DisplaySnackbarRef(binding.getRoot(), R.string.fill_inputs);
        } else {
            // If there is data, Disable the login button.
            enableLoginButton(false);
            // Set the wait feedback visibility to visible.
            setWaitFeedbackVisibility(true);
            // Request the focus for the root.
            binding.getRoot().requestFocus();
            // Login the user using the given data.
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Update the UI.
                            updateUI();
                        } else {
                            // Set the wait feedback visibility to Gone.
                            setWaitFeedbackVisibility(false);
                            // Enable the login button.
                            enableLoginButton(true);
                            // Log the exception.
                            Log.e(TAG, "onComplete: User sign in failed", task.getException());
                            // Display the exception's message.
                            DisplaySnackbarStr(binding.getRoot(), Objects
                                    .requireNonNull(task.getException()).getMessage());
                        }
                    });
        }
    }

    private void updateUI() {
        // Set the wait feedback visibility to Gone.
        setWaitFeedbackVisibility(false);
        // Start an Intent going to the SelectionActivity class.
        startActivity(new Intent(
                LoginActivity.this, SelectionActivity.class));
        // Finish the current activity.
        LoginActivity.this.finish();
    }

    private void setWaitFeedbackVisibility(boolean b) {
        if (b) {
            binding.constraintLayoutWait.setVisibility(View.VISIBLE);
        } else {
            binding.constraintLayoutWait.setVisibility(View.GONE);
        }
    }

    private void enableLoginButton(boolean b) {
        binding.buttonLogin.setEnabled(b);
    }
}