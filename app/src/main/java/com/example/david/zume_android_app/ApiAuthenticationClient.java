package com.example.david.zume_android_app;

/**
 * Created by David on 2/15/2018.
 */

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ApiAuthenticationClient {

    private String baseUrl;             //Url to connect to
    private String username;            //username of the current user
    private String password;            //password of the current user
    private String token;               //token for the current user
    private Boolean getToken = false;   //Tells if a token needs to be created
    private String urlResource;
    private String httpMethod;          // GET, POST, PUT, DELETE
    private String urlPath;
    private String lastResponse;
    private String payload;             //The information that is being sent to the url
    private LinkedHashMap<String, String> parameters;
    private Map<String, List<String>> headerFields;

    /**
     * Constructor for ApiAuthenticationClient when the user doesn't have a token
     * @param baseUrl String url to make the api call
     * @param username String username of the current user
     * @param password String password of the current user
     */
    public ApiAuthenticationClient(String  baseUrl, String username, String password, Boolean getToken) {
        this.baseUrl = baseUrl;
        this.getToken = getToken;
        this.username = username;
        this.password = password;
        this.urlResource = "";
        this.urlPath = "";
        this.httpMethod = "POST";
        parameters = new LinkedHashMap<>();
        lastResponse = "";
        payload = "";
        headerFields = new HashMap<>();
        // This is important. The application may break without this line.
        System.setProperty("jsse.enableSNIExtension", "false");
    }
    /**
     * Constructor for ApiAuthenticationClient when the user has a token
     * @param baseUrl String url to make the api call
     * @param token String username of the current user
     */
    public ApiAuthenticationClient(String  baseUrl, String token) {
        setBaseUrl(baseUrl);
        this.urlResource = "";
        this.token = token;
        this.username = "";
        this.password = "";
        this.urlPath = "";
        this.httpMethod = "POST";
        parameters = new LinkedHashMap<>();
        lastResponse = "";
        payload = "";
        headerFields = new HashMap<>();
        // This is important. The application may break without this line.
        System.setProperty("jsse.enableSNIExtension", "false");
    }

    /**
     * --&gt;http://BASE_URL.COM&lt;--/resource/path
     * @param baseUrl the root part of the URL
     * @return this
     */
    public ApiAuthenticationClient setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        if(baseUrl.equals("http://zume.hsutx.edu/wp-json/jwt-auth/v1/token/validate")){
            return this;
        }
        else if (!baseUrl.substring(baseUrl.length() - 1).equals("/")) {
            this.baseUrl += "/";
        }
        return this;
    }

    /**
     * Set the name of the resource that is used for calling the Rest API.
     * @param urlResource http://base_url.com/--&gt;URL_RESOURCE&lt;--/url_path
     * @return this
     */
    public ApiAuthenticationClient setUrlResource(String urlResource) {
        this.urlResource = urlResource;
        return this;
    }

    /**
     * Set the path  that is used for calling the Rest API.
     * This is usually an ID number for Get single record, PUT, and DELETE functions.
     * @param urlPath http://base_url.com/resource/--&gt;URL_PATH&lt;--
     * @return this
     */
    public final ApiAuthenticationClient setUrlPath(String urlPath) {
        this.urlPath = urlPath;
        return this;
    }

    /**
     * Sets the HTTP method used for the Rest API.
     * GET, PUT, POST, or DELETE
     * @return this
     */
    public ApiAuthenticationClient setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    /**
     * Get the output from the last call made to the Rest API.
     * @return String
     */
    public String getLastResponse() {
        return lastResponse;
    }

    /**
     * Get a list of the headers returned by the last call to the Rest API.
     * @return Map&lt;String, List&lt;String&gt;&gt;
     */
    public Map<String, List<String>> getHeaderFields() {
        return headerFields;
    }

    /**
     * Replace all of the existing parameters with new parameters.
     * @param parameters
     * @return this
     */
    public ApiAuthenticationClient setParameters(LinkedHashMap<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    /**
     * Set a parameter to be used in the call to the Rest API.
     * @param key the name of the parameter
     * @param value the value of the parameter
     * @return this
     */
    public ApiAuthenticationClient setParameter(String key, String value) {
        this.parameters.put(key, value);
        return this;
    }

    /**
     * Delete all parameters that are set for the Rest API call.
     * @return this
     */
    public ApiAuthenticationClient clearParameters() {
        this.parameters.clear();
        return this;
    }

    /**
     * Remove a specified parameter
     * @param key the name of the parameter to remove
     */
    public ApiAuthenticationClient removeParameter(String key) {
        this.parameters.remove(key);
        return this;
    }

    /**
     * Deletes all values used to make Rest API calls.
     * @return this
     */
    public ApiAuthenticationClient clearAll() {
        parameters.clear();
        baseUrl = "";
        this.username = "";
        this.password = "";
        this.urlResource = "";
        this.urlPath = "";
        this.httpMethod = "";
        lastResponse = "";
        payload = "";
        headerFields.clear();
        return this;
    }

    /**
     * Get the last response from the Rest API as a JSON Object.
     * @return JSONObject
     */
    public JSONObject getLastResponseAsJsonObject() {
        try {
            return new JSONObject(String.valueOf(lastResponse));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the last response from the Rest API as a JSON Array.
     * @return JSONArray
     */
    public JSONArray getLastResponseAsJsonArray() {
        try {
            return new JSONArray(String.valueOf(lastResponse));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the payload as a string from the existing parameters.
     * @return String
     */
    private String getPayloadAsString() {
        // Cycle through the parameters.
        StringBuilder stringBuffer = new StringBuilder();
        Iterator it = parameters.entrySet().iterator();
        int count = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (count > 0) {
                stringBuffer.append("&");
            }
            stringBuffer.append(pair.getKey()).append("=").append(pair.getValue());

            it.remove(); // avoids a ConcurrentModificationException
            count++;
        }
        return stringBuffer.toString();
    }

    /**
     * Make the call to the Rest API and return its response as a string.
     * @return String
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String execute() {
        String line;
        StringBuilder outputStringBuilder = new StringBuilder();

        try {
            StringBuilder urlString = new StringBuilder(baseUrl + urlResource);
            //Sets up the urls
            if (!urlPath.equals("") && httpMethod.equals("GET")) {
                urlString.append("/" + urlPath);
                if(new Character(urlString.charAt(urlString.length()-1)).equals("/")){
                    urlString = urlString.replace(urlString.length()-1, urlString.length(), "");
                }
            }
            if (parameters.size() > 0 && httpMethod.equals("GET")) {
                payload = getPayloadAsString();
                urlString.append("?" + payload);
            }
            else if (parameters.size() > 0 && httpMethod.equals("POST")) {
                payload = getPayloadAsString();
                urlString.replace(urlString.length()-1, urlString.length(), "");
                urlString.append("?" + payload);
            }


            URL url = new URL(urlString.toString());

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            Log.d("Request_Method", httpMethod);
            connection.setRequestMethod(httpMethod);

            //Sets up the url headers and body to get a token
            if(getToken){
                String urlParameters  = "username="+username+"&password="+password;
                byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
                int    postDataLength = postData.length;

                connection.setDoOutput( true );
                connection.setDoInput( true );
                connection.setInstanceFollowRedirects( false );
                connection.setRequestMethod( "POST" );

                connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Cache-Control", "no-cache");
                try( DataOutputStream wr = new DataOutputStream( connection.getOutputStream())) {
                    wr.write( postData );
                    wr.close();
                }
                catch(Exception exc){
                    exc.printStackTrace();
                }
                headerFields= connection.getHeaderFields();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    outputStringBuilder.append(line);
                }
            }
            //Sets up the url headers and body with a token to make a POST or GET.
            else {
                Log.d("Token", token);
                connection.setRequestProperty("Authorization", "Bearer" + token);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "text/plain");

                // Make the network connection and retrieve the output from the server.
                //MAkes a POST and receives any new information from the site.
                if (httpMethod.equals("POST") || httpMethod.equals("PUT")) {

                    payload = getPayloadAsString();

                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    Log.d("URL", connection.getURL().toString());
                    try {
                        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                        writer.write(payload);
                        Log.d("Payload", payload);

                        headerFields = connection.getHeaderFields();
                        Log.d("HeaderFields", headerFields.toString());

                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            outputStringBuilder.append(line);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    connection.disconnect();
                //Makes a GET request.
                } else {
                    InputStream content = (InputStream) connection.getInputStream();
                    headerFields = connection.getHeaderFields();

                    //connection.
                    BufferedReader in = new BufferedReader(new InputStreamReader(content));

                    while ((line = in.readLine()) != null) {
                        outputStringBuilder.append(line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If the outputStringBuilder is blank, the call failed.
        //Stores the resposes form the GET or POST
        if (!outputStringBuilder.toString().equals("")) {
            lastResponse = outputStringBuilder.toString();
        }
        return outputStringBuilder.toString();
    }
}

