/**
 * 
 */
package com.hchi590.chatclient;

import static com.hchi590.chatclient.Constants.UNREGISTER;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.content.Context;

/**
 * @author hchi590 Unregisters GCM ID from server.
 */
public class UnregisterToServer extends ToServer {

	private String _regID;

	/**
	 * 
	 * @param context Parent Context
	 * @param regID GCM ID to remove from server
	 */
	public UnregisterToServer(Context context, String regID) {
		super(context);
		_regID = regID;
	}

	/**
	 * Write GCM ID to be removed via ObjectOutputStream.
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
			out.close();
			bos.close();
			_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			synchronized (this) {
				this.notifyAll();
			}
		}
		synchronized (this) {
			this.notifyAll();
		}
	}

	/**
	 * 
	 */
	@Override
	protected void initialise() {
		_header += UNREGISTER;
	}

}
