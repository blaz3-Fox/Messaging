/**
 * 
 */
package com.hchi590.chatclient;

import static com.hchi590.chatclient.Constants.GCM_ID_REGISTERED;
import static com.hchi590.chatclient.Constants.REGISTER;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.Intent;

/**
 * @author hchi590 Registers GCM ID on ChatServer. Also receives a unique name
 *         determined by the server.
 */
public class RegisterToServer extends ToServer {

	private String _regID;

	/**
	 * 
	 * @param context
	 *            Parent Context
	 * @param regID
	 *            GCM ID to be registered on server
	 */
	public RegisterToServer(Context context, String regID) {
		super(context);
		_regID = regID;
	}

	/**
	 * Send serialized string containing GCM id to server. Waits for server to
	 * respond with a name for this device before broadcasting an Intent with
	 * attached information back to ChatActivity.
	 */
	protected void execute() {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					_socket.getOutputStream());
			bos.write(_headerByte);
			bos.flush();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeUTF(_regID);
			out.flush();
			ObjectInputStream in = new ObjectInputStream(
					_socket.getInputStream());
			String name = in.readUTF();
			in.close();
			out.close();
			bos.close();
			_socket.close();
			Intent registration = new Intent(GCM_ID_REGISTERED);
			registration.putExtra("name", name);
			_context.sendBroadcast(registration);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@Override
	protected void initialise() {
		_header += REGISTER;
	}

}
