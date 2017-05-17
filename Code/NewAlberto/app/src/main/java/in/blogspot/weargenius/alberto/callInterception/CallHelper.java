package in.blogspot.weargenius.alberto.callInterception;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import in.blogspot.weargenius.alberto.databases.CallDetailsDB;
import in.blogspot.weargenius.alberto.utilities.CheckMyPresenceAtHome;
import in.blogspot.weargenius.alberto.utilities.PostCallToServer;

/**
 * Helper class to detect incoming and outgoing calls.
 *
 * @author Moskvichev Andrey V.
 */
public class CallHelper {
    private CallDetailsDB callDetailsDB;
    private boolean incomingCallDetected = false;
    private boolean incomingCallReceived = false;
    private String syncedStatus = "No";
    private Context con;
    private TelephonyManager tm;
    private CallStateListener callStateListener;
    private OutgoingReceiver outgoingReceiver;
    private String incomingCallNumber;
    private boolean outputCallDetected;
    private long callStartTime;
    private long callEndTime;

    public CallHelper(Context ctx) {
        this.con = ctx;

        callDetailsDB = new CallDetailsDB(con);
        callStateListener = new CallStateListener();
        outgoingReceiver = new OutgoingReceiver();
    }

    /**
     * Start calls detection.
     */
    public void start() {
        tm = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        con.registerReceiver(outgoingReceiver, intentFilter);
    }

    /**
     * Stop calls detection.
     */
    public void stop() {
        tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
        con.unregisterReceiver(outgoingReceiver);
    }

    /**
     * Listener to detect incoming calls.
     */
    private class CallStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // called when someone is ringing to this phone
                    if (new CheckMyPresenceAtHome().getMyStatus(con)) {
                        new PostCallToServer("http://192.168.0.1/smart_home/API/android/callInformation.php",
                                new String[]{"NO", "STAT"},
                                new String[]{incomingNumber, "" + 1});
                        syncedStatus = "Yes";
                    } else
                        syncedStatus = "No";

                    incomingCallDetected = true;
                    incomingCallNumber = incomingNumber;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://When call is received
                    // called when someone is ringing to this phone
                    callStartTime = System.currentTimeMillis();
                    incomingCallReceived = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // called when Call Completed
                    if (incomingCallDetected && new CheckMyPresenceAtHome().getMyStatus(con)) {
                        new PostCallToServer("http://192.168.0.1/smart_home/API/android/callInformation.php",
                                new String[]{"NO", "STAT"},
                                new String[]{incomingCallNumber, "" + 3});
                    }
                    if (incomingCallDetected && incomingCallReceived) {

                        callEndTime = System.currentTimeMillis();
                        callEndTime = (callEndTime - callStartTime) / 1000;

                        callDetailsDB.open();
                        long x = callDetailsDB.addNewCall("0" + callEndTime, incomingCallNumber, "0", "Incoming", syncedStatus);
                        Log.d("Result:", "" + x + " Phone : " + incomingCallNumber + " " + callEndTime);
                        callDetailsDB.close();
                        incomingCallReceived = false;
                        incomingCallDetected = false;
                    } else if (incomingCallDetected && !incomingCallReceived) {
                        callDetailsDB.open();
                        long x = callDetailsDB.addNewCall("0", incomingCallNumber, "0", "Missed", syncedStatus);
                        Log.d("Result:", "" + x);
                        callDetailsDB.close();
                        incomingCallDetected = false;
                    }
                    if (outputCallDetected) {
                        callEndTime = System.currentTimeMillis();
                        callEndTime = (callEndTime - callStartTime) / 1000;
                        Log.d("Result", "Output call hanged " + callEndTime);
                        callDetailsDB.open();
                        long x = callDetailsDB.addNewCall("0" + callEndTime, incomingCallNumber, "0", "Dialled", syncedStatus);
                        Log.d("Result:", "" + x);
                        callDetailsDB.close();
                        outputCallDetected = false;

                    }
                    break;
            }
        }
    }

    /**
     * Broadcast receiver to detect the outgoing calls.
     */
    public class OutgoingReceiver extends BroadcastReceiver {

        public OutgoingReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            if (new CheckMyPresenceAtHome().getMyStatus(context)) {
                new PostCallToServer("http://192.168.0.1/smart_home/API/android/callInformation.php",
                        new String[]{"NO", "STAT"},
                        new String[]{number, "" + 2});
            }
            outputCallDetected = true;
            incomingCallNumber = number;
            callStartTime = System.currentTimeMillis();
        }
    }

}
