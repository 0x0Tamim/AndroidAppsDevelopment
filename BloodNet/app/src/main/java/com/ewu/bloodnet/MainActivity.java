package com.ewu.bloodnet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.Html;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        TextView textView = findViewById(R.id.titleText);

        textView.setText(
                Html.fromHtml(getString(R.string.bloodnet_text))
        );


        TextView signupText = findViewById(R.id.tvSignup);

        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });


        EditText etUsername = findViewById(R.id.etLoginUsername);
        EditText etPassword = findViewById(R.id.etLoginPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view -> {
            String inputUsername = etUsername.getText().toString();
            String inputPassword = etPassword.getText().toString();
            SharedPreferences sp = getSharedPreferences("user_data", MODE_PRIVATE);
            String savedUsername = sp.getString("username", "");
            String savedPassword = sp.getString("password", "");

            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputUsername.equals(savedUsername) && inputPassword.equals(savedPassword)) {
                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT ).show();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });

        }

    }
