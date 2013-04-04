/**
 * 
 */
package hchi590.server;


/**
 * Launches the ChatServer with supplied port from commandline argument
 * @author hchi590
 *
 */
public class Launcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int chatServerPort = -1;

		// Take port number and clustername from commandline arguments
		/*if (args.length == 1) {
			chatServerPort = Integer.parseInt(args[0]);
		} else {
			System.out.println("Incorrect number of arguments. Got "
					+ args.length + " arguments. Expected: ");
			System.out.println("(portnumber)");
			System.out
					.println("portnumber: port number that the server will listen on");
			System.exit(0);
		}*/
		
		chatServerPort = 5555;

		new ChatServer(chatServerPort).start();

	}

}
