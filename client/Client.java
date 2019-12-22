package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client implements ActionListener {

	public static final int ServerPORT = 4312;

	public JFrame frame;
	public JPanel conversationPanel, listPanel, container, wrapperInput;
	public JTextArea messages, friends;
	public JTextField message;
	public String name;
	public JButton send;
	private JLabel chatLabel, headerLabel;
	public Socket socket;
	public DataInputStream dataInputStream;
	public DataOutputStream dataOutputStream;
//	public ArrayList<String> users = new ArrayList<String>(); 
	private String username, ip;
	private int PORT;
	private String users = "";

	public Client(String username, String ip, int PORT) {
		this.username = username;
		this.ip = ip;
		this.PORT = PORT;
		createUI();
		try {
			System.out.println(ip + " " + PORT);
			socket = new Socket(ip, PORT);
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataOutputStream.writeUTF(this.username);

			Thread receiptMessage = new Thread(new Runnable() {
				@Override
				public void run() {
					while (!Thread.interrupted()) {
						try {
							String message = dataInputStream.readUTF();
							if (message.contains("DISCONNECTED")) {
								String users = message.split(":")[1];
//								users.remove(username.indexOf(username));
//								String userList = "";
//								for(String user : users) {
//									userList += user + "\n";
//								}
								friends.setText(users);
							} else if (message.contains("CONNECTED")) {
								String username = message.split(":")[1];
								users += username;
								friends.setText(users);
							} else {
								messages.append(message + "\n");
							}
						} catch (Exception e) {
							break;
						}
					}
				}
			});

			receiptMessage.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createUI() {
		this.frame = new JFrame("Chat Room!");
		this.frame.setSize(1280, 720);
		this.frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setLocationRelativeTo(null);
		this.frame.setResizable(false);
		this.frame.setUndecorated(false);

		this.listPanel = new JPanel(new BorderLayout(4, 4));
		this.listPanel.setPreferredSize(new Dimension(314, 680));
		this.listPanel.setBackground(Color.WHITE);

		this.conversationPanel = new JPanel(new BorderLayout(4, 4));
		this.conversationPanel.setPreferredSize(new Dimension(952, 680));

		this.wrapperInput = new JPanel(new BorderLayout(0, 4));

		this.headerLabel = new JLabel("TIN NHẮN");
		this.headerLabel.setForeground(Color.BLACK);

		this.messages = new JTextArea();
		this.messages.setBackground(Color.WHITE);
		this.messages.setEditable(false);
		this.conversationPanel.add(messages, BorderLayout.CENTER);

		this.message = new JTextField();
		this.wrapperInput.add(message, BorderLayout.CENTER);
		this.send = new JButton("Gửi tin nhắn");
		this.send.addActionListener(this);
		this.wrapperInput.add(send, BorderLayout.EAST);
		this.conversationPanel.add(headerLabel, BorderLayout.NORTH);
		this.conversationPanel.add(wrapperInput, BorderLayout.SOUTH);

		this.chatLabel = new JLabel("ĐANG HOẠT ĐỘNG");
		this.listPanel.add(chatLabel, BorderLayout.NORTH);

		this.friends = new JTextArea();
		this.friends.setBackground(Color.WHITE);
		this.friends.setEditable(false);

		this.listPanel.add(friends, BorderLayout.CENTER);
		this.frame.add(this.listPanel);
		this.frame.add(this.conversationPanel);

		this.frame.setVisible(true);
	}

//	public static void main(String[] args) {
//		new Client("localhost", "localhost", 4312);
//	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == send) {
			try {
				dataOutputStream.writeUTF(message.getText());
				message.setText("");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
