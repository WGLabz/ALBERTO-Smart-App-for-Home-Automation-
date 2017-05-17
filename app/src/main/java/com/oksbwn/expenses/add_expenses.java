package com.oksbwn.expenses;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.oksbwn.R;
import com.oksbwn.allDatabaseSetups.expenseDatabaseSetup;
import com.oksbwn.allDatabaseSetups.notesDatabaseSetup;
import com.oksbwn.allSettings.settingsMenuFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class add_expenses extends Activity {
    ListView listView;
    String[] itemsName;
    String[] itemCost;
    String[] itemId;
    EditText itemName;
    expenseDatabaseSetup eDb;
    EditText cost;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MMM/yyyy");
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        itemName =(EditText)findViewById(R.id.purchased_item);
        cost =(EditText)findViewById(R.id.item_cost);

        refreshContents();
               // Each row in the list stores country name, currency and flag

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(getApplicationContext(),"Selected is  "+position,Toast.LENGTH_LONG).show();
                try{
                itemName.setText(itemsName[position]);
                cost.setText(itemCost[position]);}catch(Exception e){}
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    buildAlertMessageNoGps(Integer.parseInt(itemId[i]),itemsName[i]);
                } catch (Exception e) {
                    Log.d("Appliance Error", e.getMessage());
                }

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_expenses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Thread th=null;
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.saveCost:
                th= new Thread() {
                    public void run() {
                        try{
                            Date dat= new Date();
                            eDb.open();
                            eDb.insertData(sdfDate.format(dat),cost.getText().toString(),itemName.getText().toString());
                            eDb.close();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    refreshContents();
                                    Toast.makeText(getApplicationContext(), "Expense Added.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch(Exception e){
                            Log.d("E :",e.getMessage());
                        }
                    }
                };
                th.start();
                break;
            case R.id.addPurchase:
                th= new Thread() {
                    public void run() {
                        try{ Intent i = new Intent(getApplicationContext(),camera.class);
                            startActivity(i);
                            finish();
                        }catch(Exception e){
                            Log.d("E :",e.getMessage());
                        }
                    }
                };
                th.start();
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                th=new Thread() {
                    public void run() {
                        try {
                            Intent i = new Intent(getApplicationContext(),settingsMenuFragment.class);
                            startActivity(i);
                            finish();
                        }catch(Exception e){
                            Log.d("Exception :",e.getMessage());
                        }
                    }
                };
                th.start();
                break;
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
    void refreshContents(){
        eDb=new expenseDatabaseSetup(add_expenses.this);
        eDb.open();
        try {
            Cursor c= eDb.getPreviousExpenses();
            if(c.getCount()!=0 && c!=null){
                itemsName=new String[c.getCount()];
                itemCost=new String[c.getCount()];
                itemId=new String[c.getCount()];
                int i=0;
                for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
                    itemsName[i]=c.getString(c.getColumnIndex("item"));
                    itemCost[i]=c.getString(c.getColumnIndex("cost"));
                    itemId[i]=c.getString(c.getColumnIndex("Sl_No"));
                    i++;
                }
            }
            else
            {

                Toast.makeText(this,"No old Purchases",Toast.LENGTH_LONG).show();
                itemsName=new String[]{""};
                itemCost=new String[]{""};
            }
        }catch(Exception e){
            Toast.makeText(this,"No old Purchases",Toast.LENGTH_LONG).show();
            itemsName=new String[]{""};
            itemCost=new String[]{""};
        }
        eDb.close();
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<itemsName.length;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", "  " + itemsName[i]);
            aList.add(hm);
        }
        // Keys used in Hashmap
        String[] from = {"txt"};
        // Ids of views in listview_layout
        int[] to = {R.id.txt};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.expense_list, from, to);

        // Getting a reference to listview of main.xml layout file
        listView = ( ListView ) findViewById(R.id.previous_purchased_item);

        // Setting the adapter to the listView
        listView.setAdapter(adapter);
    }
    int tempid;
    private void buildAlertMessageNoGps(int noteId,String bodyMessage) {
        tempid=noteId;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Item : "+bodyMessage)
                .setCancelable(true)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick( final DialogInterface dialog,  final int id) {
                        //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        expenseDatabaseSetup eDb= new expenseDatabaseSetup(add_expenses.this);
                        eDb.open();
                        Log.d("Delete value",""+eDb.deleteExpense(tempid));
                        eDb.close();
                        recreate();
                    }
                })
                .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        refreshContents();
    }
}
