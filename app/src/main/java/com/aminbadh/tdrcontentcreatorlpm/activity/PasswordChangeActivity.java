package com.aminbadh.tdrcontentcreatorlpm.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aminbadh.tdrcontentcreatorlpm.R;
import com.aminbadh.tdrcontentcreatorlpm.databinding.ActivityEmailChangeBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static com.aminbadh.tdrcontentcreatorlpm.custom.Functions.DisplaySnackbarRef;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Functions.DisplaySnackbarStr;

// Working!
public class PasswordChangeActivity extends AppCompatActivity {

    private static final String TAG = PasswordChangeActivity.class.getSimpleName();
    private ActivityEmailChangeBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the Activity's title.
        setTitle(R.string.change_password);
        // Initialise the binding object.
        binding = ActivityEmailChangeBinding.inflate(getLayoutInflater());
        // Set the Activity's content view.
        setContentView(binding.getRoot());
        // Initialise the auth object.
        auth = FirebaseAuth.getInstance();
        // Set the buttonChange's onClickListener.
        binding.buttonChange.setOnClickListener(view -> updatePassword());
    }

    private void updatePassword() {
        // Get the editTextNewEmail's text and assign it to a String object.
        String password = binding.editTextNewValue.getText().toString();
        if (password.trim().isEmpty()) {
            // If the editTextNewEmail is empty, inform the user.
            DisplaySnackbarRef(binding.getRoot(), R.string.fill_inputs);
        } else {
            // Request the focus for the root.
            binding.getRoot().requestFocus();
            // Set the wait feedback visibility to Visible.
            setWaitFeedbackVisibility(true);
            // Update the password.
            Objects.requireNonNull(auth.getCurrentUser())
                    .updatePassword(password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Display a message to the user.
                    Toast.makeText(PasswordChangeActivity.this,
                            R.string.updated, Toast.LENGTH_LONG).show();
                    // Exit the current Activity.
                    PasswordChangeActivity.this.finish();
                } else {
                    // If the task fails, log the exception.
                    Log.e(TAG, "onFailure: User was not created ", task.getException());
                    // Set the wait feedback visibility to Gone.
                    setWaitFeedbackVisibility(false);
                    // Display the exception's message.
                    DisplaySnackbarStr(binding.getRoot(),
                            Objects.requireNonNull(task.getException()).getMessage());
                }
            });
        }
    }

    private void setWaitFeedbackVisibility(boolean b) {
        if (b) {
            binding.constraintLayoutWaitRoot.setVisibility(View.VISIBLE);
        } else {
            binding.constraintLayoutWaitRoot.setVisibility(View.GONE);
        }
    }
}