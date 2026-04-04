package com.ewu.bloodnet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_signup);

        // title text a "Blood" red, "Net" green - HTML theke nichi
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText(Html.fromHtml(getString(R.string.bloodnet_text)));

        // sob input field ar button set
        EditText etUsername = findViewById(R.id.etUsername);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(view -> {

            // taking user er dewa data
            String username = etUsername.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            // kono field khali thakle bola hobe fill korte
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // password 8 character er kom hoile accept korbo na
            if (password.length() < 8) {
                Toast.makeText(SignupActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // both password same na hoile error
            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // SharedPreferences e data save korchi, simple storage
            SharedPreferences sp = getSharedPreferences("user_data", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("username", username);
            editor.putString("email", email);
            editor.putString("password", password);
            editor.apply(); // save kore debo

            // success msg dekhabo ar login page e back
            Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
