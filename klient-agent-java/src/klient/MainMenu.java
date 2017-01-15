package klient;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;

public class MainMenu extends JFrame {

	private JPanel contentPane;
	private final JLabel lblChoseAnOption = new JLabel("Chose an option");
	private Socket clientSocket;
	boolean client = false;
	private BufferedReader reader;
	private PrintWriter writer;
	private String serverMessage = "";
	private String[] sockets;
	private JComboBox comboBox;
	private JTextArea textAreaLog;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainMenu frame = new MainMenu();
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
	public MainMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		lblChoseAnOption.setBounds(15, 5, 314, 15);
		contentPane.add(lblChoseAnOption);
		
		JPanel panel_menu = new JPanel();
		panel_menu.setBounds(145, 22, 166, 35);
		contentPane.add(panel_menu);
		
		JButton btnClient = new JButton("Client");
		panel_menu.add(btnClient);
		
		JButton btnAgent = new JButton("Agent");
		panel_menu.add(btnAgent);
		
		JPanel panel_client = new JPanel();
		panel_client.setBounds(26, 85, 166, 150);
		panel_client.setVisible(false);
		contentPane.add(panel_client);
		 panel_client.setLayout(null);
		
		 comboBox = new JComboBox();
		 comboBox.setBounds(0, 0, 154, 113);
		panel_client.add(comboBox);
	/*	JScrollPane scroll = new JScrollPane (textAreaLog, 
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		contentPane.add(scroll);*/
		
		JButton btnKill = new JButton("Kill selected");
		btnKill.setBounds(0, 125, 185, 25);
		panel_client.add(btnKill);
		
		btnKill.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			//	textAreaLog.append(sockets[comboBox.getSelectedIndex()]);
				textAreaLog.append(comboBox.getSelectedItem().toString().trim());
				int socekt_id = Integer.parseInt(comboBox.getSelectedItem().toString().trim());
				//textAreaLog.append("Trying to kill agent with id " + socekt_id + "\n");
				sendMessageToServer("client-kill-"+socekt_id+"\n");
			}
			
		});
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(204, 74, 244, 161);
		contentPane.add(scrollPane);
		
		textAreaLog = new JTextArea();
		scrollPane.setViewportView(textAreaLog);
		startServerConnection();
		btnClient.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				panel_menu.setVisible(false);
				panel_client.setVisible(true);
				lblChoseAnOption.setText("Chose an agent to kill");
				client = true;
				textAreaLog.append("Trying to registrate \n");
				sendMessageToServer("client-registrate-n");
				//sendMessageToServer("client-kill-" + 1);
			}
		});
		
		btnAgent.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				panel_menu.setVisible(false);
				lblChoseAnOption.setText("Await to be killed");
				textAreaLog.append("Await to be killed"+ "\n");
			}
		});
		
	}
	
	public void sendMessageToServer(String msg){
		try {
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
				writer.println(msg);
				System.out.println("sending msg to server " + msg);
				textAreaLog.append("sending msg to server:   " + msg+ "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void readMessageFromServer(){
	
		try {		
			
				serverMessage = reader.readLine();
				//lblChoseAnOption.setText("srv msg " + serverMessage);
				System.out.println("reading msg from server " + serverMessage);
				textAreaLog.append("reading msg from server:   " + serverMessage+ "\n");
				if(serverMessage.contains("kill")){
					
				}else if(serverMessage.contains("status-")){
					serverMessage = serverMessage.replaceAll("status-", "");
					//serverMessage = serverMessage.replaceAll("-end", "");
					sockets = serverMessage.split("-");
					comboBox.setModel(new DefaultComboBoxModel(sockets));
					textAreaLog.append("updated server status \n");
				}else if(serverMessage.contains("Connected")){
				}else{
			
					textAreaLog.append("message not recognised \n");
				}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public static void shutdown() throws RuntimeException, IOException {
	    String shutdownCommand;
	    String operatingSystem = System.getProperty("os.name");

	    if ("Linux".equals(operatingSystem) || "Mac OS X".equals(operatingSystem)) {
	        shutdownCommand = "shutdown -h now";
	    }
	    else if ("Windows".equals(operatingSystem)) {
	        shutdownCommand = "shutdown.exe -s -t 0";
	    }
	    else {
	        throw new RuntimeException("Unsupported operating system.");
	    }

	    Runtime.getRuntime().exec(shutdownCommand);
	    System.exit(0);
	}
	
	public void startServerConnection(){
		try {
			 clientSocket = new Socket("localhost", 1256);
			 reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			 writer = new PrintWriter(clientSocket.getOutputStream(), true);		
			 
			//writer.println(clientMessage);
			
			//Thread.sleep(5000);
			//writer.println(clientMessage);
		//	String serverMessage = reader.readLine();
			//System.out.print(serverMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread handler = new Thread(){
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				textAreaLog.append("started listening for messages" + "\n");
					while(true){
						readMessageFromServer();
					}
				}
			
		};
		handler.start();
		System.out.println("initialized connection");
		textAreaLog.append("initialized connection" + "\n");
	}
}
