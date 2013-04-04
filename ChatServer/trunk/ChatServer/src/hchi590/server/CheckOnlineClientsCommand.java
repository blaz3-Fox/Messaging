/**
 * 
 */
package hchi590.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Return a list of all online clients across all servers
 * @author hchi590
 *
 */
public class CheckOnlineClientsCommand implements Command {

	private BufferedInputStream _in;
	private OutputStream _out;

	/**
	 * 
	 * @param in BufferedInputStream from socket
	 * @param out OutputStream from socket
	 */
	public CheckOnlineClientsCommand(BufferedInputStream in, OutputStream out) {
		_in = in;
		_out = out;
	}

	/**
	 * Retrieve list of online clients
	 */
	@Override
	public void execute() {
		List<String> clientList = null;
		try {
			ObjectInputStream oin = new ObjectInputStream(_in);
			clientList = ChatHandler.getInstance().getClientOnlineList(
					(String) oin.readObject());
			oin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ObjectOutputStream out = new ObjectOutputStream(_out);
			out.writeObject(clientList);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
