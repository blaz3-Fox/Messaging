/**
 * 
 */
package com.hchi590.chatclient;

import static com.hchi590.chatclient.Constants.UPLOAD;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import android.content.Context;
import android.util.Log;

/**
 * @author hchi590 Upload a file to the server.
 * 
 */
public class UploadFileToServer extends ToServer {

	private File _file;

	/**
	 * 
	 * @param context
	 *            Parent Context
	 * @param uri
	 *            URI linking to file to be uploaded.
	 */
	public UploadFileToServer(Context context, URI uri) {
		super(context);
		_file = new File(uri);
	}

	/**
	 * 
	 * @param context
	 *            Parent Context
	 * @param filename
	 *            Name of file to be uploaded.
	 */
	public UploadFileToServer(Context context, String filename) {
		super(context);
		_file = new File(filename);
	}

	/**
	 * 
	 * @param context
	 *            Parent Context
	 * @param file
	 *            File instance to be uploaded
	 */
	public UploadFileToServer(Context context, File file) {
		super(context);
		_file = file;
	}

	/**
	 * Read bytes from file using FileInputStream then send bytes over
	 * BufferedOutputStream to server. File buffer size is 64kB.
	 */
	protected void execute() {
		try {
			Log.d("audiotiming", _file.getName() + " Uploaded " + System.currentTimeMillis());
			BufferedOutputStream bos = new BufferedOutputStream(
					_socket.getOutputStream());
			bos.write(_headerByte);
			bos.flush();
			InputStream is = new FileInputStream(_file);

			byte[] bytes = new byte[64 * 1024];
			int count;
			while ((count = is.read(bytes)) > 0) {
				bos.write(bytes, 0, count);
				bos.flush();
			}
			bos.close();
			is.close();
			_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@Override
	protected void initialise() {
		_header += UPLOAD;
		_header += (":" + _file.getName());
	}
}
