package com.oksbwn.applianceControl;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.oksbwn.R;
import com.oksbwn.alaram_manager.change_alram_settings;
import com.oksbwn.expenses.add_expenses;
import com.oksbwn.location.getMyLocation;
import com.oksbwn.notes.NoteInterface;
import com.oksbwn.serverActivity.PostCallToServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class controlAppliances extends Activity implements View.OnClickListener
{
    // Array of strings storing country names
    ListView listView;
    String[] applianceName;
    Integer[] applianceImage;
    Integer[] applianceId;
    // Array of strings to store currencies
    String[] applianceDetails;
    int toggleStatus=0;
    Handler handler;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_appliances);

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8707b5ff")));

        ImageButton goingOut=(ImageButton)findViewById(R.id.goingOutButton);
        ImageButton goodNight=(ImageButton)findViewById(R.id.goodNightButton);
        ImageButton movieMode=(ImageButton)findViewById(R.id.movieModeButton);
        ImageButton tvMode=(ImageButton)findViewById(R.id.tvModeButton);
        ImageButton cameBack=(ImageButton)findViewById(R.id.cameBackButton);

        goingOut.setOnClickListener(this);
        goodNight.setOnClickListener(this);
        movieMode.setOnClickListener(this);
        tvMode.setOnClickListener(this);
        cameBack.setOnClickListener(this);

        refreshClass();
        //TextView doneButton =(TextView)findViewById(R.id.foodFooter);
        //  String init=initStat.getIt();
        // Each row in the list stores country name, currency and flag

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                new AlertDialog.Builder(controlAppliances.this).setMessage("Are you sure ?")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick( final DialogInterface dialog,  final int id) {
                                PostCallToServer pc=  new PostCallToServer("http://192.168.0.1/smart_home/API/android/change_load_status.php",
                                        new String[]{"NO"},
                                        new String[]{""+applianceId[position]});
                                while(pc.getResponse()==null){}
                                if(pc.getResponse().contains("ok")) {
                                    Toast.makeText(getApplicationContext(), applianceName[position] + " been toggled", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), applianceName[position] + " toggle Failed", Toast.LENGTH_SHORT).show();
                                refreshClass();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                          }
                }).create().show();
            }
        });
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
            default:
                break;
        }

        return true;
    }
    int status=0;
    public void onClick(View v){
        status=0;
        switch(v.getId()){
            case R.id.goingOutButton:
                status=1;
                break;
            case R.id.cameBackButton:
                status=2;
                break;
            case R.id.movieModeButton:
                status=3;
                break;
            case R.id.tvModeButton:
                status=4;
                break;
            case R.id.goodNightButton:
                status=5;
                break;
        }
       try{
           new AlertDialog.Builder(controlAppliances.this).setMessage("Are you sure ?")
                   .setCancelable(false)
                   .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                       public void onClick( final DialogInterface dialog,  final int id) {
                           PostCallToServer pc=new PostCallToServer("http://192.168.0.1/smart_home/API/android/changeProfile.php",
                                   new String[]{"STATUS"},
                                   new String[]{""+status});
                           while(pc.getResponse()==null){}
                           if(pc.getResponse().contains("ok"))
                               Toast.makeText(getApplicationContext(),"Profile selected",Toast.LENGTH_LONG).show();
                       }
                   })
                   .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                       public void onClick(final DialogInterface dialog, final int id) {
                           dialog.cancel();
                       }
                   }).create().show();
        } catch (Exception e) {
            Log.d("Appliance Error",e.getMessage());
        }
    }

    public void refreshClass(){

       try{
            PostCallToServer pc=    new PostCallToServer("http://192.168.0.1/smart_home/API/android/getLoads.php",
                                    new String[]{},
                                    new String[]{});
            while(pc.getResponse()==null){}
            String SetServerString = pc.getResponse();
            JSONArray jsonData= new JSONArray(SetServerString);

            applianceName=new String[jsonData.length()];
            applianceImage=new Integer[jsonData.length()];
            applianceDetails=new String[jsonData.length()];
            applianceId=new Integer[jsonData.length()];
            for(int count=0;count<jsonData.length();count++){
                JSONObject obj= (JSONObject)jsonData.get(count);

                Log.d("JSON",obj.toString());
                String image=obj.get("image").toString();
                applianceName[count]=image;
                applianceId[count]=obj.getInt("id");
                if(obj.get("status").toString().contains("F"))
                    applianceImage[count]= getResources().getIdentifier(image+"_off","drawable",getPackageName());
                else
                    applianceImage[count]= getResources().getIdentifier(image+"_on","drawable",getPackageName());
                applianceDetails[count]=obj.get("name").toString();
            }

            List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

            for(int i=0;i<applianceDetails.length;i++){
                HashMap<String, String> hm = new HashMap<String,String>();
                hm.put("txt", "  " + applianceName[i]);
                hm.put("cur"," " + applianceDetails[i]);
                hm.put("flag", Integer.toString(applianceImage[i]) );
                aList.add(hm);
            }
            // Keys used in Hashmap
            String[] from = { "flag","txt","cur" };
            // Ids of views in listview_layout
            int[] to = { R.id.flag,R.id.txt,R.id.cur};

            // Instantiating an adapter to store each items
            // R.layout.listview_layout defines the layout of each item
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_layout, from, to);

            // Getting a reference to listview of main.xml layout file
            listView = ( ListView ) findViewById(R.id.appliancesList);

            // Setting the adapter to the listView
            listView.setAdapter(adapter);
        } catch (Exception e) {
            Log.d("Exception in refresh",""+e.getMessage().toString());
        }
    }
}