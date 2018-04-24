package com.example.david.zume_android_app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brandi Turner
 * @version 4/22/2018
 */

public class EditProfileActivity extends AppCompatActivity {

    private String resultFromUserProfile = ""; // JSON from user_profile.txt or user_profile endpoint
    private String resultFromUser = ""; // JSON from user.txt or user endpoint
    private  String token = "";
    private boolean old;
    String prevFirstName = ""; // The user's first name before attempting to update
    String prevLastName = ""; // The user's last name before attempting to update
    String prevEmail = ""; // The user's email before attempting to update
    String prevPhoneNumber = ""; // The user's phone number before attempting to update
    protected String isValidCredentials = "";
    String baseURL = "https://zume.sergeantservices.com/wp-json/zume/v1/android/user/1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // ZUME button to return to the dashboard
        Button home = (Button)findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                String token= intent.getStringExtra("token");
                int user_id = intent.getIntExtra("user_id", 0);
                long timeStamp = intent.getLongExtra("timeStamp", 0);
                Bundle bundle = new Bundle();
                bundle.putString("token", token);
                bundle.putLong("timeStamp", timeStamp);
                bundle.putInt("user_id", user_id);

                intent = new Intent(EditProfileActivity.this, DashboardActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // OnClick for save button
        Button update = (Button)findViewById(R.id.save);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Set credentials
                Intent intent = getIntent();
                String token = intent.getStringExtra("token");
                int userID = intent.getIntExtra("user_id", 0);

                // Get editTexts
                final EditText first =  (EditText) findViewById(R.id.firstName);
                final EditText last =  (EditText) findViewById(R.id.lastName);
                final EditText email =  (EditText) findViewById(R.id.email);
                final EditText phone = (EditText) findViewById(R.id.phoneNumber);

                try{
                    String first_name = first.getText().toString();
                    String last_name = last.getText().toString();
                    String email_address = email.getText().toString();
                    String phone_number = phone.getText().toString();
                    boolean validData = true;

                    // Make sure that the values entered for firstName, lastName, and email are not empty
                    if((first_name == null || first_name.equals(""))){
                        first_name = prevFirstName;
                    }
                    if((last_name == null || last_name.equals(""))){
                        last_name = prevLastName;
                    }
                    if((email_address == null || email_address.equals(""))){
                        email_address = prevEmail;
                    }

                    // See if any valid changes have been made so far
                    boolean changesMade = false;
                    //Log.d("PrevPhone", prevPhoneNumber);
                    if(!first_name.equals(prevFirstName)){
                        changesMade = true;
                    }
                    if(!last_name.equals(prevLastName)){
                        changesMade = true;
                    }
                    if(!email_address.equals(prevEmail)){
                        changesMade = true;
                    }
                    if(!phone_number.equals(prevPhoneNumber)){
                        changesMade = true;
                    }
                    // If no changes have been made, then we do not want to try to update the data
                    if(changesMade) {
                        // Make sure email is of valid format
                        Pattern validEmail = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = validEmail.matcher(email_address);
                        if(!matcher.find()){
                            // Email is not valid
                            Toast.makeText(getApplicationContext(), "Email is not valid", Toast.LENGTH_LONG).show();
                            validData = false;
                        }

                        // Make sure phone number is of valid format 000-000-0000
                        Pattern validPhone = Pattern.compile("^[0-9]{3}-[0-9]{3}-[0-9]{4}$", Pattern.CASE_INSENSITIVE);
                        Matcher matcherP = validPhone.matcher(phone_number);
                        if (!matcherP.find()) {
                            // Phone is not valid
                            Toast.makeText(getApplicationContext(), "Phone Number not valid. Example: 000-000-0000", Toast.LENGTH_LONG).show();
                            validData = false;
                        }

                        // If the data is valid, then update the user's information
                        if(validData && isNetworkAvailable()){
                            updateUser(userID, token, first_name, last_name, email_address, phone_number);

                        }
                    }

                }
                catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error updating profile", Toast.LENGTH_LONG).show();
                }
            }
        });

        Intent intent = getIntent();
        token = intent.getStringExtra("token");

        FileInputStream fis= null;
        try {
            fis = openFileInput("user_profile.txt");
        } catch (FileNotFoundException e) {
            // Couldn't find user_profile info, so get that from the API
            GetUser user = new GetUser(token, this);

            this.onCreate(savedInstanceState);
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);

        try {
            resultFromUserProfile = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fis = openFileInput("user.txt");
        } catch (FileNotFoundException e) {
            // Couldn't find the user information, so get that information from the API
            GetUser user = new GetUser(token, this);
            this.onCreate(savedInstanceState);
        }
        isr = new InputStreamReader(fis);
        bufferedReader = new BufferedReader(isr);

        try {
            resultFromUser = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Set up the display
        setEditProfileScreen();

    }



    /**
     * Set the variables for the EditProfileActivity screen
     */
    private void setEditProfileScreen() {
        try{
            // Get information from the user_profile
            JSONObject reader = new JSONObject(resultFromUserProfile);
            JSONArray first = reader.getJSONArray("first_name");
            JSONArray last = reader.getJSONArray("last_name");
            try {
                JSONArray phone = reader.getJSONArray("zume_phone_number");
                // Set phoneNumber
                EditText phoneNumber = (EditText)findViewById(R.id.phoneNumber);
                phoneNumber.setText(phone.get(0).toString());
                //Log.d("Phone", phone.get(0).toString());
                this.prevPhoneNumber = phone.get(0).toString();
            }
            catch(Exception o){
                // Set phoneNumber
                EditText phoneNumber = (EditText)findViewById(R.id.phoneNumber);
                phoneNumber.setText("");
                this.prevPhoneNumber = "";
                o.printStackTrace();
            }

            // Set firstName
            EditText firstName = (EditText)findViewById(R.id.firstName);
            firstName.setText(first.get(0).toString());
            this.prevFirstName = first.get(0).toString();
            // Set lastName
            EditText lastName = (EditText)findViewById(R.id.lastName);
            lastName.setText(last.get(0).toString());
            this.prevLastName = last.get(0).toString();

            // Get email from user
            reader = new JSONObject(resultFromUser);
            String email = reader.getString("user_email");
            EditText emailText = (EditText)findViewById(R.id.email);
            emailText.setText(email);
            this.prevEmail = email;

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the user's information
     * @param user_id
     * @param token
     * @param first_name
     * @param last_name
     * @param email
     * @param phone_number
     */
    public void updateUser(final int user_id, final String token, final String first_name, final String last_name, final String email, final String phone_number) {
        try {
            final String newtoken = token;
            //Gets info to check and update the token if needed
            FileInputStream fis = null;
            fis = openFileInput("credentials.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            long tokenTime = 0;
            String username = "", password = "";
            try {
                username = bufferedReader.readLine();
                password = bufferedReader.readLine();
                bufferedReader.readLine();
                bufferedReader.readLine();
                tokenTime = Long.parseLong(bufferedReader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
            TokenTimeStamp check = new TokenTimeStamp();
            old = check.getTimeDiff(tokenTime);
            //Gets a new token and does the POST
            if (old) {
                final GetUser getToken = new GetUser(username, password, getApplicationContext());
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ApiAuthenticationClient apiAuthenticationClient =
                                new ApiAuthenticationClient(
                                        baseURL
                                        , getToken.getToken()
                                );
                        // Set Http Method type to POST
                        apiAuthenticationClient.setHttpMethod("POST");
                        // Create a LinkedHashMap of parameters to send
                        LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
                        parameters.put("user_id", String.valueOf(user_id));
                        parameters.put("first_name", first_name);
                        parameters.put("last_name", last_name);
                        parameters.put("user_email", email);
                        parameters.put("user_phone", phone_number);
                        apiAuthenticationClient.setParameters(parameters);
                        // Execute the Network Operation
                        AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(apiAuthenticationClient, getApplicationContext());
                        execute.execute();
                        GetUser user = new GetUser(getToken.getToken(), getApplicationContext());
                    }
                }, 2000);
            }
            //Uses the old token.
            else {
                try{
                    ApiAuthenticationClient apiAuthenticationClient =
                            new ApiAuthenticationClient(
                                    baseURL
                                    , newtoken
                            );
                    // Set Http Method type to POST
                    apiAuthenticationClient.setHttpMethod("POST");
                    // Create a LinkedHashMap of parameters to send
                    LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
                    parameters.put("user_id", String.valueOf(user_id));
                    parameters.put("first_name", first_name);
                    parameters.put("last_name", last_name);
                    parameters.put("user_email", email);
                    parameters.put("user_phone", phone_number);
                    apiAuthenticationClient.setParameters(parameters);
                    // Execute the Network Operation
                    AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(apiAuthenticationClient, this);
                    execute.execute();
                }catch(Exception ex){
                    Log.d("Test", "Error getting dashboard data.");
                }
        }
        }catch(Exception ex){
            Log.d("Test", "Error getting dashboard data.");
        }
    }

    // Executes the API calls
    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ApiAuthenticationClient apiAuthenticationClient;
        private Context context;

        public ExecuteNetworkOperation(ApiAuthenticationClient apiAuthenticationClient, Context context) {
            this.context = context;
            this.apiAuthenticationClient = apiAuthenticationClient;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        // Execute API call
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
            //Log.d("API_Result", result);
            // Update the user_profile, credentials, and user text files after updating data
            // in database
            if(isNetworkAvailable() && !old) {
                GetUser getUser = new GetUser(token, context);

            }

            // Wait a couple of seconds for the user_profile and user text files to update before
            // returning to the ProfileActivity
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Gets the new token
                    FileInputStream fis = null;
                    try {
                        fis = openFileInput("credentials.txt");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    try {
                        bufferedReader.readLine();
                        bufferedReader.readLine();
                        bufferedReader.readLine();
                        token = bufferedReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Get all parameters from intent
                    Intent intent = getIntent();
                    int userID = intent.getIntExtra("user_id", 0);
                    long timeStamp = intent.getLongExtra("timeStamp",0);
                    // Save parameters to bundle
                    Bundle bundle = new Bundle();
                    bundle.putInt("user_id", userID);
                    bundle.putString("token", token);
                    bundle.putLong("timeStamp", timeStamp);
                    // Return to ProfileActivity
                    intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }, 2500);
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
