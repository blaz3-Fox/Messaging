package hchi590.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * 
 * @author hchi590
 *
 */
public class ChatServer extends Thread {

	private int _port;

	/**
	 * 
	 * @param port Port number that this ChatServer will listen on
	 */
	public ChatServer(int port) {
		_port = port;
	}

	public void run() {

		ServerSocket serverSocket = null;

		// Start a new server socket for the specified port if possible
		try {
			serverSocket = new ServerSocket(_port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + _port);
			System.exit(0);
		}

		// Start a new instance of JGroups
		ChatHandler chatHandler = null;
		try {
			chatHandler = ChatHandler.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to start JGroups component. Exiting.");
			System.exit(0);
		}

		Thread cThread = new Thread(chatHandler);
		cThread.setDaemon(true);
		cThread.start();

		boolean listening = true;

		// Listen for incoming socket requests and start threads to deal with
		// each
		while (listening) {
			try {
				System.out.println("We got something!");
				new ChatServerThread(serverSocket.accept()).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
