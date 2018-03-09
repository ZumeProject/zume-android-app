/*package com.example.david.zume_android_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;


/**
 * A login screen that offers login via email/password.
 */
/*
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
/*
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
/*
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
/*
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
/*
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    try {
                        attemptLogin();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptLogin();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //
    private static final String HOST_NAME = "host";

    private int basicAuthDemo(final String username, final String password) throws IOException {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("YOUR WEBSITE HERE");

// Add authorization header
        httpGet.addHeader(BasicScheme.authenticate( new UsernamePasswordCredentials("user", "password"), "UTF-8", false));

// Set up the header types needed to properly transfer JSON
        httpGet.setHeader("Content-Type", "application/json");
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(ParseJSON.class.toString(), "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } }
        });

        /*HttpURLConnection c = (HttpURLConnection) new URL("zume.hsutx.edu/wp-json").openConnection();
        c.setUseCaches(false);
        String loginInfo = username+":"+password;
        c.setRequestProperty("Authorization", "basic " +
                Base64.encode(loginInfo.getBytes(), Base64.NO_WRAP));
        System.out.println("Debug");
        return 1;*/
/*
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
/*
    private void attemptLogin() throws IOException {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);
            int UserID = basicAuthDemo(email, password);
            if (UserID >= 0){
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            }
            if (email.equals("test@gmail.com")) {
                if (password.equals("testing")) {
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                } else {
                    mPasswordView.setError("The password is incorrect");
                    showProgress(false);
                }
            } else {
                mEmailView.setError(getString(R.string.error_invalid_email));
                showProgress(false);
            }
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
/*
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.david.zume_android_app/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.david.zume_android_app/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
/*
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
*/

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private Button button_login_login;
    private EditText editText_login_username;
    private EditText editText_login_password;
    private String username;
    private String password;
    private String baseUrl;
    private String UserID;
    private String isValidCredentials;
    private String endpoint = "false";
    private String endpoint2 = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO: Replace this with your own IP address or URL.
        //baseUrl = "http://zume.hsutx.edu/wp-json/zume/v1/android/user_profile/1";

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

                try {
                    fis = openFileInput("credentials.txt");
                    Log.d("Test", "Opened the file");

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("Test", "Failed");
                    failed = true;
                }
                if (failed) {
                    try {
                        endpoint = "user_profile";
                        Log.d("Test", "Making API call");
                        Log.d("Test", username);
                        Log.d("Test", password);
                        baseUrl = "http://zume.hsutx.edu/wp-json/zume/v1/android/user_profile/1";
                        ApiAuthenticationClient apiAuthenticationClient =
                                new ApiAuthenticationClient(
                                        baseUrl
                                        , username
                                        , password
                                );

                        AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(apiAuthenticationClient);
                        execute.execute();
                        TimeUnit.SECONDS.sleep(5);
                        endpoint = "false";
                        endpoint2 = "user";
                        baseUrl = "http://zume.hsutx.edu/wp-json/zume/v1/android/user/1";
                        Log.d("Test", "Making 2nd API call");
                        Log.d("Test", username);
                        Log.d("Test", password);
                        ApiAuthenticationClient apiAuthenticationClient2 = new ApiAuthenticationClient(
                                baseUrl
                                , username
                                , password
                        );
                        AsyncTask<Void, Void, String> execute2 = new ExecuteNetworkOperation(apiAuthenticationClient2);
                        execute2.execute();
                    } catch (Exception ex) {
                        Log.d("Test", "Error getting dashboard data.");
                    }
                } else {
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

                    if (user.equals(username) && pass.equals(password)) {
                        try {
                            isValidCredentials = bufferedReader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("Test", "Passing saved data");
                        goToDashboardActivity();
                    } else {
                        try {
                            endpoint = "user_profile";
                            ApiAuthenticationClient apiAuthenticationClient = new ApiAuthenticationClient(
                                    baseUrl
                                    , username
                                    , password
                            );
                            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(apiAuthenticationClient);
                            execute.execute();
                            TimeUnit.SECONDS.sleep(5);
                            endpoint = "false";
                            endpoint = "user";
                            baseUrl = "http://zume.hsutx.edu/wp-json/zume/v1/android/user/1";
                            ApiAuthenticationClient apiAuthenticationClient2 = new ApiAuthenticationClient(
                                    baseUrl
                                    , username
                                    , password
                            );
                            AsyncTask<Void, Void, String> execute2 = new ExecuteNetworkOperation(apiAuthenticationClient2);
                            execute2.execute();
                        } catch (Exception ex) {
                        }
                    }

                }
                /*
                endpoint = "login";
                baseUrl = "http://zume.hsutx.edu/wp-json/zume/v1/android/user_profile/1";
                ApiAuthenticationClient apiAuthenticationClient6 = new ApiAuthenticationClient(
                        baseUrl
                        , username
                        , password
                );
                AsyncTask<Void, Void, String> execute6 = new ExecuteNetworkOperation(apiAuthenticationClient6);
                execute6.execute();*/
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                goToDashboardActivity();
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
                Log.d("Test", isValidCredentials);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Hide the progress bar.
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            // Login Success
            Log.d("Test", isValidCredentials);
            if (isValidCredentials != null && !isValidCredentials.equals("")) {
                Log.d("Test", "logged in");
                Boolean failed = false;
                FileInputStream fis = null;

                if(endpoint.equals("user_profile")) {
                    Log.d("Test", "Made it to the creation of the file");
                    /*try {
                        fis = openFileInput("credentials.txt");
                        Log.d("Test", "Opened the file");

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d("Test", "Failed");
                        failed = true;
                    }*/
                    String filename = "credentials.txt";
                    try {
                        JSONObject reader = new JSONObject(isValidCredentials);
                        JSONArray id = reader.getJSONArray("id");
                        UserID = id.get(0).toString();
                        Log.d("Test", UserID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Test", username);
                    Log.d("Test", password);
                    Log.d("Test", UserID);

                    String fileContents = username + "\n" + password + "\n" + UserID+ "\n";
                    FileOutputStream outputStream;

                    try {
                        File x = new File(filename);
                        Log.d("Test", String.valueOf(x));
                        x.delete();
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                        Log.d("Test", "Made the credentials file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    filename = "user_profile.txt";
                    fileContents = isValidCredentials + "\n";
                    Log.d("Test", "Made first call");
                    try {
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                        Log.d("Test", "Made the user_profile file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(endpoint.equals("user")){
                    String filename = "user.txt";
                    String fileContents = isValidCredentials + "\n";
                    Log.d("Test", "Made second call");
                    FileOutputStream outputStream;

                    try {
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                        Log.d("Test", "Made the user file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(endpoint.equals("login")){
                    Log.d("Test", "Going to dashboard");
                    goToDashboardActivity();
                }
                    /*
                    try {
                        fis = openFileInput("user_profile.txt");
                        Log.d("Test", "Opened the file");

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d("Test", "Failed");
                        failed = true;
                    }*/
                    /*
                }else {
                    try {
                        fis = openFileInput("user_profile.txt");
                        Log.d("Test", "Opened the file");

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d("Test", "Failed");
                        failed = true;
                    }
                    if (failed) {
                        String filename = "user_profile.txt";
                        String fileContents = isValidCredentials + "\n";
                        Log.d("Test", "Made first call");
                        FileOutputStream outputStream;

                        try {
                            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                            outputStream.write(fileContents.getBytes());
                            outputStream.close();
                            Log.d("Test", "Made the file");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
            }

                try {
                    fis = openFileInput("credentials.txt");
                    Log.d("Test", "Opened the file");

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("Test", "Failed");
                    failed = true;
                }
                if(failed) {
                    String filename = "credentials.txt";
                    String fileContents = username + "\n" + password + "\n";
                    Log.d("Test", "Made first call");
                    FileOutputStream outputStream;

                    try {
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(fileContents.getBytes());
                        outputStream.close();
                        Log.d("Test", "Made the file");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        fis = openFileInput("user_profile.txt");
                        Log.d("Test", "Opened the file");

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d("Test", "Failed");
                        failed = true;
                    }
                    if (failed) {
                        String filename = "user_profile.txt";
                        String fileContents = isValidCredentials + "\n";
                        Log.d("Test", "Made first call");
                        FileOutputStream outputStream;

                        try {
                            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                            outputStream.write(fileContents.getBytes());
                            outputStream.close();
                            Log.d("Test", "Made the file");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("Test", "Made second call");
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader bufferedReader = new BufferedReader(isr);
                        String user = null, pass = null, info = null;
                        try {
                            user = bufferedReader.readLine();
                            pass = bufferedReader.readLine();
                            info = bufferedReader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String filename = "user_profile.txt";
                        String fileContents = user + "\n" + pass + "\n" + info + "\n" + isValidCredentials + "\n";
                        FileOutputStream outputStream;

                        try {
                            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                            outputStream.write(fileContents.getBytes());
                            outputStream.close();
                            Log.d("Test", "Made the file");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("Test", "Made final file");
                        goToDashboardActivity();
                    }
                }*/
            } else {
                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Open a new activity window.
     */
    private void goToDashboardActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        bundle.putString("baseUrl", baseUrl);

        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}



