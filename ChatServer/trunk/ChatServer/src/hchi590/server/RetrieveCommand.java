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
 * Retrieve messages and write them back to client
 * @author hchi590
 *
 */
public class RetrieveCommand implements Command {

	private BufferedInputStream _in;
	private OutputStream _out;

	public RetrieveCommand(BufferedInputStream in, OutputStream out) {
		_in = in;
		_out = out;
	}

	/**
	 * Retrieves a list of new messages from the ChatHandler then write it back
	 * out via a ObjectOutputStream
	 */
	@Override
	public void execute() {
		List<com.hchi590.chatclient.Message> messages = null;
		try {
			ObjectInputStream oin = new ObjectInputStream(_in);
			messages = ChatHandler.getInstance().retrieveStartingFrom(
					oin.readInt());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ObjectOutputStream out = new ObjectOutputStream(_out);
			out.writeObject(messages);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
