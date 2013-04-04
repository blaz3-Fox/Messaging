/**
 * 
 */
package com.hchi590.chatclient;

/**
 * @author hchi590 Singleton containing the address and the port number of the
 *         server to connect to for all network operations.
 */
public class ServerAddress {

	private static ServerAddress _instance;
	private String _serverAddress = "odin.cs.auckland.ac.nz";
	private int _port = 8081;

	protected ServerAddress() {

	}

	// If there is an instance of this class already instantiated, return that
	// instance. Otherwise, instantiate a new instance of this class and return
	// that.
	public static synchronized ServerAddress getInstance() {
		if (_instance == null) {
			_instance = new ServerAddress();
		}
		return _instance;
	}

	public void setServerAddress(String address) {
		_serverAddress = address;
	}

	public void setPort(int port) {
		_port = port;
	}

	public String getServerAddress() {
		return _serverAddress;
	}

	public int getPort() {
		return _port;
	}
}
