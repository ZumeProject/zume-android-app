package com.example.david.zume_android_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Brandi on 4/3/2018.
 */

public class DownloadsListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<>();
    private Context context;
    private Intent intent;
    private String resultFromAPI = "";
    private ArrayList<String[]> pdfList = new ArrayList<>();
    private boolean[] canDelete = null;



    public DownloadsListAdapter(ArrayList<String> list, Context context, Intent intent) {
        canDelete = new boolean[list.size()];
        for(int i=0; i<list.size(); i++){
            canDelete[i] = false;
        }
        this.list = list;
        this.context = context;
        this.intent = intent;
        checkForDelete();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        //if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.group_list_layout, null);
        //}

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        final Button sessionButton = (Button)view.findViewById(R.id.viewGroup);

        if(canDelete[position]){
            sessionButton.setText("Delete");
        }
        else{
            sessionButton.setText("Download");
        }

        sessionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            if(canDelete[position]){
                deleteSession(list.get(position));
                sessionButton.setText("Download");
            }
            else{
                downloadSession(list.get(position));
                sessionButton.setText("Delete");
            }

            }
        });

        return view;
    }

    public Intent getIntent(){
        return this.intent;
    }

    public void setIntent(Intent intent){
        this.intent = intent;
    }

    public void downloadSession(String session){

        FileInputStream fis = null;
        try {
            fis = context.openFileInput("downloaded_sessions.txt");
            Log.d("Test", "Opened the file");

            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            try {
                String line = bufferedReader.readLine();
                FileOutputStream outputStream = context.openFileOutput("downloaded_sessions.txt", context.MODE_PRIVATE);;
                while(line!=null){
                    Log.d("Downloaded", line);
                    outputStream.write(new String(line + "\n").getBytes());
                    line = bufferedReader.readLine();
                }
                outputStream.write(session.getBytes());

                outputStream.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                FileOutputStream outputStream = context.openFileOutput("downloaded_sessions.txt", context.MODE_PRIVATE);
                outputStream.write(session.getBytes());

                outputStream.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }

    public void deleteSession(String session){
        FileInputStream fis= null;
        try {
            fis = context.openFileInput("session_data.txt");
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

        // Make the list of pdfs to be deleted
        parseSessionData(session);

        // Delete these pdf files
        boolean deletedPdfs = true;
        File dir = context.getFilesDir();
        File file = null;
        for(String[] pdf: pdfList){
            String name = pdf[1].replace(" ", "_");
            file = new File(dir, name);
            if(!file.delete()){
                deletedPdfs = false;
            }
        }

        // Remove this session from the downloaded_sessions.txt file
        if(deletedPdfs) {
            try {
                fis = context.openFileInput("downloaded_sessions.txt");
                Log.d("Test", "Opened the file");

                isr = new InputStreamReader(fis);
                bufferedReader = new BufferedReader(isr);

                try {
                    String line = bufferedReader.readLine();
                    FileOutputStream outputStream = context.openFileOutput("downloaded_sessions.txt", context.MODE_PRIVATE);

                    while (line != null) {
                        Log.d("Downloaded", line);
                        if (!line.equals(session)) {
                            outputStream.write(new String(line + "\n").getBytes());
                        }
                        line = bufferedReader.readLine();
                    }

                    outputStream.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkForDelete(){
        boolean canDelete = false;
        FileInputStream fis = null;
        try {
            fis = context.openFileInput("downloaded_sessions.txt");
            Log.d("Test-checkForDelete", "Opened the file");

            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            try {
                String line = bufferedReader.readLine();
                String[] sessionStrings = null;
                int session = 0;
                int fileSize = 0;
                while(line!=null){
                    fileSize++;
                    line = line.replace("\n", "");
                    if(line.length()>0) {
                        sessionStrings = line.split(" ");
                        session = Integer.valueOf(sessionStrings[1]);
                        this.canDelete[session-1] = true;
                        Log.d("DeleteCheck", Integer.valueOf(session) + " " + Boolean.valueOf(this.canDelete[session-1]));
                    }
                    line = bufferedReader.readLine();
                }
                Log.d("fileSize", Integer.valueOf(fileSize).toString());

                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {

        }

        return canDelete;
    }

    public void parseSessionData(String session){
        try{
            JSONObject reader = new JSONObject(resultFromAPI);
            Log.d("Test" , resultFromAPI);
            JSONArray sessionData = reader.getJSONArray("course");
            JSONObject course = new JSONObject();
            for(int i=0; i<sessionData.length(); i++){
                String lesson = sessionData.getJSONObject(i).getJSONObject("lesson").toString();
                Log.d("Lesson", lesson);
                if(lesson.equals(session)){
                    course = sessionData.getJSONObject(i);
                }
            }
            JSONArray sessionSteps = course.getJSONArray("steps");
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
                String[] pdfString = new String[2];
                pdfString[0] = url;
                pdfString[1] = title;
                pdfList.add(pdfString);
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
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

}
