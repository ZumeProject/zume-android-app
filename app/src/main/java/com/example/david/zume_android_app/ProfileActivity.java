package com.example.david.zume_android_app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProfileActivity extends AppCompatActivity {

    private String resultFromUserProfile = "";
    private String resultFromUser = "";
    private String token ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/

        // ZUME button to return home
        Button home = (Button)findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                Integer user_id = intent.getIntExtra("user_id", 0);
                String token = intent.getStringExtra("token");
                long timeStamp = intent.getLongExtra("timeStamp", 0);

                Bundle bundle = new Bundle();
                bundle.putInt("user_id", user_id);
                bundle.putString("token", token);
                bundle.putLong("timeStamp", timeStamp);
                intent = new Intent(ProfileActivity.this, DashboardActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // Button to edit profile
        Button editProfile = (Button)findViewById(R.id.editProfile);
        // Only allow a user to edit profile if they are connected to internet
        if(isNetworkAvailable()){

            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = getIntent();

                    Integer userID = intent.getIntExtra("user_id", 0);
                    String token = intent.getStringExtra("token");
                    long timeStamp = intent.getLongExtra("timeStamp", 0);
                    Bundle bundle = new Bundle();

                    bundle.putInt("user_id", userID);
                    bundle.putString("token", token);
                    bundle.putLong("timeStamp", timeStamp);
                    intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
        else{
            editProfile.setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        token = intent.getStringExtra("token");


        FileInputStream fis = null;
        try {
            fis = openFileInput("user_profile.txt");
            Log.d("Test", "Opened the file");

        } catch (FileNotFoundException e) {
            // Couldn't find user_profile info, so get that from the API
            GetUser user = new GetUser(token, this);
            this.onCreate(savedInstanceState);
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);

        try {
            resultFromUserProfile = bufferedReader.readLine();
            Log.d("UserProfile", resultFromUserProfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fis = openFileInput("user.txt");
            Log.d("Test", "Opened the file");

        } catch (FileNotFoundException e) {
            // Couldn't find the user information, so get that information from the API
            if(isNetworkAvailable()) {
                GetUser user = new GetUser(token, this);

                this.onCreate(savedInstanceState);
            }
        }
        isr = new InputStreamReader(fis);
        bufferedReader = new BufferedReader(isr);

        try {
            resultFromUser = bufferedReader.readLine();
            Log.d("User", resultFromUser);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Test", "Passing saved data");
        // Set up the display
        setProfileScreen();

    }

    /**
     * Set the profile screen data.
     */
    private void setProfileScreen() {
        try{
            // Get JSON data
            JSONObject reader = new JSONObject(resultFromUserProfile);
            JSONArray first = reader.getJSONArray("first_name");
            JSONArray last = reader.getJSONArray("last_name");
            JSONArray nickname = reader.getJSONArray("nickname");
            try {
                JSONArray phone = reader.getJSONArray("zume_phone_number");
                // Set phone number
                TextView phoneNumber = (TextView)findViewById(R.id.phoneProfile);
                phoneNumber.setText((phone.get(0).toString()));
            }
            catch(Exception o){
                TextView phoneNumber = (TextView)findViewById(R.id.phoneProfile);
                phoneNumber.setText("");
                o.printStackTrace();
            }

            // Set first name
            TextView firstName = (TextView)findViewById(R.id.firstNameProfile);
            if(!first.get(0).equals("")){
                firstName.setText(first.get(0).toString());
            }
            else{
                firstName.setText(nickname.get(0).toString());
            }

            // Set last name
            TextView lastName = (TextView)findViewById(R.id.lastNameProfile);
            if(!last.get(0).equals("")){
                lastName.setText(last.get(0).toString());
            }
            else{
                lastName.setText(nickname.get(0).toString());
            }

            // Get email from user
            reader = new JSONObject(resultFromUser);
            String email = reader.getString("user_email");
            // set email
            TextView emailText = (TextView)findViewById(R.id.emailProfile);
            emailText.setText(email);


        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check to see if we can connect to the network.
     * @return true if we can, false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}
