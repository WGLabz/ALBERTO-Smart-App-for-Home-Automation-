package in.blogspot.weargenius.alberto.location;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.databases.MyLocationsDB;
import in.blogspot.weargenius.alberto.menu.UniversalMenuItemSelected;

public class LocationMap extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private TextView latLongDataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PreviousLocations.class));
                finish();
            }
        });
        latLongDataView = (TextView) findViewById(R.id.lat_long_data);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        PolylineOptions rectOptions = new PolylineOptions();
        rectOptions.color(Color.argb(255, 85, 166, 27));

        MyLocationsDB locationDB = new MyLocationsDB(getApplicationContext());
        locationDB.open();
        Cursor latLongData = locationDB.getLastLocations();
        int totalData = latLongData.getCount();
        latLongData.moveToFirst();
        for (int count = 0; count < totalData; count++) {
            LatLng sydney = new LatLng(latLongData.getDouble(latLongData.getColumnIndex("latitude")),
                    latLongData.getDouble(latLongData.getColumnIndex("longitude")));
            mMap.addMarker(new MarkerOptions().position(sydney).title("Velocity :" + latLongData.getDouble(latLongData.getColumnIndex("velocity"))));
            latLongData.moveToNext();
            rectOptions.add(sydney);
        }
        latLongData.moveToLast();
        LatLng sydney = new LatLng(latLongData.getDouble(latLongData.getColumnIndex("latitude")),
                latLongData.getDouble(latLongData.getColumnIndex("longitude")));

        mMap.addMarker(new MarkerOptions().position(sydney).title("Last Location."));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.0f));
        mMap.addPolyline(rectOptions);

        latLongDataView.setText("Latitude : " + sydney.latitude + " Longitude : " + sydney.longitude);
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
