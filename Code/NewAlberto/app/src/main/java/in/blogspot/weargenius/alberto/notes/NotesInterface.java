package in.blogspot.weargenius.alberto.notes;

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
import in.blogspot.weargenius.alberto.databases.NotesDetailDB;
import in.blogspot.weargenius.alberto.menu.UniversalMenuItemSelected;

public class NotesInterface extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_interface);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DetailNote.class));
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

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.notes_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        NotesDetailDB notesDB = new NotesDetailDB(getApplicationContext());
        notesDB.open();
        Cursor allNotes = notesDB.getAllNotes();
        int i = 0;
        ArrayList<String> noteHeading = new ArrayList<>(allNotes.getCount());
        ArrayList<String> noteBody = new ArrayList<>(allNotes.getCount());
        ArrayList<String> noteDate = new ArrayList<>(allNotes.getCount());
        ArrayList<String> noteTime = new ArrayList<>(allNotes.getCount());
        ArrayList<String> noteSnapsCount = new ArrayList<>(allNotes.getCount());
        ArrayList<Integer> noteId = new ArrayList<>(allNotes.getCount());
        if (allNotes.getCount() > 0)
            for (allNotes.moveToFirst(); !allNotes.isLast(); allNotes.moveToNext()) {

                noteHeading.add(i, allNotes.getString(allNotes.getColumnIndex("noteHeader")));
                noteBody.add(i, allNotes.getString(allNotes.getColumnIndex("noteContent")));
                noteDate.add(i, allNotes.getString(allNotes.getColumnIndex("currentDate")));
                noteTime.add(i, allNotes.getString(allNotes.getColumnIndex("time")));
                noteId.add(i, allNotes.getInt(allNotes.getColumnIndex("Sl_No")));
                int imageCount = 0;
                try {
                    imageCount = allNotes.getString(allNotes.getColumnIndex("images")).split("$#").length - 1;
                } catch (Exception e) {
                }
                noteSnapsCount.add(i, "" + imageCount);

                Log.d("Notes_all", allNotes.getString(allNotes.getColumnIndex("noteContent")));
                i++;
            }
        // specify an adapter (see also next example)
        adapterForNotesList mAdapter = new adapterForNotesList(this, noteId, noteHeading, noteBody, noteDate, noteTime, noteSnapsCount);
        mRecyclerView.setAdapter(mAdapter);
        notesDB.close();
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
