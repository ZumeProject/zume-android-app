package com.example.david.zume_android_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by Brandi on 3/25/2018.
 *
 * Updates a group's information.
 */

public class UpdateGroup {
    protected String isValidCredentials = "";
    String baseURL = "http://zume.hsutx.edu/wp-json/zume/v1/android/group/1";

    /**
     * Constructor initializes and makes call to ApiAuthenticationClient
     * @param username
     * @param password
     * @param group_id the id of the group
     * @param args a LinkedHashMap of group field names and values
     */
    public UpdateGroup(String token, String group_id, LinkedHashMap<String, String> args) {
        try {
            Log.d("Test", "Making API call");
            ApiAuthenticationClient apiAuthenticationClient =
                    new ApiAuthenticationClient(
                            baseURL
                            ,token
                    );
            // Set type to POST
            apiAuthenticationClient.setHttpMethod("POST");
            apiAuthenticationClient.setParameter("group", group_id);
            // Get the field names
            Set<String> keys = args.keySet();
            Iterator<String> itr = keys.iterator();
            while(itr.hasNext()){
                // Set each parameter from args
                String key = itr.next();
                String name = "args["+key+"]";
                apiAuthenticationClient.setParameter(name, args.get(key));
            }

            AsyncTask<Void, Void, String> execute = new UpdateGroup.ExecuteNetworkOperation(apiAuthenticationClient);
            execute.execute();
        } catch (Exception ex) {
            Log.d("Test", "Error posting group data.");
            ex.printStackTrace();
        }
    }

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
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                isValidCredentials = apiAuthenticationClient.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return isValidCredentials;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    return;
                }
            }, 200);
        }
    }
}
