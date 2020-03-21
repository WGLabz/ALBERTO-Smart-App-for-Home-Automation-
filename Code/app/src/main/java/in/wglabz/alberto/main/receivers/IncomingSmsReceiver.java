package in.wglabz.alberto.main.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import in.wglabz.alberto.main.helpers.MqttHelper;

public class IncomingSmsReceiver extends BroadcastReceiver {
    private String mqttTopicPrefix;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mqttTopicPrefix = prefs.getString("mqtt_topic","");
        Bundle data  = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String messageBody = smsMessage.getMessageBody();

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode jsonData = mapper.createObjectNode();
            ObjectNode details_ = mapper.createObjectNode();
            details_.put("NUMBER",sender);
            details_.put("CONTENT",messageBody);
            jsonData.put("TYPE","IN_MESSAGE");
            jsonData.put("DETAILS",details_);
            MqttHelper.publishMessage(mqttTopicPrefix+"/message",jsonData.toString());
        }

    }
}
