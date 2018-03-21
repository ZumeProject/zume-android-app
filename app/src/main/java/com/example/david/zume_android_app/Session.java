package com.example.david.zume_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Session extends AppCompatActivity {

    private String resultFromAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Button Home = (Button) findViewById(R.id.Home);
        /*
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
        try {
            JSONObject reader = new JSONObject(resultFromAPI);
            String sessionNum = (String) reader.get("next_session");
            //UserID = String.valueOf(id);
            Log.d("Test", sessionNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */

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

}
