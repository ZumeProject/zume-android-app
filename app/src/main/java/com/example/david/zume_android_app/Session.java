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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    public void findRoot(String name, Object data, int listIndex, int nestedList){
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
            if (data instanceof JSONArray) {
                JSONArray array = (JSONArray) data;
                for (int i = 0; i < array.length(); i++) {
                    if(name.equals("ol") || name.equals("ul")){
                        findRoot(name, array.get(i),i+1, listIndex+1);
                    }
                    else{
                        findRoot(name, array.get(i),listIndex,nestedList);
                    }
                }
            } else if (data instanceof JSONObject) {
                JSONObject object = (JSONObject) data;
                Iterator<String> keys = object.keys();
                while(keys.hasNext()){
                    String key = keys.next();
                    findRoot(key, object.get(key), listIndex,nestedList);
                }
            } else {
                if(name.equals("video")){
                    String text = (String)data;
                    addToContentList(text, true);
                    Log.d("PRINTT", text);
                    return;
                }
                else if(name.equals("br")){
                    Integer num = (Integer)data;
                    addToContentList(num.intValue());
                    Log.d("PRINTT", String.valueOf(num));
                    return;
                }
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
        Log.d("Internet", "activeNetworkInfo: "+new Boolean(activeNetworkInfo != null).toString());
        Log.d("Internet", "connectedOrConnecting: "+new Boolean(activeNetworkInfo.isConnectedOrConnecting()).toString());
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void JSONObjectParser(JSONObject text){

        boolean check = true;
        int breaks = 0;
        try {
            breaks = text.getInt("br");
        } catch (JSONException e) {
            e.printStackTrace();
            check = false;
        }
        if(check){
            addToContentList(breaks);
            Log.d("PrintStatement", String.valueOf(breaks));

        }
        else{
            String video = "";
            check = true;
            try {
                video = text.getString("video");
            } catch (JSONException e) {
                e.printStackTrace();
                check = false;
            }
            if(check){
                addToContentList(video,true);
                Log.d("PrintStatement",video);
            }
            else{
                String json = String.valueOf(text);
                String[] data = json.split(":");
                data[1] = data[1].substring(0, data[1].length() - 1);
                data[1] = data[1].substring(0, data[1].length() - 1);
                data[1] = data[1].replaceFirst("\"","");
                addToContentList(data[1],false);
                Log.d("PrintStatement",data[1]);

            }


        }

    /**
     * Check to see if we can connect to the network.
     * @return true if we can, false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.d("Internet", "activeNetworkInfo: "+new Boolean(activeNetworkInfo != null).toString());
        Log.d("Internet", "connectedOrConnecting: "+new Boolean(activeNetworkInfo.isConnectedOrConnecting()).toString());
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
