package com.example.david.zume_android_app;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
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

    public SessionPostHandler(Context context, String token, String group_id, LinkedHashMap<String, String> args, String userID, boolean internet){

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

                object.put("group_id", group_id);
                JSONObject arguments = new JSONObject(args);
                object.put("args", arguments);
                Log.d("PendingPosts", userID);
                object.put("user_id", userID);
                addToPendingPosts(object);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    public SessionPostHandler(Context context, String token, boolean internet, String userID){
        this.context = context;
        this.internet = internet;
        boolean remove = false;
        readFile();
        Log.d("PendingPosts", "Made it here "+String.valueOf(internet));
        if(internet){
            for(String row: resultFromFile){
                try {
                    JSONObject object = new JSONObject(row);

                    String group_id = object.get("group_id").toString();
                    String user_id = object.get("user_id").toString();
                    JSONObject args = new JSONObject(object.get("args").toString());
                    Iterator<String> keys = args.keys();
                    LinkedHashMap<String, String> map = new LinkedHashMap<>();
                    while(keys.hasNext()){
                        String key = keys.next();
                        map.put(key, args.get(key).toString());
                    }
                    //UpdateGroup update = new UpdateGroup(username, password, group_id, map);
                    Log.d("PendingPosts", group_id);
                    if(userID.equals(user_id)) {
                        UpdateGroup update = new UpdateGroup(token, group_id, map);
                        remove = true;
                    }

                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
            Log.d("PendingPosts", "deleting file");
            if(remove) {
                deleteFile();
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

    private void deleteFile(){
        FileOutputStream outputStream;
        String filename = "pending_posts.txt";
        Log.d("Test", "Made first call");
        try {
            File file = new File(context.getFilesDir()+"/"+filename);
            file.delete();

        } catch (Exception e) {
            e.printStackTrace();
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
            return;
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
