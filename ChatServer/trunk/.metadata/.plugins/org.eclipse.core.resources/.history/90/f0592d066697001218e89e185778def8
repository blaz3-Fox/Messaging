package hchi590.server;

import static hchi590.server.Constants.ADD_CLIENT_ID;
import static hchi590.server.Constants.DOWNLOAD;
import static hchi590.server.Constants.FILE_NAME;
import static hchi590.server.Constants.FILE_SEND;
import static hchi590.server.Constants.HEADER;
import static hchi590.server.Constants.MESSAGE;
import static hchi590.server.Constants.POST;
import static hchi590.server.Constants.REMOVE_CLIENT_ID;
import static hchi590.server.Constants.TIMESTAMP;
import hchi590.datastorage.DataStorageSingleton;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.Util;

/**
 * Singleton that Handles all inter-server communications via a JChannel
 * 
 * @author hchi590
 * 
 */
public class ChatHandler extends ReceiverAdapter implements Runnable {

	private JChannel _channel = null;
	private DataStorageSingleton _data;
	private BlockingQueue<com.hchi590.chatclient.Message> _sendQueue;
	private ExecutorService _pool;

	private static ChatHandler _instance;

	protected ChatHandler() throws Exception {
		_pool = Executors.newCachedThreadPool();
		_sendQueue = new LinkedBlockingQueue<com.hchi590.chatclient.Message>();
		_data = DataStorageSingleton.getInstance();
		init();
	}

	public synchronized static ChatHandler getInstance() throws Exception {
		if (_instance == null) {
			_instance = new ChatHandler();
		}
		return _instance;
	}

