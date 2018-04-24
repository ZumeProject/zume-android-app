package com.example.david.zume_android_app;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Brandi on 3/18/2018.
 *
 * This java file gets all the user's info that is needed for the app,
 * and it also gets a token for the user if it is needed.
 */

public class GetUser extends AppCompatActivity {

    private Context context;                    //context from the app
    protected String isValidCredentials = "";   //response from the GET or POST that was made
    protected String username = "";             //Username of the current user
    protected String password = "";             //password for the current user
    protected int UserID = 0;                   //userId of the current user
    protected  String token = "";               //token that will be used for GETs and POSTs
    private long timeStamp = 0;                 //Time stamp of the token
    String jwtAuth = "https://zume.sergeantservices.com/wp-json/jwt-auth/v1/token";                     //Url for getting a token
    String user_profile = "https://zume.sergeantservices.com/wp-json/zume/v1/android/user_profile/1";   //Url for getting the user's profile
    String user = "https://zume.sergeantservices.com/wp-json/zume/v1/android/user/1";                   //Url for getting the user's email
    String sessions = "https://zume.sergeantservices.com/wp-json/zume/v1/android/lessons";              //Url for getting the session data
    private boolean failed = true;

    /**
     * Constructor for GetUser when the user doesn't have a token
     * @param username String username of the current user
     * @param password String password of the current user
     */
    public GetUser(String username, String password, Context context){
        this.context = context;
        this.username = username;
        this.password = password;
        try {
            //Makes an api call to get a token
            ApiAuthenticationClient apiAuthenticationClient = new ApiAuthenticationClient(
                    jwtAuth
                    , username
                    , password
                    , true
            );
            apiAuthenticationClient.setHttpMethod("POST");
            AsyncTask<Void, Void, String> execute = new GetUser.ExecuteNetworkOperation(apiAuthenticationClient, "token", context);
            execute.execute();
        } catch (Exception ex) {
        }
    }
    /**
     * Constructor for GetUser when the user has a token
     * @param token String token of the current user
     * @param context Context the context for the app
     */
    public GetUser(String token, Context context){
        this.context = context;
        this.token = token;
        try {
            //Makes an api call to get all the user's info
            ApiAuthenticationClient apiAuthenticationClient = new ApiAuthenticationClient(
                    user_profile
                    ,this.token
            );
            apiAuthenticationClient.setHttpMethod("GET");
            AsyncTask<Void, Void, String> execute = new GetUser.ExecuteNetworkOperation(apiAuthenticationClient, "user_profile", context);
            execute.execute();
        } catch (Exception ex) {
        }
    }
    public boolean getFailed(){ return failed;}

    public String getToken() {
        return token;
    }
    public long getTimeStamp() {
        return timeStamp;
    }

    public String getUserID(){
        return String.valueOf(UserID);
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
                //Gets the user info with the new token that was created.
                if(type.equals("token")){
                    failed = false;
                    String [] tokenArray = isValidCredentials.split(":");
                    tokenArray = tokenArray[1].split(",");
                    tokenArray[0] = tokenArray[0].substring(1,tokenArray[0].length()-1);
                    token = tokenArray[0];
                    ApiAuthenticationClient apiAuthenticationClient2 = new ApiAuthenticationClient(
                            user_profile
                            , token
                    );
                    apiAuthenticationClient2.setHttpMethod("GET");
                    AsyncTask<Void, Void, String> execute2 = new GetUser.ExecuteNetworkOperation(apiAuthenticationClient2, "user_profile", context);
                    execute2.execute();

                }
                //Save the users information from the profile url
                else if(type.equals("user_profile")) {
                    failed = false;
                    FileOutputStream outputStream;
                    String filename = "user_profile.txt";
                    String fileContents = isValidCredentials + "\n";
                    try {
                        outputStream = context.openFileOutput(filename, context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                        //Log.d("Test", "Made the user_profile file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Makes an api call to get the users email
                    ApiAuthenticationClient apiAuthenticationClient2 = new ApiAuthenticationClient(
                            user
                            , token
                    );
                    apiAuthenticationClient2.setHttpMethod("GET");
                    AsyncTask<Void, Void, String> execute2 = new GetUser.ExecuteNetworkOperation(apiAuthenticationClient2, "user", context);
                    execute2.execute();

                }
                //Saves the user's credentials and the user's email
                else if(type.equals("user")){
                    failed = false;
                    String filename = "credentials.txt";
                    FileInputStream fis = null;
                    try {
                        fis = context.openFileInput(filename);
                        Log.d("Test", "Opened the file");
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader bufferedReader = new BufferedReader(isr);
                        try {
                            username = bufferedReader.readLine();
                            password = bufferedReader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d("Test", "Failed");
                    }

                    try {
                        JSONObject reader = new JSONObject(isValidCredentials);
                        int id = reader.getInt("user_id");
                        UserID = id;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Crates a time stamp for the token
                    timeStamp = System.currentTimeMillis();
                    String fileContents = username + "\n" + password + "\n" + UserID+ "\n"+ token+"\n"+timeStamp+"\n";
                    FileOutputStream outputStream;

                    try {
                        outputStream = context.openFileOutput(filename, context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                        //Log.d("Test", "Made the credentials file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    filename = "user.txt";
                    fileContents = isValidCredentials + "\n";
                    try {
                        outputStream = context.openFileOutput(filename, context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                        //Log.d("Test", "Made the user file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Makes an api call to get the session data from zume.
                    ApiAuthenticationClient apiAuthenticationClient2 = new ApiAuthenticationClient(
                                    sessions
                                    , token
                            );
                    apiAuthenticationClient2.setHttpMethod("GET");
                    AsyncTask<Void, Void, String> execute2 = new GetUser.ExecuteNetworkOperation(apiAuthenticationClient2, "sessions", context);
                    execute2.execute();
                }
                //Saves the session data from zume
                else if(type.equals("sessions")){
                    failed = false;
                    String sessions = "";
                    String filename = "session_data.txt";
                    try {
                        JSONObject reader = new JSONObject(isValidCredentials);
                        sessions = String.valueOf(reader);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String fileContents = sessions;
                    FileOutputStream outputStream;
                    try {
                        outputStream = context.openFileOutput(filename, context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}