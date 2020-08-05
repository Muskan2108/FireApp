package com.example.fireapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;
    EditText emailId, password;
    FirebaseUser currentFirebaseUser;
    Button btnSignUp;
    TextView tvSignIn;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, YourService.class);
        startService(intent);

        mFirebaseAuth = FirebaseAuth.getInstance();

        emailId = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        btnSignUp = findViewById(R.id.button);
        tvSignIn = findViewById(R.id.textView);
        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(MainActivity.this,"Fields Are Empty!", Toast.LENGTH_SHORT).show();
                }
                else if(email.isEmpty()){
                    emailId.setError("Please enter email Id");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else if(!(email.isEmpty() && pwd.isEmpty())){

                    if(pwd.length()<6){
                        Toast.makeText(MainActivity.this,"Minimum six characters required for password", Toast.LENGTH_SHORT).show();
                        password.getText().clear();
                    }
                    else {
                        mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                                } else {
                                    currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                                    String uid = currentFirebaseUser.getUid();
                                    mDatabaseReference.child("points").child(uid).setValue("0");
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                }
                            }
                        });
                    }
                }
                else {
                    Toast.makeText(MainActivity.this,"Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onBackPressed() {


        if(backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            finishAffinity();

            super.onBackPressed();
            return;
        }
        else{
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }
}
