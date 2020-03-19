package in.blogspot.weargenius.alberto.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationServiceReceiver extends BroadcastReceiver {
    public NotificationServiceReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent dailyUpdater = new Intent(context, NotificationAll.class);
        context.startService(dailyUpdater);
        Log.d("Notifications_All", "Called context.startService from AlarmReceiver.onReceive");
    }
}
