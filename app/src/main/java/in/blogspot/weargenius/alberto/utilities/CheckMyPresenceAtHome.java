package in.blogspot.weargenius.alberto.utilities;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class CheckMyPresenceAtHome {
    private boolean status = false;

    public boolean getMyStatus(Context con) {
        try {
            WifiManager wifiMgr = (WifiManager) con.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if (wifiInfo.getSSID().contains("oksbwn"))
                status = true;
        } catch (Exception e) {
            Log.d("Network Error", "Wifi is Off");
            status = false;
        }
        return status;
    }
}

