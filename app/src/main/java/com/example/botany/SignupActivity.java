package com.example.botany;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {


    EditText name,pass,country,email,conf_pass;
    Button login_here,signup;
    FirebaseAuth fAuth;

    //had to manually add database URL because chose wrong server location (singapore)
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://botany-iub-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.name);
        pass = findViewById(R.id.pass);
        conf_pass = findViewById(R.id.conf_pass);
        country = findViewById(R.id.country);
        email = findViewById(R.id.email);

        signup = (Button) findViewById(R.id.signup);
        login_here = (Button) findViewById(R.id.login_here);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = name.getText().toString().trim();
                String location = country.getText().toString().trim();
                String emailAddress = email.getText().toString().trim();
                String password = pass.getText().toString().trim();
                String confirmPassword = conf_pass.getText().toString().trim();

                if(TextUtils.isEmpty(username)){
                    name.setError("Name is required");
                    return;
                }


                if(TextUtils.isEmpty(emailAddress)){
                    email.setError("Email Address is required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    pass.setError("Password is required");
                    return;
                }

                if(TextUtils.isEmpty(confirmPassword)){
                    conf_pass.setError("Repeat Password here");
                    return;
                }

                if(password.length() < 6){
                    pass.setError("Must be more than 6 Characters");
                    return;
                }

                if(!password.equals(confirmPassword)){
                    conf_pass.setError("Passwords do not match");
                    pass.setError("Passwords do not match");
                    return;
                }


                fAuth.createUserWithEmailAndPassword(emailAddress,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            //on clicking signup button user data will be added to the realtime database
                            FirebaseUser regUser = fAuth.getCurrentUser();
                            assert regUser != null;

                            //fetch UID from authentication as identifier in the database
                            String userId = regUser.getUid();
                            myRef= database.getReference("Users").child(userId);

                            //hashmap to store all the user data
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("userId",userId);
                            hashMap.put("email",emailAddress);
                            hashMap.put("name",username);
                            hashMap.put("country",location);
                            hashMap.put("imageURL","default");

                            //if database integration and authentication is complete
                            myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        //Message user that their account was created and send them back to login page
                                        Toast.makeText(SignupActivity.this, "New account created", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    }

                                    else {
                                        Toast.makeText(SignupActivity.this, "Error creating new account" +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(SignupActivity.this, "Error creating new account" +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //go back to login page in case user does not want to create another and use their existing account
        login_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIntroActivity();
            }
        });

    }

    private void openIntroActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}