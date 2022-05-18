package com.example.botany;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText emailAddress,password;
    ImageView bg_image,leaf_asset1,leaf_asset2,leaf_asset3,leaf_asset4,leaf_asset5,leaf_asset6,top_logo,copyright;
    CardView login_card;
    Button login_button,signup_button;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        fAuth = FirebaseAuth.getInstance();

        //if user is already logged in then skipping the login and signup page and send them straight to home page
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(fAuth.getCurrentUser() != null){
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    Toast.makeText(MainActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                };
            }
        }, 2025);

        bg_image = (ImageView) findViewById(R.id.bg_image);
        leaf_asset1 = (ImageView) findViewById(R.id.leaf_asset1);
        leaf_asset2 = (ImageView) findViewById(R.id.leaf_asset2);
        leaf_asset3 = (ImageView) findViewById(R.id.leaf_asset3);
        leaf_asset4 = (ImageView) findViewById(R.id.leaf_asset4);
        leaf_asset5 = (ImageView) findViewById(R.id.leaf_asset5);
        leaf_asset6 = (ImageView) findViewById(R.id.leaf_asset6);
        top_logo = (ImageView) findViewById(R.id.top_logo);
        copyright = (ImageView) findViewById(R.id.copyright);
        login_card = (CardView) findViewById(R.id.login_card);
        login_button = (Button) findViewById(R.id.login);
        signup_button = (Button) findViewById(R.id.signup_button);
        emailAddress = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);

        //if user wants to create a new account
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openSignupActivity();
            }
        });

        //if user already has an existing account they can login with
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailAddress.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    emailAddress.setError("Email Address is required");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    password.setError("Password is required");
                    return;
                }
                else{
                    login(email,pass);
                }
            }
        });

        //main splash page animation
        bg_image.animate().translationY(-1700).setDuration(900).setStartDelay(1500);
        leaf_asset1.animate().translationX(-600).setDuration(700).setStartDelay(1800);
        leaf_asset2.animate().translationY(-200).translationX(-600).setDuration(800).setStartDelay(1600);
        leaf_asset3.animate().translationY(700).translationX(-600).setDuration(700).setStartDelay(1600);
        leaf_asset4.animate().translationY(700).translationX(300).setDuration(750).setStartDelay(1700);
        leaf_asset5.animate().translationY(-400).translationX(600).setDuration(800).setStartDelay(1600);
        leaf_asset6.animate().translationY(450).translationX(150).setDuration(700).setStartDelay(1600);
        top_logo.animate().alpha(1).translationY(-20).setDuration(500).setStartDelay(1800);
        copyright.animate().alpha(1).translationY(-130).setDuration(500).setStartDelay(1800);
        login_card.animate().alpha(1).setDuration(500).setStartDelay(1800);



    }

    private void login(String email, String pass) {
        fAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    Toast.makeText(MainActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Error Logging in. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

}