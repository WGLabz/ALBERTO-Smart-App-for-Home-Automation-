package com.oksbwn.notes;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.oksbwn.R;
import com.oksbwn.allDatabaseSetups.notesDatabaseSetup;
import com.oksbwn.allSettings.settingsMenuFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteInterface extends Activity {
    notesDatabaseSetup nDb=null;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MMM/yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
    EditText noteContent=null;
    private Spinner noteTypeList;
    CustomOnItemSelectedListener customListener= new CustomOnItemSelectedListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_interface);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        noteContent= (EditText)findViewById(R.id.note);
        noteTypeList = (Spinner) findViewById(R.id.noteTypeList);
        noteTypeList.setOnItemSelectedListener(customListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_interface, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Thread th=null;
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.saveNote:
                Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
                th= new Thread() {
                    public void run() {
                        try{
                            Log.d("Result:","ghfgfggxxxxhfghfghgh");
                            Date dat= new Date();
                            nDb= new notesDatabaseSetup(NoteInterface.this);
                            nDb.open();
                            long x=nDb.insertData(sdfDate.format(dat), sdfTime.format(dat), noteContent.getText().toString(),customListener.getText());
                            Log.d("Result:",""+x);

                            nDb.close();
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
            case R.id.action_old_notes:
                th=new Thread() {
                    public void run() {
                        try {
                            Intent i = new Intent(getApplicationContext(),showNotes.class);
                            startActivity(i);
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
}
