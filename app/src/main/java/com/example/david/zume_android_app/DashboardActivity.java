package com.example.david.zume_android_app;

import android.content.Intent;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
                int userID = intent.getIntExtra("user_id", 0);

                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("password", password);
                bundle.putInt("user_id", userID);

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
            fis = openFileInput("user_profile.txt");
            Log.d("Test", "Opened the file");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        /*try {
            Log.d("Test", bufferedReader.readLine());
            Log.d("Test", bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            resultFromAPI = bufferedReader.readLine();
            Log.d("Test", resultFromAPI);
            //Log.d("Test", bufferedReader.readLine());
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
                    String[] thisGroup = new String[3];
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
                    Log.d("Session" , String.valueOf(sessionNum));
                    thisGroup[2] = String.valueOf(sessionNum);
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

/*
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
*/

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
}
