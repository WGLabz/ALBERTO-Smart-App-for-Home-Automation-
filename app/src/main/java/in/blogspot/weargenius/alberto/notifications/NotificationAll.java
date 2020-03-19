package in.blogspot.weargenius.alberto.notifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import in.blogspot.weargenius.alberto.R;
import in.blogspot.weargenius.alberto.databases.FoodHadTodayDB;
import in.blogspot.weargenius.alberto.foods.FoodMenu;
import in.blogspot.weargenius.alberto.utilities.CheckMyPresenceAtHome;

public class NotificationAll extends IntentService {
    SimpleDateFormat timeToPOPNotification = new SimpleDateFormat("HH");
    int timeInHour = 0;
    boolean notInHome = true;
    FoodHadTodayDB afToDb = null;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MMM/yyyy");
    private Timer timer = new Timer();

    public NotificationAll() {
        super("MyServiceName");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Notifications_All", "About to execute MyTask");
        doChecks();
    }

    public void doChecks() {
        try {
            CheckMyPresenceAtHome checkPresence = new CheckMyPresenceAtHome();
            notInHome = checkPresence.getMyStatus(getApplicationContext());
            afToDb = new FoodHadTodayDB(NotificationAll.this);
            afToDb.open();
            //Codes Those Executes with time
            Date dat = new Date();
            timeInHour = Integer.parseInt(timeToPOPNotification.format(dat));
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notInHome) {

            }
            if (timeInHour > 8 && notInHome && afToDb.addedTodayMeals("breakfast", sdfDate.format(dat))) {
                Intent intent = new Intent(getApplicationContext(), FoodMenu.class);
                intent.putExtra("TYPE", "breakfast");
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
                Notification foodNotification = new Notification.Builder(this)
                        .setContentTitle("Alberto: Food Manager")
                        .setContentText("Breakfast Completed ?")
                        .setSmallIcon(R.drawable.food_notification)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true).build();
                notificationManager.notify(0, foodNotification);
            }
            if (timeInHour > 14 && notInHome && afToDb.addedTodayMeals("lunch", sdfDate.format(dat))) {
                Intent intent = new Intent(getApplicationContext(), FoodMenu.class);
                intent.putExtra("TYPE", "lunch");
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
                Notification foodNotification = new Notification.Builder(this)
                        .setContentTitle("Alberto: Food Manager")
                        .setContentText("Lunch Completed ?")
                        .setSmallIcon(R.drawable.food_notification)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true).build();
                notificationManager.notify(0, foodNotification);
            }
            if (timeInHour > 19 && notInHome && afToDb.addedTodayMeals("snacks", sdfDate.format(dat))) {
                Intent intent = new Intent(getApplicationContext(), FoodMenu.class);
                intent.putExtra("TYPE", "snacks");
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
                Notification foodNotification = new Notification.Builder(this)
                        .setContentTitle("Alberto: Food Manager")
                        .setContentText("Snacks Completed ?")
                        .setSmallIcon(R.drawable.food_notification)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true).build();
                notificationManager.notify(0, foodNotification);
            }
            if (timeInHour > 21 && notInHome && afToDb.addedTodayMeals("dinner", sdfDate.format(dat))) {
                Intent intent = new Intent(getApplicationContext(), FoodMenu.class);
                intent.putExtra("TYPE", "dinner");
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
                Notification foodNotification = new Notification.Builder(this)
                        .setContentTitle("Alberto: Food Manager")
                        .setContentText("Dinner Completed ?")
                        .setSmallIcon(R.drawable.food_notification)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true).build();
                notificationManager.notify(0, foodNotification);
            }
            afToDb.close();
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
    }
}