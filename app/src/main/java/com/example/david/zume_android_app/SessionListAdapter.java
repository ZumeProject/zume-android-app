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

import java.util.ArrayList;

/**
 * Created by Brandi on 3/22/2018.
 */

public class SessionListAdapter extends BaseAdapter implements ListAdapter{
    private ArrayList<SessionRow> list = new ArrayList<SessionRow>();
    private Context context;
    private Intent intent;



    public SessionListAdapter(ArrayList<SessionRow> list, Context context, Intent intent) {
        this.list = list;
        this.context = context;
        this.intent = intent;
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
                        String next_session = "0";

                        Intent intent = getIntent();
                        String username = intent.getStringExtra("username");
                        String password = intent.getStringExtra("password");
                        String groupID = intent.getStringExtra("groupID");
                        String groupName = intent.getStringExtra("groupName");
                        String session_number = intent.getStringExtra("session_number");

                        next_session = session_number;

                        Bundle bundle = new Bundle();
                        bundle.putString("username", username);
                        bundle.putString("password", password);
                        bundle.putString("groupID", groupID);
                        bundle.putString("groupName", groupName);
                        bundle.putString("next_session", next_session);


                        intent = new Intent(context, GroupActivity.class);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });
            }
            //Handle TextView and display string from your list
            else {
                view = inflater.inflate(R.layout.session_list_text_layout, null);
                TextView listItemText = (TextView)view.findViewById(R.id.session_item_text);
                listItemText.setText(list.get(position).getText());
                Log.d("ListViewDebug", list.get(position).getText());
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
