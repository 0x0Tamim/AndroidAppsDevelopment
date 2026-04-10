package com.ewu.loginapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupActivity extends AppCompatActivity {

    EditText etNewUsername, etNewPassword, etConfirmPassword;
    Button btnSignup;

    public static String savedUsername = "";
    public static String savedPassword = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignup = findViewById(R.id.btnSignup);


        btnSignup.setOnClickListener(view -> {

            String username = etNewUsername.getText().toString();
            String password = etNewPassword.getText().toString();
            String confirm = etConfirmPassword.getText().toString();

            if(username.isEmpty()||password.isEmpty()||confirm.isEmpty()){
                Toast.makeText(this,"Please fill all fields",Toast.LENGTH_SHORT).show();
            }

            else if(!password.equals(confirm)){
                Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show();
            }

            else{
                savedUsername = username;
                savedPassword = password;
                Toast.makeText(this,"Sign Up Successful",Toast.LENGTH_SHORT).show();
            }


        });

    }
}