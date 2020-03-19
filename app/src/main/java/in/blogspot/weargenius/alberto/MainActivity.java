package in.blogspot.weargenius.alberto;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import in.blogspot.weargenius.alberto.alarmManager.SmartHomeAlarm;
import in.blogspot.weargenius.alberto.expenses.AllExpenses;
import in.blogspot.weargenius.alberto.location.LocationMap;
import in.blogspot.weargenius.alberto.location.MyLocationTracker;
import in.blogspot.weargenius.alberto.location.PreviousLocations;
import in.blogspot.weargenius.alberto.menu.UniversalMenuItemSelected;
import in.blogspot.weargenius.alberto.notes.NotesInterface;
import in.blogspot.weargenius.alberto.smarthome.ControlAppliances;
import in.blogspot.weargenius.alberto.synchronizations.dailySyncs;
import in.blogspot.weargenius.alberto.utilities.CheckMyPresenceAtHome;
import in.blogspot.weargenius.alberto.utilities.StartAllServices;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private boolean iAmAtHome = false;
    private CardView notesButton;
    private ImageView locationImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        iAmAtHome = new CheckMyPresenceAtHome().getMyStatus(getApplicationContext());
        new StartAllServices(getApplicationContext());

        notesButton = (CardView) findViewById(R.id.notes_cv);
        notesButton.setOnClickListener(this);
        findViewById(R.id.expenses_cv).setOnClickListener(this);
        findViewById(R.id.smart_home_cv).setOnClickListener(this);
        findViewById(R.id.alaram_cv).setOnClickListener(this);
        findViewById(R.id.location_cv).setOnClickListener(this);
        locationImage = (ImageView) findViewById(R.id.location);

        findViewById(R.id.location_cv).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Turn ON/OFF");
                builder.setMessage("Location Map");

                builder.setPositiveButton("MAP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), LocationMap.class));
                        finish();
                    }
                });
                builder.setNeutralButton("LIST", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), PreviousLocations.class));
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
                return true;
            }
        });
        if (isTheServiceRunning(MyLocationTracker.class))
            locationImage.setImageResource(R.drawable.location_running);
        else
            locationImage.setImageResource(R.drawable.location);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.sync_to_desktop).setCheckable(false);
        menu.findItem(R.id.sync_to_desktop).setIcon(R.drawable.offline);
        if (iAmAtHome)
            menu.findItem(R.id.sync_to_desktop).setIcon(R.drawable.online);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sync_to_desktop) {
            item.setIcon(R.drawable.uploading);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new dailySyncs(getApplicationContext());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Synchronization Finished.", Toast.LENGTH_LONG).show();
                            item.setIcon(R.drawable.online);

                        }
                    });
                }
            }).start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        new UniversalMenuItemSelected(item, getApplicationContext());
        finish();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notes_cv:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), NotesInterface.class));
                        finish();
                    }
                }).start();
                break;
            case R.id.expenses_cv:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), AllExpenses.class));
                        finish();
                    }
                }).start();
                break;
            case R.id.smart_home_cv:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), ControlAppliances.class));
                        finish();
                    }
                }).start();
                break;
            case R.id.alaram_cv:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), SmartHomeAlarm.class));
                        finish();
                    }
                }).start();
                break;
            case R.id.location_cv:
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                }
                new Thread() {
                    public void run() {
                        try {
                            if (!isTheServiceRunning(MyLocationTracker.class)) {
                                Intent getMyLocationServiceIntent = new Intent(getApplicationContext(), MyLocationTracker.class);
                                startService(getMyLocationServiceIntent);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        locationImage.setImageResource(R.drawable.location_running);
                                    }
                                });
                            } else {
                                stopService(new Intent(getApplicationContext(), MyLocationTracker.class));
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        locationImage.setImageResource(R.drawable.location);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            Log.d("Exception :", e.getMessage());
                        }
                    }
                }.start();
                break;
        }
    }

    private boolean isTheServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("Please Turn On GPS to Run This Application")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
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
