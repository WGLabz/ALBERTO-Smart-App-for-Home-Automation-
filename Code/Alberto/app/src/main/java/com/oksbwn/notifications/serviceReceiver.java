package com.oksbwn.notifications;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class serviceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent dailyUpdater = new Intent(context, notificationsFood.class);

        context.startService(dailyUpdater);
        Log.d("AlarmReceiver", "Called context.startService from AlarmReceiver.onReceive");
    }
}
