package com.oksbwn.emails_gmail;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.search.FlagTerm;
import com.oksbwn.R;
import com.oksbwn.allSettings.settingsMenuFragment;
import com.oksbwn.serverActivity.PostCallToServer;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class gmail_inbox extends Activity {


    private String messageBody="";
    String[] mailBody;
    String demoMails="";
    String[] mailIds;
    SimpleAdapter adapter;
    List<HashMap<String,String>>  mailsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmail_inbox);

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        loadDemoMails();
        loadMails(demoMails);
        new LongOperation().execute("http://myhome.roboticsitems.in/API/utilities/readMails.php");

    }
    private void clickedOnUserMail(String bodyMessage,final String emailID,final int mailPosition) {
        new PostCallToServer("http://myhome.roboticsitems.in/API/utilities/markMailRead.php",
                new String[]{"ID"},
                new String[]{""+emailID});
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Message : "+(Html.fromHtml(bodyMessage)))
                .setCancelable(true)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick( final DialogInterface dialog,  final int id) {
                        //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                       deleteMail(emailID,mailPosition);
                    }
                })
                .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private void loadMails(String Mails){
        try {

            if(Mails.equalsIgnoreCase("failed")){
                Toast.makeText(getApplicationContext(), "Failed to connect.", Toast.LENGTH_LONG).show();
            }
            else{
                JSONArray emailDataArray=new JSONArray(Mails);
                int noOfNewMails=emailDataArray.length();
                if (noOfNewMails == 0) {
                    //No unread Messages
                    Toast.makeText(getApplicationContext(), "No unread Mails.", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), noOfNewMails+" unread Mails.", Toast.LENGTH_LONG).show();

                    String[] mailDates = new String[noOfNewMails];
                    Integer[] senderImage = new Integer[noOfNewMails];
                    String[] mailSender = new String[noOfNewMails];
                    String[] mailSubjects = new String[noOfNewMails];
                    mailIds= new String[noOfNewMails];
                    mailBody = new String[noOfNewMails];

                    for (int i = 0; i < noOfNewMails; i++) {
                        JSONObject tempMail=new JSONObject();
                        tempMail=(JSONObject) emailDataArray.get(i);
                        messageBody="";
                        mailSender[i]= tempMail.getString("from");
                        mailDates[i]= tempMail.getString("date");
                        mailSubjects[i]= tempMail.getString("subject");
                        mailBody[i]= tempMail.getString("body");
                        mailIds[i]=tempMail.getString("uid");
                        senderImage[i]=R.drawable.moviemode;
                    }
                    mailsList = new ArrayList<HashMap<String,String>>();
                    for(int noteLen=0;noteLen<noOfNewMails;noteLen++){
                        HashMap<String, String> hm = new HashMap<String,String>();
                        hm.put("FROM", mailSender[noteLen]);
                        hm.put("DATE",  mailDates[noteLen]);
                        hm.put("SUB", mailSubjects[noteLen]);
                        hm.put("IMAGE", Integer.toString(senderImage[noteLen]));
                        mailsList.add(hm);
                    }

                    String[] from = {"FROM","DATE","SUB","IMAGE" };
                    int[] to = {R.id.mailfrom,R.id.maildate,R.id.mailsubject,R.id.senderimage};

                    adapter = new SimpleAdapter(getBaseContext(), mailsList, R.layout.email_listview, from, to);
                    ListView listView = ( ListView ) findViewById(R.id.emaillist);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                            try {
                                clickedOnUserMail(mailBody[position],mailIds[position],position);
                            } catch (Exception e) {
                                Log.d("Appliance Error", e.getMessage());
                            }

                        }});
                }
            }
        }catch (Exception e) {
            //Log.d("Email Error",""+e.getMessage().toString());
            Toast.makeText(getApplicationContext(), "Failed to load mails", Toast.LENGTH_LONG).show();
        }
    }
    private class LongOperation  extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(gmail_inbox.this);

        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            Dialog.setMessage("Loading Mails.");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method
                HttpGet httpget = new HttpGet(urls[0]);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Content = Client.execute(httpget, responseHandler);

            }
            catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {
                Toast.makeText(getApplicationContext(),"Failed to load mails.",Toast.LENGTH_LONG).show();
            } else {
                loadMails(Content);
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gmail_inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                try {
                    Intent i = new Intent(getApplicationContext(),com.oksbwn.main.mainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }catch(Exception e){
                    Log.d("Exception :",e.getMessage());
                }
                return true;
            case R.id.refreshmails:
                Toast.makeText(getApplicationContext(),"Syncing",Toast.LENGTH_LONG).show();
                new LongOperation().execute("http://myhome.roboticsitems.in/API/utilities/readMails.php");
                break;
            default:
                break;
        }

        return true;
    }
    public void loadDemoMails(){
        demoMails="[{\"to\":[\"OKSBWN Bikash <oksbwn@gmail.com>\"],\"from\":\"OKSBWN Bikash <oksbwn@gmail.com>\",\"date\":\"Fri, 27 Nov 2015 10:09:14 +0530\",\"subject\":\"Hello\",\"uid\":23315,\"unread\":true,\"answered\":false,\"body\":\"\",\"html\":true}]";
    }
    public void deleteMail(String id,int position){

        new PostCallToServer("http://myhome.roboticsitems.in/API/utilities/deleteMail.php",
                new String[]{"ID"},
                new String[]{""+id});
        mailsList.remove(position);
        //adapter.;
        adapter.notifyDataSetChanged();
    }
}
