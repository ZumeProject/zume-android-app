package com.example.david.zume_android_app;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DashboardActivity extends AppCompatActivity {


    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String[]> listItems=new ArrayList<String[]>();


    private String resultFromAPI = "";
    private String baseUrl = "";
    private String username = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button viewProfile = (Button)findViewById(R.id.viewProfile);

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                String username = intent.getStringExtra("username");
                String password = intent.getStringExtra("password");

                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("password", password);

                intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        Button notifications = (Button)findViewById(R.id.notifications);

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, Notifications.class));
            }
        });

        Button logOut = (Button)findViewById(R.id.logOut);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            }
        });

        Button downloads = (Button)findViewById(R.id.downloads);

        downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, Downloads.class));
            }
        });
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
            Log.d("Test", bufferedReader.readLine());
            Log.d("Test", bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            resultFromAPI = bufferedReader.readLine();
            Log.d("Test", resultFromAPI);
            Log.d("Test", bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Test", "Passing saved data");
        setGroupList();

        /*Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        Log.d("Test", username);
        Log.d("Test", password);

        baseUrl = "http://zume.hsutx.edu/wp-json/zume/v1/android/user_profile/1";
        Log.d("Test", "Made it to the new code");
        Boolean failed = false;
        FileInputStream fis= null;
        if(username.equals(null) && password.equals(null)){
            try {
                fis = openFileInput("UserProfile.txt");
                Log.d("Test", "Opened the file");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
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
            try {
                resultFromAPI = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("Test", "Passing saved data");
            setGroupList();
        }
        else {
            try {
                fis = openFileInput("UserProfile.txt");
                Log.d("Test", "Opened the file");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                failed = true;
            }
            if (failed) {
                try {
                    ApiAuthenticationClient apiAuthenticationClient =
                            new ApiAuthenticationClient(
                                    baseUrl
                                    , username
                                    , password
                            );

                    AsyncTask<Void, Void, String> execute = new DashboardActivity.ExecuteNetworkOperation(apiAuthenticationClient);
                    execute.execute();
                } catch (Exception ex) {
                    Log.d("Test", "Error getting dashboard data.");
                }
            } else {
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
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
                if (user.equals(username) && pass.equals(password)) {
                    try {
                        resultFromAPI = bufferedReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("Test", "Passing saved data");
                    setGroupList();
                } else {
                    try {

                        ApiAuthenticationClient apiAuthenticationClient =
                                new ApiAuthenticationClient(
                                        baseUrl
                                        , username
                                        , password
                                );

                        AsyncTask<Void, Void, String> execute = new DashboardActivity.ExecuteNetworkOperation(apiAuthenticationClient);
                        execute.execute();
                    } catch (Exception ex) {
                        Log.d("Test", "Error getting dashboard data.");
                    }
                }
            }
        }*/

    }

    /**
     * This subclass handles the network operations in a new thread.
     * It starts the progress bar, makes the API call, and ends the progress bar.
     */
    /*
    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ApiAuthenticationClient apiAuthenticationClient;
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
                String filename = "UserProfile.txt";
                String fileContents = username+"\n"+password+"\n"+resultFromAPI+"\n";
                FileOutputStream outputStream;

                try {
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(fileContents.getBytes());
                    outputStream.close();
                    Log.d("Test", "Made the file");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setGroupList();
            }
            // Login Failure
            else {
                Toast.makeText(getApplicationContext(), "Error opening dashboard: Invalid Credentials", Toast.LENGTH_LONG).show();
            }
        }
    }
*/
    /**
     * Open a new activity window.
     */
    private void setGroupList() {
        try{
            JSONObject reader = new JSONObject(resultFromAPI);

            JSONArray first = reader.getJSONArray("first_name");
            JSONArray last = reader.getJSONArray("last_name");
            JSONArray nickname = reader.getJSONArray("nickname");
            //JSONArray lastActive = reader.getJSONArray("zume_last_active");

            TextView name = (TextView)findViewById(R.id.name);
            if(!first.get(0).equals("") || !last.get(0).equals("")){
                name.setText(first.get(0) + " " + last.get(0));
            }
            else{
                name.setText(nickname.get(0).toString());
            }

            JSONArray allFields = reader.names();
            ArrayList<String[]> groups = new ArrayList<String[]>();
            for(int i=0; i<allFields.length(); i++){
                if(allFields.get(i).toString().contains("zume_group_")){
                    String[] thisGroup = new String[2];
                    thisGroup[0] = allFields.get(i).toString();
                    Log.d("Group ID", thisGroup[0]);
                    JSONArray groupName = reader.getJSONArray(thisGroup[0]);
                    thisGroup[1] = getGroupName(groupName.get(0).toString());
                    //listItems.add(getGroupName(thisGroup[1]));
                    Log.d("Group Name", thisGroup[1]);
                    listItems.add(thisGroup);
                    groups.add(thisGroup);
                }
            }

            GroupListAdapter adapter = new GroupListAdapter(listItems, this, getIntent());
            ListView listView = (ListView)findViewById(R.id.listView);
            listView.setAdapter(adapter);
            Log.d("Test", "Set listView adapter");
/*
            adapter=new ArrayAdapter<TextView>(this, android.R.layout.simple_list_item_1, listItems);
            for(String[] group: groups){
                TextView clickableGroup = new TextView(this);
                clickableGroup.setText(group[1]);
                clickableGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        String username = intent.getStringExtra("username");
                        String password = intent.getStringExtra("password");

                        Bundle bundle = new Bundle();
                        bundle.putString("username", username);
                        bundle.putString("password", password);

                        intent = new Intent(DashboardActivity.this, GroupActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                listItems.add(clickableGroup);
                adapter.notifyDataSetChanged();

            }
*/

        }
        catch(Exception e) {
            Log.d("Test Error - listView", e.getMessage());
        }

    }

/*
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
*/

    public String getGroupName(String data){
        String groupName = "";
        String pattern = "group_name";
        boolean err = false;

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(data);
        if(m.find()){
            Log.d("Test", "Found the pattern in "+data);
            String nameLength = "";
            int i=4;
            boolean found = false;
            while(!found) {
                String thisChar = ""+data.charAt(m.start() + pattern.length() + i);
                if(!thisChar.equals(":")){
                    nameLength = nameLength+thisChar;
                    i++;
                }
                else{
                    found = true;
                }
            }

            int nameL = 0;
            if(!nameLength.equals("")){
                try{
                    nameL = new Integer(nameLength).intValue();
                }
                catch(Exception e){
                    err = true;
                    Log.d("Test", "Length of group name not found.");
                }
            }

            if(!err){
                Log.d("Test", "Name Length is "+nameL);
                for(int j=0; j<nameL; j++){
                    groupName = groupName+data.charAt(m.start() + pattern.length() + i + 2 + j);
                }
            }
        }

        return groupName;
    }
}
