package com.hchi590.chatclient;

import static com.hchi590.chatclient.Constants.SENDER_ID;
import static com.hchi590.chatclient.Constants.GCM_MESSAGE_RECEIVED;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * 
 * @author hchi590
 * Handles incoming GCM notifications.
 */
public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onError(Context context, String errorId) {

	}

	/**
	 * Broadcasts information from incoming GCM notification.
	 */
	@Override
	protected void onMessage(Context context, Intent intent) {
		String header = intent.getExtras().getString("data0");
		String index = intent.getExtras().getString("data1");
		long time = Long.parseLong(intent.getExtras().getString("time"));
		long timeToArrive = System.currentTimeMillis() - time;
		Log.i("timing",
				"Time taken for notification to arrive since build: "
						+ Long.toString(timeToArrive));
		Log.i("test", index);
		Intent display = new Intent(GCM_MESSAGE_RECEIVED);
		display.putExtra("header", header);
		display.putExtra("data", index);
		display.putExtra("time", time);
		context.sendBroadcast(display);
	}

	@Override
	protected void onRegistered(Context context, String regId) {

	}

	@Override
	protected void onUnregistered(Context context, String regId) {

	}
}
