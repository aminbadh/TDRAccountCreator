package com.aminbadh.tdrcontentcreatorlpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aminbadh.tdrcontentcreatorlpm.R;
import com.aminbadh.tdrcontentcreatorlpm.adapter.SelectionRecyclerAdapter;
import com.aminbadh.tdrcontentcreatorlpm.databinding.ActivitySelectionBinding;
import com.aminbadh.tdrcontentcreatorlpm.interfaces.OnMainListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SelectionActivity extends AppCompatActivity implements OnMainListener {

    private ActivitySelectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set activity's title.
        setTitle(R.string.app_name);
        // Initialise the binding object.
        binding = ActivitySelectionBinding.inflate(getLayoutInflater());
        // Set Activity's content view.
        setContentView(binding.getRoot());
        // Setup the RecyclerView.
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        // Create an ArrayList of Strings.
        ArrayList<String> items = getItemsArrayList();
        // Create a SelectionRecyclerAdapter object and initialise it.
        SelectionRecyclerAdapter adapter = new SelectionRecyclerAdapter(items, this);
        // Setup the RecyclerView.
        binding.recyclerViewSelection.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewSelection.setHasFixedSize(true);
        binding.recyclerViewSelection.setAdapter(adapter);
    }

    private ArrayList<String> getItemsArrayList() {
        // Create an ArrayList of Strings.
        ArrayList<String> items = new ArrayList<>();
        // Add items.
        items.add("Create an account (Professor)");
        items.add("Create an account (Admin)");
        items.add("Change email address");
        items.add("Change password");
        // Return the ArrayList.
        return items;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.men_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SelectionActivity.this, LoginActivity.class));
            SelectionActivity.this.finish();
        }
        return true;
    }

    @Override
    public void onMainListener(int position) {
        switch (position) {
            case 0:
                // Start an Intent going to the AccountCreatorProfessorActivity class.
                startActivity(new Intent(SelectionActivity.this,
                        AccountCreatorProfessorActivity.class));
                break;
            case 1:
                // Start an Intent going to the AccountCreatorAdminActivity class.
                startActivity(new Intent(SelectionActivity.this,
                        AccountCreatorAdminActivity.class));
                break;
            case 2:
                // Start an Intent going to the EmailChangeActivity class.
                startActivity(new Intent(SelectionActivity.this,
                        EmailChangeActivity.class));
                break;
            case 3:
                // Start an Intent going to the PasswordChangeActivity class.
                startActivity(new Intent(SelectionActivity.this,
                        PasswordChangeActivity.class));
                break;
        }
    }
}