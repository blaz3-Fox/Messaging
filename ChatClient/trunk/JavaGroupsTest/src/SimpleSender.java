import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class SimpleSender {

	/**
	 * @param args
	 */
	public static void main(String[] args)  {
		// TODO Auto-generated method stub
		
		// connect to the server.
		Socket s = new Socket();
		
		SocketAddress address = new InetSocketAddress("127.0.0.1", 5555);
		
		try {
			s.connect(address, 5000 );
			
			BufferedOutputStream in = new BufferedOutputStream(s.getOutputStream());
			
			in.write("Motherfucking bytes".getBytes());
			in.write("Motherfucking bytes".getBytes());
			in.flush();
			
			BufferedInputStream woopwoopwoop = new BufferedInputStream(s.getInputStream());
			
			s.close();
		} catch (IOException e) {
			
			
		}
	}

}
