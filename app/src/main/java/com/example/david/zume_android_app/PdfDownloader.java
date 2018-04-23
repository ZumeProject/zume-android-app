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

    /**
     * Get all session data and save pdf data in pdfUrls
     * @return
     */
    public ArrayList<String[]> execute(){
        getSessions();
        saveToList();
        return pdfUrls;
    }

    /**
     * Parse session_data.txt to get the pdf information
     */
    public void getSessions(){
            FileInputStream fis= null;
            try {
                fis = context.openFileInput("session_data.txt");
                Log.d("Test", "Opened the file");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            try {
                resultFromAPI = bufferedReader.readLine();
                Log.d("Test", resultFromAPI);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Make the list of pdfs to be downloaded
            parseSessionData();
    }

    /**
     * Make the list of pdfs to be downloaded
     */
    public void parseSessionData(){
        try{
            JSONObject reader = new JSONObject(resultFromAPI);
            Log.d("Test-Downloader" , resultFromAPI);
            JSONArray sessionData = reader.getJSONArray("course");
            // For each session in "course", find the pdfs for the session
            for(int i=0; i<sessionData.length(); i++) {
                int sessionNum = i;
                JSONObject session = sessionData.getJSONObject(sessionNum);
                JSONArray sessionSteps = session.getJSONArray("steps");
                Log.d("SessionSteps" , sessionSteps.toString());
                // Initiate method to find the pdfs in each session
                findRoot("steps", sessionSteps);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Recursively searches JSON arrays and objects to find pdfs in the JSON data. The pdfs are added pdfUrls
     * @param name the name of the current data index
     * @param data the data at this position
     */
    public void findRoot(String name, Object data){
        // We found a pdf, so add it to pdfUrls
        if(name.equals("link")){
            JSONObject pdf = (JSONObject) data;
            Iterator<String> ids = pdf.keys();
            String url = "";
            String title = "";
            int i = 0;
            try {
                while (ids.hasNext()) {
                    String key = ids.next();
                    // Get the url
                    if (i == 0) {
                        url = (String) pdf.get(key);
                    }
                    // Get the title
                    else if (i == 1) {
                        title = (String) pdf.get(key);
                    }
                    i++;
                }
                // Add this pdf to pdfUrls
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
            // Move through the new JSON array
            if (data instanceof JSONArray) {
                JSONArray array = (JSONArray) data;
                for (int i = 0; i < array.length(); i++) {
                    findRoot(name, array.get(i));
                }
            }
            // Move through the new JSON object
            else if (data instanceof JSONObject) {
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

    /**
     * Save pdfUrls to session_pdfs.txt to keep track of what is downloaded
     */
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
