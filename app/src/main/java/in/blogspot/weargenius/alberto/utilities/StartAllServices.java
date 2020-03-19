package in.blogspot.weargenius.alberto.utilities;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

import in.blogspot.weargenius.alberto.callInterception.CallDetectService;
import in.blogspot.weargenius.alberto.notifications.NotificationServiceReceiver;

/**
 * Created by oksbwn on 1/1/2016.
 */
public class StartAllServices {
    private Context context = null;

    public StartAllServices(Context con) {
        this.context = con;
        if (!isTheServiceRunning(CallDetectService.class)) {
            Thread th2 = new Thread() {
                public void run() {
                    try {
                        Intent callIntent = new Intent(context, CallDetectService.class);
                        context.startService(callIntent);
                    } catch (Exception e) {
                        Log.d("Exception :", e.getMessage());
                    }
                }
            };
            th2.start();
        }
        if (PendingIntent.getBroadcast(context, 0, new Intent("in.blogspot.weargenius.alberto.notifications.NotificationServiceReceiver"), PendingIntent.FLAG_NO_CREATE) == null) {

            Toast.makeText(context, "Started notifications.", Toast.LENGTH_LONG).show();

            Calendar updateTime = Calendar.getInstance();
            updateTime.setTimeZone(TimeZone.getDefault());
            updateTime.set(Calendar.HOUR_OF_DAY, 12);
            updateTime.set(Calendar.MINUTE, 30);
            Intent downloader = new Intent(context, NotificationServiceReceiver.class);
            downloader.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        }
    }

    private boolean isTheServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
