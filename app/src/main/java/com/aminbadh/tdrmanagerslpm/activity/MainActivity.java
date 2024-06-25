package com.aminbadh.tdrmanagerslpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aminbadh.tdrmanagerslpm.R;
import com.aminbadh.tdrmanagerslpm.adapter.MainRecyclerAdapter;
import com.aminbadh.tdrmanagerslpm.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialise the binding object.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // Set the Activity's content view.
        setContentView(binding.getRoot());
        // Set the Activity's title.
        setTitle(R.string.full_name);
        // Setup RecyclerView.
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start the login Activity.
            startActivity(new Intent(this, LoginActivity.class));
            // Finish the current Activity.
            finish();
        }
    }

    private void setupRecyclerView() {
        // Create the adapter.
        MainRecyclerAdapter adapter = new MainRecyclerAdapter(getItems(), position -> {
            if (position == 0) {
                // Start an intent going to the CreateProfessorActivity.
                startActivity(new Intent(this, CreateProfessorActivity.class));
            } else if (position == 1) {
                // Start an intent going to the CreateAdminActivity.
                startActivity(new Intent(this, CreateAdminActivity.class));
            }
        });
        // Setup the recyclerView.
        binding.RV.setHasFixedSize(true);
        binding.RV.setLayoutManager(new LinearLayoutManager(this));
        binding.RV.setAdapter(adapter);
    }

    private ArrayList<String> getItems() {
        // Create and return an ArrayList.
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(getString(R.string.create_prof));
        arrayList.add(getString(R.string.create_admin));
        return arrayList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Create a new AlertDialog#Builder.
        new AlertDialog.Builder(this)
                // Set the dialog's title.
                .setTitle(R.string.logout_q)
                // Set the dialog's content.
                .setMessage(R.string.logout_info)
                // Set the dialog's positive button.
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    // Logout the user.
                    FirebaseAuth.getInstance().signOut();
                    // Start the LoginActivity.
                    startActivity(new Intent(this, LoginActivity.class));
                    // Finish the current Activity.
                    finish();
                })
                // Set the dialog's negative button.
                .setNegativeButton(R.string.no, null)
                // Show the dialog.
                .show();
        return true;
    }
}