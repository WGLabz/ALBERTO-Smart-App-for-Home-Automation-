package com.oksbwn.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.oksbwn.R;
import com.oksbwn.allSettings.settingsMenuFragment;
import com.oksbwn.allDatabaseSetups.addFoodToDatabase;
import com.oksbwn.allDatabaseSetups.expenseDatabaseSetup;
import com.oksbwn.allDatabaseSetups.purchasedItemDatabaseSetup;
import com.oksbwn.allDatabaseSetups.databaseSetup;
import com.oksbwn.navigation_drawer.CommunityFragment;
import com.oksbwn.navigation_drawer.FindPeopleFragment;
import com.oksbwn.navigation_drawer.PhotosFragment;
import com.oksbwn.navigation_drawer.NavDrawerListAdapter;
import com.oksbwn.navigation_drawer.NavDrawerItem;
import com.oksbwn.notes.NoteInterface;
import com.oksbwn.allDatabaseSetups.notesDatabaseSetup;
import com.oksbwn.notifications.notificationsFood;
import com.oksbwn.phoneCall.CallDetectService;
import com.oksbwn.processMessages.readInboxMessage;
import com.oksbwn.readContactsToPC.SyncContacts;
import com.oksbwn.serverActivity.UploadFileToServer;
import com.oksbwn.vlc.vlcControlsFragment;

public class mainActivity extends Activity{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    //
    String paths[];

    private boolean iAmAtHome=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Check and run all background services
        setupAllServices();

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem("E- Mail", navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
        if(iAmAtHome) {
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        }else{
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        }


        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.stats).setCheckable(false);
        menu.findItem(R.id.stats).setIcon(R.drawable.offline);
        if(iAmAtHome)
            menu.findItem(R.id.stats).setIcon(R.drawable.online);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        Thread th;
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.stats:
                if(iAmAtHome) {
                    final MenuItem TempItem = item;
                    item.setIcon(R.drawable.uploading);
                    th = new Thread() {
                        public void run() {
                            try {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(mainActivity.this, "Started Syncing", Toast.LENGTH_LONG).show();
                                    }
                                });

                                try {
                                    Intent i = new Intent(mainActivity.this, readInboxMessage.class);
                                    startService(i);
                                } catch (Exception e) {
                                    Log.d("Settings Error", e.getMessage());
                                }
                                try {
                                    Intent i = new Intent(mainActivity.this, SyncContacts.class);
                                    startService(i);
                                } catch (Exception e) {
                                    Log.d("Exception :", e.getMessage());
                                }
                                try {
                                    notesDatabaseSetup notesDb = new notesDatabaseSetup(mainActivity.this);
                                    notesDb.open();
                                    notesDb.putDataToServer();
                                    notesDb.close();
                                    addFoodToDatabase foodDb = new addFoodToDatabase(mainActivity.this);
                                    foodDb.open();
                                    foodDb.putDataToServer();
                                    foodDb.close();
                                } catch (Exception e) {
                                }

                                try {
                                    try {
                                        expenseDatabaseSetup eDb = new expenseDatabaseSetup(mainActivity.this);
                                        eDb.open();
                                        eDb.putDataToServer();
                                        eDb.close();
                                    } catch (Exception e) {
                                        Log.d("Update sync error", "Error in syncing Expenses");
                                    }
                                    try {
                                        purchasedItemDatabaseSetup pItemDb = new purchasedItemDatabaseSetup(mainActivity.this);
                                        pItemDb.open();
                                        paths = pItemDb.getDataToUpload();
                                        for (int i = 0; i < paths.length; i++) {
                                            new UploadFileToServer(paths[i], getApplicationContext());
                                            i++;
                                        }
                                        pItemDb.close();
                                    } catch (Exception e) {
                                        Log.d("Update sync error", "Error in syncing Expenses");
                                    }

                                } catch (Exception e) {
                                }
                                new Thread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    databaseSetup locationDb = new databaseSetup(mainActivity.this);
                                                    locationDb.open();
                                                    locationDb.putDataToServer();
                                                    locationDb.close();
                                                } catch (Exception e) {
                                                }
                                            }
                                        }
                                ).start();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        TempItem.setIcon(R.drawable.online);
                                        Toast.makeText(mainActivity.this, "Syncing finished.", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (Exception e) {
                                Log.d("Exception :", e.getMessage());
                            }

                        }
                    };
                    th.start();
                }else{
                    Toast.makeText(getApplicationContext(),"Can't sync. Out of home now.",Toast.LENGTH_LONG).show();
                }
                return true;
            // action with ID action_settings was selected
            default:
                return super.onOptionsItemSelected(item);
             }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.stats).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = null;
                new Thread() {
                    public void run() {
                        try {
                            Intent i = new Intent(getApplicationContext(),com.oksbwn.emails_gmail.gmail_inbox.class);
                            startActivity(i);
                        }catch(Exception e){
                            Log.d("Exception :",e.getMessage());
                        }
                    }
                }.start();
                break;
            case 2:
                fragment = new PhotosFragment();
                break;
            case 3:
                fragment = new CommunityFragment();
                break;
            case 4:
                if(iAmAtHome){
                    fragment = null;
                    new Thread() {
                        public void run() {
                            try {
                                Intent i = new Intent(getApplicationContext(),com.oksbwn.xbmc_remote.remote_interface.class);
                                startActivity(i);
                            }catch(Exception e){
                                Log.d("Exception :",e.getMessage());
                            }
                        }
                    }.start();
                }else{
                    fragment = new settingsMenuFragment();
                }
                break;
            case 5:
                fragment = new settingsMenuFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    private boolean isTheServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void setupAllServices(){
        if (PendingIntent.getBroadcast(this, 0,new Intent("com.oksbwn.notifications.serviceReceiver"),PendingIntent.FLAG_NO_CREATE) == null) {

            Toast.makeText(getApplicationContext(),"Started notifications.",Toast.LENGTH_LONG).show();

            Calendar updateTime = Calendar.getInstance();
            updateTime.setTimeZone(TimeZone.getDefault());
            updateTime.set(Calendar.HOUR_OF_DAY, 12);
            updateTime.set(Calendar.MINUTE, 30);
            Intent downloader = new Intent(getApplicationContext(), com.oksbwn.notifications.serviceReceiver.class);
            downloader.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        }
        if(!isTheServiceRunning(CallDetectService.class)) {
            Thread th2 = new Thread() {
                public void run() {
                    try {
                        Intent callIntent = new Intent(getApplicationContext(), CallDetectService.class);
                        startService(callIntent);
                    } catch (Exception e) {
                        Log.d("Exception :", e.getMessage());
                    }
                }
            };
            th2.start();
        }
        try{
            WifiManager wifiMgr = (WifiManager) mainActivity.this.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if(wifiInfo.getSSID().contains("oksbwn"))
                iAmAtHome=true;
        }catch(Exception e){
            Log.d("Network Error",e.getMessage());
        }
    }
}
