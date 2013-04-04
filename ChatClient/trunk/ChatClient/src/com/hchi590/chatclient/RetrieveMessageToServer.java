/**
 * 
 */
package com.hchi590.chatclient;

import static com.hchi590.chatclient.Constants.MESSAGE_UPDATE;
import static com.hchi590.chatclient.Constants.RETRIEVE;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

/**
 * @author hchi590 Retrieve any new messages from the server.
 */
public class RetrieveMessageToServer extends ToServer {

	private int _index;

	/**
	 * 
	 * @param context
	 *            Parent Context
	 * @param index
	 *            Index of message to start retrieving from i.e. the index of
	 *            the last element in the messages list in the calling Context.
	 */
	public RetrieveMessageToServer(Context context, int index) {
		super(context);
		_index = index;
	}

	/**
	 * Send index of messages to be retrieved from to the server, then broadcast
	 * an Intent containing the retrieved messages so the Parent Context can
	 * handle them.
	 */
	protected void execute() {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					_socket.getOutputStream());
			bos.write(_headerByte);
			bos.flush();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeInt(_index);
			out.flush();
			ObjectInputStream in = new ObjectInputStream(
					_socket.getInputStream());
			ArrayList<Message> messages;
			messages = (ArrayList<Message>) in.readObject();
			//Broadcast Intent containing new messages
			Intent update = new Intent(MESSAGE_UPDATE);
			update.putExtra("messages", messages);
			_context.sendBroadcast(update);
			out.close();
			bos.close();
			in.close();
			_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@Override
	protected void initialise() {
		_header += RETRIEVE;
	}

}
