package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import helppackage.ClientUser;
import helppackage.SendableData;

public class ClientThread implements Runnable {
	
	private Server 				server;
	private ObjectInputStream 	in;
	private ObjectOutputStream 	out;
	private Preferences 		preference;
	private ClientUser			client;
	
	public ClientThread(Server server, Socket client) {
		this.server = server;
		preference = Preferences.userRoot().node(Server.class.getName());
		setupObjectStreams(client);
		sendStartupRequestToClient();
	}
	
	/**
	 * Listen for incoming data from client
	 */
	public void run() {
		try {
			handle(in.readObject());
		} 
		catch (ClassNotFoundException | IOException e) {
			System.err.println("ClientThread.run(): Failed to read inputstream");
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the ClientUser object of the client at this Thread
	 * @return	ClientUser object
	 */
	public ClientUser getClient() {
		return this.client;
	}
	
	/**
	 * Force current client to disconnect from server
	 */
	public void forceDisconnect() {
		SendableData data = new SendableData();
		data.setMainCode(2000);
		
		sendToClient(data);
	}
	
	/**
	 * Creates streams to enable communication with server
	 * @param client	Clients socket
	 */
	private void setupObjectStreams(Socket client) {
		try {
			out = new ObjectOutputStream(client.getOutputStream());
			in	= new ObjectInputStream(client.getInputStream());
		} 
		catch (IOException e) {
			System.err.println("ClientThread: Failed to setup streams");
			e.printStackTrace();
		}
	}
	
	/**
	 * Send data to client
	 * @param data	Data to be sent
	 */
	private void sendToClient(SendableData data) {
		try {
			out.writeObject(data);
			out.flush();
		} catch (IOException e) {
			System.out.println("ClientThread.sendToClient(): Couldn't send data");
			e.printStackTrace();
		}
	}
	
	/**
	 * Send request of what the server wants from the client.
	 */
	private void sendStartupRequestToClient() {
		SendableData data = new SendableData();
		data.setMainCode(1000);
		
		try {
			//for each option that is true, add it do the SendableData
			for(String key : preference.keys()) {
				if(preference.getBoolean(key, false)) {
					data.addCode(Server.sendCodes.getCode(key));
				}
			}
		} catch (BackingStoreException e) {
			System.out.println("ClientThread: Failed to read properties");
			e.printStackTrace();
		}
		
		sendToClient(data);
	}
	
	/**
	 * Handles incoming data to server
	 * @param o	Incoming object
	 */
	private void handle(Object o) {
		SendableData data = (SendableData)o;
		
		switch(data.getMainCode()) {
		//Client answer server request '1000'
		case 1001:
			this.client = new ClientUser(server.assignClientUniqueId());
			for(int i = 0; i < data.getCode().size(); i++) {
				switch(data.getCode().get(i)) {
				case 1002:
					client.setComputername((String)data.getData().get(i));
					break;
				case 1004:
					client.setUsername((String)data.getData().get(i));
					break;
				case 1006:
					client.setIpaddress((String)data.getData().get(i));
					break;
				}
			}
			server.addUser(client);
			break;
		}
	}
}
