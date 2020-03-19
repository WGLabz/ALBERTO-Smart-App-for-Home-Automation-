package in.blogspot.weargenius.alberto.location;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import in.blogspot.weargenius.alberto.databases.MyLocationsDB;

public class MyLocationTracker extends Service {
    protected Context context;
    Timer gpsTimer = new Timer();
    boolean gps_recorder_running;
    Location lastLocation;
    float lastprovidertimestamp;
    private double velocity = 0;
    private double latitude = 0;
    private double longitude = 0;
    private MyLocationsDB locationDB = null;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "Started Location Service", Toast.LENGTH_LONG).show();
        startRecording();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "Location Service Stopped", Toast.LENGTH_LONG).show();
        //db.close();
        if (gpsTimer != null) {
            gpsTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }

    public void startRecording() {
        long checkInterval = 120000;
        long minDistance = 1;
        // receive updates
        LocationManager locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        for (String s : locationManager.getAllProviders()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(s, checkInterval,
                    minDistance, new LocationListener() {

                        @Override
                        public void onStatusChanged(String provider,
                                                    int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }

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
        Log.d("MY_LOCATION", "update received:" + l);
        if (l == null) {
            Log.d("MY_LOCATION", "Empty location");
            if (force)
                Log.d("MY_LOCATION", "Current location not available");
            return;
        }
        if (lastLocation != null) {
            float distance = l.distanceTo(lastLocation);
            Log.d("MY_LOCATION", "Distance to last: " + distance);
            if (l.distanceTo(lastLocation) < minDistance && !force) {
                Log.d("MY_LOCATION", "Position didn't change");
                return;
            }
            if (l.getAccuracy() >= lastLocation.getAccuracy()
                    && l.distanceTo(lastLocation) < l.getAccuracy() && !force) {
                Log.d("MY_LOCATION",
                        "Accuracy got worse and we are still "
                                + "within the accuracy range.. Not updating");
                return;
            }
            if (l.getTime() <= lastprovidertimestamp && !force) {
                Log.d("MY_LOCATION", "Timestamp not never than last");
                return;
            }
        }
        lastLocation = l;
        lastprovidertimestamp = l.getTime();
        // upload/store your location here
        latitude = l.getLatitude();
        longitude = l.getLongitude();
        velocity = l.getSpeed();
        try {
            if (latitude != 0 && longitude != 0) {

                locationDB = new MyLocationsDB(MyLocationTracker.this);
                locationDB.open();
                locationDB.addLocation("" + latitude, "" + longitude, "" + velocity);
                locationDB.close();
                Log.d("MY_LOCATION", "" + latitude + " : " + longitude + " : " + velocity);

            }
        } catch (Exception e) {
            Log.d("MY_LOCATION", "Location error");
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
            Log.d("MY_LOCATION", "No GPS Location available.");
            return networkLocation;
        }
        if (networkLocation == null) {
            Log.d("MY_LOCATION", "No Network Location available");
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
            Log.d("MY_LOCATION", "Returning current GPS Location");
            return gpslocation;
        }
        // gps is old, we can't trust it. use network location
        if (!networkIsOld) {
            Log.d("MY_LOCATION", "GPS is old, Network is current, returning network");
            return networkLocation;
        }
        // both are old return the newer of those two
        if (gpslocation.getTime() > networkLocation.getTime()) {
            Log.d("MY_LOCATION", "Both are old, returning gps(newer)");
            return gpslocation;
        } else {
            Log.d("MY_LOCATION", "Both are old, returning network(newer)");
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
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return location;
                }
                location = locationManager.getLastKnownLocation(provider);
            }
        } catch (IllegalArgumentException e) {
            Log.d("MY_LOCATION", "Cannot acces Provider " + provider);
        }
        return location;
    }
} 

