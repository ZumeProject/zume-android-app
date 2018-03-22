package com.example.david.zume_android_app;

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

    /**
     * Creates a row for text or video.
     * @param value
     * @param isVideo
     */
    public SessionRow(String value, boolean isVideo){
        this.isVideo = isVideo;
        if(isVideo){
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

    public boolean isVideo(){
        return this.isVideo;
    }

    public boolean isSpace(){
        return this.isSpace;
    }

    public boolean isEnd(){
        return this.isEnd;
    }

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
}
