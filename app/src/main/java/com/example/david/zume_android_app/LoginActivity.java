package com.example.david.zume_android_app;

import android.app.Dialog;
import android.app.ProgressDialog;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

public class LoginActivity extends AppCompatActivity {
    private Button button_login_login;
    private EditText editText_login_username;
    private EditText editText_login_password;
    private String username;
    private String password;
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
                if (failed && isNetworkAvailable()) {
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
                        Log.d("Test", "Passing saved data");
                        if(isNetworkAvailable()){
                            makeApiCall(getApplicationContext());
                        }
                        goToDashboardActivity();
                    } else {
                        if(isNetworkAvailable()) {
                            makeApiCall(getApplicationContext());
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
        if(isNetworkAvailable() && !(new File(getApplicationContext().getFilesDir()+"/pdfs_downloaded.txt").exists())) {
            AsyncTask<Void, String, String> download = new DownloadFileAsync().execute();
        }

        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        bundle.putString("baseUrl", baseUrlUserProfile);

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
                SessionPostHandler pendingPosts = new SessionPostHandler(cont, isNetworkAvailable());
                Log.d("What!", String.valueOf(auth.getFailed()));
                if(!auth.getFailed()){
                    goToDashboardActivity();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                    Log.d("Testing" , String.valueOf(auth.getFailed()));
                }
            }
        }, 500);

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

                            URL url = new URL(thisUrl[0]);
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