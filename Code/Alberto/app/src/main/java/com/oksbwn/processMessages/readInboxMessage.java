package com.oksbwn.processMessages;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.oksbwn.location.getMyLocation;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class readInboxMessage extends Service {
    public readInboxMessage() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate()
    {
       Thread th=new Thread() {
       public void run() {
        try {

            HttpGet httpget = new HttpGet("http://192.168.0.1/smart_home/API/android/getMaxMessageNo.php");
            HttpClient client= new DefaultHttpClient();
            BasicResponseHandler responseHandler = new BasicResponseHandler();
            String SetServerString = client.execute(httpget, responseHandler);

            Uri uriSMSURI = Uri.parse("content://sms");
            Cursor cur = getContentResolver().query(uriSMSURI, new String[]{"_id","address", "date", "body",
                    "type", "read"},"date > "+SetServerString, null,null);
            String sms = "";
            JSONObject jsonObj = new JSONObject();
            JSONArray jsonArr = new JSONArray();
            while (cur.moveToNext()) {
                JSONObject pnObj = new JSONObject();
                pnObj.put("BODY",cur.getString(cur.getColumnIndex("body")));
                pnObj.put("ADDRESS",cur.getString(cur.getColumnIndex("address")));
                pnObj.put("ID",cur.getString(cur.getColumnIndex("_id")));
                pnObj.put("DATE",cur.getString(cur.getColumnIndex("date")));
                pnObj.put("TYPE",cur.getString(cur.getColumnIndex("type")));
                pnObj.put("READ",cur.getString(cur.getColumnIndex("read")));
                jsonArr.put(pnObj);
            }
            jsonObj.put("DATA", jsonArr);
            Log.d("Message is : ",jsonObj.toString());

            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                    HttpVersion.HTTP_1_1);
            client = new DefaultHttpClient(params);
            HttpPost post = new HttpPost("http://192.168.0.1/smart_home/API/android/saveMessages.php");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("DATA",jsonObj.toString()));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs,"utf-8"));
            client.execute(post);

            stopService(new Intent(getBaseContext(),readInboxMessage.class));
            // nManager.cancel(id);
        }catch(Exception e){
            Log.d("Exception SMS",e.getMessage());
            stopService(new Intent(getBaseContext(),readInboxMessage.class));
        }
       }
       };
        th.start();
        super.onCreate();
    }
    @Override
    public void onDestroy()
    {
        Log.d("Service","Message service closed.");
        super.onDestroy();
    }

}
