/**
 * 
 */
package com.hchi590.chatclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

/**
 * @author hchi590 Attempts to retrieve a server address from a known naming
 *         server.
 */
public class GetServer extends Thread {

	public GetServer() {

	}

	public void run() {
		try {
			Socket socket = new Socket("en-301-0167-030-28521", 35000);
			ObjectInputStream in = new ObjectInputStream(
					socket.getInputStream());
			String address = (String) in.readObject();
			try {
				ServerAddress.getInstance().setServerAddress(address);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.i("get", address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//Notify waiting threads that operation is complete.
		synchronized (this) {
			this.notifyAll();
		}
	}
}
