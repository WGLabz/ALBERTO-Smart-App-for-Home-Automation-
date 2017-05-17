package com.oksbwn.main;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.oksbwn.R;
import com.oksbwn.alaram_manager.change_alram_settings;
import com.oksbwn.applianceControl.audioController;
import com.oksbwn.expenses.add_expenses;
import com.oksbwn.location.getMyLocation;
import com.oksbwn.notes.NoteInterface;

public class HomeFragment extends Fragment implements View.OnClickListener{
    Activity activity;
	public HomeFragment(){}
    ImageButton allSettings;
    ImageButton locationService;
    ImageButton myNotes;
    ImageButton applianceController;
    ImageButton applianceControlProfiles;
    ImageButton changeAudioStatus;
    ImageButton addExpense;
    ImageButton setAlaram;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        allSettings = (ImageButton) rootView.findViewById(R.id.imageButton9);
        locationService = (ImageButton) rootView.findViewById(R.id.imageButton2);
        myNotes = (ImageButton) rootView.findViewById(R.id.imageButton3);
        applianceController = (ImageButton) rootView.findViewById(R.id.imageButton4);
        applianceControlProfiles = (ImageButton)rootView.findViewById(R.id.imageButton6);
        changeAudioStatus = (ImageButton) rootView.findViewById(R.id.imageButton5);
        addExpense = (ImageButton) rootView.findViewById(R.id.imageButton7);
        setAlaram = (ImageButton) rootView.findViewById(R.id.imageButton8);

        allSettings.setOnClickListener(this);
        locationService.setOnClickListener(this);
        myNotes.setOnClickListener(this);
        applianceController.setOnClickListener(this);
        applianceControlProfiles.setOnClickListener(this);
        changeAudioStatus.setOnClickListener(this);
        setAlaram.setOnClickListener(this);
        addExpense.setOnClickListener(this);
        //Run Services on Startup
        if (isTheServiceRunning(getMyLocation.class)) {
            locationService.setImageResource(R.drawable.location_running);
        }
        return rootView;
    }
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.activity = activity;
    }
    public void onClick(View v){
        Thread th;
        v.setBackgroundColor(Color.argb(50, 0, 0, 0));
        switch(v.getId()){
            case R.id.imageButton2:
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                }
                th=new Thread() {
                    public void run() {
                        try {
                            if(!isTheServiceRunning(getMyLocation.class)) {
                                Intent  getMyLocationServiceIntent = new Intent(getActivity(), getMyLocation.class);
                                getActivity().startService(getMyLocationServiceIntent);
                                getActivity(). runOnUiThread(new Runnable() {
                                    public void run() {
                                        locationService.setImageResource(R.drawable.location_running);
                                    }
                                });
                            }
                            else
                            {
                                getActivity().stopService(new Intent(getActivity(),getMyLocation.class));
                                getActivity(). runOnUiThread(new Runnable() {
                                    public void run() {
                                        locationService.setImageResource(R.drawable.location);
                                    }
                                });
                            }
                        }catch(Exception e){
                            Log.d("Exception :",e.getMessage());
                        }
                    }
                } ;

                th.start();
                break;
            case R.id.imageButton3:
                th=new Thread() {
                    public void run() {
                        try {
                            Intent i = new Intent(getActivity(),NoteInterface.class);
                            startActivity(i);
                        }catch(Exception e){
                            Log.d("Exception :",e.getMessage());
                        }
                    }
                };
                th.start();
                break;
            case R.id.imageButton5:
                th=new Thread() {
                    public void run() {
                        try {
                            Intent i = new Intent(getActivity(),audioController.class);
                            startActivity(i);
                        }catch(Exception e){
                            Log.d("Exception :",e.getMessage());
                        }
                    }
                };
                th.start();
                break;
            case R.id.imageButton6:
                th=new Thread() {
                    public void run() {
                        try {
                            Intent i = new Intent(getActivity(), com.oksbwn.applianceControl.controlAppliances.class);
                            startActivity(i);
                        }catch(Exception e){
                            Log.d("Exception :",e.getMessage());
                        }
                    }
                };
                th.start();
                break;
            case R.id.imageButton7:
                th=new Thread() {
                    public void run() {
                        try {
                            Intent i = new Intent(getActivity(),add_expenses.class);
                            startActivity(i);
                        }catch(Exception e){
                            Log.d("Exception :",e.getMessage());
                        }
                    }
                };
                th.start();
                break;
            case R.id.imageButton8:
                th=new Thread() {
                    public void run() {
                        try {
                            Intent i = new Intent(getActivity(),change_alram_settings.class);
                            startActivity(i);
                        }catch(Exception e){
                            Log.d("Exception :",e.getMessage());
                        }
                    }
                };
                th.start();
                break;
            default:
                break;
        }
        v.setBackgroundColor(Color.argb(0,0,0,0));
    }
    private boolean isTheServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager)  getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder( getActivity());
        builder.setMessage("Please Turn On GPS to Run This Application")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick( final DialogInterface dialog,  final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
