/**
 * 
 */
package hchi590.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;

/**
 * Write file data back to client if a stream request is made from client
 * 
 * @author hchi590
 * 
 */
public class StreamCommand implements Command {

	private String _header;
	private OutputStream _out;
	private String _filepath;

	public StreamCommand(String header, OutputStream out) {
		_header = header;
		_out = out;
	}

	public void execute() {
		processRequest();
		respond();
	}

	/**
	 * Extract file to stream from HTTP request from client
	 * 
	 * @return
	 */
	public boolean processRequest() {

		String[] lines = _header.split("\n");

		String filepath = lines[0].substring(5);
		int spaceIndex = filepath.indexOf(" ");
		if (spaceIndex != -1) {
			filepath = filepath.substring(0, spaceIndex);
		}
		_filepath = filepath;
		System.out.println(filepath);

		return true;
	}

	/**
	 * Write HTTP header back to client then write data back over
	 * BufferedOutputStream
	 */
	public void respond() {

		File file = new File(_filepath);
		long fileSize = 0;
		byte[] buffer = new byte[64 * 1024];
		try {
			BufferedOutputStream bos = new BufferedOutputStream(_out, 64 * 1024);

			fileSize = file.length();
			FileInputStream input = new FileInputStream(file);
			//System.out.println(fileSize);

			long bytesLeft = fileSize;

			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			String mimeType = fileNameMap.getContentTypeFor(_filepath);
			if (mimeType != null) {
				mimeType = mimeType.replace('/', '\\');
			} else if (file.getName().contains(".flac")) {
				mimeType = "audio\flac";
			} else if (file.getName().contains(".3gp")) {
				mimeType = "video\3gpp";
			}

			// Create HTTP header
			String headers = "HTTP/1.0 200 OK\r\n";
			headers += "Content-Type: " + mimeType + "\r\n";
			headers += "Content-Length: " + fileSize + "\r\n";
			headers += "Connection: close\r\n";
			headers += "\r\n";

			bos.write(headers.getBytes());
			bos.flush();

			while (bytesLeft > 0) {
				int read = input.read(buffer, 0, buffer.length);
				if (read == -1) {
					break;
				}
				bos.write(buffer, 0, read);
				bos.flush();
				bytesLeft -= read;
			}

			input.close();
			bos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
