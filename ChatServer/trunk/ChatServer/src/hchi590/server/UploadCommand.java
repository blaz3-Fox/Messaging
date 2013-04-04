/**
 * 
 */
package hchi590.server;

import static hchi590.server.Constants.DOWNLOAD;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Saves an uploaded file from server, posts the file over ChatHandler
 * 
 * @author hchi590
 * 
 */
public class UploadCommand implements Command {

	private BufferedInputStream _in;
	private String _filename;

	public UploadCommand(BufferedInputStream in, String filename) {
		_in = in;
		_filename = filename;
	}

	/**
	 * Send a GCM push notification out informing clients connected to this
	 * server of a new file before writing the file to disk. The ChatHandler is
	 * then informed of the new file so it can be sent over to the other
	 * servers.
	 */
	@Override
	public void execute() {

		try {
			File file = new File(_filename);
			String[] args = new String[] { DOWNLOAD, _filename };
			new Thread(new PushNotification(args, false)).start();
			byte[] bytes = new byte[1024];
			FileOutputStream fout = new FileOutputStream(file);
			int count;
			while ((count = _in.read(bytes)) > 0) {
				fout.write(bytes, 0, count);
			}
			fout.close();
			_in.close();
			try {
				ChatHandler.getInstance().uploadFile(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println("Upload finished");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
