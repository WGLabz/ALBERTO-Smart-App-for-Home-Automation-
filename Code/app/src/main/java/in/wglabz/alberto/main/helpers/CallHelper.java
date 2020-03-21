package in.wglabz.alberto.main.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CallHelper {
    private final String mqttTopicPrefix;
    private boolean incomingCallDetected = false;
    private boolean incomingCallReceived = false;
    private Context con;
    private TelephonyManager telephonyManager;
    private CallStateListener callStateListener;
    private long callStartTime;
    private long callEndTime;

    public CallHelper(Context ctx) {
        this.con = ctx;
        callStateListener = new CallStateListener();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mqttTopicPrefix = prefs.getString("mqtt_topic","");
    }

    public void start() {
        telephonyManager = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void stop() {
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
    }

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
                        MqttHelper.publishMessage(mqttTopicPrefix+"/call",jsonData.toString());

                    } else if (incomingCallDetected && !incomingCallReceived) {
                        details_.put("DURATION",0);
                        jsonData.put("DETAILS",details_);
                        jsonData.put("TYPE","INCOMING_CALL_MISSED");
                        MqttHelper.publishMessage(mqttTopicPrefix+"/call",jsonData.toString());

                    }
                  else {
                        callEndTime = System.currentTimeMillis();
                        callEndTime = (callEndTime - callStartTime) / 1000;
                        details_.put("DURATION",callEndTime);

                        jsonData.put("DETAILS",details_);
                        jsonData.put("TYPE","OUTGOING_CALL");

                        MqttHelper.publishMessage(mqttTopicPrefix+"/call",jsonData.toString());
                    }
                    incomingCallReceived = false;
                    incomingCallDetected = false;
                    break;
            }
        }
    }
}