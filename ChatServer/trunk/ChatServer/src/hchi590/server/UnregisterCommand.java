/**
 * 
 */
package hchi590.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Removes GCM ID from client with server
 * @author hchi590
 *
 */
public class UnregisterCommand implements Command {

	private BufferedInputStream _in;

	public UnregisterCommand(BufferedInputStream in) {
		_in = in;
	}

	/**
	 * Sends unregister request to ChatHandler
	 */
	@Override
	public void execute() {
		try {
			ObjectInputStream oin = new ObjectInputStream(_in);
			ChatHandler.getInstance().unRegisterID((String) oin.readUTF());
			oin.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
