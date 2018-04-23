package com.example.david.zume_android_app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class LoginActivity extends AppCompatActivity {
    private Button button_login_login;
    private EditText editText_login_username;
    private EditText editText_login_password;
    private String username;
    private String password;
    private String token;
    private Integer user_id;
    private String baseUrlUserProfile = "http://zume.hsutx.edu/wp-json/zume/v1/android/user_profile/1";
    private GetUser auth;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private Button startBtn;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*
         * Bypass button to help with debugging. So I don't have to type in a login
         */
//        Button bypass = (Button) findViewById(R.id.bypass_button);
//        bypass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                username = "daoffner";
//                password = "Astra2008";
//                goToDashboardActivity();
//            }
//        });

        Button register = (Button) findViewById(R.id.button_login_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://zume.hsutx.edu/wp-login.php?action=register"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
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
                // switched && to ||
                if (failed && isNetworkAvailable()) {
                    makeApiCall(getApplicationContext());
                }
                //Checks to see if it is the same user loging in.
                //If it is it will go to the next page.
                //Otherwise it will create new files for the new user.
                else if(failed && !isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(), "Can't log in. No Internet connection", Toast.LENGTH_LONG).show();
                }
                else{
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    String user = null, pass = null, oldToken = null;
                    try {
                        user = bufferedReader.readLine();
                        pass = bufferedReader.readLine();
                        bufferedReader.readLine();
                        oldToken = bufferedReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("Test", user);
                    Log.d("Test", pass);
                    //Checking if its the same user loging in.
                    if (user.equals(username) && pass.equals(password)) {
                        Log.d("Test", "Passing saved data");
                        if(isNetworkAvailable()){
                            //get token from file.
                            //get new info.
                            makeApiCall(getApplicationContext());
                            // USe this to reuse token auth = new GetUser(oldToken,getApplicationContext() );
                            //Go to get User to handle what happens.
                            //check token
                        }
                        else{
                            //get token from file.
                            token = oldToken;
                            goToDashboardActivity();
                        }
                    } else {
                        if(isNetworkAvailable()) {
                            makeApiCall(getApplicationContext());
                        }else{
                            Toast.makeText(getApplicationContext(), "Can't log in. No Internet connection", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading files..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    /**
     * Open the Dashboard window.
     */
    private void goToDashboardActivity() {
        //if(isNetworkAvailable() && !(new File(getApplicationContext().getFilesDir()+"/pdfs_downloaded.txt").exists())) {
        //Fix logic after the &&
        // If network is available and the pdfs have not already been downloaded, download them (note, due to the API not returning URLs for the PDFs, when that is fixed, we will need to comment out this code so that the pdfs_downloaded.txt may be overwritten and the pdfs saved)
        if(isNetworkAvailable() && !(new File(getApplicationContext().getFilesDir()+"/pdfs_downloaded.txt").exists())) {
            AsyncTask<Void, String, String> download = new DownloadFileAsync().execute();
        }

        Date complete = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("US/Central"));
        String createdDate = format.format(complete);
        createdDate = createdDate.replace(" ", "%20");

        LoggingPostHandler handler = new LoggingPostHandler(getApplicationContext(), token, createdDate, "login", "logged_id", String.valueOf(user_id), isNetworkAvailable());


        Bundle bundle = new Bundle();
        //bundle.putString("username", username);
        //bundle.putString("password", password);
        //bundle.putString("baseUrl", baseUrlUserProfile);
        bundle.putString("token", token);

        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Makes a call to get info from the user and user_profile endpoints. Also updates session information if there are pending posts.
     * @param context
     */
    private void makeApiCall(Context context) {
        final Context cont = context;
        auth = new GetUser(username, password, context);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("Login - Internet", String.valueOf(isNetworkAvailable()));
                Log.d("What!", String.valueOf(auth.getFailed()));
                if(!auth.getFailed()) {
                    token = auth.getToken();
                    String userID = auth.getUserID();
                    SessionPostHandler pendingPosts = new SessionPostHandler(cont, token, isNetworkAvailable(), userID);
                    LoggingPostHandler pendingLogs = new LoggingPostHandler(cont, token, isNetworkAvailable(), userID);
                }

            }
        }, 2000);
        auth = new GetUser(username, password, context);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("Login - Internet", String.valueOf(isNetworkAvailable()));
                Log.d("What!", String.valueOf(auth.getFailed()));
                if(!auth.getFailed()){
                    token = auth.getToken();
                    goToDashboardActivity();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                    Log.d("Testing" , String.valueOf(auth.getFailed()));
                }
            }
        }, 2000);

    }

    class DownloadFileAsync extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(Void... params) {

            if(isNetworkAvailable()) {
                try{
                    File pdfExists = new File(getFilesDir(), "pdfs_downloaded.txt");
                    OutputStream output = new FileOutputStream(pdfExists);
                    DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date today = new Date();
                    output.write(format.format(today).getBytes());
                    output.close();
                }
                catch(Exception e){
                    e.printStackTrace();
                }


                PdfDownloader downloader = new PdfDownloader(getApplicationContext());
                ArrayList<String[]> urls = downloader.execute();
                int count;
                if (urls != null) {
                    for (String[] thisUrl : urls) {
                        try {
                            String urlPath = thisUrl[0];
                            if(!urlPath.contains("http://") && !urlPath.contains("https://")){
                                urlPath = "http://"+urlPath;
                            }
                            Log.d("PDF URL", urlPath);
                            URL url = new URL(urlPath);
                            URLConnection conexion = url.openConnection();
                            conexion.connect();

                            int lenghtOfFile = conexion.getContentLength();
                            //Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                            InputStream input = new BufferedInputStream(url.openStream());
                            File file = new File(getFilesDir(), thisUrl[1].replace(" ", "_").replace("/", "_"));
                            OutputStream output = new FileOutputStream(file);

                            byte data[] = new byte[1024];

                            long total = 0;

                            while ((count = input.read(data)) != -1) {
                                total += count;
                                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                                output.write(data, 0, count);
                            }

                            output.flush();
                            output.close();
                            input.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return null;

        }
        protected void onProgressUpdate(String... progress) {
            //Log.d("ANDRO_ASYNC",progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
    }

    /**
     * Check to see if we can connect to the network.
     * @return true if we can, false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //Log.d("Internet", "activeNetworkInfo: "+new Boolean(activeNetworkInfo != null).toString());
        //Log.d("Internet", "connectedOrConnecting: "+String.valueOf(activeNetworkInfo.isConnectedOrConnecting()));
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}