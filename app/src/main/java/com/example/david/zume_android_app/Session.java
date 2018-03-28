package com.example.david.zume_android_app;

import android.content.Intent;
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
        /*
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
        sessionParser();*/

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
            Log.d("Test", resultFromAPI);
            //Log.d("Test", bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Test", "Passing saved data");
        parseSessionData();
       // sessionParser();

/*
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
*/

    }

    public void endList(){
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

    /*
    private void sessionParser() {
        JSONObject reader = null;
        try {
            reader = new JSONObject(resultFromAPI);
            Log.d("Test" , resultFromAPI);
            JSONArray sessionData = reader.getJSONArray("course");
            int sessionNum = Integer.parseInt(session_number);
            JSONObject session = sessionData.getJSONObject(sessionNum-1);
            Log.d("Test" , String.valueOf(sessionData));
            Log.d("Test" , String.valueOf(session));
            JSONArray sessionSteps = session.getJSONArray("steps");
            Log.d("Test" , String.valueOf(sessionSteps));
            for (int i=0;i<sessionSteps.length();i++){
                JSONObject step = sessionSteps.getJSONObject(i);
                String title = step.getString("title");
                addToContentList(title,false);
                Log.d("PrintStatement",title);
                JSONArray content = step.getJSONArray("content");
                contentParser(empty,content);
            }
            //contentParser(empty ,sessionSteps);
            //sessionSteps.length();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void contentParser(JSONObject text, JSONArray parse) {
        boolean object = false;
        if (!(String.valueOf(text)).equals(String.valueOf(empty))){
            Log.d("Test" , String.valueOf(text));
            //call Brandi's text display
            JSONObjectParser(text);
            //addToContentList()
            return;
        }
        else{
            for(int i=0;i<parse.length();i++){
                JSONArray stillParse = null;// = emptyArray;
                try{
                    stillParse= parse.getJSONArray(i);

                }catch (JSONException e) {
                    e.printStackTrace();
                    object = true;
                }
                if(object){
                    JSONObject doneParsing = empty;
                    try{
                        doneParsing= parse.getJSONObject(i);
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    object = false;
                    JSONArray activityDescription = null;
                    try{
                       activityDescription = doneParsing.getJSONArray("activity-description");
                    }catch (JSONException e) {
                        e.printStackTrace();
                        object = true;
                    }
                    if(object){
                        object = false;
                        JSONArray center = null;
                        try{
                            center = doneParsing.getJSONArray("center");
                        }catch (JSONException e) {
                            e.printStackTrace();
                            object = true;
                        }
                        if(object){
                            contentParser(doneParsing,emptyArray);
                        }
                        else{
                            try {
                                JSONObject centerText = center.getJSONObject(0);
                                JSONObjectParser(centerText);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else{
                        activityParser(empty,activityDescription);
                    }
                }
                else{
                    contentParser(empty,stillParse);
                }
            }
            return;
        }
    }
    private void activityParser(JSONObject text, JSONArray parse) {
        boolean object = false;
        if (!(String.valueOf(text)).equals(String.valueOf(empty))) {
            Log.d("Test", String.valueOf(text));
            //call Brandi's text display
            JSONObjectParser(text);
            //addToContentList()

            return;
        } else {
            for (int i = 0; i < parse.length(); i++) {
                JSONArray stillParse = null;// = emptyArray;
                try {
                    stillParse = parse.getJSONArray(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                    object = true;
                }
                if (object) {
                    JSONObject doneParsing = empty;
                    try {
                        doneParsing = parse.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    object = false;
                    JSONArray ol = null;
                    try{
                        ol = doneParsing.getJSONArray("ol");
                    }catch (JSONException e) {
                        e.printStackTrace();
                        object = true;
                    }
                    if(object){
                        activityParser(doneParsing,emptyArray);
                    }
                    else{
                        olParser(empty,ol);
                    }
                    //activityParser(doneParsing, emptyArray);
                } else {
                    activityParser(empty, stillParse);
                }
            }
            return;
        }
    }
    private void olParser(JSONObject text, JSONArray parse) {
        boolean object = false;
        if (!(String.valueOf(text)).equals(String.valueOf(empty))) {
            Log.d("Test", String.valueOf(text));
            //call Brandi's text display
            //addToContentList()
            JSONObjectParser(text);
            return;
        } else {
            for (int i = 0; i < parse.length(); i++) {
                JSONArray stillParse = null;// = emptyArray;
                try {
                    stillParse = parse.getJSONArray(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                    object = true;
                }
                if (object) {
                    JSONObject doneParsing = empty;
                    try {
                        doneParsing = parse.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    object = false;
                    JSONArray li = null;
                    try{
                        li = doneParsing.getJSONArray("li");
                    }catch (JSONException e) {
                        e.printStackTrace();
                        object = true;
                    }
                    if(object){
                        olParser(doneParsing,emptyArray);
                    }
                    else{
                        liParser(empty,li,i+1);
                    }
                    //activityParser(doneParsing, emptyArray);
                } else {
                    olParser(empty, stillParse);
                }
            }
            return;
        }
    }

    private void liParser(JSONObject text, JSONArray parse, int listIndex) {
        boolean object = false;
        if (!(String.valueOf(text)).equals(String.valueOf(empty))) {
            //String message = text+" "+listIndex;
            Log.d("Test", String.valueOf(text));
            Log.d("Test", String.valueOf(listIndex));
            //call Brandi's text display
            String index = String.valueOf(listIndex);
            String para = "";
            try {
                 para = text.getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            addToContentList(index+"."+para,false);
            Log.d("PrintStatement",index+"."+para);
            //addToContentList()
            return;
        } else {
            for (int i = 0; i < parse.length(); i++) {
                JSONArray stillParse = null;// = emptyArray;
                try {
                    stillParse = parse.getJSONArray(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                    object = true;
                }
                if (object) {
                    JSONObject doneParsing = empty;
                    try {
                        doneParsing = parse.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    object = false;
                    /*
                    JSONArray ol = null;
                    try{
                        JSONArray ol = doneParsing.getJSONArray("ol");
                    }catch (JSONException e) {
                        e.printStackTrace();
                        object = true;
                    }
                    if(object){
                        olParser(doneParsing,emptyArray);
                    }
                    else{
                        activityParser(empty,ol);
                    }*/
    /*
                    liParser(doneParsing, emptyArray,listIndex);
                } else {
                    liParser(empty, stillParse,listIndex);
                }
            }
            return;
        }
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


        /*
        boolean check = true;
        String para = "";
        try {
            para = text.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
            check = false;
        }
        if(check){
            addToContentList(para,false);
        }
        else{
            check = true;
            int breaks = 0;
            try {
                breaks = text.getInt("br");
            } catch (JSONException e) {
                e.printStackTrace();
                check = false;
            }
            if(check){
                addToContentList(breaks);
            }
            else{
                String video = "";
                try {
                    video = text.getString("video");
                    addToContentList(video,true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    //check = false;
                }
            }
        }*/

/*
    }*/

}
