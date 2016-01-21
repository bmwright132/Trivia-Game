/*
 * Brandon Wright, Jake Snow, Jared Aarons
 * Dr.Liu
 * December 3rd, 2015
 * Server for Trivia game
 */
import java.net.*;
import java.util.*;
import java.io.*;
public class Server {
	private static ArrayList<ClientThread> clientThreads = new ArrayList<ClientThread>();

	public static void main(String [] args) throws IOException
	{
		// Test for correct # of args
		if (args.length != 1)
			throw new IllegalArgumentException("Parameter(s): <Port>");

		int servPort = Integer.parseInt(args[0]);
		ServerSocket servSock = new ServerSocket(servPort);
		
		// Run forever, accepting and servicing connections 
		while (true)
		{ 
			Socket clntSock = servSock.accept();
			ClientThread t = new ClientThread(clntSock,clientThreads); //create thread for each client
			//keep adding clients to the pool of Threads
			if(t!=null && clientThreads.size() <=2) 
			{
				clientThreads.add(t);	
			t.start();
			}					
		}

	}
}
