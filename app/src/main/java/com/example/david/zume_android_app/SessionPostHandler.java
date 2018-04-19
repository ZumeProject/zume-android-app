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

public class SessionPostHandler extends AppCompatActivity {

    private ArrayList<String> resultFromFile = new ArrayList<String>();
    private Context context = null;
    private boolean internet;

    public SessionPostHandler(Context context, String token, String group_id, LinkedHashMap<String, String> args, boolean internet){

        this.internet = internet;
        if(this.internet){
            Log.d("Internet", "true for sessionPostHandler");
        }
        else{
            Log.d("Internet", "false for sessionPostHandler");
        }
        this.context = context;
        if(internet){
            Log.d("Network", "Network available - updating group data");
            //UpdateGroup update = new UpdateGroup(username, password, group_id, args);
            UpdateGroup update = new UpdateGroup(token, group_id, args);

        }
        else{
            Log.d("Network", "Network unavailable - adding to pending posts");
            try {
                JSONObject object = new JSONObject();
                //object.put("username", username);
                //object.put("password", password);
                object.put("token", token);

                object.put("group_id", group_id);
                JSONObject arguments = new JSONObject(args);
                object.put("args", arguments);
                addToPendingPosts(object);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    public SessionPostHandler(Context context, boolean internet){
        this.context = context;
        this.internet = internet;
        if(internet){
            for(String row: resultFromFile){
                try {
                    JSONObject object = new JSONObject(row);
                    //String username = object.get("username").toString();
                    //String password = object.get("password").toString();
                    String token = object.get("token").toString();

                    String group_id = object.get("group_id").toString();
                    JSONObject args = new JSONObject(object.get("args").toString());
                    Iterator<String> keys = args.keys();
                    LinkedHashMap<String, String> map = new LinkedHashMap<>();
                    while(keys.hasNext()){
                        String key = keys.next();
                        map.put(key, args.get(key).toString());
                    }
                    //UpdateGroup update = new UpdateGroup(username, password, group_id, map);
                    UpdateGroup update = new UpdateGroup(token, group_id, map);

                }
                catch(JSONException e){
                    e.printStackTrace();
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
        String filename = "pending_posts.txt";
        Log.d("Test", "Made first call");
        try {
            outputStream = context.openFileOutput(filename, context.MODE_PRIVATE);

            for(String row: resultFromFile){
                Log.d("Pending_Posts", row);
                byte[] bytes = new String(row+"\n").getBytes();
                outputStream.write(bytes);
            }

            //outputStream.write(new String("").getBytes());
            outputStream.close();
            Log.d("Test", "Made the pending_posts file");
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
            fis = context.openFileInput("pending_posts.txt");
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
