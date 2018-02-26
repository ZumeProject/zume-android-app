package com.example.david.zume_android_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {


    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();


    private String resultFromAPI = "";
    private String baseUrl = "";
    private String username = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button viewProfile = (Button)findViewById(R.id.viewProfile);

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                String username = intent.getStringExtra("username");
                String password = intent.getStringExtra("password");

                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("password", password);

                intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        Button notifications = (Button)findViewById(R.id.notifications);

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, Notifications.class));
            }
        });

        Button logOut = (Button)findViewById(R.id.logOut);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            }
        });

        Button downloads = (Button)findViewById(R.id.downloads);

        downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, Downloads.class));
            }
        });

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        baseUrl = "http://zume.hsutx.edu/wp-json/zume/v1/android/user_profile/1";
        try {

            ApiAuthenticationClient apiAuthenticationClient =
                    new ApiAuthenticationClient(
                            baseUrl
                            , username
                            , password
                    );

            AsyncTask<Void, Void, String> execute = new DashboardActivity.ExecuteNetworkOperation(apiAuthenticationClient);
            execute.execute();
        } catch (Exception ex){
            Log.d("Test","Error getting dashboard data.");
        }

    }

    /**
     * This subclass handles the network operations in a new thread.
     * It starts the progress bar, makes the API call, and ends the progress bar.
     */
    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ApiAuthenticationClient apiAuthenticationClient;

        /**
         * Overload the constructor to pass objects to this class.
         */
        public ExecuteNetworkOperation(ApiAuthenticationClient apiAuthenticationClient) {
            this.apiAuthenticationClient = apiAuthenticationClient;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Display the progress bar.
            //findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.d("Test", "Made to the execute method");
                resultFromAPI = apiAuthenticationClient.execute();
            } catch (Exception e) {
                Log.d("Test", "Error making it to execute method");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Hide the progress bar.
            //findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            // Credentials correct
            if (resultFromAPI != null && !resultFromAPI.equals("")) {
                setGroupList();
            }
            // Login Failure
            else {
                Toast.makeText(getApplicationContext(), "Error opening dashboard: Invalid Credentials", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Open a new activity window.
     */
    private void setGroupList() {
        try{
            JSONObject reader = new JSONObject(resultFromAPI);

            JSONArray first = reader.getJSONArray("first_name");
            JSONArray last = reader.getJSONArray("last_name");
            JSONArray nickname = reader.getJSONArray("nickname");
            JSONArray lastActive = reader.getJSONArray("zume_last_active");

            TextView name = (TextView)findViewById(R.id.name);
            if(!first.get(0).equals("") || !last.get(0).equals("")){
                name.setText(first.get(0) + " " + last.get(0));
            }
            else{
                name.setText(nickname.get(0).toString());
            }

            JSONArray allFields = reader.names();
            ArrayList<String[]> groups = new ArrayList<String[]>();
            for(int i=0; i<allFields.length(); i++){
                if(allFields.get(i).toString().contains("zume_group_")){
                    String[] thisGroup = new String[2];
                    thisGroup[0] = allFields.get(i).toString();
                    Log.d("Group ID", thisGroup[0]);
                    listItems.add(thisGroup[0]);
                    JSONArray groupName = reader.getJSONArray(thisGroup[0]);
                    thisGroup[1] = groupName.get(0).toString();
                    Log.d("Group Name", thisGroup[1]);
                    groups.add(thisGroup);
                }
            }

            GroupListAdapter adapter = new GroupListAdapter(listItems, this);
            ListView listView = (ListView)findViewById(R.id.listView);
            listView.setAdapter(adapter);
/*
            adapter=new ArrayAdapter<TextView>(this, android.R.layout.simple_list_item_1, listItems);
            for(String[] group: groups){
                TextView clickableGroup = new TextView(this);
                clickableGroup.setText(group[1]);
                clickableGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        String username = intent.getStringExtra("username");
                        String password = intent.getStringExtra("password");

                        Bundle bundle = new Bundle();
                        bundle.putString("username", username);
                        bundle.putString("password", password);

                        intent = new Intent(DashboardActivity.this, GroupActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                listItems.add(clickableGroup);
                adapter.notifyDataSetChanged();

            }
*/

        }
        catch(Exception e) {
            Log.d("Test", e.getMessage());
        }

    }

/*
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
*/
}
