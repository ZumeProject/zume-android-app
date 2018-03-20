package com.example.david.zume_android_app;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private String getBaseUrlSessionData = "http://zume.hsutx.edu/wp-json/zume/v1/android/lessons/all";
    private String baseUrl;
    private AsyncTask<Void, Void, String> execute;
    private String UserID;
    private String isValidCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
                    makeApiCall(baseUrlUserProfile);
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
                        makeApiCall(baseUrlUserProfile);
                    }

                }
            }
        });
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
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                isValidCredentials = apiAuthenticationClient.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return isValidCredentials;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Hide the progress bar.
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            // Login Success
            if (isValidCredentials != null && !isValidCredentials.equals("")) {
                //Creating te credentials file and user_profile file
                //Also makes a call for the second Api call
                if(baseUrl.equals(baseUrlUserProfile)) {
                    FileOutputStream outputStream;
                    String filename = "user_profile.txt";
                    String fileContents = isValidCredentials + "\n";
                    Log.d("Test", "Made first call");
                    try {
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                        Log.d("Test", "Made the user_profile file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    makeApiCall(baseUrlUser);
                }
                //Makes the user file and calls the goToDashboardActivity method
                else if(baseUrl.equals(baseUrlUser)){

                    String filename = "credentials.txt";

                    Log.d("Test", username);
                    Log.d("Test", password);
                    //Mess with this after the endpoint is fixed
                    try {
                        JSONObject reader = new JSONObject(isValidCredentials);
                        int id = reader.getInt("user_id");
                        UserID = String.valueOf(id);
                        Log.d("Test", UserID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String fileContents = username + "\n" + password + "\n" + UserID+ "\n";
                    FileOutputStream outputStream;

                    try {
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                        Log.d("Test", "Made the credentials file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    filename = "user.txt";
                    fileContents = isValidCredentials + "\n";
                    Log.d("Test", "Made second call");
                    try {
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                        Log.d("Test", "Made the user file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    makeApiCall(getBaseUrlSessionData);
                }else if(baseUrl.equals("http://zume.hsutx.edu/wp-json/zume/v1/android/lessons/all")){
                    FileOutputStream outputStream;
                    String filename = "session_data.txt";
                    String fileContents = isValidCredentials + "\n";
                    Log.d("Test", isValidCredentials);
                    Log.d("Test", "Made third call");
                    try {
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                        Log.d("Test", "Made the session_data file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    goToDashboardActivity();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
            }
        }
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
    private void makeApiCall(String url) {
        baseUrl = url;
        ApiAuthenticationClient apiAuthenticationClient = new ApiAuthenticationClient(
                baseUrl
                , username
                , password
        );
        execute = new ExecuteNetworkOperation(apiAuthenticationClient);
        execute.execute();
    }
}



