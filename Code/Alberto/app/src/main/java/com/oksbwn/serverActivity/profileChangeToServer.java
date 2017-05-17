package com.oksbwn.serverActivity;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OKSBWN on 8/2/2015.
 */
public class profileChangeToServer {
    public int sendDataToControlAppliances(int data) {
        try {
            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                    HttpVersion.HTTP_1_1);
            HttpClient client = new DefaultHttpClient(params);
            HttpPost post=null;
            switch(data){
                case 1:
                    post = new HttpPost("http://192.168.0.1/API/fromLappyServer/myProfiles/leavingRoom.php");
                    break;
                case 2:
                    post = new HttpPost("http://192.168.0.1/API/fromLappyServer/myProfiles/cameToRoom.php");
                    break;
                case 3:
                    post = new HttpPost("http://192.168.0.1/API/fromLappyServer/myProfiles/goodNight.php");
                    break;
                case 4:
                    post = new HttpPost("http://192.168.0.1/API/fromLappyServer/myProfiles/tvMode.php");
                    break;
                case 5:
                    post = new HttpPost("http://192.168.0.1/API/fromLappyServer/myProfiles/movieMode.php");
                    break;
            }
            HttpResponse response = client.execute(post);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer res = new StringBuffer();
            String line = "";

            while ((line = rd.readLine()) != null) {
                res.append(line);
            }

            if (res.toString().contains("1")) {
                data=1;
            }
        } catch (Exception e) {Log.d("Appliance Error",e.getMessage());
        }
        return data;
    }
}
