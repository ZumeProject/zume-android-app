package com.example.david.zume_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupActivity extends AppCompatActivity {

    private String resultFromAPI = "";
    private String next_session = "0";
    private String group_id = "";
    //private String username = "";
    private String groupName = "";
    private String members = "";
    //private String password = "";
    private String token = "";
    private String userID = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        Button startSession = (Button)findViewById(R.id.startSession);
        Intent intent = getIntent();
        next_session = intent.getStringExtra("next_session");
        group_id = intent.getStringExtra("groupID");
        //username = intent.getStringExtra("username");
        //password = intent.getStringExtra("password");
        groupName= intent.getStringExtra("groupName");
        members = intent.getStringExtra("members");
        token = intent.getStringExtra("token");
        userID = intent.getStringExtra("user_id");
        if(Integer.valueOf(next_session)>10){
            startSession.setText("Review Sessions");
            startSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(GroupActivity.this, SessionList.class));
                }
            });
        }
        else {
            startSession.setText("Start Session " + next_session);
            startSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("session_number", next_session);
                    bundle.putString("group_id", group_id);
                    //bundle.putString("username", username);
                    //bundle.putString("password", password);
                    bundle.putString("groupName", groupName);
                    Log.d("Members-Session", members);
                    bundle.putString("members", members);
                    bundle.putBoolean("Has_a_Group", true);
                    bundle.putString("user_id", userID);

//                FileInputStream fis = null;
//
//                try {
//                    fis = openFileInput("credentials.txt");
//                    Log.d("Test", "Opened the file");
//                    InputStreamReader isr = new InputStreamReader(fis);
//                    BufferedReader bufferedReader = new BufferedReader(isr);
//                    try {
//                        bufferedReader.readLine();
//                        bufferedReader.readLine();
//                        bufferedReader.readLine();
//                        token = bufferedReader.readLine();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    Log.d("Test", "Failed");
//                }
                    bundle.putString("token", token);
                    Intent intent = new Intent(GroupActivity.this, Session.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        Button home = (Button)findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("session_number", next_session);
                bundle.putString("group_id", group_id);
                //bundle.putString("username", username);
                //bundle.putString("password", password);
                bundle.putString("token", token);
//                FileInputStream fis = null;
//                String token = null;
//                try {
//                    fis = openFileInput("credentials.txt");
//                    Log.d("Test", "Opened the file");
//                    InputStreamReader isr = new InputStreamReader(fis);
//                    BufferedReader bufferedReader = new BufferedReader(isr);
//                    try {
//                        bufferedReader.readLine();
//                        bufferedReader.readLine();
//                        bufferedReader.readLine();
//                        token = bufferedReader.readLine();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    Log.d("Test", "Failed");
//                }
//                bundle.putString("token", token);

                Intent intent = new Intent(GroupActivity.this, DashboardActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Test", "Passing saved data");
        setScreen();

    }

    public void setScreen(){
        try{
            Intent intent = getIntent();

            //String username = intent.getStringExtra("username");
            //String password = intent.getStringExtra("password");
            String groupID = intent.getStringExtra("groupID");
            String groupName = intent.getStringExtra("groupName");
            String nextSession = intent.getStringExtra("next_session");

            TextView groupNameView = (TextView)findViewById(R.id.groupName);
            groupNameView.setText(groupName);
        }
        catch(Exception e){
            Log.d("Test", "Group not found in bundle...");
        }
    }

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
