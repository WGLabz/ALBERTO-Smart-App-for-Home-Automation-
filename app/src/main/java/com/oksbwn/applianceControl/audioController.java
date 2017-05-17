package com.oksbwn.applianceControl;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.oksbwn.R;
import com.oksbwn.serverActivity.audioChangeToServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class audioController extends Activity {
    // Array of strings storing country names

    String[] applianceName = {
            "Laptop -Woofer",
            "Desktop -Woofer",
            "TV -Woofer",
            "Extra -Woofer",
            "Laptop -Head Phone",
            "Desktop -Head Phone",
            "TV -Head Phone",
            "Extra -Head Phone"
    } ;
    Integer[] applianceImage = {
            R.drawable.lapwoo,
            R.drawable.deskwoo,
            R.drawable.tvwoo,
            R.drawable.tv,
            R.drawable.lapphead,
            R.drawable.deskhead,
            R.drawable.tvhead,
            R.drawable.tv
    };
    // Array of strings to store currencies
    String[] applianceDetails = new String[]{
            "Connect woofer with Laptop.",
            "Connect woofer with Desktop.",
            "Connect woofer with TV.",
            "Connect woofer with Extra pin.",
            "Connect Head Phone with Laptop.",
            "Connect Head Phone with Desktop.",
            "Connect Head Phone with TV.",
            "Connect Head Phone with Extra Pin."
    };
    int toggleStatus=0;
    audioChangeToServer sa=new audioChangeToServer();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliance_control_profiles);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //TextView doneButton =(TextView)findViewById(R.id.foodFooter);
        // String init=initStat.getIt();
        // Each row in the list stores country name, currency and flag
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
        ListView listView = ( ListView ) findViewById(R.id.appliancesList);

        // Setting the adapter to the listView
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                Thread th=new Thread() {
                    public void run() {
                        try {
                            toggleStatus = sa.sendDataToControlAppliances(position);
                        } catch (Exception e) {
                            Log.d("Appliance Error",e.getMessage());
                        }
                    }
                };
                th.start();
                if(toggleStatus==1) {
                    Toast.makeText(getApplicationContext(),"Thank You", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
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
}