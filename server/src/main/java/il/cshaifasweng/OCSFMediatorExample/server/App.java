package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
	
	private static SimpleServer server;
    public static void main( String[] args ) throws IOException
    {
        int port = 3000;
        server = new SimpleServer(port);
        server.listen();
        System.out.println("Server listening on port " + port);
    }
}