	/**
	 * Post a message to the JChannel to be shared with the other servers
	 * 
	 * @param message
	 *            The message to be sent
	 */
	public void post(com.hchi590.chatclient.Message message) {
		try {
			System.out.println("Post " + message.getMessage());
			_sendQueue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		eventLoop();
		_channel.close();
	}

	/**
	 * Create JChannel then create or join a cluster, get previous state of
	 * cluster if available
	 * 
	 */
	private void init() throws Exception {

		String cluster = null;
		String config = null;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"config.ini"));
			String line;
			String[] values = new String[2];
			while ((line = reader.readLine()) != null) {
				values = line.split(":");
				if (values[0].equals("config")) {
					config = values[1];
				} else if (values[0].equals("cluster")) {
					cluster = values[1];
				}
			}
			_channel = new JChannel(config);
			ProtocolStack stack = _channel.getProtocolStack();
			System.out.println(stack.printProtocolSpecAsPlainString());
			_channel.setReceiver(this);
			_channel.connect(cluster);
			_channel.getState(null, 10000);
			System.out.println(_channel.getAddressAsString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Main event loop - Waits for messages to send from sendQueue before
	 * sending on JChannel
	 */
	private void eventLoop() {
		while (true) {
			try {
				com.hchi590.chatclient.Message line;
				while ((line = _sendQueue.poll()) == null) {

				}
				HashMap<String, Object> data = new HashMap<String, Object>();
				data.put(HEADER, POST);
				data.put(MESSAGE, line);
				data.put(TIMESTAMP, System.currentTimeMillis());
				Message msg = new Message(null, null, data);
				_channel.send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends GCM ID across JChannel to maintain a global list of connected
	 * devices
	 * 
	 * @author hchi590
	 * 
	 */
	private class SendClientID implements Runnable {

		private String id;

		SendClientID(String id) {
			this.id = id;
		}

		public void run() {
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put(HEADER, ADD_CLIENT_ID);
			data.put(MESSAGE, id);
			Message msg = new Message(null, null, data);
			try {
				_channel.send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends request to remove ID from global list across JChannel
	 * 
	 * @author hchi590
	 * 
	 */
	private class RemoveClientID implements Runnable {

		private String id;

		RemoveClientID(String id) {
			this.id = id;
		}

		public void run() {
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put(HEADER, REMOVE_CLIENT_ID);
			data.put(MESSAGE, id);
			Message msg = new Message(null, null, data);
			try {
				_channel.send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Saves a file upon receiving one from another server
	 * 
	 * @author hchi590
	 * 
	 */
	private class SaveFile implements Runnable {

		private Message _msg;

		SaveFile(Message msg) {
			_msg = msg;
		}

		public void run() {
			String filename = "";
			// Save only if file came from another server
			if (!_msg.getSrc().equals(_channel.getAddress())) {
				System.out.println("Receiving file");
				HashMap<String, Object> data = (HashMap<String, Object>) _msg
						.getObject();
				filename = (String) data.get(FILE_NAME);
				System.out.println("About to retrieve: " + filename);
				File file = new File(filename);
				byte[] content = (byte[]) data.get(MESSAGE);
				try {
					// Prepare to write
					FileOutputStream fos = new FileOutputStream(file);
					// Send GCM push notification of new file to connected
					// devices
					String[] args = new String[] { DOWNLOAD, filename };
					_pool.execute(new PushNotification(args, false));
					// Save file
					fos.write(content);
					System.out.println("Written: " + filename);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * Callback for when a new server member joins the cluster
	 */
	public void viewAccepted(View new_view) {
		System.out.println("** view: " + new_view);
	}

	/**
	 * Callback for when a message is received
	 */
	public void receive(Message msg) {
		HashMap<String, Object> contents = (HashMap<String, Object>) msg
				.getObject();
		// Determine appropriate action based on the header
		if (((String) contents.get(HEADER)).equals(POST)) {
			long timeToSend = System.currentTimeMillis()
					- (Long) contents.get(TIMESTAMP);
			com.hchi590.chatclient.Message message = (com.hchi590.chatclient.Message) contents
					.get(MESSAGE);
			System.out.println(message.getMessage() + ":Time from server to server:"
					+ timeToSend);
			String index = Integer.toString(_data.addMessage(message));
			// Send push notification of new message to connected devices
			String[] args = new String[] { POST, index };
			_pool.execute(new PushNotification(args, false));
		} else if (((String) contents.get(HEADER)).equals(ADD_CLIENT_ID)) {
			_data.registerGlobalID((String) contents.get(MESSAGE));
			//System.out.println(_data.getGlobalRegIds());
		} else if (((String) contents.get(HEADER)).equals(REMOVE_CLIENT_ID)) {
			_data.unRegisterID((String) contents.get(MESSAGE));
			//System.out.println(_data.getGlobalRegIds());
		} else if (((String) contents.get(HEADER)).equals(FILE_SEND)) {
			_pool.execute(new SaveFile(msg));
		}
	}

	/**
	 * Returns the regIds and messages held by this server
	 */
	public void getState(OutputStream output) throws Exception {
		Util.objectToStream(_data.getGlobalRegIds(), new DataOutputStream(
				output));
		Util.objectToStream(_data.getMessages(), new DataOutputStream(output));
	}

	/**
	 * Sets the state of the server
	 */
	public void setState(InputStream input) throws Exception {
		List<String> set = (List<String>) Util
				.objectFromStream(new DataInputStream(input));
		List<com.hchi590.chatclient.Message> list = (List<com.hchi590.chatclient.Message>) Util
				.objectFromStream(new DataInputStream(input));
		_data.setState(set, list);
		System.out.println("received regIds (" + set.size() + "):");
		for (String str : set) {
			System.out.println(str);
		}
		System.out.println("received messages (" + list.size()
				+ " messages in chat history):");
		for (com.hchi590.chatclient.Message str : list) {
			System.out.println(str.getMessage());
		}
	}

	/**
	 * Pass a device id to be registered in the data storage
	 * 
	 * @param regId
	 *            ID to be stored
	 */
	public String registerID(String regId) {
		String name = _channel.getAddressAsString() + "-"
				+ _data.registerID(regId);
		_pool.execute(new SendClientID(regId));
		return name;
	}

	/**
	 * Pass a device id to be unregistered in the data storage
	 * 
	 * @param regId
	 *            ID to be stored
	 */
	public void unRegisterID(String regId) {
		_data.unRegisterID(regId);
		_pool.execute(new RemoveClientID(regId));
	}

	/**
	 * 
	 * @param id
	 * @return The list of all connected devices across all members of the
	 *         server cluster
	 */
	public List<String> getClientOnlineList(String id) {
		List<String> clientList = _data.getGlobalRegIds();
		clientList.remove(id);
		return clientList;
	}

	/**
	 * Retrieve messages starting from specified index
	 * 
	 * @param index
	 *            Index of message last seen by requesting device
	 * @return List of all messages (com.hchi590.chatclient.Message) above
	 *         specified index
	 */
	public List<com.hchi590.chatclient.Message> retrieveStartingFrom(int index) {
		return _data.retrieveStartingFrom(index);
	}

	/**
	 * Retrieve a message from the data storage
	 * 
	 * @param index
	 *            Index of message to retrieve
	 * @return Returns the message at the specified index
	 */
	public com.hchi590.chatclient.Message retrieveMessage(int index) {
		return _data.retrieveMessage(index);
	}

	/**
	 * Send a file to other server members of the cluster
	 * 
	 * @param file
	 *            File to be sent
	 */
	public void uploadFile(File file) {
		try {
			// Load all bytes from the file
			RandomAccessFile rfile = new RandomAccessFile(file, "rw");
			byte[] payload = new byte[(int) rfile.length()];
			rfile.read(payload);
			// Put bytes into message to be sent over JChannel
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put(HEADER, FILE_SEND);
			data.put(MESSAGE, payload);
			data.put(FILE_NAME, file.getName());
			for (Address a : generateOthers()) {
				//System.out.println(a.toString());
				// Send message to all other members of server cluster
				Message msg = new Message(a, null, data);
				try {
					_channel.send(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate of list of addresses of all other members in the current cluster
	 * of servers connected by JChannel
	 * 
	 * @return List of addresses (type org.jgroups.Address)
	 */
	private List<Address> generateOthers() {
		List<Address> addresses = _channel.getView().getMembers();
		List<Address> modified = new ArrayList<Address>();
		modified.addAll(addresses);
		modified.remove(_channel.getAddress());
		return modified;
	}

}
