/**
 * 
 */
package com.hchi590.chatclient;

import static com.hchi590.chatclient.Constants.FAILED_TO_CONNECT;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.content.Context;
import android.content.Intent;

/**
 * @author CHIN Base class of all communications to the server.
 */
public abstract class ToServer extends Thread {

	protected Socket _socket;
	protected boolean _isConnected;
	protected String _header;
	protected Context _context;
	protected byte[] _headerByte;

	/**
	 * 
	 * @param context
	 *            Parent context
	 */
	public ToServer(Context context) {
		_context = context;
		_header = Long.toString(System.currentTimeMillis()) + ":";
		_headerByte = new byte[1024];
	}

	/**
	 * Assign the header for communication. All headers contain at least the
	 * time the request was made and a string constant defined in protocols that
	 * determines the type of request it is. Values are separated by colons
	 * (":").
	 */
	protected abstract void initialise();

	/**
	 * Send data to server. All outgoing server communications should first be
	 * wrapped by a BufferedOutputStream. All subclassing methods should first
	 * write the _headerBytes field and flush the stream before any other
	 * communication.
	 */
	protected abstract void execute();

	/**
	 * Generate a socket to send data through.
	 */
	private void generateSocket() {
		_socket = new Socket();
	}

	/**
	 * Generate header to be sent to server from header string
	 */
	private void generateHeaderByte() {
		byte[] buffer = _header.getBytes();
		System.arraycopy(buffer, 0, _headerByte, 0, buffer.length);
	}

	/**
	 * Attempt to connect to server specified by ServerAddress singleton. Times
	 * out after 5 seconds.
	 */
	protected void connectSocket() {
		SocketAddress address = new InetSocketAddress(ServerAddress
				.getInstance().getServerAddress(), ServerAddress.getInstance()
				.getPort());
		try {
			_socket.connect(address, 5000);
			_isConnected = true;
		} catch (IOException e) {
			e.printStackTrace();
			// Broadcast Intent indicating failure to connect so Parent Context
			// can inform the user.
			String error = "Unable to connect to: "
					+ ServerAddress.getInstance().getServerAddress();
			Intent displayError = new Intent(FAILED_TO_CONNECT);
			displayError.putExtra("message", error);
			_context.sendBroadcast(displayError);
			_isConnected = false;
		}
	}

	public void run() {
		initialise();
		generateHeaderByte();
		generateSocket();
		connectSocket();
		if (_isConnected) {
			execute();
		}
	}
}
