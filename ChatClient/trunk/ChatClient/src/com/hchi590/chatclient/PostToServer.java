/**
 * 
 */
package com.hchi590.chatclient;

import static com.hchi590.chatclient.Constants.POST;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

/**
 * @author hchi590 Post a message to the server
 */
public class PostToServer extends ToServer {

	private Message _message;

	/**
	 * 
	 * @param context
	 *            Parent Context
	 * @param message
	 *            Message to be sent through to server
	 */
	public PostToServer(Context context, Message message) {
		super(context);
		_message = message;
	}

	/**
	 * Posts serialized message using ObjectOutputStream to server. Waits for
	 * server response before exiting to prevent messages from being sent out of
	 * order.
	 */
	protected void execute() {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					_socket.getOutputStream());
			bos.write(_headerByte);
			bos.flush();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(_message);
			oos.flush();
			ObjectInputStream in = new ObjectInputStream(
					_socket.getInputStream());
			in.readUTF();
			oos.close();
			bos.close();
			in.close();
			_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@Override
	protected void initialise() {
		_header += POST;
	}

}
