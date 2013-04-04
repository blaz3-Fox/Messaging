package hchi590.datastorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides Singleton interface to interact with the underlying data storage
 * structures of the messages and registered GCM IDs of the apps on connected
 * devices.
 * 
 * 
 * @author hchi590
 * 
 */
public class DataStorageSingleton {

	private static DataStorageSingleton _instance = null;

	private List<com.hchi590.chatclient.Message> _messages = Collections
			.synchronizedList(new ArrayList<com.hchi590.chatclient.Message>());
	private Set<String> _localRegIDs = Collections
			.synchronizedSet(new HashSet<String>());
	private Set<String> _globalRegIDs = Collections
			.synchronizedSet(new HashSet<String>());
	private Map<String, String> _usernames = Collections
			.synchronizedMap(new HashMap<String, String>());

	protected DataStorageSingleton() {

	}

	/**
	 * Returns an instance of this class if one already exists, otherwise
	 * instantiates an instance to return.
	 * 
	 * @return
	 */
	public synchronized static DataStorageSingleton getInstance() {
		if (_instance == null) {
			_instance = new DataStorageSingleton();
		}
		return _instance;
	}

	/**
	 * Add a GCM id to the local list and the shared list between servers
	 * 
	 * @param id
	 *            GCM Id to add to set
	 */
	public String registerID(String id) {
		// System.out.println(id);
		if (!id.equals("")) {
			synchronized (_localRegIDs) {
				_localRegIDs.add(id);
			}
			registerGlobalID(id);
			if (_usernames.containsKey(id)) {
				return _usernames.get(id);
			} else {
				String name = Integer.toString(_usernames.size());
				_usernames.put(id, name);
				return name;
			}
		} else {
			return "";
		}
	}

	/**
	 * Add a GCM id to the shared list
	 * 
	 * @param id
	 */
	public void registerGlobalID(String id) {
		synchronized (_globalRegIDs) {
			_globalRegIDs.add(id);
		}
	}

	/**
	 * Remove GCM id for a device
	 * 
	 * @param id
	 *            GCM to remove
	 */
	public void unRegisterID(String id) {
		synchronized (_localRegIDs) {
			_localRegIDs.remove(id);
		}
		synchronized (_globalRegIDs) {
			_globalRegIDs.remove(id);
		}
	}

	/**
	 * Adds a message to the message list, returning the index of the added
	 * message
	 * 
	 * @param message
	 *            String message to be added to list
	 * @return index of the message added
	 */
	public int addMessage(com.hchi590.chatclient.Message message) {
		synchronized (_messages) {
			_messages.add(message);
			return (_messages.size() - 1);
		}
	}

	/**
	 * Return the message at a given index
	 * 
	 * @param index
	 *            Index of the message to be retrieved
	 * @return the message at that index
	 */
	public com.hchi590.chatclient.Message retrieveMessage(int index) {
		synchronized (_messages) {
			return _messages.get(index);
		}
	}

	public List<com.hchi590.chatclient.Message> retrieveStartingFrom(int index) {
		synchronized (_messages) {
			if (index >= _messages.size()) {
				index = _messages.size() - 1;
			}
			/*
			 * System.out.println("Retrieved between " + index + " and " +
			 * _messages.size());
			 */
			List<com.hchi590.chatclient.Message> messages = new ArrayList<com.hchi590.chatclient.Message>();
			messages.addAll(_messages.subList(index + 1, _messages.size()));
			return messages;
		}
	}

	/**
	 * Get the messages list
	 * 
	 * @return The list of messages
	 */
	public List<com.hchi590.chatclient.Message> getMessages() {
		synchronized (_messages) {
			return _messages;
		}
	}

	/**
	 * Get the list of GCM IDs
	 * 
	 * @return The list of GCM IDs
	 */
	public List<String> getRegIds() {
		synchronized (_localRegIDs) {
			return new ArrayList<String>(_localRegIDs);
		}
	}

	/**
	 * Get the list of GCM IDs across all servers
	 * 
	 * @return
	 */
	public List<String> getGlobalRegIds() {
		synchronized (_globalRegIDs) {
			return new ArrayList<String>(_globalRegIDs);
		}
	}

	/**
	 * Clear and set the state of the ID List and the messages list
	 * 
	 * @param regIds
	 *            List of regIDs to be added
	 * @param state
	 *            List of messages to be added
	 */
	public void setState(List<String> regIds,
			List<com.hchi590.chatclient.Message> messages) {
		synchronized (_globalRegIDs) {
			_globalRegIDs.clear();
			_globalRegIDs.addAll(regIds);
		}
		synchronized (_messages) {
			_messages.clear();
			_messages.addAll(messages);
		}
	}

}
