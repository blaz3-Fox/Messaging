/**
 * 
 */
package hchi590.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Responds to a request to download a file on the server
 * 
 * @author hchi590
 * 
 */
public class DownloadCommand implements Command {

	private OutputStream _out;
	private String _filename;

	/**
	 * 
	 * @param out
	 *            OutputStream to write to
	 * @param filename
	 *            Name of file requested
	 */
	public DownloadCommand(OutputStream out, String filename) {
		_out = out;
		_filename = filename;
	}

	/**
	 * Writes file to BufferedOutputStream. Buffer size is 64kB.
	 */
	@Override
	public void execute() {
		try {
			File file = new File(_filename);
			BufferedOutputStream bos = new BufferedOutputStream(_out);
			byte[] bytes = new byte[1024];
			FileInputStream fin = new FileInputStream(file);
			int length;
			while ((length = fin.read(bytes)) > 0) {
				bos.write(bytes, 0, length);
			}
			fin.close();
			bos.close();
			System.out.println("Download finished");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
