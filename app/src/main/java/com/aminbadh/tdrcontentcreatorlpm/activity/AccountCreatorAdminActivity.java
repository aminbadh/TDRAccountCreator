package com.aminbadh.tdrcontentcreatorlpm.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aminbadh.tdrcontentcreatorlpm.R;
import com.aminbadh.tdrcontentcreatorlpm.custom.Admin;
import com.aminbadh.tdrcontentcreatorlpm.databinding.ActivityAccountCreatorAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import static com.aminbadh.tdrcontentcreatorlpm.custom.Constants.ADMIN_PIN_CODE_DOC_REF;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Constants.PIN_FIELD;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Constants.ROLE_ADMIN;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Constants.USERS_COLLECTION;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Functions.DisplaySnackbarRef;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Functions.DisplaySnackbarStr;
import static com.aminbadh.tdrcontentcreatorlpm.custom.Functions.getInternetConnectionStatus;

// Working!
public class AccountCreatorAdminActivity extends AppCompatActivity {

    private static final String TAG = AccountCreatorAdminActivity.class.getSimpleName();
    private ActivityAccountCreatorAdminBinding binding;
    private FirebaseAuth auth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String PINCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set activity's title.
        setTitle(R.string.account_creator_admin);
        // Initialise the binding object.
        binding = ActivityAccountCreatorAdminBinding.inflate(getLayoutInflater());
        // Set Activity's content view.
        setContentView(binding.getRoot());
        // Initialise the FirebaseAuth object.
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
            String userPassword = binding.editTextUserPasswordAdmin.getText().toString();
            String email = binding.editTextEmailAdmin.getText().toString();
            String password = binding.editTextPasswordAdmin.getText().toString();
            String fistName = binding.editTextFirstNameAdmin.getText().toString();
            String lastName = binding.editTextLastNameAdmin.getText().toString();
            String pin = binding.editTextPinAdmin.getText().toString();
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
                    || fistName.trim().isEmpty() || lastName.trim().isEmpty() || pin.trim().isEmpty()) {
                // If one of the required EditTexts is left empty,
                // Inform the user that he must fill all required inputs.
                DisplaySnackbarRef(binding.getRoot(), R.string.fill_inputs);
            } else if (!pin.equals(PINCode)) {
                // If the PINCode variable isn't equal to the retrieved pin code,
                // inform the user that the pin code is wrong.
                DisplaySnackbarRef(binding.getRoot(), R.string.wrong_pin);
            } else {
                // Request the focus for the root.
                binding.getRoot().requestFocus();
                // Set the wait feedback visibility to Visible.
                setWaitFeedbackVisibility(true);
                // Create and initialise a String object for later use.
                String userEmail = auth.getCurrentUser().getEmail();
                // Create the user using the given email and password.
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            // If the task was successful, log the user ID.
                            Log.i(TAG, "onSuccess: User successfully created => " +
                                    Objects.requireNonNull(auth.getCurrentUser().getUid()));
                            // Assign the user ID to a variable for later use.
                            String userId = auth.getCurrentUser().getUid();
                            // Create the user's document using the following method.
                            createUserDoc(userId, fistName, lastName, userEmail, userPassword);
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
            // inform him that he need internet connection to do this operation.
            DisplaySnackbarRef(binding.getRoot(), R.string.you_need_internet);
        }
    }

    private void createUserDoc(String userId, String fistName, String lastName, String email,
                               String password) {
        // Create a Admin object using the given data.
        Admin admin = new Admin(fistName, lastName, ROLE_ADMIN);
        // Update the user.
        Log.i(TAG, "createUserDoc: User's email: " + email);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Add the user's data to the database.
                        db.collection(USERS_COLLECTION).document(userId).set(admin)
                                .addOnSuccessListener(aVoid -> {
                                    // If the task was successful log a message.
                                    Log.i(TAG, "onSuccess: Document created successfully!");
                                    // Show a Toast informing the user of the success of the operation.
                                    Toast.makeText(AccountCreatorAdminActivity.this,
                                            getString(R.string.account_created), Toast.LENGTH_LONG).show();
                                    // Leave the current Activity.
                                    AccountCreatorAdminActivity.this.onBackPressed();
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
                        DisplaySnackbarStr(binding.getRoot(), task.getException().getMessage());
                    }
                });
    }


    private void setWaitFeedbackVisibility(boolean b) {
        if (b) {
            binding.constraintLayoutWaitRootAdmin.setVisibility(View.VISIBLE);
        } else {
            binding.constraintLayoutWaitRootAdmin.setVisibility(View.GONE);
        }
    }
}