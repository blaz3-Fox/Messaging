/**
 * 
 */
package com.hchi590.chatclient;

import java.io.Serializable;

/**
 * @author hchi590 Class that defines data to be stored with each message
 *         posted.
 */
public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _message;
	private String _author;
	private long _time;

	/**
	 * 
	 * @param message
	 *            Text content of message
	 * @param author
	 *            Author of the message
	 */
	public Message(String message, String author, long time) {
		_message = message;
		_author = author;
		_time = time;
	}

	public String getMessage() {
		return _message;
	}

	public String getAuthor() {
		return _author;
	}

	public long getTime() {
		return _time;
	}

}
