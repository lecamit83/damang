package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatHandler implements Runnable {
	private Socket socket;
	private String name, label;
	private DataInputStream socketInputStream;
	private DataOutputStream socketOutputStream;

	private boolean isLogged = true;

	public ChatHandler(Socket socket, String name, String label) throws IOException {
		this.socket = socket;
		this.name = name;
		this.label = label;
		this.socketInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		this.socketOutputStream = new DataOutputStream(socket.getOutputStream());
		this.isLogged = true;
		joiningRoom();
	}

	private void joiningRoom() throws IOException {
		String users = "";
		this.sendData("Tôi: vừa tham gia");
		for (ChatHandler s : ChatServer.listSockets) {
			s.sendData("CONNECTED:" + this.name + "\n");
			s.sendData(this.name + ": vừa tham gia");
			users = users.concat(s.getName()).concat("\n");
		}
		users = users.concat("(Tôi) ").concat(this.name).concat("\n");
		this.sendData("CONNECTED:" + users);
	}
	private void leavingRoom(List<String> users) {
		for (ChatHandler s : ChatServer.listSockets) {
			try {
				int index = users.indexOf(s.getLabel());
				s.sendData("DISCONNECTED:" + toString(users, index));
				s.sendData(this.name + ": vừa thoát khỏi phòng");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String toString(List<String> users, int index) {
		String res = "";
		for(int i = 0; i < users.size(); i++) {
			if(i == index) {
				 res = res.concat("(Tôi) ").concat(users.get(i).split("-")[0]).concat("\n");	
			} else {
				res = res.concat(users.get(i).split("-")[0]).concat("\n");	
			}
		}
		return res;
	}
	
	public String getName() {
		return this.name;
	}
	public String getLabel() {
		return this.label;
	}
	public void close() throws IOException {
		this.socketInputStream.close();
		this.socketOutputStream.close();
		this.socket.close();
	}

	public void sendData(String message) throws IOException {
		socketOutputStream.writeUTF(message);
		socketOutputStream.flush();
	}

	@Override
	public void run() {
		while (this.isLogged) {
			try {
				String message = socketInputStream.readUTF();
				for (ChatHandler s : ChatServer.listSockets) {
					if (!s.getLabel().equals(this.label)) {
						s.sendData(name + ":" + message);
					} else {
						s.sendData("Tôi:" + message);
					}
				}
			} catch (Exception e) {
				this.isLogged = false;
				break;
//				e.printStackTrace();
			}
		}
		if (!this.isLogged) {
			List<String> users = new ArrayList<>(); 
			for (int i = 0; i < ChatServer.listSockets.size(); i++) {
				if (ChatServer.listSockets.get(i).getLabel().equals(this.label)) {
					ChatServer.listSockets.remove(i);
					i--;
				} else {
//					users = users.concat(ChatServer.listSockets.get(i).getName()).concat("\n");
					users.add(ChatServer.listSockets.get(i).getLabel());
				}
			}
			System.out.println(name + " disconnected!");
			leavingRoom(users);
			try {
				this.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
