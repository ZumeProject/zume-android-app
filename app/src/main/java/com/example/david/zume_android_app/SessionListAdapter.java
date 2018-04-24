package com.example.david.zume_android_app;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
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
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ArrayList<SessionRow> list = new ArrayList<SessionRow>();
    private Context context;
    private Intent intent;
    private boolean internet;
    private static boolean checkedPermission = false;
    //private Context activityContext = null;



    public SessionListAdapter(ArrayList<SessionRow> list, Context context, Intent intent, Boolean internet) {
        this.list = list;
        this.context = context;
        this.intent = intent;
        this.internet = internet;
        verifyStoragePermissions(context);
        while(!checkedPermission){}
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
            if(list.get(position).isVideo() && !list.get(position).getVideoEmbed().equals("")){
                view = inflater.inflate(R.layout.session_list_video_layout, null);
                //TextView listItemText = (TextView)view.findViewById(R.id.session_item_text);
                WebView webview = (WebView)view.findViewById(R.id.session_item_video);
                //String embed = "<iframe src=\"https://player.vimeo.com/video/247063338?app_id=122963\" width=\"640\" height=\"360\" frameborder=\"0\" title=\"(06) Producers vs Consumers\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>";
                //listItemText.setText(cleanText(list.get(position).getVideoEmbed()));
                DisplayMetrics displayMetrics = new DisplayMetrics();
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
                wm.getDefaultDisplay().getMetrics(displayMetrics);
                int width = new Double(displayMetrics.widthPixels * .6).intValue();
                int height = (9*width)/16;
                String css = "<style> iframe { position: absolute; top:0; left: 0; width: "+width+"px; height: "+height+"; }</style>";
                webview.loadData(css+list.get(position).getVideoEmbed(), "text/html", null);
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
//                        File file = new File(context.getFilesDir(), list.get(position).getPdfTitle().replace(" ", "_").replace("/", "_"));
//                        Uri path = Uri.fromFile(file);
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(path, "application/pdf");
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        File file = new File(context.getFilesDir(), list.get(position).getPdfTitle().replace(" ", "_").replace("/", "_"));
                        //file = new File("/storage/emulated/0/data/data/com.example.david.zume_android_app/files/"+list.get(position).getPdfTitle().replace(" ", "_").replace("/", "_")+".pdf");
                        file = new File(Environment.getExternalStorageDirectory()
                                .getAbsolutePath()+"/Download/", list.get(position).getPdfTitle().replace(" ", "_").replace("/", "_")+".pdf");

                        /*------------------------
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Uri apkURI = FileProvider.getUriForFile(
                                context,
                                context.getApplicationContext()
                                        .getPackageName() + ".provider", file);
                        intent.setDataAndType(apkURI, "application/pdf");
                        *///---------------------------

                        Uri apkURI = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(apkURI, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        //intent.setDataAndType(apkURI, mimeType);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        PdfHandler pdf = new PdfHandler(context);
//                        //pdf.openPdf(String.valueOf(file));
//                        pdf.openPdf("/data/data/com.example.david.zume_android_app/files/Zúme_Video_Scripts:_3_3_Group");
//                        //data/data/com.example.david.zume_android_app/files/Zúme_Guidebook


 //                       Uri apkURI = FileProvider.getUriForFile(SessionListAdapter.this, BuildConfig.APPLICATION_ID + ".provider",
  //                              context.startActivity(intent));
//                                context,
//                                context.getApplicationContext()
//                                        .getPackageName() + ".provider", file);
//                        install.setDataAndType(apkURI, mimeType);
//                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

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
                            final String groupID = intent.getStringExtra("group_id");
                            final String[] token = {intent.getStringExtra("token")};
                            final String userID = intent.getStringExtra("user_id");
                            Log.d("Group_id", groupID);
                            final String groupName = intent.getStringExtra("groupName");
                            String session_number = intent.getStringExtra("session_number");
                            final long[] timeStamp = {intent.getLongExtra("timeStamp", 0)};

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
                            final LinkedHashMap<String, String> args = new LinkedHashMap<>();
                            args.put(session_number, "true");
                            args.put(session_complete, session_complete_date);
                            args.put(next_session_key, next_session);

                            // Set meta value for zume_logging table
                            final String members = intent.getStringExtra("members");
                            final String meta = "group_" + members;

                            //Come back here to make use of the timestamp.
                            //pass the username and password to this function.
                            //So open the credentials file in Session.java
                            String username = intent.getStringExtra("username");
                            String password = intent.getStringExtra("password");
                            TokenTimeStamp check = new TokenTimeStamp();
                            boolean old = check.getTimeDiff(timeStamp[0]);
                            if (old) {
                                final GetUser getToken = new GetUser(username, password, context);
                                final Handler handler = new Handler();
                                final String finalNext_session = next_session;
                                final String finalSession_complete_date = session_complete_date;
                                final String finalSession_number = session_number;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        token[0] = getToken.getToken();
                                        timeStamp[0] = getToken.getTimeStamp();
                                        LoggingPostHandler logging = new LoggingPostHandler(context, token[0], finalSession_complete_date, "course", finalSession_number, meta, groupID, userID, internet);
                                        SessionPostHandler handler = new SessionPostHandler(context, token[0], groupID, args, userID, internet);


                                        Bundle bundle = new Bundle();
                                        bundle.putString("token", token[0]);
                                        bundle.putLong("timeStamp", timeStamp[0]);
                                        bundle.putString("groupID", groupID);
                                        bundle.putString("groupName", groupName);
                                        bundle.putString("next_session", finalNext_session);
                                        bundle.putString("members", members);
                                        bundle.putString("user_id", userID);

                                        // Update the local session number for this group
                                        updateSession(finalNext_session, groupID);


                                        // Return to GroupActivity and refresh user information
                                        final Intent i = new Intent(context, GroupActivity.class);
                                        i.putExtras(bundle);
                                        GetUser gu = new GetUser(token[0], context);

                                        context.startActivity(i);

                                    }
                                }, 4000);
                            } else {
                                LoggingPostHandler logging = new LoggingPostHandler(context, token[0], session_complete_date, "course", session_number, meta, groupID, userID, internet);
                                SessionPostHandler handler = new SessionPostHandler(context, token[0], groupID, args, userID, internet);


                                Bundle bundle = new Bundle();
                                bundle.putString("token", token[0]);
                                bundle.putLong("timeStamp", timeStamp[0]);
                                bundle.putString("groupID", groupID);
                                bundle.putString("groupName", groupName);
                                bundle.putString("next_session", next_session);
                                bundle.putString("members", members);
                                bundle.putString("user_id", userID);

                                // Update the local session number for this group
                                updateSession(next_session, groupID);


                                // Return to GroupActivity and refresh user information
                                final Intent i = new Intent(context, GroupActivity.class);
                                i.putExtras(bundle);
                                GetUser gu = new GetUser(token[0], context);

                                context.startActivity(i);
                            }
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

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param context
     */
    public static void verifyStoragePermissions(Context context) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    (Activity)context,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        checkedPermission = true;
    }
}
