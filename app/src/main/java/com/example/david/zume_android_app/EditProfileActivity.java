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

    private String resultFromAPI = "";
    private String baseUrl = "";
    private String username = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


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

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        FileInputStream fis= null;
        try {
            fis = openFileInput("UserProfile.txt");
            Log.d("Test", "Opened the file");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        try {
            bufferedReader.readLine();
            bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            resultFromAPI = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Test", "Passing saved data");
        setProfileScreen();
/*
        baseUrl = "http://zume.hsutx.edu/wp-json/zume/v1/android/user_profile/1";
        try {

            ApiAuthenticationClient apiAuthenticationClient =
                    new ApiAuthenticationClient(
                            baseUrl
                            , username
                            , password
                    );

            AsyncTask<Void, Void, String> execute = new ProfileActivity.ExecuteNetworkOperation(apiAuthenticationClient);
            execute.execute();
        } catch (Exception ex){
            Log.d("Test","Error getting profile data.");
        }
        */

    }

    /**
     * This subclass handles the network operations in a new thread.
     * It starts the progress bar, makes the API call, and ends the progress bar.
     */

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ApiAuthenticationClient apiAuthenticationClient;

        /**
         * Overload the constructor to pass objects to this class.
         */
        public ExecuteNetworkOperation(ApiAuthenticationClient apiAuthenticationClient) {
            this.apiAuthenticationClient = apiAuthenticationClient;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Display the progress bar.
            //findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.d("Test", "Made to the execute method");
                resultFromAPI = apiAuthenticationClient.execute();
            } catch (Exception e) {
                Log.d("Test", "Error making it to execute method");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Hide the progress bar.
            //findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            // Credentials correct
            if (resultFromAPI != null && !resultFromAPI.equals("")) {
                setProfileScreen();
            }
            // Login Failure
            else {
                Toast.makeText(getApplicationContext(), "Error opening profile: Invalid Credentials", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Open a new activity window.
     */
    private void setProfileScreen() {
        try{
            JSONObject reader = new JSONObject(resultFromAPI);
            JSONArray first = reader.getJSONArray("first_name");
            JSONArray last = reader.getJSONArray("last_name");
            JSONArray nickname = reader.getJSONArray("nickname");
            JSONArray lastActive = reader.getJSONArray("zume_last_active");

            TextView name = (TextView)findViewById(R.id.name);
            if(!first.get(0).equals("") || !last.get(0).equals("")){
                name.setText(first.get(0) + " " + last.get(0));
            }
            else{
                name.setText(nickname.get(0).toString());
            }

            TextView activity = (TextView)findViewById(R.id.lastActivity);
            if(!lastActive.get(0).equals("")){
                activity.setText("Last Active: " + lastActive.get(0));
            }


        }
        catch(Exception e) {

        }

        TextView home = (TextView)findViewById(R.id.home);
    }

}
