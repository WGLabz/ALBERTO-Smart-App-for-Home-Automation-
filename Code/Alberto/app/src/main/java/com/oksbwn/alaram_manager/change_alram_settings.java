package com.oksbwn.alaram_manager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.oksbwn.R;
import com.oksbwn.main.mainActivity;
import com.oksbwn.serverActivity.PostCallToServer;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.Arrays;

public class change_alram_settings extends Activity implements View.OnClickListener{
    TextView sunText;
    TextView monText;
    TextView tueText;
    TextView wedText;
    TextView thuText;
    TextView friText;
    TextView satText;
    String alarmStatus="OFF";
    Switch sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_alram_settings);

        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        sunText=(TextView)findViewById(R.id.sunText);
        monText=(TextView)findViewById(R.id.monText);
        tueText=(TextView)findViewById(R.id.tueText);
        wedText=(TextView)findViewById(R.id.wedText);
        thuText=(TextView)findViewById(R.id.thuText);
        friText=(TextView)findViewById(R.id.friText);
        satText=(TextView)findViewById(R.id.satText);

        sunText.setOnClickListener(this);
        monText.setOnClickListener(this);
        tueText.setOnClickListener(this);
        wedText.setOnClickListener(this);
        thuText.setOnClickListener(this);
        friText.setOnClickListener(this);
        satText.setOnClickListener(this);
        setTimInTextBox();
        getActionBar().setTitle("Alarm Manager");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.change_alram_settings, menu);

        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem actionViewItem = menu.findItem(R.id.alramStatusSwitch);
        View v = MenuItemCompat.getActionView(actionViewItem);

        sw = (Switch) v.findViewById(R.id.alramActionBarSwitch);
        if(alarmStatus.equalsIgnoreCase("ON"))
            sw.setChecked(true);
        else
            sw.setChecked(false);

        return super.onPrepareOptionsMenu(menu);
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
                Toast.makeText(this, "Alarm times set.", Toast.LENGTH_SHORT).show();
                String status;

                if(sw.isChecked())
                    status="ON";
                else
                    status="OFF";

                new PostCallToServer("http://192.168.0.1/smart_home/API/android/saveAlramTimes.php",
                        new String[]{"STATUS","SUN","MON","TUE","WED","THU","FRI","SAT"},
                        new String[]{status,sunText.getText().toString(),monText.getText().toString(),tueText.getText().toString(),wedText.getText().toString()
                                ,thuText.getText().toString(),friText.getText().toString(),satText.getText().toString()});
                break;
            // action with ID action_settings was selected
            case android.R.id.home:
                try {
                    Intent i = new Intent(getApplicationContext(),mainActivity.class);
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
    public void setTimInTextBox(){
        try {
            PostCallToServer post=new PostCallToServer("http://192.168.0.1/smart_home/API/android/getAlramTimes.php",new String[]{},new String[]{});
            while(post.getResponse()==null){}
            JSONObject jsonObj = new JSONObject(post.getResponse());
            sunText.setText(jsonObj.getString("sun"));
            monText.setText(jsonObj.getString("mon"));
            tueText.setText(jsonObj.getString("tue"));
            wedText.setText(jsonObj.getString("wed"));
            thuText.setText(jsonObj.getString("thu"));
            friText.setText(jsonObj.getString("fri"));
            satText.setText(jsonObj.getString("sat"));
            alarmStatus=jsonObj.getString("status").toString();

            if(alarmStatus.equalsIgnoreCase("ON"))
                sw.setChecked(true);
            else
                sw.setChecked(false);
        } catch (Exception e) {
            Log.d("Exception in refresh","Exception");
        }
    }
    @Override
    public void onClick(View view) {
        final TextView textTime=(TextView)view;
        double timeIs;
        timeIs = Double.parseDouble(textTime.getText().toString());
        Log.d("Date",textTime.getText().toString());
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        textTime.setText(hourOfDay + "." + minute);
                    }
                }, (int)timeIs,(int)((timeIs-(int)timeIs)*100), true);

        tpd.show();
    }
}