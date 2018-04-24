package com.example.david.zume_android_app;

import android.content.Intent;
import android.os.Bundle;
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
/**
 * Created by David.
 *
 * This page of the app displays the Group's next session, and takes them to the session
 */
public class GroupActivity extends AppCompatActivity {

    private String next_session = "0";  //Next Session for the Group
    private String group_id = "";       //Id of the group
    private String groupName = "";      //Name of the group
    private String members = "";        //Names of members in the group
    private String token = "";          //Token for the user
    private String userID = "0";        //The User's Id
    private  long timeStamp;            //Time stamp of the token

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Button startSession = (Button)findViewById(R.id.startSession);
        Intent intent = getIntent();
        //Getting information from the intent.
        next_session = intent.getStringExtra("next_session");
        group_id = intent.getStringExtra("groupID");
        groupName= intent.getStringExtra("groupName");
        members = intent.getStringExtra("members");
        timeStamp = intent.getLongExtra("timeStamp", 0);
        //Updates the token since it could have been updated.
        FileInputStream fiss = null;
        try {
            fiss = openFileInput("credentials.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isrr = new InputStreamReader(fiss);
        BufferedReader bufferedReaderr = new BufferedReader(isrr);
        try {
            bufferedReaderr.readLine();
            bufferedReaderr.readLine();
            bufferedReaderr.readLine();
            token = bufferedReaderr.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        userID = intent.getStringExtra("user_id");
        //Takes the user to Session List
        if(Integer.valueOf(next_session)>10){
            startSession.setText("Review Sessions");
            startSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(GroupActivity.this, SessionList.class));
                }
            });
        }
        //Takes the user to the next session.
        else {
            startSession.setText("Start Session " + next_session);
            startSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Setting the bundle.
                    Bundle bundle = new Bundle();
                    bundle.putString("session_number", next_session);
                    bundle.putString("group_id", group_id);
                    bundle.putString("groupName", groupName);
                    Log.d("Members-Session", members);
                    bundle.putString("members", members);
                    bundle.putBoolean("Has_a_Group", true);
                    bundle.putString("user_id", userID);
                //Passes the username and password so it can be used in SessionListAdapter to check the token
                FileInputStream fis = null;
                String username = "", password = "";
                try {
                    fis = openFileInput("credentials.txt");
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
                    bundle.putString("token", token);
                    bundle.putLong("timeStamp", timeStamp);
                    bundle.putString("password", password);
                    bundle.putString("username", username);
                    Intent intent = new Intent(GroupActivity.this, Session.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
        //Takes the user to the dashboard.
        Button home = (Button)findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting the bundle.
                Bundle bundle = new Bundle();
                bundle.putString("session_number", next_session);
                bundle.putString("group_id", group_id);
                bundle.putString("token", token);
                bundle.putLong("timeStamp", timeStamp);

                Intent intent = new Intent(GroupActivity.this, DashboardActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        setScreen();

    }

    //Sets the screen for the current group and user.
    public void setScreen(){
        try{
            Intent intent = getIntent();

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

    //Gets the group's name.
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
