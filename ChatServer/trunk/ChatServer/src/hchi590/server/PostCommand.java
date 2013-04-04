/**
 * 
 */
package hchi590.server;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Post a message to the ChatHandler
 * @author hchi590
 *
 */
public class PostCommand implements Command {

	private BufferedInputStream _in;
	private OutputStream _out;

	/**
	 * 
	 * @param in 
	 * @param out
	 */
	public PostCommand(BufferedInputStream in, OutputStream out) {
		_in = in;
		_out = out;
	}

	/**
	 * Read Message object from input stream and post to ChatHandler
	 */
	@Override
	public void execute() {
		try {
			ObjectInputStream oin = new ObjectInputStream(_in);
			ChatHandler.getInstance().post((com.hchi590.chatclient.Message) oin.readObject());
			ObjectOutputStream out = new ObjectOutputStream(_out);
			out.writeUTF("received");
			out.flush();
			oin.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
