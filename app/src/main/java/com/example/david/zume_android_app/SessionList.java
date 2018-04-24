package com.example.david.zume_android_app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * Created by David on 4/10/2018.
 *
 * This java file lets a user view sessions if they don't have a group.
 */
public class SessionList extends Activity {

    Button[] btn;
    LinearLayout linear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_list);

        String sessionJSON = null;
        FileInputStream fis= null;
        //reads the session data
        try {
            fis = openFileInput("session_data.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);

        try {
            sessionJSON = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject reader = null;
        try {
            reader = new JSONObject(sessionJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray sessionData = null;
        try {
            sessionData = reader.getJSONArray("course");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int length = sessionData.length();
        btn = new Button[length];
        linear = (LinearLayout) findViewById(R.id.linear);
        //Creates list of buttons to go to any session
        for (int i = 0; i < btn.length; i++) {
            btn[i] = new Button(this);
            LinearLayout.LayoutParams buttonparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT,1.0f);
            btn[i].setLayoutParams(buttonparam);
            btn[i].setTag(i);
            String name = "Start Session "+(i+1);
            btn[i].setText(name);
            btn[i].setOnClickListener(btnClicked);
            linear.addView(btn[i]);
        }

        Button home = (Button)findViewById(R.id.home);
        //Takes the user back to the Dashboard
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                String token = intent.getStringExtra("token");
                long timeStamp = intent.getLongExtra("timeStamp", 0);

                Bundle bundle = new Bundle();
                bundle.putString("token", token);
                bundle.putLong("timeStamp", timeStamp);

                intent = new Intent(SessionList.this, DashboardActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    View.OnClickListener btnClicked = new View.OnClickListener() {
        @Override
        //Takes the user to the session.
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            Intent intent = getIntent();
            String token = intent.getStringExtra("token");
            long timeStamp = intent.getLongExtra("timeStamp", 0);
            int sessionNumber = (int) v.getTag();
            sessionNumber += 1;
            bundle.putString("session_number", String.valueOf(sessionNumber));
            bundle.putBoolean("Has_a_Group", false);
            bundle.putString("token", token);
            bundle.putLong("timeStamp", timeStamp);
            intent = new Intent(SessionList.this, Session.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

}
