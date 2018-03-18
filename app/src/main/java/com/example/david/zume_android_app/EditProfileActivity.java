package com.example.david.zume_android_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class EditProfileActivity extends AppCompatActivity {

    private String resultFromUserProfile = "";
    private String resultFromUser = "";
    private String baseUrl = "";
    private String username = "";
    private String password = "";
    private String test = "";
    String prevFirstName = "";
    String prevLastName = "";
    String prevEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button home = (Button)findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                String username = intent.getStringExtra("username");
                String password = intent.getStringExtra("password");

                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("password", password);

                intent = new Intent(EditProfileActivity.this, DashboardActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        Button update = (Button)findViewById(R.id.save);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                String username = intent.getStringExtra("username");
                String password = intent.getStringExtra("password");
                int userID = intent.getIntExtra("user_id", 0);

                final EditText first =  (EditText) findViewById(R.id.firstName);
                final EditText last =  (EditText) findViewById(R.id.lastName);
                final EditText email =  (EditText) findViewById(R.id.email);

                try{
                    String first_name = first.getText().toString();
                    String last_name = last.getText().toString();
                    String email_address = email.getText().toString();

                    if((first_name == null || first_name.equals("")) || (last_name == null || last_name.equals(""))){

                    }

                    UpdateUser user = new UpdateUser(userID, username, password, first_name, last_name, email_address);
                }
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Error updating profile", Toast.LENGTH_LONG).show();
                }
            }
        });

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        FileInputStream fis= null;
        try {
            fis = openFileInput("user_profile.txt");
            Log.d("Test", "Opened the file");

        } catch (FileNotFoundException e) {
            GetUser user = new GetUser(username, password);
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
            bufferedReader.readLine();
            bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fis = openFileInput("user.txt");
            Log.d("Test", "Opened the file");

        } catch (FileNotFoundException e) {
            GetUser user = new GetUser(username, password);
            this.onCreate(savedInstanceState);
        }
        isr = new InputStreamReader(fis);
        bufferedReader = new BufferedReader(isr);

        try {
            resultFromUser = bufferedReader.readLine();
            Log.d("User", resultFromUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bufferedReader.readLine();
            bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Test", "Passing saved data");
        setEditProfileScreen();

    }



    /**
     * Open a new activity window.
     */
    private void setEditProfileScreen() {
        try{
            JSONObject reader = new JSONObject(resultFromUserProfile);
            JSONArray first = reader.getJSONArray("first_name");
            JSONArray last = reader.getJSONArray("last_name");

            EditText firstName = (EditText)findViewById(R.id.firstName);
            firstName.setText(first.get(0).toString());
            this.prevFirstName = first.get(0).toString();

            EditText lastName = (EditText)findViewById(R.id.lastName);
            lastName.setText(last.get(0).toString());
            this.prevLastName = last.get(0).toString();

        }
        catch(Exception e) {

        }
        try{
            JSONObject reader = new JSONObject(resultFromUser);
            JSONObject email = reader.getJSONObject("email");

            EditText emailText = (EditText)findViewById(R.id.email);
            emailText.setText(email.toString());
            this.prevEmail = email.toString();

        }
        catch(Exception e) {

        }
    }
}
