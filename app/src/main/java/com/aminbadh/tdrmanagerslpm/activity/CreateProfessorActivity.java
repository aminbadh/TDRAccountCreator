package com.aminbadh.tdrmanagerslpm.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aminbadh.tdrmanagerslpm.R;
import com.aminbadh.tdrmanagerslpm.custom.Professor;
import com.aminbadh.tdrmanagerslpm.databinding.ActivityCreateProfessorBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.aminbadh.tdrmanagerslpm.custom.Constants.ROLE_PROF;
import static com.aminbadh.tdrmanagerslpm.custom.Constants.USERS_COLLECTION;
import static com.aminbadh.tdrmanagerslpm.custom.Functions.displaySnackbarRef;
import static com.aminbadh.tdrmanagerslpm.custom.Functions.displaySnackbarStr;
import static com.aminbadh.tdrmanagerslpm.custom.Functions.getInternetConnectionStatus;

public class CreateProfessorActivity extends AppCompatActivity {

    private static final String TAG = CreateProfessorActivity.class.getSimpleName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityCreateProfessorBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set Activity's title.
        setTitle(R.string.create_prof);
        // Set the orientation to locked.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        // Initialise the binding object.
        binding = ActivityCreateProfessorBinding.inflate(getLayoutInflater());
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
            // If the user have internet connection, get the texts from the EditTexts.
            String email = binding.editTextEmail.getText().toString();
            String password = binding.editTextPassword.getText().toString();
            String firstName = binding.editTextNameFirst.getText().toString();
            String lastName = binding.editTextNameLast.getText().toString();
            String subject = binding.editTextSubject.getText().toString();
            // And get the arrays using the splitText method.
            ArrayList<String> level1S = splitText(binding.editTextLevel1S.getText().toString());
            ArrayList<String> level2Sc = splitText(binding.editTextLevel2Sc.getText().toString());
            ArrayList<String> level3M = splitText(binding.editTextLevel3M.getText().toString());
            ArrayList<String> level3Sc = splitText(binding.editTextLevel3Sc.getText().toString());
            ArrayList<String> level4L = splitText(binding.editTextLevel4L.getText().toString());
            ArrayList<String> level4M = splitText(binding.editTextLevel4M.getText().toString());
            ArrayList<String> level4Sc = splitText(binding.editTextLevel4Sc.getText().toString());
            ArrayList<String> level4T = splitText(binding.editTextLevel4T.getText().toString());
            // Do check.
            if (email.trim().isEmpty() || password.trim().isEmpty()
                    || firstName.trim().isEmpty() || lastName.trim().isEmpty()
                    || subject.trim().isEmpty()) {
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
                                // Get the levels list.
                                ArrayList<String> levels = getLevels(level1S, level2Sc, level3M, level3Sc,
                                        level4L, level4M, level4Sc, level4T);
                                // Create a Professor object using the given data.
                                Professor professor = new Professor(firstName, lastName, subject, ROLE_PROF,
                                        levels, level1S, level2Sc, level3M, level3Sc, level4L, level4M,
                                        level4Sc, level4T);
                                // Add this user data to the database.
                                db.collection(USERS_COLLECTION).document(auth.getCurrentUser().getUid())
                                        .set(professor).addOnSuccessListener(aVoid -> {
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

    private ArrayList<String> splitText(String text) {
        if (text.trim().isEmpty()) {
            // If the given text is empty we return an empty ArrayList.
            return new ArrayList<>();
        }
        // Split the given text into parts using "," and return the new ArrayList.
        String[] strings = text.trim().split(",");
        return new ArrayList<>(Arrays.asList(strings));
    }

    private ArrayList<String> getLevels(ArrayList<String> level1S, ArrayList<String> level2Sc,
                                        ArrayList<String> level3M, ArrayList<String> level3Sc,
                                        ArrayList<String> level4L, ArrayList<String> level4M,
                                        ArrayList<String> level4Sc, ArrayList<String> level4T) {
        ArrayList<String> levels = new ArrayList<>();
        if (!level1S.isEmpty()) {
            levels.add("1S");
        }
        if (!level2Sc.isEmpty()) {
            levels.add("2Sc");
        }
        if (!level3M.isEmpty()) {
            levels.add("3M");
        }
        if (!level3Sc.isEmpty()) {
            levels.add("3Sc");
        }
        if (!level4L.isEmpty()) {
            levels.add("4L");
        }
        if (!level4M.isEmpty()) {
            levels.add("4M");
        }
        if (!level4Sc.isEmpty()) {
            levels.add("4Sc");
        }
        if (!level4T.isEmpty()) {
            levels.add("4T");
        }
        return levels;
    }
}