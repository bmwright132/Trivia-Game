/*
 * Brandon Wright, Jake Snow, Jared Aarons
 * Dr.Liu
 * December 3rd, 2015
 * Client login screen for Trivia game
 */
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ClientLogin extends JFrame 
{
	private JPanel panelContent;
	private JTextField txtName;
	private static ClientLogin frame;
	private JTextField txtServer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
		

			public void run() {
				try {
					frame = new ClientLogin();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientLogin() 
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 322, 236);
		panelContent = new JPanel();
		panelContent.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(panelContent);
		panelContent.setLayout(null);
		
		JLabel lblName = new JLabel("Username:");
		lblName.setBounds(61, 36, 86, 14);
		panelContent.add(lblName);
		
		txtName = new JTextField();
		txtName.setText("Enter Username");
		txtName.setBounds(143, 33, 121, 20);
		panelContent.add(txtName);
		txtName.setColumns(10);
		
		JButton btnJoin = new JButton("Connect");
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					joinChat();
				} catch (Exception e1) {
					System.out.println("Unable to join game.");
					e1.printStackTrace();
				}
			}
		});
		btnJoin.setBounds(102, 121, 89, 23);
		panelContent.add(btnJoin);	
		
		JLabel lblNewLabel = new JLabel("Server:");
		lblNewLabel.setBounds(61, 79, 73, 20);
		panelContent.add(lblNewLabel);
		
		txtServer = new JTextField();
		txtServer.setText("Enter Server");
		txtServer.setBounds(143, 75, 121, 28);
		panelContent.add(txtServer);
		txtServer.setColumns(10);
		
	}
	
	
	public void joinChat() throws Exception
	{
		frame.dispose();
		Client gameFrame= new Client(txtName.getText(),txtServer.getText(),"20000");
		gameFrame.setVisible(true);
		gameFrame.runMain();
	}
}
