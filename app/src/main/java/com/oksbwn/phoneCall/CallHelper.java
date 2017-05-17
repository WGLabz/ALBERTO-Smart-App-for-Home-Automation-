package com.oksbwn.phoneCall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.oksbwn.serverActivity.PostCallToServer;

/**
 * Helper class to detect incoming and outgoing calls.
 * @author Moskvichev Andrey V.
 *
 */
public class CallHelper {
    int i=0;
	/**
	 * Listener to detect incoming calls. 
	 */
	private class CallStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// called when someone is ringing to this phone
                    new PostCallToServer("http://192.168.0.1/smart_home/API/android/callInformation.php",
                        new String[]{"NO","STAT"},
                        new String[]{incomingNumber,""+1});
                    i=1;
				break;
            case TelephonyManager.CALL_STATE_IDLE:
				// called when someone is ringing to this phone
                    if(i==1) {
                        new PostCallToServer("http://192.168.0.1/smart_home/API/android/callInformation.php",
                            new String[]{"NO","STAT"},
                            new String[]{incomingNumber,""+3});
                        i=0;
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

            new PostCallToServer("http://192.168.0.1/smart_home/API/android/callInformation.php",
                    new String[]{"NO","STAT"},
                    new String[]{number,""+2});
	    }
	}

	private Context ctx;
	private TelephonyManager tm;
	private CallStateListener callStateListener;
	
	private OutgoingReceiver outgoingReceiver;

	public CallHelper(Context ctx) {
		this.ctx = ctx;
		
		callStateListener = new CallStateListener();
		outgoingReceiver = new OutgoingReceiver();
	}
	
	/**
	 * Start calls detection.
	 */
	public void start() {
		tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		ctx.registerReceiver(outgoingReceiver, intentFilter);
	}
	
	/**
	 * Stop calls detection.
	 */
	public void stop() {
		tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
		ctx.unregisterReceiver(outgoingReceiver);
	}

}
