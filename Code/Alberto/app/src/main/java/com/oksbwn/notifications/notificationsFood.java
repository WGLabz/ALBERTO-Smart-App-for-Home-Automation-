package com.oksbwn.notifications;
/*
* Change on : 12/12/2015
* Creating new Notificatin Class
* */
import android.app.IntentService;
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
public class notificationsFood extends IntentService  {
    SimpleDateFormat timeToPOPNotification = new SimpleDateFormat("HH");
    int timeInHour=0;
    boolean notInHome=true;
    addFoodToDatabase  afToDb= null;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MMM/yyyy");
    private Timer timer = new Timer();
    public notificationsFood() {
        super("MyServiceName");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("MyService", "About to execute MyTask");
        doChecks();
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
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            if(notInHome){

            }
            if(timeInHour>8 && notInHome && afToDb.addedTodayMeals("breakfast",sdfDate.format(dat)))
            {
                Intent intent = new Intent(getApplicationContext(), foodMenu.class);
                intent.putExtra("type","breakfast");
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
                Notification foodNotification  = new Notification.Builder(this)
                        .setContentTitle("Alberto: Food Manager")
                        .setContentText("Breakfast Completed ?")
                        .setSmallIcon(R.drawable.food_notification)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .addAction(R.drawable.food_notification,"Add", pIntent).build();
                notificationManager.notify(0, foodNotification);
            }
            if(timeInHour>14 && notInHome && afToDb.addedTodayMeals("lunch",sdfDate.format(dat)))
            {
                Intent intent = new Intent(getApplicationContext(), foodMenu.class);
                intent.putExtra("type","lunch");
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
                Notification foodNotification  = new Notification.Builder(this)
                        .setContentTitle("Alberto: Food Manager")
                        .setContentText("Lunch Completed ?")
                        .setSmallIcon(R.drawable.food_notification)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .addAction(R.drawable.food_notification,"Add", pIntent).build();
                notificationManager.notify(0, foodNotification);
            }
            if(timeInHour>19&& notInHome && afToDb.addedTodayMeals("snacks",sdfDate.format(dat)))
            {
                Intent intent = new Intent(getApplicationContext(), foodMenu.class);
                intent.putExtra("type","snacks");
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
                Notification foodNotification  = new Notification.Builder(this)
                        .setContentTitle("Alberto: Food Manager")
                        .setContentText("Snacks Completed ?")
                        .setSmallIcon(R.drawable.food_notification)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .addAction(R.drawable.food_notification,"Add", pIntent).build();
                notificationManager.notify(0, foodNotification);
            }
            if(timeInHour>21 && notInHome && afToDb.addedTodayMeals("dinner",sdfDate.format(dat)))
            {
                Intent intent = new Intent(getApplicationContext(), foodMenu.class);
                intent.putExtra("type","dinner");
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
                Notification foodNotification  = new Notification.Builder(this)
                        .setContentTitle("Alberto: Food Manager")
                        .setContentText("Dinner Completed ?")
                        .setSmallIcon(R.drawable.food_notification)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .addAction(R.drawable.food_notification,"Add", pIntent).build();
                notificationManager.notify(0, foodNotification);
            }
            afToDb.close();
        }
        catch (Exception e)
        {
            Log.d("Error", e.getMessage());
        }
}
}
