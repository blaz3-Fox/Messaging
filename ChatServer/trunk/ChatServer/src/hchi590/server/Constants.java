package hchi590.server;

/**
 * @author hchi590
 * Constants that need to be consistent across all classes.
 */
public class Constants {

	// Internal String constants
	static final String HEADER = "1";
	static final String AUTHOR = "2";
	static final String MESSAGE = "3";
	static final String FILE_SEND = "13";
	static final String FILE_NAME = "14";
	static final String ADD_CLIENT_ID = "receive";
	static final String REMOVE_CLIENT_ID = "remove";

	static final String APIKEY = "AIzaSyAMihnXfgPBjbwlm5dK8NCVFfhrqqU-fGI";

	// Protocols
	public static final String POST = "100";
	public static final String REGISTER = "101";
	public static final String UNREGISTER = "102";
	public static final String RETRIEVE = "104";
	public static final String UPLOAD = "105";
	public static final String DOWNLOAD = "106";
	public static final String STREAM = "107";
	public static final String CHECK_ONLINE_CLIENTS = "108";
	public static final String TEST = "109";
	
	public static final String TIMESTAMP = "200"; 
}
