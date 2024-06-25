package com.aminbadh.tdraccountcreator;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.aminbadh.tdraccountcreator.Constants.DATA_COLLECTION;
import static com.aminbadh.tdraccountcreator.Constants.PIN_DOCUMENT;
import static com.aminbadh.tdraccountcreator.Constants.TEACHERS_COLLECTION;

/*
    App fictionally (Demo):
    - Create A user Using Firebase Auth (email-password).   |   ✔
    - Get User's :                                          |
        . Name                                              |   ✔
        . Subject                                           |   ✔
        . Classes That He teach                             |   ✔
    - Use PIN Code For Secure Account Creation.             |   ✔
    - Create A Corresponding Document With Auto Id For      |   ✔
    Each User In The DB With User's Info In It.             |

    - Need More Information? Read The Code Bellow, enjoy! ❤
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference PINRef = db.collection(DATA_COLLECTION)
            .document(PIN_DOCUMENT);
    private FirebaseAuth mAuth;

    private String mPINCode;
    private boolean havePINCode = false;

    private Button buttonCreate;
    private EditText editTextEmail, editTextPassword, editTextFistName, editTextLastName,
            editTextSubject, editText1S, editText2L, editText2Sc, editTextPINCode;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        getPINCode();
        setUpViews();
        setUpBehaviour();
    }

    /**
     * This method gets the PIN code form a doc in the firebase database
     * and then initialise the mPINCode variable if the task was successful.
     */
    private void getPINCode() {
        // We get the PIN document.
        PINRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // If the task was successful we get the PIN code and set it
                // to the mPINCode variable and update the havePINCode to true.
                mPINCode = documentSnapshot.getString(getString(R.string.pin));
                havePINCode = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // If the task failed we log the exception and we display
                // a Snackbar to inform the user that a problem happened.
                havePINCode = false;
                Log.w(TAG, "onFailure: ", e);
                Snackbar.make(nestedScrollView, Objects.requireNonNull(e.getMessage()),
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * This method as usual sets up the views by initialising them.
     */
    private void setUpViews() {
        buttonCreate = findViewById(R.id.buttonCreate);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextFistName = findViewById(R.id.editTextNameFirst);
        editTextLastName = findViewById(R.id.editTextNameLast);
        editTextSubject = findViewById(R.id.editTextSubject);
        editTextPINCode = findViewById(R.id.editTextPINCode);
        editText1S = findViewById(R.id.editTextLevel1S);
        editText2L = findViewById(R.id.editTextLevel2L);
        editText2Sc = findViewById(R.id.editTextLevel2Sc);
        nestedScrollView = findViewById(R.id.NSV);
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * This method gets the connection status of the user and return it as a boolean.
     *
     * @return true if the user has internet connection, otherwise: false.
     */
    private boolean getInternetConnectionStatus() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /**
     * This method sets up the app behaviour, specifically adds a click listener
     * to the create account button.
     */
    private void setUpBehaviour() {
        progressBar.setVisibility(View.GONE);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for internet connection.
                if (getInternetConnectionStatus()) {
                    // Check if we have a PIN code or we need a retry.
                    if (!havePINCode) {
                        getPINCode();
                    }
                    // Get string values from editTexts.
                    final String email = editTextEmail.getText().toString();
                    String password = editTextPassword.getText().toString();
                    final String firstName = editTextFistName.getText().toString();
                    final String lastName = editTextLastName.getText().toString();
                    final String subject = editTextSubject.getText().toString();
                    String PINCode = editTextPINCode.getText().toString();
                    final ArrayList<String> level1S = splitText(editText1S.getText().toString());
                    final ArrayList<String> level2L = splitText(editText2L.getText().toString());
                    final ArrayList<String> level2Sc = splitText(editText2Sc.getText().toString());
                    // If we still haven't a PIN code we display a message to inform the user
                    // and we stop the operation by using return keyword.
                    if (!havePINCode) {
                        Snackbar.make(nestedScrollView,
                                R.string.error_getting_pin_code,
                                BaseTransientBottomBar.LENGTH_LONG).show();
                        return;
                    }
                    // Make sure that the user wrote something in each input.
                    if (PINCode.isEmpty()
                            || email.trim().isEmpty()
                            || password.trim().isEmpty()
                            || firstName.trim().isEmpty()
                            || lastName.trim().isEmpty()
                            || subject.trim().isEmpty()) {
                        // if not we tell him that he need to fill all of them.
                        Snackbar.make(nestedScrollView,
                                R.string.please_fill_all_inputs,
                                BaseTransientBottomBar.LENGTH_LONG).show();
                    } else if (!PINCode.equals(mPINCode)) {
                        // if he entered the wrong PIN code we inform him.
                        Snackbar.make(nestedScrollView,
                                R.string.wrong_pin_code,
                                BaseTransientBottomBar.LENGTH_LONG).show();
                    } else {
                        // if every thing is set, we inform him that the operation
                        // begin by using a Snackbar and a progress bar.
                        Snackbar.make(nestedScrollView, R.string.please_wait,
                                BaseTransientBottomBar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.VISIBLE);
                        // we register the user in the firebase Auth system.
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(MainActivity.this,
                                        new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // if the operation was successful we continue
                                                    // working on writing user data to the database.
                                                    Log.d(TAG, "createUserWithEmail: success");
                                                    createAccountData(email, firstName, lastName,
                                                            subject, level1S, level2L, level2Sc);
                                                } else {
                                                    // if not we inform him by displaying a Snackbar.
                                                    Log.w(TAG, "createUserWithEmail: failure",
                                                            task.getException());
                                                    Snackbar.make(nestedScrollView,
                                                            Objects.requireNonNull(Objects
                                                                    .requireNonNull(
                                                                            task.getException())
                                                                    .getMessage()),
                                                            BaseTransientBottomBar.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                }

                                            }
                                        });
                    }
                } else {
                    // if not we inform him that he need internet connection
                    // by displaying a Snackbar.
                    Snackbar.make(nestedScrollView, R.string.you_are_offline,
                            BaseTransientBottomBar.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * This method splits a string into a string array.
     *
     * @param text is the text that we want to split,
     *             in this case it's the text coming from
     *             levels editTexts.
     * @return a string array containing all classes that
     * the teachers teach in a specific level.
     */
    private ArrayList<String> splitText(String text) {
        // We check first if the given text is empty,
        // if so we will return an empty ArrayList.
        if (text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        // Split the given text to parts using "," and return
        // the Array that we got in a new ArrayList.
        String[] strings = text.trim().split(",");
        return new ArrayList<>(Arrays.asList(strings));
    }

    /**
     * This method saves user data in the teachers collection in the database.
     *
     * @param email     of the user.
     * @param firstName of the user.
     * @param lastName  of the user.
     * @param subject   that the user teach.
     * @param level1S   classes that the user teach in 1S.
     * @param level2L   classes that the user teach in 2L.
     * @param level2Sc  classes that the user teach in 2Sc.
     */
    private void createAccountData(String email, String firstName, String lastName, String subject,
                                   ArrayList<String> level1S, ArrayList<String> level2L,
                                   ArrayList<String> level2Sc) {
        // We create a levels ArrayList using the getLevels method.
        ArrayList<String> levels = getLevels(level1S, level2L, level2Sc);
        // We create a Teacher object using the given data.
        final Teacher teacher = new Teacher(email, firstName, lastName, subject, levels,
                level1S, level2L, level2Sc);
        // We add this object to the teachers collection in the database.
        db.collection(TEACHERS_COLLECTION).add(teacher)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // If the task was successful we inform the user
                        // that the account is created.
                        Snackbar.make(nestedScrollView, R.string.account_created,
                                BaseTransientBottomBar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        Log.w(TAG, "onSuccess: Account Created " + documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // If the task was unsuccessful we inform the user as usual.
                Log.w(TAG, "onFailure: ", e);
                Snackbar.make(nestedScrollView, Objects.requireNonNull(e.getMessage()),
                        BaseTransientBottomBar.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * This method creates an ArrayList with the levels that the teacher teach.
     *
     * @param level1S  The ArrayList of classes that the teacher teach in 1S level.
     * @param level2L  The ArrayList of classes that the teacher teach in 2L level.
     * @param level2Sc The ArrayList of classes that the teacher teach in 2Sc level.
     * @return ArrayList levels containing levels that the user teach.
     */
    private ArrayList<String> getLevels(ArrayList<String> level1S,
                                        ArrayList<String> level2L,
                                        ArrayList<String> level2Sc) {
        // Create a new ArrayList.
        ArrayList<String> levels = new ArrayList<>();
        if (!level1S.isEmpty()) {
            // If the level1S ArrayList isn't empty,
            // which means that the user teach at least one class
            // in this level, we add "1S" level to levels ArrayList.
            levels.add("1S");
        }
        if (!level2L.isEmpty()) {
            // If the level2L ArrayList isn't empty,
            // which means that the user teach at least one class
            // in this level, we add "2L" level to levels ArrayList.
            levels.add("2L");
        }
        if (!level2Sc.isEmpty()) {
            // If the level2Sc ArrayList isn't empty,
            // which means that the user teach at least one class
            // in this level, we add "2Sc" level to levels ArrayList.
            levels.add("2Sc");
        }
        // return levels ArrayList.
        return levels;
    }
}