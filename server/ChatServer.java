package server;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatServer {
	public static final int PORT = 4312;
	public static List<ChatHandler> listSockets = new ArrayList<>();
	
	public static void main(String[] args) {
		ServerSocket socketServer = null;
		Socket socket = null;
		ChatHandler chatHandler = null;
		
		try {
			socketServer = new ServerSocket(PORT);
			System.out.println("Server is running on PORT=" + PORT);
			while (true) {
				System.out.println("Waiting a client ...");
				socket = socketServer.accept();
				if (socket.isConnected()) {
					DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
					String username = dataInputStream.readUTF();
					System.out.println("Client accepted! " + socket);
	
					chatHandler = new ChatHandler(socket, username, username + "-" + new Date().getTime());
					listSockets.add(chatHandler);
					new Thread(chatHandler).start();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
