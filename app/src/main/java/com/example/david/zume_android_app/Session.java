package com.example.david.zume_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Session extends AppCompatActivity {

    private String resultFromAPI;
    private ArrayList<SessionRow> contentList = new ArrayList<SessionRow>();
    String session_number = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        setSession();

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

    /*
     * Sets the screen for the current session.
     */
    public void setSession(){
        // Set session number.
        Intent intent = getIntent();
        session_number = intent.getStringExtra("session_number");
        TextView sessionNumber = (TextView) findViewById(R.id.session_number);
        sessionNumber.setText("Session "+session_number);

        // David's code for parsing session data here
        // Upon grabbing a data item, call addToContentList for that item
        // Examples of how you'll use addToContentList() methods
        addToContentList("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis eget massa lobortis, efficitur justo at, fermentum elit. Donec consectetur nisl eu leo cursus tincidunt. Phasellus tellus mauris, eleifend ut massa in, ultrices ornare neque. Mauris ut dictum erat. Proin finibus eleifend neque, eget blandit neque elementum at. Quisque ac libero justo. Vestibulum lacinia tincidunt finibus. Vivamus vitae congue erat, id fringilla mauris.", false);
        addToContentList( 3);
        addToContentList("https://www.lipsum.com/feed/html", true);
        addToContentList("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis eget massa lobortis, efficitur justo at, fermentum elit. Donec consectetur nisl eu leo cursus tincidunt. Phasellus tellus mauris, eleifend ut massa in, ultrices ornare neque. Mauris ut dictum erat. Proin finibus eleifend neque, eget blandit neque elementum at. Quisque ac libero justo. Vestibulum lacinia tincidunt finibus. Vivamus vitae congue erat, id fringilla mauris.", false);

        // The code below this point is required for SessionListAdapter to work
        addToContentList(true);
        SessionListAdapter adapter = new SessionListAdapter(contentList, this, getIntent());
        ListView listView = (ListView)findViewById(R.id.listViewSession);
        listView.setAdapter(adapter);

    }

    /**
     * Add video or text SessionRow to the contentList
     * @param value text/video
     * @param isVideo
     */
    public void addToContentList(String value, boolean isVideo){
        SessionRow row = new SessionRow(value, isVideo);
        this.contentList.add(row);
    }

    /**
     * Add space SessionRow to the contentList.
     * @param numSpaces number of spaces
     */
    public void addToContentList(int numSpaces){
        SessionRow row = new SessionRow(numSpaces);
        this.contentList.add(row);
    }

    /**
     * Add the last SessionRow to the contentList.
     * @param isEnd
     */
    public void addToContentList(boolean isEnd){
        SessionRow row = new SessionRow(isEnd);
        this.contentList.add(row);
    }

}
