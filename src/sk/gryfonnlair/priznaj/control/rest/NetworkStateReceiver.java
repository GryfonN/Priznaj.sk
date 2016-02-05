package sk.gryfonnlair.priznaj.control.rest;

import sk.gryfonnlair.priznaj.PriznajApplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Receiver ktory reaguje na zmenu network connectivity
 * 
 * @author gryfonn
 * 
 */
public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (PriznajApplication.D) {
			Log.d(PriznajApplication.DEBUG_TAG, "NetworkStateReceiver>Network connectivity change");
		}
		if (intent.getExtras() != null) {
			RestDroid.isNetworkConnected(context);
		}
	}
}
