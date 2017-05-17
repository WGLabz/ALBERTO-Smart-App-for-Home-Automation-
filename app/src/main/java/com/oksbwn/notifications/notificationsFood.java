package com.oksbwn.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.oksbwn.R;
import com.oksbwn.dailyFoods.foodMenu;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.oksbwn.allDatabaseSetups.addFoodToDatabase;
public class notificationsFood extends Service {
    SimpleDateFormat timeToPOPNotification = new SimpleDateFormat("HH");
    int timeInHour=0;
    boolean notInHome=true;
    addFoodToDatabase  afToDb= null;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MMM/yyyy");
    private Timer timer = new Timer();

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate()
    {

        Toast.makeText(getApplicationContext(), "Started Alert Service", Toast.LENGTH_LONG).show();
       // nManager.cancel(id);
        timer.scheduleAtFixedRate( new TimerTask() {

            public void run() {
                doChecks();
            }

        }, 0, 900000);
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        Toast.makeText(getApplicationContext(), "Alert Service Stopped",Toast.LENGTH_LONG).show();
        //db.close();
        if (timer != null){
            timer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        return super.onStartCommand(intent, flags, startId);

    }
public void doChecks(){
    try
    {
        WifiManager wifiMgr = (WifiManager) notificationsFood.this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        String name = wifiInfo.getSSID();
       // Toast.makeText(notificationsFood.this,name, Toast.LENGTH_LONG).show();
        if(name.contains("oksbwn"))
            notInHome=false;
        else
            notInHome=true;
        afToDb= new addFoodToDatabase(notificationsFood.this);
        afToDb.open();
        //Codes Those Executes with time
        Date dat= new Date();
        timeInHour=Integer.parseInt(timeToPOPNotification.format(dat));
        if(notInHome){
            
        }
        if(timeInHour>8 && notInHome && afToDb.addedTodayMeals("breakfast",sdfDate.format(dat)))
        {
            NotificationManager nManagerBreakfast=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            String body="Breakfast Completed ?";
            String tle="Alberto";
            Notification n =new Notification(R.drawable.food_notification,body,System.currentTimeMillis());
            Intent i=new Intent(getApplicationContext(),foodMenu.class);
            i.putExtra("type","breakfast");
            PendingIntent pen= PendingIntent.getActivity(getApplicationContext(),0,i,0);
            n.setLatestEventInfo(getApplicationContext(),tle,body,pen);
            n.defaults=Notification.DEFAULT_ALL;
            n.flags|=n.FLAG_AUTO_CANCEL;
            nManagerBreakfast.notify(1233244,n);
        }
        if(timeInHour>14 && notInHome && afToDb.addedTodayMeals("lunch",sdfDate.format(dat)))
        {
            NotificationManager nManagerLunch=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Intent i=new Intent(getApplicationContext(),foodMenu.class);
            i.putExtra("type","lunch");
            PendingIntent pen= PendingIntent.getActivity(getApplicationContext(),0,i,0);
            String body="Lunch Completed ?";
            String tle="Alberto";
            Notification n =new Notification(R.drawable.food_notification,body,System.currentTimeMillis());
            n.setLatestEventInfo(getApplicationContext(),tle,body,pen);
            n.defaults=Notification.DEFAULT_ALL;
            n.flags|=n.FLAG_AUTO_CANCEL;
            nManagerLunch.notify(423542345,n);
        }
        if(timeInHour>19&& notInHome && afToDb.addedTodayMeals("snacks",sdfDate.format(dat)))
        {
            NotificationManager nManagerSnacks=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Intent i=new Intent(getApplicationContext(),foodMenu.class);
            i.putExtra("type","snacks");
            PendingIntent pen= PendingIntent.getActivity(getApplicationContext(),0,i,0);
            String body="Snacks Completed ?";
            String tle="Alberto";
            Notification n =new Notification(R.drawable.food_notification,body,System.currentTimeMillis());
            n.setLatestEventInfo(getApplicationContext(),tle,body,pen);
            n.defaults=Notification.DEFAULT_ALL;
            n.flags|=n.FLAG_AUTO_CANCEL;
            nManagerSnacks.notify(3546534,n);
        }
        if(timeInHour>21 && notInHome && afToDb.addedTodayMeals("dinner",sdfDate.format(dat)))
        {
            NotificationManager nManagerDinner=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Intent i=new Intent(getApplicationContext(),foodMenu.class);
            i.putExtra("type","dinner");
            PendingIntent pen= PendingIntent.getActivity(getApplicationContext(),0,i,0);
            String body="Dinner Completed ?";
            String tle="Alberto";
            Notification n =new Notification(R.drawable.food_notification,body,System.currentTimeMillis());
            n.setLatestEventInfo(getApplicationContext(),tle,body,pen);
            n.defaults=Notification.DEFAULT_ALL;
            n.flags|=n.FLAG_AUTO_CANCEL;
            nManagerDinner.notify(346456,n);
        }
        afToDb.close();
    }
    catch (Exception e)
    {
        Log.d("Error", e.getMessage());
    }
}
}
