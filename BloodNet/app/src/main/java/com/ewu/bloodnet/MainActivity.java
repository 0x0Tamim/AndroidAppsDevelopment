package com.ewu.bloodnet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        // title text a "Blood" red, "Net" green color deoa hoyeche - HTML use
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText(Html.fromHtml(getString(R.string.bloodnet_text)));


        TextView signupText = findViewById(R.id.tvSignup);
        signupText.setOnClickListener(v -> {
            Intent goToSignup = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(goToSignup);
        });

        // input field ar login button
        EditText etUsername = findViewById(R.id.etLoginUsername);
        EditText etPassword = findViewById(R.id.etLoginPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        // login button e click dile
        btnLogin.setOnClickListener(view -> {

            // user je username ar password diyeche seta nichi
            String typedUsername = etUsername.getText().toString();
            String typedPassword = etPassword.getText().toString();

            // SharedPreferences theke save kora username/password anchi
            SharedPreferences sp = getSharedPreferences("user_data", MODE_PRIVATE);
            String savedUsername = sp.getString("username", "");
            String savedPassword = sp.getString("password", "");

            // jodi kono field khali thake
            if (typedUsername.isEmpty() || typedPassword.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // username ar password match korle home page e pathabo
            if (typedUsername.equals(savedUsername) && typedPassword.equals(savedPassword)) {
                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                // home activity te jabo
                Intent goHome = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(goHome);
                finish(); // login page band kore dibo
            } else {
                // match na korle error msg dekhabo
                Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
