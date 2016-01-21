/*
 * Brandon Wright, Jake Snow, Jared Aarons
 * Dr.Liu
 * December 3rd, 2015
 * This thread handles server/client communication.
 */
import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class ClientThread extends Thread
{

	private Socket clntSock;
	private ArrayList<ClientThread> threads;
	private DataInputStream in;
	private DataOutputStream out;
	private Message myInfo;
	private Message opponentInfo;
	private String receiveStr;
	private String name,oppName;

	public ClientThread(Socket s, ArrayList<ClientThread> threads) throws IOException{
		clntSock=s;
		this.threads=threads;
		in = new DataInputStream(clntSock.getInputStream());
		out = new DataOutputStream(clntSock.getOutputStream());	

	}

	public void run()
	{
		SocketAddress clientAddress = clntSock.getRemoteSocketAddress();
		System.out.println("Handling client at " + clientAddress);
		try{
			myInfo = new Message("",0,"hello");
			opponentInfo = new Message("",0,"");
			
			for(int i=0;i<threads.size();i++)
			{
				ClientThread t = threads.get(i);
				if(t!=this)
					t.out.writeUTF(myInfo.toString());		
			}
	
			receiveStr=in.readUTF();
			opponentInfo.setMsg(receiveStr);
			if(opponentInfo.getmType()==0)
			{
				oppName=opponentInfo.getName();
				myInfo.setmType(1);
				myInfo.setContent("begin");
			}	
			for(int i=0;i<threads.size();i++) 
			{
				ClientThread t = threads.get(i);
				if(t!=this)
					t.out.writeUTF(myInfo.toString());		
			}

			receiveStr=in.readUTF();
			opponentInfo.setMsg(receiveStr);          

			while(!opponentInfo.getContent().trim().equals("finished"))
			{
				for(int i=0;i<threads.size();i++)
				{
					ClientThread t= threads.get(i);
					if(t!=this)  
						t.out.writeUTF(receiveStr);				
				}
				receiveStr=in.readUTF(); 
				updateScore(receiveStr);
			}
			for(int i=0;i<threads.size();i++)
			{
				ClientThread t= threads.get(i);
				if(t!=this)
					t.out.writeUTF(opponentInfo.toString());					
			}
			myInfo.setmType(4);
			myInfo.setContent("closing");
			out.writeUTF(myInfo.toString());
		}
		catch(EOFException ex)
		{
			
		}
		catch(IOException e)
		{
			e.printStackTrace();		
		}
		finally{
			try{
				receiveStr=in.readUTF();
				myInfo.setMsg(receiveStr);

				if(myInfo.getmType()==5) //
				{
					threads.remove(Thread.currentThread());
					clntSock.close();
					System.out.println("Connection closed!");
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
				threads.remove(Thread.currentThread());
			}
		}
	}

	public void updateScore(String str)
	{
		opponentInfo.setMsg(str);		
	}

}
