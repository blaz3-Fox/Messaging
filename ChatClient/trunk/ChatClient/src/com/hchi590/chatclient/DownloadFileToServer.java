/**
 * 
 */
package com.hchi590.chatclient;

import static com.hchi590.chatclient.Constants.DOWNLOAD;
import static com.hchi590.chatclient.Constants.FILE_DOWNLOADED;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author hchi590 Download a file from the Server
 */
public class DownloadFileToServer extends ToServer {

	private String _filename;
	private String _dest;

	/**
	 * @param context
	 *            Parent context.
	 * @param filename
	 *            Name of file to download from the server.
	 * @param dest
	 *            Destination folder of the downloaded file.
	 */
	public DownloadFileToServer(Context context, String filename, String dest) {
		super(context);
		_filename = filename;
		_dest = dest;
	}

	/**
	 * Write file out using BufferedOutputStream. Buffer size of file is 64kB.
	 */
	protected void execute() {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					_socket.getOutputStream());
			bos.write(_headerByte);
			bos.flush();
			BufferedInputStream in = new BufferedInputStream(
					_socket.getInputStream());
			String filename = _dest + "/" + _filename;
			File file = new File(filename);
			FileOutputStream fos = new FileOutputStream(file);

			byte[] bytes = new byte[64 * 1024];
			int count = 0;
			while ((count = in.read(bytes)) > 0) {
				fos.write(bytes, 0, count);
			}
			bos.close();
			in.close();
			fos.close();
			Intent intent = new Intent(FILE_DOWNLOADED);
			intent.putExtra("file", filename);
			_context.sendBroadcast(intent);
			Log.d("debugging", "Read file");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Write the header to inform server of incoming file. Header is in format:
	 * (Long indicating time request was sent):(DOWNLOAD: Constant indicating
	 * download):(name of file to be downloaded)
	 */
	@Override
	protected void initialise() {
		_header += DOWNLOAD;
		_header += (":" + _filename);
	}
}
