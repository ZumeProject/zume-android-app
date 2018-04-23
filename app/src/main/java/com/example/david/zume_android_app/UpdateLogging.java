package com.example.david.zume_android_app;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by Brandi on 3/25/2018.
 *
 * Updates a group's information.
 */

public class UpdateLogging {
    protected String isValidCredentials = "";
    String baseURL = "https://zume.sergeantservices.com/wp-json/zume/v1/android/logging";

    /**
     * Makes Post to update the zume_logging table for a group-type action
     * @param token
     * @param createdDate
     * @param page
     * @param action
     * @param meta
     * @param group_id
     */
    public UpdateLogging(String token, String createdDate, String page, String action, String meta, String group_id) {
        try {
            Log.d("Test", "Making API call");
            ApiAuthenticationClient apiAuthenticationClient =
                    new ApiAuthenticationClient(
                            baseURL
                            , token
                    );
            // Set type to POST
            apiAuthenticationClient.setHttpMethod("POST");
            apiAuthenticationClient.setParameter("created_date", createdDate);
            apiAuthenticationClient.setParameter("page", page);
            apiAuthenticationClient.setParameter("action", action);
            apiAuthenticationClient.setParameter("meta", meta);
            apiAuthenticationClient.setParameter("group_id", group_id);

            AsyncTask<Void, Void, String> execute = new UpdateLogging.ExecuteNetworkOperation(apiAuthenticationClient);
            execute.execute();
        } catch (Exception ex) {
            Log.d("UpdateLogging", "Error posting group-logging data.");
            ex.printStackTrace();
        }
    }

    /**
     * Makes Post to update the zume_logging table for a user-type action
     * @param token
     * @param createdDate
     * @param page
     * @param action
     */
    public UpdateLogging(String token, String createdDate, String page, String action) {
        try {
            Log.d("Test", "Making API call");
            ApiAuthenticationClient apiAuthenticationClient =
                    new ApiAuthenticationClient(
                            baseURL
                            , token
                    );
            // Set type to POST
            apiAuthenticationClient.setHttpMethod("POST");
            apiAuthenticationClient.setParameter("created_date", createdDate);
            apiAuthenticationClient.setParameter("page", page);
            apiAuthenticationClient.setParameter("action", action);

            AsyncTask<Void, Void, String> execute = new UpdateLogging.ExecuteNetworkOperation(apiAuthenticationClient);
            execute.execute();
        } catch (Exception ex) {
            Log.d("UpdateLogging", "Error posting user-logging data.");
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
