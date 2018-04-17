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

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class Session extends AppCompatActivity {

    private String resultFromAPI;
    private int sessionNumber;
    private JSONObject empty = new JSONObject("{\"empty\":\"\"}");
    private JSONArray emptyArray = new JSONArray("[{\"empty1\":\"\"},{\"empty2\":\"\"}]");

    public Session() throws JSONException {
    }

    private ArrayList<SessionRow> contentList = new ArrayList<SessionRow>();
    String session_number = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        setSession();
        endList();

        Button Home = (Button) findViewById(R.id.Home);
        Home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = getIntent();
                String next_session = intent.getStringExtra("next_session");
                String group_id = intent.getStringExtra("groupID");
                String groupName = intent.getStringExtra("groupName");
                String username = intent.getStringExtra("username");
                String password = intent.getStringExtra("password");
                String token = intent.getStringExtra("token");


                Bundle bundle = new Bundle();
                bundle.putString("session_number", next_session);
                bundle.putString("next_number", next_session);
                bundle.putString("group_id", group_id);
                bundle.putString("groupName", groupName);
                bundle.putString("username", username);
                bundle.putString("password", password);
                bundle.putString("token", token);
                intent = new Intent(Session.this, DashboardActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
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

        Log.d("Internet", "Session "+new Boolean(isNetworkAvailable()).toString());
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
        parseSessionData();
    }

    /**
     * Initialize our SessionListAdapter and check for internet
     */
    public void endList(){
        addToContentList(true);
        SessionListAdapter adapter = new SessionListAdapter(contentList, this, getIntent(), isNetworkAvailable());
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

    public void addToContentList(String url, String title){
        SessionRow row = new SessionRow(url, title);
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

    public void parseSessionData(){
        try{
            JSONObject reader = new JSONObject(resultFromAPI);
            Log.d("Test" , resultFromAPI);
            JSONArray sessionData = reader.getJSONArray("course");
            int sessionNum = Integer.parseInt(session_number);
            JSONObject session = sessionData.getJSONObject(sessionNum-1);
            JSONArray sessionSteps = session.getJSONArray("steps");
            findRoot("steps", sessionSteps,0,0);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This function sorts through a JSON object to try and find the roots of the object
     * @param name The name of the current index
     * @param data The data of the current index
     * @param listIndex The index for a list
     * @param nestedList The nestedList
     */
    public void findRoot(String name, Object data, int listIndex, int nestedList){
        // If we are looking at a link, then we know this is a pdf, so we want to save it in our list as a pdf
        if(name.equals("link")){
            JSONObject pdf = (JSONObject) data;
            Iterator<String> ids = pdf.keys();
            String url = "";
            String title = "";
            int i = 0;
            try {
                while (ids.hasNext()) {
                    String key = ids.next();
                    if (i == 0) {
                        url = (String) pdf.get(key);
                    } else if (i == 1) {
                        title = (String) pdf.get(key);
                    }
                    i++;
                }
                addToContentList(url, title);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            return;
        }
        try {
            // If it is an array, then we will proceed to traverse the array
            if (data instanceof JSONArray) {
                JSONArray array = (JSONArray) data;
                for (int i = 0; i < array.length(); i++) {
                    // Check if this is a list item
                    if(name.equals("ol") || name.equals("ul")){
                        findRoot(name, array.get(i),i+1, listIndex+1);
                    }
                    // This is a regular array
                    else{
                        findRoot(name, array.get(i),listIndex,nestedList);
                    }
                }
                // If this is a JSON object, then traverse the object
            } else if (data instanceof JSONObject) {
                JSONObject object = (JSONObject) data;
                Iterator<String> keys = object.keys();
                // Move through the object
                while(keys.hasNext()){
                    String key = keys.next();
                    findRoot(key, object.get(key), listIndex,nestedList);
                }
                // Add video to list if this is a video
            } else {
                if(name.equals("video")){
                    String text = (String)data;
                    addToContentList(text, true);
                    Log.d("PRINTT", text);
                    return;
                }
                // Add space to list if this is a space
                else if(name.equals("br")){
                    Integer num = (Integer)data;
                    addToContentList(num.intValue());
                    Log.d("PRINTT", String.valueOf(num));
                    return;
                }
                // Add text to list if this is text
                else{
                    String text = (String)data;
                    if(listIndex > 0){
                        text = listIndex+". "+text;
                        if(nestedList > 1){
                            text = text+" (nested!!!!!)";
                        }
                        addToContentList(text, false);
                    }
                    else{
                        addToContentList(text, false);
                    }
                    Log.d("PRINTT", text);
                    return;
                }
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Check to see if we can connect to the network.
     * @return true if we can, false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
     //   Log.d("Internet", "activeNetworkInfo: "+new Boolean(activeNetworkInfo != null).toString());
     //   Log.d("Internet", "connectedOrConnecting: "+new Boolean(activeNetworkInfo.isConnectedOrConnecting()).toString());
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
