package in.blogspot.weargenius.alberto.mobileMessages;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import in.blogspot.weargenius.alberto.utilities.PostCallToServer;

public class ReadInboxMessages extends Service {
    public ReadInboxMessages() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(ReadInboxMessages.this, "Syncing Messages", Toast.LENGTH_SHORT).show();
        Thread th = new Thread() {
            public void run() {
                try {
                    PostCallToServer pc = new PostCallToServer("http://192.168.0.1/smart_home/API/android/getMaxMessageNo.php", new String[]{}, new String[]{});
                    int timer = 0;
                    while (pc.getResponse() == null || timer < 5000) {
                        timer++;
                    }
                    String maxMessageNo = pc.getResponse();
                    Log.d("Message_Process", maxMessageNo);

                    Uri uriSMSURI = Uri.parse("content://sms");
                    Cursor cur = getContentResolver().query(uriSMSURI, new String[]{"_id", "address", "date", "body",
                            "type", "read"}, "date > " + maxMessageNo, null, null);
                    String sms = "";

                    JSONObject jsonObj = new JSONObject();
                    JSONArray jsonArr = new JSONArray();
                    while (cur.moveToNext()) {
                        JSONObject pnObj = new JSONObject();
                        pnObj.put("BODY", cur.getString(cur.getColumnIndex("body")));
                        pnObj.put("ADDRESS", cur.getString(cur.getColumnIndex("address")));
                        pnObj.put("ID", cur.getString(cur.getColumnIndex("_id")));
                        pnObj.put("DATE", cur.getString(cur.getColumnIndex("date")));
                        pnObj.put("TYPE", cur.getString(cur.getColumnIndex("type")));
                        pnObj.put("READ", cur.getString(cur.getColumnIndex("read")));
                        jsonArr.put(pnObj);
                    }
                    jsonObj.put("DATA", jsonArr);
                    Log.d("Message_Process", jsonObj.toString());

                    new PostCallToServer("http://192.168.0.1/smart_home/API/android/saveMessages.php", new String[]{"DATA"}, new String[]{jsonObj.toString()});

                    stopService(new Intent(getBaseContext(), ReadInboxMessages.class));
                    // nManager.cancel(id);
                } catch (Exception e) {
                    Log.d("Message_Process", "Exception");
                    stopService(new Intent(getBaseContext(), ReadInboxMessages.class));
                }
            }
        };
        th.start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d("Service", "Message service closed.");
        super.onDestroy();
    }

}
