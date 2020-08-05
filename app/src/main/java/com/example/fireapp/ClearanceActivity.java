package com.example.fireapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


public class ClearanceActivity extends AppCompatActivity {

    Button btnLogout,cleared, notCleared;
    private long backPressedTime;
    private Toast backToast;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser currentFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clearance);

        Intent intent = new Intent(ClearanceActivity.this, YourService.class);
        startService(intent);
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent mIntent = getIntent();
        final int dustbinNo = mIntent.getIntExtra("dustbinNo",-1);

        btnLogout = findViewById(R.id.logout);
        cleared = findViewById(R.id.cleared);
        notCleared = findViewById(R.id.notCleared);

        cleared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser(dustbinNo);
                Intent intToHome = new Intent(ClearanceActivity.this, HomeActivity.class);
                startActivity(intToHome);
            }
        });

        notCleared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToHome = new Intent(ClearanceActivity.this, HomeActivity.class);
                startActivity(intToHome);
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth.getInstance().signOut();
                        Intent intToMain = new Intent(ClearanceActivity.this, MainActivity.class);
                        startActivity(intToMain);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setMessage("Do you want to logout for sure?");

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void addUser(int dustbinNo){
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = currentFirebaseUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long points = Long.valueOf(dataSnapshot.child("points").child(uid).getValue().toString());
                points = points +1;
                mDatabaseReference.child("points").child(uid).setValue(points);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        System.out.println("UID of current user = "+ uid);

        UserClearedDustbin userClearedDustbin = new UserClearedDustbin();
        userClearedDustbin.setUid(uid);

        userClearedDustbin.setTimestamp(userClearedDustbin.getTimestamp());

        if(dustbinNo>=0){
            userClearedDustbin.setDustbinNo(dustbinNo);
        }

        String id = mDatabaseReference.child("users").push().getKey();

        mDatabaseReference.child("users").child(id).setValue(userClearedDustbin);
        Toast.makeText(this,"Thank you for clearing the dustbin",Toast.LENGTH_SHORT).show();

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
