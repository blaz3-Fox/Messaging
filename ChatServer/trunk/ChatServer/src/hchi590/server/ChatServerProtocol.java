package hchi590.server;

import static hchi590.server.Constants.CHECK_ONLINE_CLIENTS;
import static hchi590.server.Constants.POST;
import static hchi590.server.Constants.REGISTER;
import static hchi590.server.Constants.RETRIEVE;
import static hchi590.server.Constants.UNREGISTER;
import static hchi590.server.Constants.DOWNLOAD;
import static hchi590.server.Constants.UPLOAD;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Determines what action should be taken to handle incoming requests
 * 
 * @author hchi590
 * 
 */
public class ChatServerProtocol {

	public ChatServerProtocol() {

	}

	/**
	 * Generate the correct command depending on the request from the client
	 * 
	 * @param request
	 *            The request from the client
	 * @return The command corresponding to each request
	 */
	public Command generateCommand(String header, BufferedInputStream in,
			OutputStream out) {
		if (header.contains("GET")) {
			return new StreamCommand(header, out);
		} else {
			String[] headers = header.split(":");
			String commandType = headers[1];
			if (commandType.equals(REGISTER)) {
				return new RegisterCommand(in, out);
			} else if (commandType.equals(UNREGISTER)) {
				return new UnregisterCommand(in);
			} else if (commandType.equals(POST)) {
				return new PostCommand(in, out);
			} else if (commandType.equals(RETRIEVE)) {
				return new RetrieveCommand(in, out);
			} else if (commandType.equals(CHECK_ONLINE_CLIENTS)) {
				return new CheckOnlineClientsCommand(in, out);
			} else if (commandType.equals(DOWNLOAD)) {
				return new DownloadCommand(out, headers[2]);
			} else if (commandType.equals(UPLOAD)) {
				return new UploadCommand(in, headers[2]);
			} else {
				return null;
			}
		}
	}
}
