package com.example.fireapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.util.TypedValue;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    Button btnLogout;

    private NotificationHelper mNotificationHelper;

    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private long backPressedTime;
    private Toast backToast;
    private TextView devText;
    private DatabaseReference mDatabaseReference, mDatabaseReferenceDustbin, mDatabaseReferenceDustbin1, mDatabaseReferenceDustbin2, mDatabaseReferenceDustbin3;
    private String stmp = "Developers - ";
    int textViewCount = 20; // maximum number of dustbins that can be added in the system
    int totalDustbins =0;
    long points = 0;
    String filledDateTime[] = new String[textViewCount];
    TextView[] textViewArray = new TextView[textViewCount];
    private TextView ultrasonicText,filledText,infraredText, locationText,dhtTemperatureText,dhtHumidityText, pointsTextView, weightTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Intent intent = new Intent(HomeActivity.this, YourService.class);
        startService(intent);

        mNotificationHelper = new NotificationHelper(this);

        btnLogout = findViewById(R.id.logout);
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout1);

        devText = findViewById(R.id.textViewDev);
        ultrasonicText = findViewById(R.id.ultrasonic);
        filledText = findViewById(R.id.filled);
        infraredText = findViewById(R.id.infrared);
        locationText = findViewById(R.id.location);
        dhtHumidityText = findViewById(R.id.dhtHumidity);
        dhtTemperatureText = findViewById(R.id.dhtTemperature);
        pointsTextView = findViewById(R.id.pointsTextView);
        weightTextView = findViewById(R.id.weight);
        System.out.println("textViewCount here is = "+textViewCount);

        for(int i=0;i<textViewCount;i++){
            filledDateTime[i] = "01-01-2020 00:00:00";
        }



        mDatabaseReferenceDustbin = FirebaseDatabase.getInstance().getReference().child("dustbin"); // this is for text view formation and details
        mDatabaseReferenceDustbin.keepSynced(true);
        mDatabaseReferenceDustbin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    long dustbinCount = dataSnapshot.getChildrenCount();
                    if(dustbinCount>totalDustbins){
                        int temp = totalDustbins;
                        for(int i = totalDustbins; i < dustbinCount; i++) {
                            textViewArray[i] = new TextView(HomeActivity.this);
                            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT);
                            layoutParams.gravity = Constraints.LayoutParams.RIGHT;
                            layoutParams.gravity = Gravity.RIGHT;
                            layoutParams.gravity = Gravity.TOP;
                            layoutParams.setMargins(2, 2, 2, 2); // (left, top, right, bottom)
                            textViewArray[i].setLayoutParams(layoutParams);
                            textViewArray[i].setText("Dustbin Number "+ (i+1));
                            textViewArray[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                            linearLayout.addView(textViewArray[i]);
                            temp=temp+1;
                        }
                        totalDustbins=temp;
                    }
                    for(int i=0;i<totalDustbins;i++){
                        final int j=i;
                        textViewArray[j].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.out.println("Dustbin "+(j+1)+" selected.....");
                                int count = (int)dataSnapshot.child(String.valueOf(j)).getChildrenCount();
                                long ultrasonic = Long.valueOf(dataSnapshot.child(String.valueOf(j)).child(String.valueOf(count-1)).child("ultrasonic").getValue().toString());
                                int infrared = Integer.valueOf(dataSnapshot.child(String.valueOf(j)).child(String.valueOf(count-1)).child("infrared").getValue().toString());
                                int filled = Integer.valueOf(dataSnapshot.child(String.valueOf(j)).child(String.valueOf(count-1)).child("filled").getValue().toString());
                                String location = dataSnapshot.child(String.valueOf(j)).child(String.valueOf(count-1)).child("location").getValue().toString();
                                float dhtTemperature = Float.valueOf(dataSnapshot.child(String.valueOf(j)).child(String.valueOf(count-1)).child("dht temp").getValue().toString());
                                float dhtHumidity = Float.valueOf(dataSnapshot.child(String.valueOf(j)).child(String.valueOf(count-1)).child("dht humid").getValue().toString());
                                float weight = Float.valueOf(dataSnapshot.child(String.valueOf(j)).child(String.valueOf(count-1)).child("weight").getValue().toString());
                                ultrasonicText.setText("Ultrasonic:\n"+ultrasonic + " mm");
                                if(infrared==0)
                                    infraredText.setText("Garbage detected:\n"+"no");
                                else
                                    infraredText.setText("Garbage detected:\n"+"yes");
                                if(filled==0)
                                    filledText.setText("Dustbin filled:\n"+"no");
                                else
                                    filledText.setText("Dustbin filled:\n"+"yes");
                                weightTextView.setText("Weight of Garbage:\n" + weight + " kgs");
                                locationText.setText("Location:\n"+location);
                                dhtHumidityText.setText("DHT Humidity:\n"+ dhtHumidity + " %");
                                dhtTemperatureText.setText("DHT Temperature:\n"+dhtTemperature + " ℃");
                            }
                        });
                    }
                }
                else {
                    textViewArray[0] = new TextView(HomeActivity.this);
                    LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Constraints.LayoutParams.RIGHT;
                    layoutParams.gravity = Gravity.RIGHT;
                    layoutParams.gravity = Gravity.TOP;
                    layoutParams.setMargins(2, 2, 2, 2); //(left, top, right, bottom)
                    textViewArray[0].setLayoutParams(layoutParams);
                    textViewArray[0].setText("No smart dustbins registered in the city");
                    textViewArray[0].setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    linearLayout.addView(textViewArray[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());


        mDatabaseReferenceDustbin1=FirebaseDatabase.getInstance().getReference(); // this class is for notification functionality
        mDatabaseReferenceDustbin1.keepSynced(true);
        mDatabaseReferenceDustbin1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int count = (int)(dataSnapshot.child("dustbin").getChildrenCount());
                    for(int i=0;i<count;i++){
                        int countNoOfData = (int) dataSnapshot.child("dustbin").child(String.valueOf(i)).getChildrenCount();
                        long ifFilled = Long.valueOf(dataSnapshot.child("dustbin").child(String.valueOf(i)).child(String.valueOf(countNoOfData-1)).child("filled").getValue().toString());
                        Date newDate = new Date(dataSnapshot.child("dustbin").child(String.valueOf(i)).child(String.valueOf(countNoOfData-1)).child("timestamp").getValue(Long.class));
                        String newDateString = sfd.format(newDate);
                        final int j =i;

                        filledDateTime[j] = dataSnapshot.child("filledDateTime").child(String.valueOf(i)).getValue().toString();

                        System.out.println("new Date = "+newDateString+" and filled date value of dustbinNo "+(i+1)+" is "+ filledDateTime[i]);
                        long diff = returnDifference(filledDateTime[i],newDateString);
                        System.out.println("And the difference of dates are "+ diff);

                        if( ifFilled == 1 && diff >= 10000){
                            filledDateTime[j] = sfd.format(newDate);
                            mDatabaseReferenceDustbin2 = FirebaseDatabase.getInstance().getReference();
                            mDatabaseReferenceDustbin2.child("filledDateTime").child(String.valueOf(j)).setValue(filledDateTime[j]);

                            String location = dataSnapshot.child("dustbin").child(String.valueOf(j)).child(String.valueOf(countNoOfData-1)).child("location").getValue().toString();
                            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent homeToClearance = new Intent(HomeActivity.this, ClearanceActivity.class);
                                    homeToClearance.putExtra("dustbinNo",j);
                                    System.out.println("intent with sent integer started");
                                    startActivity(homeToClearance);
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.setMessage("Dustbin number " + (i+1) + " is filled at location coordinates " + location  );
                            AlertDialog dialog = builder.create();
                            dialog.setTitle("Overload of Dustbin");
                            dialog.show();
                            String msg = "Dustbin number " + (i+1) + " is filled at location coordinates " + location + ". Tap to empty the dustbin.";

                            sendOnChannel1("DUSTBIN OVERFLOW", msg, i);


                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("developers");
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {
                        stmp += (dataSnapshot.child(Integer.toString(i)).getValue().toString());
                        stmp += " ";
                    }
                }
                else{
                    stmp += "None";
                }
                devText.setText(stmp);
                stmp = "Developers - ";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReferenceDustbin3 = FirebaseDatabase.getInstance().getReference();
        mDatabaseReferenceDustbin3.keepSynced(true);
        mDatabaseReferenceDustbin3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = currentUser.getUid();
                points = Long.valueOf(dataSnapshot.child("points").child(uid).getValue().toString());
                pointsTextView.setText("Points: "+points);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth.getInstance().signOut();
                        Intent intToMain = new Intent(HomeActivity.this, LoginActivity.class);
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


    public long returnDifference(String startDate, String endDate) {
        //milliseconds
        long different = 0;
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        try{
            Date end = sfd.parse(endDate);
            Date start= sfd.parse(startDate);
            different = end.getTime() - start.getTime();
        } catch (ParseException e){
            e.printStackTrace();
        }

        return different;
    }

    public void sendOnChannel1(String title, String message, long dustbinNo) {
        NotificationCompat.Builder nb = mNotificationHelper.getChannel1Notification(title,message, dustbinNo);
        mNotificationHelper.getManager().notify(1,nb.build());
    }

    public void sendOnChannel2(String title, String message) {
        NotificationCompat.Builder nb = mNotificationHelper.getChannel2Notification(title,message);
        mNotificationHelper.getManager().notify(2,nb.build());
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

