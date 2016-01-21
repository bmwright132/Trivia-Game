/*
 * Brandon Wright, Jake Snow, Jared Aarons
 * Dr.Liu
 * December 3rd, 2015
 * GUI for Trivia game
 */
import java.net.*;
import java.text.DecimalFormat;
import java.util.*;
import java.io.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Client extends JFrame 
{
	private JPanel contentPane;
	private JLabel label;
	private Socket socket;
	private String name;
	private ThreadRead opponent;
	private JTextArea question;
	private JTextArea gameChoices;
	private JTextField clientAns;
	private ArrayList<Questions> questions = new ArrayList< Questions>(); //arraylist of questions
	private JTextField clientScore;
	private JTextField opponentScore;
	private DataInputStream in;
	private DataOutputStream out;
	private int score=0;
	private String answer;
	private int index=0; //pointer for questions array
	private Message serverMsg;
	private Message myScore; 
	private int numSeconds = 180; //set to 3 minutes
	private Timer clock; //my clock
	private Timer clock2;//opponent clock
	private String clock_display="00:00"; //initializes clock display


	public Client(String n, String server, String p) throws Exception
	{
		int servPort=Integer.parseInt(p);
		socket = new Socket(server, servPort);
		

		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		System.out.println("Connected to server");
		name=n;
		serverMsg = new Message("",0,"");
		myScore = new Message(name,0,"");
		File file = new File("questionFile.txt");
		Scanner inFile = new Scanner(file);
		while(inFile.hasNextLine())
		{
			String q = inFile.nextLine();
			String [] choice = inFile.nextLine().split(" ");
			String ans = inFile.nextLine();
			questions.add(new Questions(q,choice,ans));
		}
		inFile.close();
		clock = new Timer(1000, new MyTimerListener()); //initialize action listener for my clock
		clock2 = new Timer(1000, new MyTimerListener());//initialize action listener for opponent clock

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 339);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblYourScore = new JLabel(n+ "'s score:"); //label for my name and score
		lblYourScore.setBounds(10, 27, 100, 14);
		contentPane.add(lblYourScore);

		clientScore = new JTextField();
		clientScore.setEditable(false);
		clientScore.setColumns(10);
		clientScore.setBounds(114, 24, 86, 20);
		contentPane.add(clientScore);
		clientScore.setText(""+score);

		opponentScore = new JTextField();
		opponentScore.setEditable(false);
		opponentScore.setBounds(344, 24, 86, 20);
		contentPane.add(opponentScore);
		opponentScore.setColumns(10);

		JLabel lblOpponentScore = new JLabel("Opponent's score:");
		lblOpponentScore.setBounds(226, 27, 120, 14);
		contentPane.add(lblOpponentScore);

		question = new JTextArea();
		question.setEditable(false);
		question.setText("Waiting for the opponent!");
		question.setBounds(10, 97, 414, 22);
		contentPane.add(question);

		gameChoices = new JTextArea();
		gameChoices.setEditable(false);
		gameChoices.setBounds(10, 126, 414, 89);
		contentPane.add(gameChoices);

		clientAns = new JTextField();
		clientAns.setBounds(10, 269, 190, 20);
		contentPane.add(clientAns);
		clientAns.setColumns(10);

		JButton submit = new JButton("Submit");
		//When submit button is clicked this function adds 5 points 
		//to the total score if the answer is right and 0 points if it is wrong
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(clientAns.getText().trim().equalsIgnoreCase(answer))
				{
					score+= 5;
					clientScore.setText(""+score);
				}
				clientAns.setText(null);
				myScore.setContent(Integer.toString(score)); 
				myScore.setmType(2);
				try {
					out.writeUTF(myScore.toString());
				}		
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
				try{
					generateQuestion();
				}catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});
		submit.setBounds(210, 268, 89, 23);
		contentPane.add(submit);

		JLabel lblTypeInYour = new JLabel("Enter A, B, C, or D:");
		lblTypeInYour.setBounds(10, 244, 120, 14);
		contentPane.add(lblTypeInYour);
		
		label = new JLabel("");
		label.setBounds(10, 301, 61, 16);
		contentPane.add(label);
		sendMsg(name,0, "hello");
	}

	/**sends message to DataOutputStream
	 * @param name
	 * @param type
	 * @param content
	 * @throws IOException
	 */
	public void sendMsg(String name, int type, String content) throws IOException{
		Message g=new Message(name, type, content);
		out.writeUTF(g.toString());
	}

	/**Generates a question from the Question array and gets the correct answer
	 * @throws Exception
	 */
	public void generateQuestion() throws Exception{		
		if(index<=10)
		{
			question.setText(questions.get(index).getQuestion());  
			answer=questions.get(index).getAnswer().trim();
			gameChoices.setText(null);
			for(int i=0;i<4;i++) 
			{
				if(i==0)
					gameChoices.append("A: "+questions.get(index).getChoice(i)+"\n");
				else if (i==1)
					gameChoices.append("B: "+questions.get(index).getChoice(i)+"\n");
				else if (i==2)
					gameChoices.append("C: " +questions.get(index).getChoice(i)+"\n");
				else
					gameChoices.append("D: "+questions.get(index).getChoice(i)+"\n");
			}
			question.repaint();
			gameChoices.repaint();					
			index++;
		}
		else
		{
			question.setText("Waiting for opponent to finish");
			gameChoices.setText(null);
			sendMsg(name, 3, "finished");
		}
	}

	/**Calculates the winner and loser at the end of the game
	 * @throws IOException
	 */
	public void getWinner() throws IOException
	{
		if(opponent.getOpponentScore()>score)
		{
			JOptionPane.showMessageDialog(null, "You lose");
		}
		else if(opponent.getOpponentScore()<score)
		{
			JOptionPane.showMessageDialog(null, "You Are The Victor!");
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Tie Game!");
		}
		contentPane.setVisible(false);
		dispose();
		
	}
	/**
	 * @return numSeconds
	 */
	public int getNumSec() 
	{
		return numSeconds;
	}
	/**
	 * This method creates a Thread for reading communication for each client
	 */
	public void runMain()
	{
		opponent = new ThreadRead(in,socket,serverMsg,opponentScore, this, clock, clock2);
		opponent.execute();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#getName()
	 */
	public String getName() 
	{
		return name;
	}
	
	/**This method formats number of seconds into a clock display
	 * @param numSeconds
	 * @return String clock display
	 */
	public String formatTime(int numSeconds )
	   {
	      int minutes = numSeconds/60;
	      int seconds = numSeconds % 60;
	      DecimalFormat formatter = new DecimalFormat("00");
	   		
	      String format = formatter.format(minutes)+":"+formatter.format(seconds);
	      return format;
	   	
	   }
	
	private class MyTimerListener implements ActionListener
	   {
	      /**
	      This method increments numSeconds every 1000 milliseconds and displays the
	      formatted time
	      @param e ActionEvent object
	      */
	      public void actionPerformed(ActionEvent e)
	      {
	         if(numSeconds != 0)
	         {
	    	  numSeconds--;
	         }
	         clock_display = formatTime(numSeconds);
	         try
	         {	           
	           label.setText(formatTime(numSeconds));
	         }
	         catch(Exception ex)
	         {
	            System.out.println("ERROR DISPLAYING TIME");
	         }
	         
	      
	      }
	   	
	   		
	   }
}
