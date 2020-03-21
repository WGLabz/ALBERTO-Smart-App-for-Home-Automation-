package in.wglabz.alberto.main.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CallHelper {
    private boolean incomingCallDetected = false;
    private boolean incomingCallReceived = false;
    private Context con;
    private TelephonyManager telephonyManager;
    private CallStateListener callStateListener;
    private OutgoingReceiver outgoingReceiver;
    private boolean outputCallDetected;
    private long callStartTime;
    private long callEndTime;

    public CallHelper(Context ctx) {
        this.con = ctx;
        callStateListener = new CallStateListener();
        outgoingReceiver = new OutgoingReceiver();
    }


    public void start() {
        telephonyManager = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        con.registerReceiver(outgoingReceiver, intentFilter);
    }

    /**
     * Stop calls detection.
     */
    public void stop() {
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
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
                    incomingCallDetected = true;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    callStartTime = System.currentTimeMillis();
                    incomingCallReceived = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // called when Call Completed
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode jsonData = mapper.createObjectNode();
                    ObjectNode details_ = mapper.createObjectNode();
                    details_.put("NUMBER",incomingNumber);

                    if (incomingCallDetected && incomingCallReceived) {
                        callEndTime = System.currentTimeMillis();
                        callEndTime = (callEndTime - callStartTime) / 1000;

                        details_.put("DURATION",callEndTime);
                        jsonData.put("DETAILS",details_);
                        jsonData.put("TYPE","INCOMING_CALL_RECEIVED");
                        MqttHelper.publishMessage("bikash/call",jsonData.toString());

                    } else if (incomingCallDetected && !incomingCallReceived) {
                        details_.put("DURATION",0);
                        jsonData.put("DETAILS",details_);
                        jsonData.put("TYPE","INCOMING_CALL_MISSED");
                        MqttHelper.publishMessage("bikash/call",jsonData.toString());

                    }
                    if (outputCallDetected) {
                        callEndTime = System.currentTimeMillis();
                        callEndTime = (callEndTime - callStartTime) / 1000;
                        details_.put("DURATION",callEndTime);

                        jsonData.put("DETAILS",details_);
                        jsonData.put("TYPE","OUTGOING_CALL");

                        MqttHelper.publishMessage("bikash/call",jsonData.toString());
                    }
                    incomingCallReceived = false;
                    incomingCallDetected = false;
                    outputCallDetected = false;

                    break;
            }
        }
    }

    /**
     * Broadcast receiver to detect the outgoing calls.
     */
    public class OutgoingReceiver extends BroadcastReceiver {

        public OutgoingReceiver() {
            Log.w("CALL_RECEIVER","OUTGOING CALL REGISTERD");
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w("CALL_RECEIVER","Out Call Detected !!");
            outputCallDetected = true;
            callStartTime = System.currentTimeMillis();
        }
    }

}