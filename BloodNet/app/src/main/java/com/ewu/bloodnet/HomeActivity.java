package com.ewu.bloodnet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        setContentView(R.layout.activity_home);

        // profile card
        LinearLayout cardProfile = findViewById(R.id.cardProfile);

        // profile card e click korle profile page e jabe
        cardProfile.setOnClickListener(view -> {
            Toast.makeText(HomeActivity.this, "Profile Clicked", Toast.LENGTH_SHORT).show();

            // profile activity te jabo
            Intent goToProfile = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(goToProfile);
            finish(); // home page off kore dibo
        });

        // search card
        LinearLayout cardSearch = findViewById(R.id.cardSearch);

        // search card e click korle search page e jabe
        cardSearch.setOnClickListener(view -> {
            Toast.makeText(HomeActivity.this, "Search Clicked", Toast.LENGTH_SHORT).show();

            // search activity te jabo
            Intent goToSearch = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(goToSearch);
            finish();
        });
    }
}
