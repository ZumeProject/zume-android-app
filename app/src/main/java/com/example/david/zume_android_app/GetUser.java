package com.example.david.zume_android_app;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Brandi on 3/18/2018.
 */

public class GetUser extends AppCompatActivity {

    private Context context;
    protected String isValidCredentials = "";
    protected String username = "";
    protected String password = "";
    protected int UserID = 0;
    String user_profile = "http://zume.hsutx.edu/wp-json/zume/v1/android/user_profile/1";
    String user = "http://zume.hsutx.edu/wp-json/zume/v1/android/user/1";

    public GetUser(String username, String password, Context context){
        this.context = context;
        this.username = username;
        this.password = password;
        try {
            ApiAuthenticationClient apiAuthenticationClient = new ApiAuthenticationClient(
                    user_profile
                    , username
                    , password
            );
            AsyncTask<Void, Void, String> execute = new GetUser.ExecuteNetworkOperation(apiAuthenticationClient, "user_profile", context);
            execute.execute();
        } catch (Exception ex) {
        }
    }

    /**
     * This subclass handles the network operations in a new thread.
     * It starts the progress bar, makes the API call, and ends the progress bar.
     */
    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ApiAuthenticationClient apiAuthenticationClient;
        private Context context;
        private String type = "user_profile"; // user_profile or user GET type?


        /**
         * Overload the constructor to pass objects to this class.
         */
        public ExecuteNetworkOperation(ApiAuthenticationClient apiAuthenticationClient, String type, Context context) {
            this.context = context;
            this.apiAuthenticationClient = apiAuthenticationClient;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

            // Login Success
            if (isValidCredentials != null && !isValidCredentials.equals("")) {
                //Creating te credentials file and user_profile file
                //Also makes a call for the second Api call
                if(type.equals("user_profile")) {
                    FileOutputStream outputStream;
                    String filename = "user_profile.txt";
                    String fileContents = isValidCredentials + "\n";
                    Log.d("Test", "Made first call");
                    try {
                        outputStream = context.openFileOutput(filename, context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                        Log.d("Test", "Made the user_profile file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Now make the call to rewrite the user file
                    ApiAuthenticationClient apiAuthenticationClient2 = new ApiAuthenticationClient(
                            user
                            , username
                            , password
                    );
                    AsyncTask<Void, Void, String> execute2 = new GetUser.ExecuteNetworkOperation(apiAuthenticationClient2, "user", context);
                    execute2.execute();

                }
                else if(type.equals("user")){

                    String filename = "credentials.txt";

                    Log.d("Test", username);
                    Log.d("Test", password);
                    //Mess with this after the endpoint is fixed
                    try {
                        JSONObject reader = new JSONObject(isValidCredentials);
                        int id = reader.getInt("user_id");
                        UserID = id;
                        Log.d("Test", String.valueOf(UserID));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String fileContents = username + "\n" + password + "\n" + UserID+ "\n";
                    FileOutputStream outputStream;

                    try {
                        outputStream = context.openFileOutput(filename, context.MODE_PRIVATE);
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
                        outputStream = context.openFileOutput(filename, context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                        Log.d("Test", "Made the user file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
