package com.example.david.zume_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Downloads extends AppCompatActivity {

    private String username = "";
    private String password = "";
    private String resultFromAPI = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        Button startSession = (Button)findViewById(R.id.startSession);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        Button Home = (Button) findViewById(R.id.Home);

        Home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("username", username);
                b.putString("password", password);
                startActivity(new Intent(Downloads.this, DashboardActivity.class).putExtras(b));
            }
        });

        ArrayList<String> listItems = new ArrayList<>();

        // David's code for parsing session data here
        //Intent intent = getIntent();
        //sessionNumber= Integer.parseInt(intent.getStringExtra("session_number"));
        // Log.d("Test" , String.valueOf(sessionNumber));

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
            JSONObject object = new JSONObject(resultFromAPI);
            JSONArray courses = object.getJSONArray("course");

            for(int i=0; i<courses.length(); i++){
                JSONObject course = courses.getJSONObject(i);
                String courseName = course.getString("lesson");
                listItems.add(courseName);
            }

            bufferedReader.close();
            Log.d("Test", resultFromAPI);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DownloadsListAdapter adapter = new DownloadsListAdapter(listItems, this, getIntent());
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

}
