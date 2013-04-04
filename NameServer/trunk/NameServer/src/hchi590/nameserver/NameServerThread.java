/**
 * 
 */
package hchi590.nameserver;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * @author hchi590 Writes an address from the workingAddressesList in NameServer
 *         back to client. Which address is determined by Counter class.
 * 
 */
public class NameServerThread extends Thread {

	private Socket _socket;
	private List<String> _addresses;

	public NameServerThread(Socket socket, List<String> addresses) {
		_socket = socket;
		_addresses = addresses;
	}

	public void run() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					_socket.getOutputStream());
			int index = Counter.getInstance().getCounter();
			String address = _addresses.get(index);
			System.out.println(address);
			out.writeObject(address);
			out.close();
			_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
