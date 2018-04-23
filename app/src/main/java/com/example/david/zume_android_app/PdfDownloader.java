package com.example.david.zume_android_app;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Brandi on 4/10/2018.
 */

public class PdfDownloader {

    private String resultFromAPI;
    Context context = null;
    ArrayList<String[]> pdfUrls = new ArrayList<>();

    public PdfDownloader(Context context){
        this.context = context;
        Log.d("PDFDownloader", "Made it here!");
    }

    public ArrayList<String[]> execute(){
       // FileInputStream fis = null;
      //  try {
       //     fis = context.openFileInput("session_pdfs.txt");
       //     Log.d("Test-check for sessions", "Opened the file");
      //  }
      //  catch(FileNotFoundException e){
            getSessions();
            saveToList();
            return pdfUrls;
       // }

       // return null;
    }

    public void getSessions(){
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
            parseSessionData();
    }

    public void parseSessionData(){
        try{
            JSONObject reader = new JSONObject(resultFromAPI);
            Log.d("Test-Downloader" , resultFromAPI);
            JSONArray sessionData = reader.getJSONArray("course");
            for(int i=0; i<sessionData.length(); i++) {
                int sessionNum = i;
                JSONObject session = sessionData.getJSONObject(sessionNum);
                JSONArray sessionSteps = session.getJSONArray("steps");
                Log.d("SessionSteps" , sessionSteps.toString());
                findRoot("steps", sessionSteps);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void findRoot(String name, Object data){
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
                String[] thisPdf = new String[2];
                thisPdf[0] = url;
                Log.d("addingURL", url);
                thisPdf[1] = title;
                pdfUrls.add(thisPdf);
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
                    findRoot(name, array.get(i));
                }
            } else if (data instanceof JSONObject) {
                JSONObject object = (JSONObject) data;
                Iterator<String> keys = object.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    findRoot(key, object.get(key));
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void saveToList(){
        FileInputStream fis = null;

        try {
            File file = new File(context.getFilesDir(), "session_pdfs.txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for(String[] urlString: pdfUrls){
                Log.d("Url", urlString[0]);
                bw.write(urlString[0]);
                bw.newLine();
            }
        }
        catch(Exception E){
            E.printStackTrace();
        }
    }


}
