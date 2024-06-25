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
public class EmailChangeActivity extends AppCompatActivity {

    private static final String TAG = EmailChangeActivity.class.getSimpleName();
    private ActivityEmailChangeBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set Activity's title.
        setTitle(R.string.change_email);
        // Initialise the binding object.
        binding = ActivityEmailChangeBinding.inflate(getLayoutInflater());
        // Set the Activity's content view.
        setContentView(binding.getRoot());
        // Initialise the auth object.
        auth = FirebaseAuth.getInstance();
        // Set the buttonChange's onClickListener.
        binding.buttonChange.setOnClickListener(view -> changeEmailAddress());
    }

    private void changeEmailAddress() {
        // Get the new email address.
        String newEmailAddress = binding.editTextNewValue.getText().toString();
        if (newEmailAddress.trim().isEmpty()) {
            // If the editTextNewEmail is empty, inform the user.
            DisplaySnackbarRef(binding.getRoot(), R.string.fill_inputs);
        } else {
            // Request the focus for the root.
            binding.getRoot().requestFocus();
            // Set the wait feedback visibility to Visible.
            setWaitFeedbackVisibility(true);
            // If the user entered a new email address, update the email address.
            Objects.requireNonNull(auth.getCurrentUser())
                    .updateEmail(newEmailAddress).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Display a message to the user.
                    Toast.makeText(EmailChangeActivity.this,
                            R.string.updated, Toast.LENGTH_LONG).show();
                    // Exit the current Activity.
                    EmailChangeActivity.this.finish();
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