package com.example.david.zume_android_app;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TimeZone;

/**
 * Created by Brandi on 3/22/2018.
 */

public class SessionListAdapter extends BaseAdapter implements ListAdapter{
    private ArrayList<SessionRow> list = new ArrayList<SessionRow>();
    private Context context;
    private Intent intent;
    private boolean internet;



    public SessionListAdapter(ArrayList<SessionRow> list, Context context, Intent intent, Boolean internet) {
        this.list = list;
        this.context = context;
        this.intent = intent;
        this.internet = internet;
        if(this.internet){
            Log.d("Internet", "true for sessionListAdapter");
        }
        else{
            Log.d("Internet", "false for sessionListAdapter");
        }
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
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Choose view based on if this row contains a video
            if(list.get(position).isVideo()){
                view = inflater.inflate(R.layout.session_list_text_layout, null);
                TextView listItemText = (TextView)view.findViewById(R.id.session_item_text);
                listItemText.setText(cleanText(list.get(position).getVideo()));
            }
            //Handle pdf link
            else if(list.get(position).isPdf()){
                view = inflater.inflate(R.layout.session_list_text_layout, null);
                TextView listItemText = (TextView)view.findViewById(R.id.session_item_text);
                listItemText.setText(cleanText(list.get(position).getPdfTitle()));
                listItemText.setTextColor(Color.BLUE);
                listItemText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        // Open the pdf in a pdf reader installed on the device if possible
                        File file = new File(context.getFilesDir(), list.get(position).getPdfTitle().replace(" ", "_").replace("/", "_"));
                        Uri path = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(path, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        try {
                            context.startActivity(intent);
                        }
                        // No pdf reader is installed on this device so let the user know
                        catch(ActivityNotFoundException e){
                            e.printStackTrace();
                            Toast.makeText(context,"No Application available to view PDF.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            //Handle line breaks
            else if(list.get(position).isSpace()){
                view = inflater.inflate(R.layout.session_list_space_layout, null);
                TextView listItemText = (TextView)view.findViewById(R.id.session_item_space);
                listItemText.setHeight(list.get(position).getNumSpaces()*20);
            }
            //Handle last item
            else if(list.get(position).isEnd()){
                view = inflater.inflate(R.layout.session_list_end_layout, null);
                TextView listItemText = (TextView)view.findViewById(R.id.session_item_end);
                Button completeSession = (Button)view.findViewById(R.id.complete_session);
                completeSession.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Make post for session here (or add to pending posts if no internet) Maybe handle that in session posting class
                        Intent intent = getIntent();
                        Boolean recordCompletion = intent.getBooleanExtra("Has_a_Group", false);
                        if(recordCompletion) {
                            String next_session = "0";
                            String groupID = intent.getStringExtra("group_id");
                            String token = intent.getStringExtra("token");
                            String userID = intent.getStringExtra("user_id");
                            Log.d("Group_id", groupID);
                            String groupName = intent.getStringExtra("groupName");
                            String session_number = intent.getStringExtra("session_number");
                            long timeStamp = intent.getLongExtra("timeStamp", 0);

                            // Get next_session
                            next_session = new Integer(new Integer(session_number) + 1).toString();
                            // Create arg keys
                            session_number = "session_" + session_number;
                            String session_complete = session_number + "_complete";
                            String next_session_key = "next_session";

                            // Set the completed date and time
                            Date complete = new Date();
                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            format.setTimeZone(TimeZone.getTimeZone("US/Central"));
                            String session_complete_date = format.format(complete);
                            session_complete_date = session_complete_date.replace(" ", "%20");

                            // Add argument keys and values to args for sessionPostHandler
                            LinkedHashMap<String, String> args = new LinkedHashMap<>();
                            args.put(session_number, "true");
                            args.put(session_complete, session_complete_date);
                            args.put(next_session_key, next_session);

                            // Set meta value for zume_logging table
                            String members = intent.getStringExtra("members");
                            String meta = "group_"+members;
                            //Come back here to make use of the timestamp.
                            LoggingPostHandler logging = new LoggingPostHandler(context, token, session_complete_date, "course", session_number, meta, groupID, userID, internet);
                            SessionPostHandler handler = new SessionPostHandler(context, token, groupID, args, userID, internet);


                            Bundle bundle = new Bundle();
                            bundle.putString("token",token);
                            bundle.putLong("timeStamp", timeStamp);
                            bundle.putString("groupID", groupID);
                            bundle.putString("groupName", groupName);
                            bundle.putString("next_session", next_session);
                            bundle.putString("members", members);

                            // Update the local session number for this group
                            updateSession(next_session, groupID);


                            // Return to GroupActivity and refresh user information
                            final Intent i = new Intent(context, GroupActivity.class);
                            i.putExtras(bundle);
                            GetUser gu = new GetUser(token, context);

                            context.startActivity(i);
                        }
                        // If there is no group associated with this session, just retrun to the sessionList
                        else{
                            final Intent i = new Intent(context, SessionList.class);
                            Bundle bundle = new Bundle();
                            i.putExtras(bundle);
                            context.startActivity(i);
                        }
                    }
                });
            }
            //Handle TextView and display string from your list
            else {
                view = inflater.inflate(R.layout.session_list_text_layout, null);
                TextView listItemText = (TextView)view.findViewById(R.id.session_item_text);
                listItemText.setText(cleanText(list.get(position).getText()));
            }

        
        return view;
    }

    /**
     * Updates the local copy of the next session a group needs to complete
     * @param next_session The next session after the one just completed
     * @param group_id The id of the current group
     */
    public void updateSession(String next_session, String group_id){
        try {
            File file = new File(context.getFilesDir(), group_id);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(new String(next_session+"\n").getBytes());

            fos.flush();
            fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Handles characters that were not translated from the lessons endpoint.
     * @param text The text to be "cleaned"
     * @return the "clean" text
     */
    public String cleanText(String text){
        text = text.replace("\u00c3\u009a", "\u00DA"); // Uppercase accented U
        text = text.replace("\u00c3\u00ba", "\u00FA"); // Lowercase accented u
        text = text.replace("\u00e2\u0080\u0094", "-"); // Hyphen
        text = text.replace("\u00e2\u0080\u0099", "'"); // Apostrophe
        text = text.replace("\u00e2\u0080\u009c", "\""); // Quotation open
        text = text.replace("\u00c2", "");
        text = text.replace("\u00e2\u0080\u009d", "\""); // Quotation close
        text = text.replace("\u00e2\u0080\u00a6", "...");
        return text;
    }

    public Intent getIntent(){
        return this.intent;
    }

    public void setIntent(Intent intent){
        this.intent = intent;
    }
}
