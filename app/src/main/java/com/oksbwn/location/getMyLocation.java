package com.oksbwn.location;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.oksbwn.allDatabaseSetups.databaseSetup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class getMyLocation extends Service {
    protected Context context;
    double velocity=0;
    double latitude=0;
    double longitude=0;
    databaseSetup db=null;
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MMM/yyyy");
    Date date;
    Timer gpsTimer = new Timer();
    boolean gps_recorder_running;
    String TAG="Location Service";
    Location lastLocation;
    float lastprovidertimestamp;
    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate()
    {
        Toast.makeText(getApplicationContext(), "Started Location Service", Toast.LENGTH_LONG).show();
        sdfDate.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        sdfTime.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        startRecording();
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        Toast.makeText(getApplicationContext(), "Location Service Stopped",Toast.LENGTH_LONG).show();
        //db.close();
        if (gpsTimer != null){
            gpsTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        return super.onStartCommand(intent, flags, startId);

    }
    public void startRecording() {
        long checkInterval =120000;
        long minDistance = 1;
        // receive updates
        LocationManager locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        for (String s : locationManager.getAllProviders()) {
            locationManager.requestLocationUpdates(s, checkInterval,
                    minDistance, new LocationListener() {

                        @Override
                        public void onStatusChanged(String provider,
                                                    int status, Bundle extras) {}

                        @Override
                        public void onProviderEnabled(String provider) {}

                        @Override
                        public void onProviderDisabled(String provider) {}

                        @Override
                        public void onLocationChanged(Location location) {
                            // if this is a gps location, we can use it
                            if (location.getProvider().equals(
                                    LocationManager.GPS_PROVIDER)) {
                                doLocationUpdate(location, true);
                            }
                        }
                    });
            // //Toast.makeText(this, "GPS Service STARTED",
            // Toast.LENGTH_LONG).show();
            gps_recorder_running = true;
        }
        // start the gps receiver thread
        gpsTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Location location = getBestLocation();
                doLocationUpdate(location, false);
            }
        }, 0, checkInterval);
    }
    public void doLocationUpdate(Location l, boolean force) {
        long minDistance = 1;
        String formattedDate;
        String formattedTime;
        Log.d(TAG, "update received:" + l);
        if (l == null) {
            Log.d(TAG, "Empty location");
            if (force)
                Toast.makeText(this, "Current location not available",
                        Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastLocation != null) {
            float distance = l.distanceTo(lastLocation);
            Log.d(TAG, "Distance to last: " + distance);
            if (l.distanceTo(lastLocation) < minDistance && !force) {
                Log.d(TAG, "Position didn't change");
                return;
            }
            if (l.getAccuracy() >= lastLocation.getAccuracy()
                    && l.distanceTo(lastLocation) < l.getAccuracy() && !force) {
                Log.d(TAG,
                        "Accuracy got worse and we are still "
                                + "within the accuracy range.. Not updating");
                return;
            }
            if (l.getTime() <= lastprovidertimestamp && !force) {
                Log.d(TAG, "Timestamp not never than last");
                return;
            }
        }
        lastLocation=l;
        lastprovidertimestamp=l.getTime();
        // upload/store your location here
        latitude=l.getLatitude();
        longitude=l.getLongitude();
        velocity=l.getSpeed();
        try
        {
            if(latitude!=0 && longitude!=0)
            {

                date = new Date();
                formattedDate = sdfDate.format(date);
                formattedTime = sdfTime.format(date);
                //Add to database
                // Toast.makeText(getApplicationContext(), latitude+" : "+longitude, Toast.LENGTH_LONG).show();
                db=new databaseSetup(getMyLocation.this);
                db.open();
                db.insertData(""+formattedDate,""+formattedTime,""+latitude,""+longitude,""+velocity);
                db.close();

            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * try to get the 'best' location selected from all providers
     */
    private Location getBestLocation() {
        Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
        Location networkLocation =
                getLocationByProvider(LocationManager.NETWORK_PROVIDER);
        // if we have only one location available, the choice is easy
        if (gpslocation == null) {
            Log.d(TAG, "No GPS Location available.");
            return networkLocation;
        }
        if (networkLocation == null) {
            Log.d(TAG, "No Network Location available");
            return gpslocation;
        }
        // a locationupdate is considered 'old' if its older than the configured
        // update interval. this means, we didn't get a
        // update from this provider since the last check
        long old = System.currentTimeMillis() - 120000;
        boolean gpsIsOld = (gpslocation.getTime() < old);
        boolean networkIsOld = (networkLocation.getTime() < old);
        // gps is current and available, gps is better than network
        if (!gpsIsOld) {
            Log.d(TAG, "Returning current GPS Location");
            return gpslocation;
        }
        // gps is old, we can't trust it. use network location
        if (!networkIsOld) {
            Log.d(TAG, "GPS is old, Network is current, returning network");
            return networkLocation;
        }
        // both are old return the newer of those two
        if (gpslocation.getTime() > networkLocation.getTime()) {
            Log.d(TAG, "Both are old, returning gps(newer)");
            return gpslocation;
        } else {
            Log.d(TAG, "Both are old, returning network(newer)");
            return networkLocation;
        }
    }

    /**
     * get the last known location from a specific provider (network/gps)
     */
    private Location getLocationByProvider(String provider) {
        Location location = null;
        LocationManager locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locationManager.isProviderEnabled(provider)) {
                location = locationManager.getLastKnownLocation(provider);
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Cannot acces Provider " + provider);
        }
        return location;
    }
} 

