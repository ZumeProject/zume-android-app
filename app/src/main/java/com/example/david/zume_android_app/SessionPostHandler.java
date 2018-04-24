package com.example.david.zume_android_app;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
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

    /**
     * Constructor for attempting to update the group session data
     * @param context the context
     * @param token the current token
     * @param group_id the current group ID
     * @param args the LinkedHashMap of keys and values to be updated
     * @param userID the user ID
     * @param internet boolean for if the user has internet access
     */
    public SessionPostHandler(Context context, String token, final String group_id, final LinkedHashMap<String, String> args, String userID, boolean internet){

        this.internet = internet;
        this.context = context;
        // If we have internet, update the session information for the group
        if(internet){
            Log.d("Network", "Network available - updating group data");
            UpdateGroup update = new UpdateGroup(token, group_id, args);

        }
        // If we don't have internet, we will add this to our list of pending session posts
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

    /**
     * Constructor for updating groups from the list of pending posts.
     * @param context the context
     * @param token the current token
     * @param internet boolean for if the user has internet access
     * @param userID the user ID
     */
    public SessionPostHandler(Context context, String token, boolean internet, final String userID){
        this.context = context;
        this.internet = internet;
        boolean remove = false; // Flag for if the file should be deleted after (only delete if we successfully updated any data)
        // Get the current list of data in the pending posts file
        readFile();
        Log.d("PendingPosts", "Made it here "+String.valueOf(internet));
        if(internet){
            // For each post, update the group information if possible
            for(String row: resultFromFile){
                try {
                    JSONObject object = new JSONObject(row);
                    final String group_id = object.get("group_id").toString();
                    final String user_id = object.get("user_id").toString();
                    JSONObject args = new JSONObject(object.get("args").toString());
                    Iterator<String> keys = args.keys();
                    // Construct the map of keys and values to be updated for the group
                    final LinkedHashMap<String, String> map = new LinkedHashMap<>();

                    while(keys.hasNext()){
                        String key = keys.next();
                        map.put(key, args.get(key).toString());
                    }
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
            // Delete the file if we successfully updated data
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
     * @return true if successful, false otherwise
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

            outputStream.close();
            Log.d("Test", "Made the pending_posts file");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete the pending_posts.txt file
     */
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
