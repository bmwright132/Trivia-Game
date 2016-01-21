/*
 * Brandon Wright, Jake Snow, Jared Aarons
 * Dr.Liu
 * December 3rd, 2015
 * Client login screen for Trivia game
 */
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;

public class ThreadRead extends SwingWorker<Void, String >{
	private DataInputStream in;
	private Message opponent;
	private Socket socket;
	private JTextField areaChatContent;
	private String receiveMsg;
	private boolean opponentDone=false;
	private Timer clock;
	private Timer clock2;
	private int opponentScore=0;
	private JTextField txtOpponentScore;
	private String name;
	private Client c;
	private boolean finishedOpp=false, finishedSelf=false;

	public ThreadRead(DataInputStream in, Socket socket,Message msg, JTextField a,Client c, Timer clock, Timer clock2)
	{
		this.in=in;
		this.socket=socket;
		opponent=msg;
		txtOpponentScore=a;
		this.c=c;
		this.clock = clock;
		this.clock2 = clock2;
	}

	protected Void doInBackground() throws Exception {
		try {
			do{
				
				String receiveStr = in.readUTF();
				opponent.setMsg(receiveStr);
				
				if(opponent.getmType()==3)
				{
					finishedOpp=true;
					clock2.stop();
				}
				if(opponent.getmType()==4)
				{
					finishedSelf=true;
					clock.stop();
				}
				publish(receiveStr);

			}while(!finishedSelf||!finishedOpp);//run until both clients are finished

		}
		catch (IOException ex) {
			ex.printStackTrace();
		} 	
		return null;
	}

	protected void process(List<String> list) 
	{
		//display every score
		for(String rStr:list)
		{
			opponent.setMsg(rStr);
			int type =opponent.getmType();
			try
			{
				if(type==0)
				{
					c.sendMsg(c.getName(), 1, "start");
				}
				else if(type==1)
				{
					clock.start();//start clock
					name=opponent.getName();
					System.out.println(name);
					c.generateQuestion();
				} 
				else if(type==2)
				{
					txtOpponentScore.setText(opponent.getContent()+"\n");
					opponentScore = Integer.parseInt(opponent.getContent().trim());
				}
			}catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	//Called when clients finish Trivia
	public void done(){
		try {
			System.out.println("Done");
			c.getWinner();
			c.sendMsg(name, 5, "closed");

			socket.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();}
	}
	
	public boolean opponentDone()
	{
		return opponentDone;
	}
	
	public int getOpponentScore()
	{
		return opponentScore;
	}
}
