package com.example.david.zume_android_app;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
        //if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Choose view based on if this row contains a video
            if(list.get(position).isVideo()){
                view = inflater.inflate(R.layout.session_list_text_layout, null);
                TextView listItemText = (TextView)view.findViewById(R.id.session_item_text);
                listItemText.setText(list.get(position).getVideo());
            }
            //Handle pdf link
            else if(list.get(position).isPdf()){
                view = inflater.inflate(R.layout.session_list_text_layout, null);
                TextView listItemText = (TextView)view.findViewById(R.id.session_item_text);
                listItemText.setText(list.get(position).getPdfTitle());
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
                        catch(ActivityNotFoundException e){
                            e.printStackTrace();
                            Toast.makeText(context,"No Application available to view PDF.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            //Handle line break
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
                            String username = intent.getStringExtra("username");
                            Log.d("Username", "SessionListAdapter " + username);
                            String password = intent.getStringExtra("password");
                            String groupID = intent.getStringExtra("group_id");
                            Log.d("Group_id", groupID);
                            String groupName = intent.getStringExtra("groupName");
                            String session_number = intent.getStringExtra("session_number");

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


                            SessionPostHandler handler = new SessionPostHandler(context, username, password, groupID, args, internet);


                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("password", password);
                            bundle.putString("groupID", groupID);
                            bundle.putString("groupName", groupName);
                            bundle.putString("next_session", next_session);


                            final Intent i = new Intent(context, GroupActivity.class);
                            i.putExtras(bundle);
                            GetUser gu = new GetUser(username, password, context);

                            context.startActivity(i);
                        }
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
                listItemText.setText(list.get(position).getText());
                //Log.d("ListViewDebug", list.get(position).getText());
            }

        //}

        
        return view;
    }

    public Intent getIntent(){
        return this.intent;
    }

    public void setIntent(Intent intent){
        this.intent = intent;
    }
}
