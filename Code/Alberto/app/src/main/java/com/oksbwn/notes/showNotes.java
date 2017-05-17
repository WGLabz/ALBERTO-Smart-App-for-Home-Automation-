package com.oksbwn.notes;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.oksbwn.R;
import com.oksbwn.allDatabaseSetups.notesDatabaseSetup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class showNotes extends Activity {
    String[] noteIdArray;
    String[] noteBodyArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notes);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        notesDatabaseSetup nDb= new notesDatabaseSetup(showNotes.this);
        nDb.open();
        Cursor allNotes=nDb.getAllNotes();
        Log.d("Result:", "" + allNotes);

        int iRow=allNotes.getColumnIndex("Sl_No");
        int iTime=allNotes.getColumnIndex("currentTime");
        int iDate=allNotes.getColumnIndex("currentDate");
        int iNote=allNotes.getColumnIndex("note");
        int iType=allNotes.getColumnIndex("noteType");
        int iSynced=allNotes.getColumnIndex("syncPC");
        if(allNotes!=null){
                        noteIdArray = new String[allNotes.getCount()];
            String[] noteTimeArray = new String[allNotes.getCount()];
            String[] noteDateArray = new String[allNotes.getCount()];
                        noteBodyArray = new String[allNotes.getCount()];
            String[] noteTypeArray = new String[allNotes.getCount()];
            String[] noteSyncedArray = new String[allNotes.getCount()];
            int i=0;
            for(allNotes.moveToFirst();!allNotes.isAfterLast();allNotes.moveToNext()){
                // result=result+c.getString(iRow)+" "+c.getString(iDate)+" "+ c.getString(iLat)+" "+c.getString(iLong)+" "+c.getString(iTime)+" "+c.getString(iVel);
                try{
                    noteIdArray[i]=allNotes.getString(iRow);
                    noteTimeArray[i]=allNotes.getString(iTime);
                    noteDateArray[i]=allNotes.getString(iDate);
                    noteBodyArray[i]=allNotes.getString(iNote);
                    noteTypeArray[i]=allNotes.getString(iType);
                    noteSyncedArray[i]=allNotes.getString(iSynced);
                    i++;
                        }catch(Exception e){Log.d("Exception ",e.getMessage());}
            }
            try{
                List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
                for(int noteLen=0;noteLen<noteIdArray.length;noteLen++){
                    HashMap<String, String> hm = new HashMap<String,String>();
                    hm.put("id", "  " + noteIdArray[noteLen]+" " + noteTimeArray[noteLen]+ " " + noteDateArray[noteLen]);
                    hm.put("body", "  " + noteBodyArray[noteLen]);
                    aList.add(hm);
                }

                String[] from = { "id","body" };
                int[] to = { R.id.textId,R.id.textNote};

                SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.noteslist, from, to);
                ListView listView = ( ListView ) findViewById(R.id.notesAll);

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {

                                try {
                                    buildAlertMessageNoGps(Integer.parseInt(noteIdArray[position]),noteBodyArray[position]);
                                } catch (Exception e) {
                                    Log.d("Appliance Error", e.getMessage());
                                }

                    }});


            }catch (Exception e){}
            }
        nDb.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Thread th=null;
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
    int tempid;
    private void buildAlertMessageNoGps(int noteId,String bodyMessage) {
        tempid=noteId;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Message : "+bodyMessage)
                .setCancelable(true)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick( final DialogInterface dialog,  final int id) {
                        //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                       notesDatabaseSetup nDb= new notesDatabaseSetup(showNotes.this);
                       nDb.open();
                        Log.d("Delete value",""+nDb.deleteNote(tempid));
                        nDb.close();
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
    }
}
