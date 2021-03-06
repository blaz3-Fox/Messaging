package hchi590.server;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Thread that handles requests from the client
 * 
 * @author hchi590
 * 
 */
public class ChatServerThread extends Thread {

	private Socket _socket = null;

	/**
	 * 
	 * @param socket The connecting socket
	 */
	public ChatServerThread(Socket socket) {
		super("ChatServerThread");
		_socket = socket;
		System.out.println("Created a thread to deal with this bitch");
	}

	public void run() {
		try {
			/// this bit gets information from the socket and converts it into a command and then executes a command.
			
			
			//Read header from incoming request
			byte[] headerByte = new byte[1024];
			BufferedInputStream bin = new BufferedInputStream(_socket.getInputStream());
			//bin.read(headerByte);
			
			String headerString = new String(headerByte).trim();
			OutputStream out = _socket.getOutputStream();
			//ChatServerProtocol csp = new ChatServerProtocol();

			//Generate a command to execute based on header
			/*Command command = csp.generateCommand(headerString, bin, out);
			if (command != null) {
				command.execute();
			}*/
			
			byte[] bytes = new byte[512];
			String s= "";
			int count;
			while ((count = bin.read(bytes)) > 0) {
				s += bytes.toString();
			}
			
			System.out.println(s);
			
			System.out.println("done");

			// Close I/O streams and socket on exit
			bin.close();
			out.close();
			_socket.close();

		} catch (IOException  e) {
			e.printStackTrace();
		} 
	}
}
