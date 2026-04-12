package ewubd.edu.tamim2023360298;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    private Button btnExit,btnSignup,btnToggle;
    private EditText etuserId,etPass,etRepass;

    private SharedPreferences pref;
    private TextView tvTitle;
    String userId,prevPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        btnExit = findViewById(R.id.btnExit);
        btnSignup = findViewById(R.id.btnSignup);
        btnToggle = findViewById(R.id.btnToggle);

        etuserId = findViewById(R.id.etuserId);
        etPass = findViewById(R.id.etPass);
        etRepass = findViewById(R.id.etRepass);
        tvTitle = findViewById(R.id.tvTitle);



        pref = this.getPreferences(MODE_PRIVATE);
        userId = pref.getString("USER_ID","NO-ACCOUNT");
        if(!userId.equals("NO-ACCOUNT")){

            prevPass = pref.getString("PASS",null);

            etRepass.setVisibility(View.GONE);
            findViewById(R.id.tvRepassLabel).setVisibility(View.GONE);
            tvTitle.setText("Login");
            btnSignup.setText("Login");
            btnToggle.setText("Don't have an account");
        }

        // ADD this block after the btnExit listener closing });  (after line 59):
        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear saved account to switch to Signup mode, or vice versa
                SharedPreferences.Editor et = pref.edit();
                et.remove("USER_ID");
                et.remove("PASS");
                et.apply();
                recreate(); // restarts the activity fresh
            }
        });


        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("btnExit...signUpActivity");
                finish();
            }
        });


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("btnSignup...signUpActivity");

                String userId = etuserId.getText().toString();
                String pass = etPass.getText().toString();
                String rePass = etRepass.getText().toString();

                System.out.println(userId);
                System.out.println(pass);
                System.out.println(rePass);


                if(userId.length()<4){
                    Toast.makeText(SignUpActivity.this,"User id must be 4-8 letters",Toast.LENGTH_LONG).show();
                    return;
                }


                if(pass.length()<4){
                    Toast.makeText(SignUpActivity.this,"Password must be greater than or equal 4 letters",Toast.LENGTH_LONG).show();
                    return;
                }

                if(prevPass == null){

                    if (rePass.length() < 4) {

                        Toast.makeText(SignUpActivity.this,"Repassword must be greater than or equal 4 letters",Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(!pass.equals(rePass)){
                        System.out.println("Password didn't match");
                        return;
                    }
                }
                else{
                    if(!userId.equals(userId)){
                        Toast.makeText(SignUpActivity.this,"User Id didn't match",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!pass.equals(prevPass)) {

                        Toast.makeText(SignUpActivity.this,"Password didn't match",Toast.LENGTH_LONG).show();
                        return;
                    }
                }



                SharedPreferences.Editor et = pref.edit();
                et.putString("USER_ID", userId);
                et.putString("PASS", pass);
                et.apply();

                Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(i);
                finish();




            }
        });
    }

    public void onStart(){
        super.onStart();
        System.out.println("onStart...SignUpActivity");
    }

    public void onPause(){
        super.onPause();
        System.out.println("onPause...SignupActivity");
    }

    public void onResume(){
        super.onResume();
        System.out.println("onResume....SignupActivity");
    }

    public void onStop(){
        super.onStop();
        System.out.println("onStop...SignupActivity");
    }

    public void onDestroy(){
        super.onDestroy();
        System.out.println("onDestroy...SignupActivity");
    }




}