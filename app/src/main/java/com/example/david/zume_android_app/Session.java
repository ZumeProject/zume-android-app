package com.example.david.zume_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Session extends AppCompatActivity {

    private String resultFromAPI;
    private int sessionNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Button Home = (Button) findViewById(R.id.Home);
        Intent intent = getIntent();
        sessionNumber= Integer.parseInt(intent.getStringExtra("session_number"));
        Log.d("Test" , String.valueOf(sessionNumber));

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
            resultFromAPI = bufferedReader.readLine();
            Log.d("Test", resultFromAPI);
            //Log.d("Test", bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Test", "Passing saved data");
        sessionParser();

        Home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Session.this, DashboardActivity.class));
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    private void sessionParser() {
        JSONObject reader = null;
        try {
            reader = new JSONObject(resultFromAPI);
            Log.d("Test" , resultFromAPI);
            JSONArray sessionData = reader.getJSONArray("course");
            JSONObject session = sessionData.getJSONObject(sessionNumber-1);
            Log.d("Test" , String.valueOf(sessionData));
            Log.d("Test" , String.valueOf(session));
            JSONArray sessionSteps = session.getJSONArray("steps");
            Log.d("Test" , String.valueOf(sessionSteps));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
