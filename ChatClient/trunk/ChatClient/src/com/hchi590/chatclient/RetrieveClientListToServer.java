/**
 * 
 */
package com.hchi590.chatclient;

import static com.hchi590.chatclient.Constants.CHECK_ONLINE_CLIENTS;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;

/**
 * @author hchi590 Retrieves a list of online clients from the server TODO:
 *         Requires broadcast of Intent to update the clientList in Parent Context.
 */
public class RetrieveClientListToServer extends ToServer {

	private String _regID;
	/**
	 * 
	 * @param context
	 *            Parent Context
	 */
	public RetrieveClientListToServer(Context context, String regID) {
		super(context);
		_regID = regID;
	}

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
			ArrayList<String> clients;
			clients = (ArrayList<String>) in.readObject();
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
		_header += CHECK_ONLINE_CLIENTS;
	}

}
