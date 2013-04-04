/**
 * 
 */
package hchi590.nameserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * @author hchi590 Checks a list of addresses in addresses.txt for whether they
 *         accept an incoming connection. 
 *         Needs: 
 *         - Security checks 
 *         - Ability to
 *         monitor when new server comes online
 */
public class NameServer {

	static final String TEST = "109";

	public static void main(String[] args) {
		ArrayList<String> addresses = new ArrayList<String>();
		ArrayList<String> workingAddresses = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"addresses.txt"));
			String line;
			while ((line = reader.readLine()) != null) {
				addresses.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Checks each address with a 2 second timeout. Adds to working
		// addresses if connection can be established.
		for (String s : addresses) {
			try {
				SocketAddress address = new InetSocketAddress(s, 31000);
				Socket socket = new Socket();
				socket.connect(address, 2000);
				BufferedOutputStream bos = new BufferedOutputStream(
						socket.getOutputStream());
				String header = System.currentTimeMillis() + ":" + TEST;
				byte[] headerByte = new byte[1024];
				byte[] byteFromString = header.getBytes();
				System.arraycopy(byteFromString, 0, headerByte, 0,
						byteFromString.length);
				bos.write(headerByte);
				bos.close();
				socket.close();
				workingAddresses.add(s);
			} catch (UnknownHostException e) {
				// e.printStackTrace();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
		for (String s : workingAddresses) {
			System.out.println(s);
		}
		// Wait for connections on port 35000.
		if (addresses.size() > 0) {
			ServerSocket ssocket = null;
			try {
				ssocket = new ServerSocket(35000);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				Counter.getInstance().setMax(workingAddresses.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
			boolean connected = true;
			while (connected) {
				try {
					new NameServerThread(ssocket.accept(), workingAddresses)
							.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.err.println("No addresses in addresses.txt.");
		}
	}

}
