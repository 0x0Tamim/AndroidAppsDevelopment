package com.ewu.bloodnet;

import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        TextView textView = findViewById(R.id.titleText);

        textView.setText(
                Html.fromHtml(getString(R.string.bloodnet_text))
        );

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

    btnRegister.setOnClickListener(view -> {
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

if(username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
    Toast.makeText(SignupActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
    return;
}

if(password.length()<8){
    Toast.makeText(SignupActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
return;
}

if(!password.equals(confirmPassword)){
    Toast.makeText(SignupActivity.this,"Passwords do not match", Toast.LENGTH_SHORT).show();
    return;
}

SharedPreferences sp = getSharedPreferences("user_data", MODE_PRIVATE);
SharedPreferences.Editor editor = sp.edit();
editor.putString("username", username);
editor.putString("email", email);
editor.putString("password", password);
editor.apply();

Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
finish();

    });


    }
}