package com.example.david.zume_android_app;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by Brandi on 3/25/2018.
 */

public class LoggingPostHandler extends AppCompatActivity {

    private ArrayList<String> resultFromFile = new ArrayList<String>();
    private Context context = null;
    private boolean internet;

    public LoggingPostHandler(Context context, String token, String createdDate, String page, String action, String meta, String group_id, boolean internet){

        this.internet = internet;
        if(this.internet){
            Log.d("Internet", "true for loggingPostHandler");
        }
        else{
            Log.d("Internet", "false for loggingPostHandler");
        }
        this.context = context;
        if(internet){
            Log.d("Network", "Network available - updating group-logging data");
            UpdateLogging update = new UpdateLogging(token, createdDate, page, action, meta, group_id);
        }
        else{
            Log.d("Network", "Network unavailable - adding to pending logging posts");
            try {
                JSONObject object = new JSONObject();
                object.put("token", token);
                object.put("createdDate", createdDate);
                object.put("page", page);
                object.put("action", action);
                object.put("meta", meta);
                object.put("group_id", group_id);
                addToPendingPosts(object);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    public LoggingPostHandler(Context context, String token, String createdDate, String page, String action,  boolean internet){

        this.internet = internet;
        if(this.internet){
            Log.d("Internet", "true for loggingPostHandler");
        }
        else{
            Log.d("Internet", "false for loggingPostHandler");
        }
        this.context = context;
        if(internet){
            Log.d("Network", "Network available - updating group-logging data");
            UpdateLogging update = new UpdateLogging(token, createdDate, page, action);
        }
        else{
            Log.d("Network", "Network unavailable - adding to pending logging posts");
            try {
                JSONObject object = new JSONObject();
                object.put("token", token);
                object.put("createdDate", createdDate);
                object.put("page", page);
                object.put("action", action);
                addToPendingPosts(object);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    public LoggingPostHandler(Context context, boolean internet){
        this.context = context;
        this.internet = internet;
        if(internet){
            for(String row: resultFromFile){
                try {
                    JSONObject object = new JSONObject(row);
                    String token = object.get("token").toString();
                    String date = object.get("createdDate").toString();
                    String page = object.get("page").toString();
                    String action = object.get("action").toString();
                    String meta = object.get("meta").toString();
                    String group_id = object.get("group_id").toString();
                    UpdateLogging update = new UpdateLogging(token, date, page, action, meta, group_id);
                }
                catch(JSONException e){
                    try{
                        JSONObject object = new JSONObject(row);
                        String token = object.get("token").toString();
                        String date = object.get("createdDate").toString();
                        String page = object.get("page").toString();
                        String action = object.get("action").toString();
                        UpdateLogging update = new UpdateLogging(token, date, page, action);
                    }
                    catch(JSONException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
        else{
            return;
        }
    }

    /**
     * Add a pending post to the pending_posts file
     * @param object JSONObject representing the post
     * @return if the action succeeds
     */
    private boolean addToPendingPosts(JSONObject object){
        readFile();
        resultFromFile.add(object.toString());
        return writeFile();
    }

    /**
     * Using resultFromFile, create new pending_posts file
     * @return true if successfull, false otherwise
     */
    private boolean writeFile(){
        FileOutputStream outputStream;
        String filename = "logging_posts.txt";
        try {
            outputStream = context.openFileOutput(filename, context.MODE_PRIVATE);

            for(String row: resultFromFile){
                Log.d("logging_Posts", row);
                byte[] bytes = new String(row+"\n").getBytes();
                outputStream.write(bytes);
            }

            //outputStream.write(new String("").getBytes());
            outputStream.close();
            Log.d("LoggingHandler", "Made the logging_posts file");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Read the pending_posts file and create resultFromfile
     */
    private void readFile(){
        FileInputStream fis= null;
        try {
            fis = context.openFileInput("logging_posts.txt");
            Log.d("Test", "Opened the file");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(fis!=null) {
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            try {

                String line = bufferedReader.readLine();
                while (line != null) {
                    resultFromFile.add(line);
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
