/**
 * 
 */
package hchi590.server;

import static hchi590.server.Constants.APIKEY;
import hchi590.datastorage.DataStorageSingleton;

import java.io.IOException;
import java.util.List;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

/**
 * Sends a GCM push notification to connected clients
 * 
 * @author hchi590
 * 
 */
public class PushNotification implements Runnable {

	private DataStorageSingleton _data;
	private Message _message;

	/**
	 * 
	 * @param args
	 *            Data to attach to GCM message
	 * @param collapse
	 *            Whether GCM message can collapse if more than one is sent
	 *            before responded to by client
	 */
	public PushNotification(String[] args, boolean collapse) {
		_data = DataStorageSingleton.getInstance();
		_message = buildMessage(args, collapse);
	}

	/**
	 * Send GCM message to all clients
	 */
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		Sender sender = new Sender(APIKEY);
		// Attempt to multicast the message using GCM
		MulticastResult result = null;
		try {
			List<String> devices = _data.getRegIds();
			// Multicast only if there are devices connected to this server
			if (!devices.isEmpty()) {
				result = sender.send(_message, _data.getRegIds(), 2);
				// System.out.println(_data.getRegIds());
				// System.out.println(result.getResults());
			}
			long endTime = System.currentTimeMillis();
			long timeToConstruct = endTime - startTime;

			System.out.println("Finished GCM broadcast, time to construct: "
					+ timeToConstruct);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Build a GCM message with the given arguments
	 * 
	 * @param args
	 *            Data to attach to GCM message
	 * @param collapse
	 *            Whether GCM message can be collapsed
	 * @return GCM Message
	 */
	private Message buildMessage(String[] args, boolean collapse) {
		Message.Builder messageBuilder = new Message.Builder();
		for (int i = 0; i < args.length; i++) {
			messageBuilder.addData(("data" + i), args[i]);
			// System.out.println(args[i]);
		}
		messageBuilder.addData("time",
				Long.toString(System.currentTimeMillis()));
		if (collapse) {
			messageBuilder.collapseKey("sync");
		}
		return messageBuilder.build();
	}

}
