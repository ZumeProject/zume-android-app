package com.example.david.zume_android_app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class SessionList extends Activity {

    Button[] btn;
    LinearLayout linear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_list);

        String sessionJSON = null;
        FileInputStream fis= null;
        try {
            fis = openFileInput("session_data.txt");
            Log.d("Test", "Opened the file");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);

        try {
            sessionJSON = bufferedReader.readLine();
            Log.d("Test", sessionJSON);
            //Log.d("Test", bufferedReader.readLine());
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
        for (int i = 0; i < btn.length; i++) {
            btn[i] = new Button(this);
            LinearLayout.LayoutParams buttonparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT,1.0f);
            btn[i].setLayoutParams(buttonparam);
            //btn[i].setWidth(match_parent);
            btn[i].setTag(i);
            String name = "Start Session "+(i+1);
            btn[i].setText(name);
            btn[i].setOnClickListener(btnClicked);
            linear.addView(btn[i]);
        }

        Button home = (Button)findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                String username = intent.getStringExtra("username");
                //String password = intent.getStringExtra("password");
                String token = intent.getStringExtra("token");

                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("token", token);
                //bundle.putString("password", password);

                intent = new Intent(SessionList.this, DashboardActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    View.OnClickListener btnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            Intent intent = getIntent();
            String username = intent.getStringExtra("username");
            String token = intent.getStringExtra("token");
            int sessionNumber = (int) v.getTag();
            sessionNumber += 1;
            bundle.putString("session_number", String.valueOf(sessionNumber));
            bundle.putBoolean("Has_a_Group", false);
            bundle.putString("username", username);
            bundle.putString("token", token);
            intent = new Intent(SessionList.this, Session.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

}
