package com.example.david.zume_android_app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Brandi on 3/22/2018.
 */

public class SessionRow {
    private boolean isEnd = false;
    private boolean isSpace = false;
    private int numSpaces = 0;
    private boolean isVideo = false;
    private String text = "";
    private String video = "";
    private String pdfUrl = "";
    private String pdfTitle = "";
    private String videoEmbed = "";
    private boolean isPdf = false;
    private boolean videoEmbedRecieved = false;
    private String urlPath = "";
    private boolean internet = false;

    /**
     * Creates a row for text or video.
     * @param value
     * @param isVideo
     */
    public SessionRow(String value, boolean isVideo, boolean internet){
        this.internet = internet;
        this.isVideo = isVideo;
        if(isVideo){
            urlPath = "https://vimeo.com/api/oembed.json?url="+value.replace("https://player.vimeo.com","https%3A//vimeo.com");

            AsyncTask<Void, Void, String> embedAsync = new GetVideoEmbedAsync().execute();
            videoEmbedRecieved = false;
            this.video = value;
        }
        else{
            this.text = value;
        }
    }

    /**
     * Create a row for just space.
     * @param numSpaces the number of spaces
     */
    public SessionRow(int numSpaces){
        this.numSpaces = numSpaces;
        this.isSpace = true;
    }

    /**
     * Create the last row.
     * @param isEnd
     */
    public SessionRow(boolean isEnd){
        this.isEnd = true;
    }

    /**
     * Creates a row with a pdf link.
     * @param url pdf url
     * @param title pdf title
     */
    public SessionRow(String url, String title){
        this.pdfUrl = url;
        this.pdfTitle = title;
        this.isPdf = true;
    }



    public boolean isVideo(){
        return this.isVideo;
    }

    public boolean isSpace(){
        return this.isSpace;
    }

    public boolean isEnd(){
        return this.isEnd;
    }

    public boolean isPdf(){return this.isPdf;}

    public void setIsSpace(boolean isSpace){
        this.isSpace = isSpace;
    }

    public void setIsEnd(boolean isEnd){
        this.isEnd = isEnd;
    }

    public void setIsVideo(boolean isVideo){
        this.isVideo = isVideo;
    }

    public int getNumSpaces(){
        return this.numSpaces;
    }

    public void setNumSpaces(int numSpaces){
        this.numSpaces = numSpaces;
    }

    public String getText(){
        return this.text;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getVideo(){
        return this.video;
    }

    public void setVideo(String video){
        this.video = video;
    }

    public String getVideoEmbed(){
        return this.videoEmbed;
    }

    public void setVideoEmbed(String videoEmbed){
        this.videoEmbed = videoEmbed;
    }

    public String getPdfUrl(){return this.pdfUrl;}

    public String getPdfTitle(){return this.pdfTitle;}

    class GetVideoEmbedAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            if(internet) {
                try {
                    URL url = new URL(urlPath);
                    Log.d("SessionRowURL", url.toString());
                    HttpsURLConnection conexion = (HttpsURLConnection) url.openConnection();
                    conexion.setRequestMethod("GET");
                    conexion.connect();
                    String result = "";

                    InputStream input = new BufferedInputStream(url.openStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result = result+line;
                    }
                    input.close();

                    JSONObject object = new JSONObject(result);
                    String embed = object.get("html").toString();
                    videoEmbed = embed;
                }
                catch(Exception e){
                    videoEmbed = "";
                    e.printStackTrace();
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(String unused) {
            videoEmbedRecieved = true;
        }
    }
}
