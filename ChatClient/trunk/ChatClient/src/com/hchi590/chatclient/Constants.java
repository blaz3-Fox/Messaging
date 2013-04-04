/**
 * 
 */
package com.hchi590.chatclient;

/**
 * @author hchi590
 * Constants that need to be consistent across all classes.
 */
public class Constants {

	// GCM Information
	static final String SENDER_ID = "496456472794";
	
	// Internal Messages for Broadcasts
	static final String STATE_MESSAGES = "com.hchi590.chatclient.STATE_MESSAGES";
	static final String GCM_MESSAGE_RECEIVED = "com.hchi590.chatclient.MESSAGE_RECEIVED";
	static final String GCM_ID_REGISTERED = "com.hchi590.chatclient.GCM_ID_REGISTERED";
	static final String MESSAGE_UPDATE = "com.hchi590.chatclient.MESSAGE_UPDATE";
	static final String FAILED_TO_CONNECT = "com.hchi590.chatclient.FAILED_TO_CONNECT";
	static final String FILE_DOWNLOADED = "com.hchi590.chatclient.FILE_DOWNLOADED";

	// Protocols
	static final String POST = "100";
	static final String REGISTER = "101";
	static final String UNREGISTER = "102";
	static final String RETRIEVE = "104";
	static final String UPLOAD = "105";
	static final String DOWNLOAD = "106";
	static final String STREAM = "107";
	static final String CHECK_ONLINE_CLIENTS = "108";

}
