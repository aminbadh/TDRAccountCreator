package com.aminbadh.tdrcontentcreatorlpm.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aminbadh.tdrcontentcreatorlpm.R;
import com.aminbadh.tdrcontentcreatorlpm.custom.Professor;
import com.aminbadh.tdrcontentcreatorlpm.databinding.ActivityAccountCreatorProfessorBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.aminbadh.tdrcontentcreatorlpm.custom.Constants.ADMIN_PIN_CODE_DOC_REF;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Constants.PIN_FIELD;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Constants.ROLE_PROF;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Constants.USERS_COLLECTION;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Functions.DisplaySnackbarRef;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Functions.DisplaySnackbarStr;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Functions.getInternetConnectionStatus;

public class AccountCreatorProfessorActivity extends AppCompatActivity {

    private static final String TAG = AccountCreatorProfessorActivity.class.getSimpleName();
    private ActivityAccountCreatorProfessorBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth;
    private String PINCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set activity's title.
        setTitle(R.string.account_creator_prof);
        // Initialise the binding object.
        binding = ActivityAccountCreatorProfessorBinding.inflate(getLayoutInflater());
        // Set Activity's content view.
        setContentView(binding.getRoot());
        // initialise the FirebaseAuth object.
        auth = FirebaseAuth.getInstance();
        // Get the PIN code.
        getPinCode();
        // Set wait feedback visibility to gone
        setWaitFeedbackVisibility(false);
    }

    private void getPinCode() {
        // Get the pin code.
        db.document(ADMIN_PIN_CODE_DOC_REF).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Assign the retrieved value to the PINCode variable.
                    PINCode = documentSnapshot.getString(PIN_FIELD);
                }).addOnFailureListener(e -> {
            // Log the exception.
            Log.e(TAG, "getPinCode, onFailure: ", e);
            // Display the exception's message.
            DisplaySnackbarStr(binding.getRoot(), e.getMessage());
        });
    }

    public void createAccount(View view) {
        if (getInternetConnectionStatus(this)) {
            // If the user have internet connection, get the texts from the EditTexts.
            String userPassword = binding.editTextUserPassword.getText().toString();
            String email = binding.editTextEmail.getText().toString();
            String password = binding.editTextPassword.getText().toString();
            String firstName = binding.editTextNameFirst.getText().toString();
            String lastName = binding.editTextNameLast.getText().toString();
            String subject = binding.editTextSubject.getText().toString();
            String pin = binding.editTextPINCode.getText().toString();
            // And get the arrays using the splitText method.
            ArrayList<String> level1S = splitText(binding.editTextLevel1S.getText().toString());
            ArrayList<String> level2Sc = splitText(binding.editTextLevel2Sc.getText().toString());
            ArrayList<String> level3M = splitText(binding.editTextLevel3M.getText().toString());
            ArrayList<String> level3Sc = splitText(binding.editTextLevel3Sc.getText().toString());
            ArrayList<String> level4L = splitText(binding.editTextLevel4L.getText().toString());
            ArrayList<String> level4M = splitText(binding.editTextLevel4M.getText().toString());
            ArrayList<String> level4Sc = splitText(binding.editTextLevel4Sc.getText().toString());
            ArrayList<String> level4T = splitText(binding.editTextLevel4T.getText().toString());
            if (PINCode.isEmpty()) {
                // If the PINCode variable is empty,
                // try getting the code again.
                getPinCode();
                // Inform the user.
                DisplaySnackbarRef(binding.getRoot(), R.string.error_get_pin);
                // And stop the operation.
                return;
            }
            if (userPassword.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()
                    || firstName.trim().isEmpty() || lastName.trim().isEmpty()
                    || subject.trim().isEmpty() || pin.trim().isEmpty()) {
                // If one of the required EditTexts is empty,
                // Inform the user that he must fill all required inputs.
                DisplaySnackbarRef(binding.getRoot(), R.string.fill_inputs);
            } else if (!pin.equals(PINCode)) {
                // If the PINCode variable isn't equal to the retrieved pin code,
                // Inform the user that the pin code is wrong.
                DisplaySnackbarRef(binding.getRoot(), R.string.wrong_pin);
            } else {
                // Request the focus for the root.
                binding.getRoot().requestFocus();
                // Set the wait feedback visibility to Visible.
                setWaitFeedbackVisibility(true);
                // Create and initialise a String object for later use.
                String userEmail = Objects.requireNonNull(auth.getCurrentUser()).getEmail();
                // Create the user using the passed email and password.
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            // If the task was successful, log the user ID.
                            Log.i(TAG, "onSuccess: User successfully created => " +
                                    Objects.requireNonNull(auth.getCurrentUser()).getUid());
                            // Assign this ID to a variable for later use.
                            String userId = auth.getCurrentUser().getUid();
                            // Create the user's document using the following method.
                            createUserDoc(userId, firstName, lastName, subject,
                                    level1S, level2Sc, level3M, level3Sc, level4L, level4M,
                                    level4Sc, level4T, userEmail, userPassword);
                        }).addOnFailureListener(e -> {
                    // If the task fails, log the exception.
                    Log.e(TAG, "onFailure: User was not created ", e);
                    // Set the wait feedback visibility to Gone.
                    setWaitFeedbackVisibility(false);
                    // Display the exception's message.
                    DisplaySnackbarStr(binding.getRoot(), e.getMessage());
                });
            }
        } else {
            // If the user don't have internet connection available,
            // inform him that he needs internet connection to do this operation.
            DisplaySnackbarRef(binding.getRoot(), R.string.you_need_internet);
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

    private void createUserDoc(String uId, String firstName, String lastName, String subject,
                               ArrayList<String> level1S, ArrayList<String> level2Sc,
                               ArrayList<String> level3M, ArrayList<String> level3Sc,
                               ArrayList<String> level4L, ArrayList<String> level4M,
                               ArrayList<String> level4Sc, ArrayList<String> level4T,
                               String email, String password) {
        ArrayList<String> levels = getLevels(level1S, level2Sc, level3M, level3Sc,
                level4L, level4M, level4Sc, level4T);
        // Create a Professor object using the given data.
        Professor professor = new Professor(firstName, lastName, subject, ROLE_PROF, levels,
                level1S, level2Sc, level3M, level3Sc, level4L, level4M, level4Sc, level4T);
        // Update the user.
        Log.i(TAG, "createUserDoc: User's email: " + email);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Add this user data to the database.
                        db.collection(USERS_COLLECTION).document(uId).set(professor)
                                .addOnSuccessListener(aVoid -> {
                                    // If the task was successful, log a message.
                                    Log.i(TAG, "onSuccess: Document created successfully!");
                                    // Show a Toast informing the user of the success of the operation.
                                    Toast.makeText(AccountCreatorProfessorActivity.this,
                                            getString(R.string.account_created), Toast.LENGTH_LONG).show();
                                    // Leave the current Activity.
                                    AccountCreatorProfessorActivity.this.onBackPressed();
                                }).addOnFailureListener(e -> {
                            // If the task fails, log the exception.
                            Log.e(TAG, "onFailure: Document wasn't created", e);
                            // Set the wait feedback visibility to Gone.
                            setWaitFeedbackVisibility(false);
                            // Display the exception's message.
                            DisplaySnackbarStr(binding.getRoot(), e.getMessage());
                        });
                    } else {
                        // If the task fails, log the exception.
                        Log.e(TAG, "onFailure: Login failed", task.getException());
                        // Set the wait feedback visibility to Gone.
                        setWaitFeedbackVisibility(false);
                        // Display the exception's message.
                        DisplaySnackbarStr(binding.getRoot(),
                                Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    private ArrayList<String> getLevels(ArrayList<String> level1S, ArrayList<String> level2Sc,
                                        ArrayList<String> level3M, ArrayList<String> level3Sc,
                                        ArrayList<String> level4L, ArrayList<String> level4M,
                                        ArrayList<String> level4Sc, ArrayList<String> level4T) {
        // Create a new ArrayList.
        ArrayList<String> levels = new ArrayList<>();
        if (!level1S.isEmpty()) {
            // If the level1S ArrayList isn't empty,
            // which means that the user teach at least one class
            // in this level, add "1S" level to the levels ArrayList.
            levels.add("1S");
        }
        if (!level2Sc.isEmpty()) {
            // If the level2Sc ArrayList isn't empty,
            // which means that the user teach at least one class
            // in this level, add "2Sc" level to the levels ArrayList.
            levels.add("2Sc");
        }
        if (!level3M.isEmpty()) {
            // If the level3M ArrayList isn't empty,
            // which means that the user teach at least one class
            // in this level, add "3M" level to the levels ArrayList.
            levels.add("3M");
        }
        if (!level3Sc.isEmpty()) {
            // If the level3Sc ArrayList isn't empty,
            // which means that the user teach at least one class
            // in this level, add "3Sc" level to the levels ArrayList.
            levels.add("3Sc");
        }
        if (!level4L.isEmpty()) {
            // If the level4L ArrayList isn't empty,
            // which means that the user teach at least one class
            // in this level, add "4L" level to the levels ArrayList.
            levels.add("4L");
        }
        if (!level4M.isEmpty()) {
            // If the level4M ArrayList isn't empty,
            // which means that the user teach at least one class
            // in this level, add "4M" level to the levels ArrayList.
            levels.add("4M");
        }
        if (!level4Sc.isEmpty()) {
            // If the level4Sc ArrayList isn't empty,
            // which means that the user teach at least one class
            // in this level, add "4Sc" level to the levels ArrayList.
            levels.add("4Sc");
        }
        if (!level4T.isEmpty()) {
            // If the level4T ArrayList isn't empty,
            // which means that the user teach at least one class
            // in this level, add "4T" level to the levels ArrayList.
            levels.add("4T");
        }
        // return the levels ArrayList.
        return levels;
    }

    private void setWaitFeedbackVisibility(boolean b) {
        if (b) {
            binding.constraintLayoutWaitRoot.setVisibility(View.VISIBLE);
        } else {
            binding.constraintLayoutWaitRoot.setVisibility(View.GONE);
        }
    }
}