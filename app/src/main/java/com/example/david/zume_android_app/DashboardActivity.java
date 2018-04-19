package com.example.david.zume_android_app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import java.nio.Buffer;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {


    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String[]> listItems=new ArrayList<String[]>();


    private String resultFromAPI = "";
    private String userString = "";
    //private String baseUrl = "";
    private String username = "";
    //private String password = "";
    private Integer user_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FileInputStream fi= null;
        try {
            fi = openFileInput("user.txt");
            Log.d("Test", "Opened the file");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader ir = new InputStreamReader(fi);
        BufferedReader br = new BufferedReader(ir);
        /*try {
            Log.d("Test", bufferedReader.readLine());
            Log.d("Test", bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            userString = br.readLine();
            Log.d("User", userString);
            JSONObject user = new JSONObject(userString);
            this.user_id = new Integer(user.get("user_id").toString());
            //Log.d("Test", bufferedReader.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                    //String password = intent.getStringExtra("password");

                    String token = intent.getStringExtra("token");
                    FileInputStream fis = null;
                    try {
                        fis = openFileInput("credentials.txt");
                        Log.d("Test", "Opened the file");
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader bufferedReader = new BufferedReader(isr);
                        try {
                            bufferedReader.readLine();
                            bufferedReader.readLine();
                            user_id = Integer.parseInt(bufferedReader.readLine());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d("Test", "Failed");
                    }
                    //Log.d("Username", username);

                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
                    //bundle.putString("password", password);
                    bundle.putString("token", token);
                    bundle.putInt("user_id", user_id);

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

        Button viewSessions = (Button)findViewById(R.id.view_sessions);

        viewSessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, SessionList.class));
            }
        });
        FileInputStream fis= null;
        try {
            fis = openFileInput("user_profile.txt");
            Log.d("Test", "Opened the file");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);

        try {
            resultFromAPI = bufferedReader.readLine();
            Log.d("Test", resultFromAPI);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Test", "Passing saved data");
        setScreen();

    }


    /**
     * Set the screen.
     */
    private void setScreen() {
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

            // Get information about each of the user's groups.
            JSONArray allFields = reader.names();
            ArrayList<String[]> groups = new ArrayList<String[]>();
            for(int i=0; i<allFields.length(); i++){
                if(allFields.get(i).toString().contains("zume_group_")){
                    String[] thisGroup = new String[4];
                    // Set group ID
                    thisGroup[0] = allFields.get(i).toString();
                    Log.d("Group ID", thisGroup[0]);
                    JSONArray groupName = reader.getJSONArray(thisGroup[0]);
                    // Set group name
                    thisGroup[1] = getGroupName(groupName.get(0).toString());
                    // Get sessino information
                    JSONArray sessionReader = reader.getJSONArray(thisGroup[0]);
                    Log.d("Test", String.valueOf(sessionReader));
                    JSONObject groupInfo = sessionReader.getJSONObject(0);
                    int sessionNum = groupInfo.getInt("next_session");
                    String members = groupInfo.getString("members");
                    Log.d("Members", members);
                    Log.d("Session" , String.valueOf(sessionNum));
                    // Make sure we get the correct local version of the session number.
                    thisGroup[2] = getSession(String.valueOf(sessionNum), thisGroup[0]);
                    thisGroup[3] = members;
                    Log.d("Group Name", thisGroup[1]);
                    listItems.add(thisGroup);
                    groups.add(thisGroup);
                }
            }

            GroupListAdapter adapter = new GroupListAdapter(listItems, this, getIntent());
            ListView listView = (ListView)findViewById(R.id.listView);
            listView.setAdapter(adapter);
            Log.d("Test", "Set listView adapter");

        }
        catch(Exception e) {
            Log.d("Test Error - listView", e.getMessage());
        }

    }

    /**
     * Get the group name from the group JSON object.
     * @param data JSON String
     * @return String group name
     */
    public String getGroupName(String data){
        String groupName = "";
        try{
            JSONObject group = new JSONObject(data);
            groupName = group.getString("group_name");
        }catch(JSONException e){
            e.printStackTrace();
        }
        return groupName;
    }

    /**
     * Check to see if we can connect to the network.
     * @return true if we can, false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
  //      Log.d("Internet", "activeNetworkInfo: "+new Boolean(activeNetworkInfo != null).toString());
//        Log.d("Internet", "connectedOrConnecting: "+new Boolean(activeNetworkInfo.isConnectedOrConnecting()).toString());
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * See if the session number stored locally is greater than the session number retrieved from the api - we may have pending session posts.
     * @param next_session
     * @param group_id
     * @return
     */
    public String getSession(String next_session, String group_id){
        try {
            File file = new File(getFilesDir(), group_id);
            if(file.exists() && file.isFile()){
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String session = br.readLine().replace("\n", "");
                if(Integer.valueOf(session)>Integer.valueOf(next_session)){
                    return session;
                }
                return next_session;
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(new String(next_session+"\n").getBytes());

            fos.flush();
            fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return next_session;
    }
}
