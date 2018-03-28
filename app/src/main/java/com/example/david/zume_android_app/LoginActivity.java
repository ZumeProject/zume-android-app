package com.example.david.zume_android_app;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity {
    private Button button_login_login;
    private EditText editText_login_username;
    private EditText editText_login_password;
    private String username;
    private String password;
    private String baseUrlUserProfile = "http://zume.hsutx.edu/wp-json/zume/v1/android/user_profile/1";
    private String baseUrlUser = "http://zume.hsutx.edu/wp-json/zume/v1/android/user/1";
    private String getBaseUrlSessionData = "http://zume.hsutx.edu/wp-json/zume/v1/android/lessons";
    private String baseUrl;
    private AsyncTask<Void, Void, String> execute;
    private String UserID;
    private String isValidCredentials;
    private GetUser auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         /*
        * Bypass button to help with debugging. So I don't have to type in a login
         */
        Button bypass = (Button) findViewById(R.id.bypass_button);
        bypass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = "daoffner";
                password = "Astra2008";
                goToDashboardActivity();
            }
        });
        editText_login_username = (EditText) findViewById(R.id.editText_login_username);
        editText_login_password = (EditText) findViewById(R.id.editText_login_password);
        button_login_login = (Button) findViewById(R.id.button_login_login);
        button_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editText_login_username.getText().toString();
                password = editText_login_password.getText().toString();

                Boolean failed = false;
                FileInputStream fis = null;

                //Checks to see if the user information exists
                try {
                    fis = openFileInput("credentials.txt");
                    Log.d("Test", "Opened the file");

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("Test", "Failed");
                    failed = true;
                }

                //Creates new files with the user information if the credentials are correct.
                if (failed) {
                    makeApiCall(getApplicationContext());
                }
                //Checks to see if it is the same user loging in.
                //If it is it will go to the next page.
                //Otherwise it will create new files for the new user.
                else {
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    String user = null, pass = null;
                    try {
                        user = bufferedReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        pass = bufferedReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("Test", user);
                    Log.d("Test", pass);
                    //Checking if its the same user loging in.
                    if (user.equals(username) && pass.equals(password)) {
                        try {
                            isValidCredentials = bufferedReader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("Test", "Passing saved data");
                        goToDashboardActivity();
                    } else {
                        makeApiCall(getApplicationContext());
                    }

                }
            }
        });
    }
    /**
     * Open the Dashboard window.
     */
    private void goToDashboardActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        bundle.putString("baseUrl", baseUrlUserProfile);

        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    /**
    * Makes a call to get the info from the endpoint http://zume.hsutx.edu/wp-json/zume/v1/android/user/1"
     */
    private void makeApiCall(Context context) {
        auth = new GetUser(username, password, context);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("What!", String.valueOf(auth.getFailed()));
                if(!auth.getFailed()){
                    goToDashboardActivity();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        }, 200);

    }
}