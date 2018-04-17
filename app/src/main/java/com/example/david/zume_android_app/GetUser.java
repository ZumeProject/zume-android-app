package com.example.david.zume_android_app;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;

/**
 * Created by Brandi on 3/18/2018.
 */

public class GetUser extends AppCompatActivity {

    private Context context;
    protected String isValidCredentials = "";
    protected String username = "";
    protected String password = "";
    protected int UserID = 0;
    protected  String token = "";
    String jwtAuth = "http://zume.hsutx.edu/wp-json/jwt-auth/v1/token";
    String jwtToken = "http://zume.hsutx.edu/wp-json/jwt-auth/v1/token/validate";
    String user_profile = "http://zume.hsutx.edu/wp-json/zume/v1/android/user_profile/1";
    String user = "http://zume.hsutx.edu/wp-json/zume/v1/android/user/1";
    private boolean failed = true;

    public GetUser(String username, String password, Context context){
        /*this.context = context;
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
        }*/
        this.context = context;
        this.username = username;
        this.password = password;
        try {
            ApiAuthenticationClient apiAuthenticationClient = new ApiAuthenticationClient(
                    jwtAuth
                    , username
                    , password
                    , true
            );
            apiAuthenticationClient.setHttpMethod("POST");
            AsyncTask<Void, Void, String> execute = new GetUser.ExecuteNetworkOperation(apiAuthenticationClient, "user_profile", context);
            execute.execute();
        } catch (Exception ex) {
        }
    }
    public GetUser(String token, Context context){
        this.context = context;
        try {
            ApiAuthenticationClient apiAuthenticationClient = new ApiAuthenticationClient(
                    jwtToken
                    ,  token
            );
            apiAuthenticationClient.setHttpMethod("POST");
            AsyncTask<Void, Void, String> execute = new GetUser.ExecuteNetworkOperation(apiAuthenticationClient, "check_token", context);
            execute.execute();
        } catch (Exception ex) {
        }
    }
    public boolean getFailed(){ return failed;}

    public String getToken() {
        return token;
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
                if(isValidCredentials.substring(2,7).equals("token")){
                    failed = false;
                    String [] tokenArray = isValidCredentials.split(":");
                    tokenArray = tokenArray[1].split(",");
                    tokenArray[0] = tokenArray[0].substring(1,tokenArray[0].length()-1);
                    Log.d("Test", tokenArray[0]);
                    token = tokenArray[0];
                    Log.d("Test", token);
                    ApiAuthenticationClient apiAuthenticationClient2 = new ApiAuthenticationClient(
                            user_profile
                            , token
                    );
                    apiAuthenticationClient2.setHttpMethod("GET");
                    AsyncTask<Void, Void, String> execute2 = new GetUser.ExecuteNetworkOperation(apiAuthenticationClient2, "user_profile", context);
                    execute2.execute();

                }
                else if(type.equals("user_profile")) {
                    failed = false;
                    Log.d("What!", String.valueOf(failed));
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
                            , token
                    );
                    apiAuthenticationClient2.setHttpMethod("GET");
                    AsyncTask<Void, Void, String> execute2 = new GetUser.ExecuteNetworkOperation(apiAuthenticationClient2, "user", context);
                    execute2.execute();

                }
                else if(type.equals("user")){
                    failed = false;
                    Log.d("What!", String.valueOf(failed));

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
                    String fileContents = username + "\n" + password + "\n" + UserID+ "\n"+ token+"\n";
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
                }else if(type.equals("check_token")){

                    Log.d("Test", isValidCredentials);
                }
            }
        }
    }
}