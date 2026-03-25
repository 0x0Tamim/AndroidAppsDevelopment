package com.ewu.bloodnet;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        LinearLayout cardProfile = findViewById(R.id.cardProfile);

        cardProfile.setOnClickListener(view -> {

            Toast.makeText(HomeActivity.this, "Profile Clicked", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });


        LinearLayout cardSearch = findViewById(R.id.cardSearch);

        cardSearch.setOnClickListener(view -> {

            Toast.makeText(HomeActivity.this, "Search Clicked", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
            finish();
        });



    }
}