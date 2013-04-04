/**
 * 
 */
package hchi590.server;

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Registers GCM ID from client with server
 * @author hchi590
 *
 */
public class RegisterCommand implements Command {

	private BufferedInputStream _in;
	private OutputStream _out;

	/**
	 * 
	 * @param in
	 * @param out
	 */
	public RegisterCommand(BufferedInputStream in, OutputStream out) {
		_in = in;
		_out = out;
	}

	/**
	 * 
	 */
	@Override
	public void execute() {
		try {
			ObjectInputStream oin = new ObjectInputStream(_in);
			String name = ChatHandler.getInstance().registerID((String) oin.readUTF());
			ObjectOutputStream out = new ObjectOutputStream(_out);
			out.writeUTF(name);
			out.flush();
			oin.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
