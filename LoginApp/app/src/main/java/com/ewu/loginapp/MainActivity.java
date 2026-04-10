package com.ewu.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(v -> {

            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if(username.isEmpty()||password.isEmpty()){
                Toast.makeText(this,"Please all fields", Toast.LENGTH_SHORT).show();
            }
            else if(username.equals("admin") && password.equals("12345")){
                Toast.makeText(this,  "Login Successful", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });


        btnSignup.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(i);

        });
    }
}
