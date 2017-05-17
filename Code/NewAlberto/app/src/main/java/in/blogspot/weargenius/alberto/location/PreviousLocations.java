package in.blogspot.weargenius.alberto.location;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.databases.MyLocationsDB;
import in.blogspot.weargenius.alberto.menu.UniversalMenuItemSelected;

public class PreviousLocations extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_locations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LocationMap.class));
                finish();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.old_locations);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        MyLocationsDB locationDB = new MyLocationsDB(getApplicationContext());
        locationDB.open();
        Cursor lastLocations = locationDB.getAllLocations();
        int i = 0;
        ArrayList<String> locationLatitude = new ArrayList<>(lastLocations.getCount());
        ArrayList<String> locationLongitude = new ArrayList<>(lastLocations.getCount());
        ArrayList<String> locationDate = new ArrayList<>(lastLocations.getCount());
        ArrayList<String> locationTime = new ArrayList<>(lastLocations.getCount());
        ArrayList<String> locationVelocity = new ArrayList<>(lastLocations.getCount());
        ArrayList<Integer> locationId = new ArrayList<>(lastLocations.getCount());
        if (lastLocations.getCount() > 0)
            for (lastLocations.moveToFirst(); !lastLocations.isLast(); lastLocations.moveToNext()) {

                locationLatitude.add(i, lastLocations.getString(lastLocations.getColumnIndex("latitude")));
                locationLongitude.add(i, lastLocations.getString(lastLocations.getColumnIndex("longitude")));
                locationDate.add(i, lastLocations.getString(lastLocations.getColumnIndex("currentDate")));
                locationTime.add(i, lastLocations.getString(lastLocations.getColumnIndex("currentTime")));
                locationVelocity.add(i, lastLocations.getString(lastLocations.getColumnIndex("velocity")));
                locationId.add(i, lastLocations.getInt(lastLocations.getColumnIndex("Sl_No")));

                Log.d("Old_Locations", lastLocations.getString(lastLocations.getColumnIndex("latitude")));
                i++;
            }
        // specify an adapter (see also next example)
        adapterForPreviousLocationsList mAdapter = new adapterForPreviousLocationsList(this, locationId, locationLatitude, locationLongitude, locationDate, locationTime, locationVelocity);
        mRecyclerView.setAdapter(mAdapter);
        locationDB.close();
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
}
