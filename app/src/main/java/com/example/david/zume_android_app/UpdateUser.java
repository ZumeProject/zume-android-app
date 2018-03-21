package com.example.david.zume_android_app;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;

/**
 * Created by Brandi on 3/18/2018.
 */

public class UpdateUser {

    protected String isValidCredentials = "";
    String baseURL = "http://zume.hsutx.edu/wp-json/zume/v1/android/user";

    public UpdateUser(int user_id, String username, String password, String first_name, String last_name, String email, String phone_number) {
        try {
            Log.d("Test", "Making API call");
            ApiAuthenticationClient apiAuthenticationClient =
                    new ApiAuthenticationClient(
                            baseURL
                            , username
                            , password
                    );
            apiAuthenticationClient.setHttpMethod("POST");
            apiAuthenticationClient.setParameter("user_id", String.valueOf(user_id));
            apiAuthenticationClient.setParameter("first_name", first_name);
            apiAuthenticationClient.setParameter("last_name", last_name);
            apiAuthenticationClient.setParameter("email", email);
            apiAuthenticationClient.setParameter("user_phone", phone_number);
            AsyncTask<Void, Void, String> execute = new UpdateUser.ExecuteNetworkOperation(apiAuthenticationClient);
            execute.execute();
        } catch (Exception ex) {
            Log.d("Test", "Error getting dashboard data.");
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


        }
    }
}
