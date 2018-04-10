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
 * Created by Brandi on 2/25/2018.
 */

public class GroupListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String[]> list = new ArrayList<String[]>();
    private Context context;
    private Intent intent;



    public GroupListAdapter(ArrayList<String[]> list, Context context, Intent intent) {
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
            view = inflater.inflate(R.layout.group_list_layout, null);
        //}

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position)[1]);

        //Handle buttons and add onClickListeners
        Button viewGroupBtn = (Button)view.findViewById(R.id.viewGroup);

        viewGroupBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("Test", "Going to group..."+list.get(position)[0]);
                Intent intent = getIntent();
                String username = intent.getStringExtra("username");
                String password = intent.getStringExtra("password");

                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("password", password);
                bundle.putString("groupID", list.get(position)[0]);
                bundle.putString("groupName", list.get(position)[1]);
                bundle.putString("next_session", list.get(position)[2]);

                intent = new Intent(context, GroupActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
                notifyDataSetChanged();
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
}
