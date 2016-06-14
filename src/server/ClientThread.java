package server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import helppackage.ClientUser;
import helppackage.SendableData;

public class ClientThread implements Runnable {
	
	private Server 				server;
	private ObjectInputStream 	in;
	private ObjectOutputStream 	out;
	private Preferences 		preference;
	private ClientUser			client;
	
	private boolean 			showPrintScreen = false;
	
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
		while(true) {
			try {
				handle(in.readObject());
			}
			catch (ClassNotFoundException | IOException e) {
				server.removeClient(client.getId());
				break;
			}
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
	 * Send request to client to take a printscreen
	 */
	public void requestPrintScreen(boolean showPrintScreen) {
		SendableData data = new SendableData();
		data.setMainCode(1002);
		
		sendToClient(data);
		this.showPrintScreen = showPrintScreen;
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
	 * Read incoming data that represents an image
	 * @param data	SendableData-object from client that contains data about an image
	 * @return		BufferedImage-object represents an image
	 */
	private BufferedImage readPrintScreenFromClient(SendableData data) {
		int height 		= (int)data.getData().get(0);
		int width 		= (int)data.getData().get(1);
		int[] pixels 	= (int[])data.getData().get(2);
		
		BufferedImage bi = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
        bi.setRGB(0, 0, width, height, pixels, 0, width);
        
        if(this.showPrintScreen) {
        	try {
				ImageIO.write(bi, "png", new File("printscreen.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	System.out.println("IMAGE SHOULD ALSO BE SHOWN");
        }
        else {
        	try {
				ImageIO.write(bi, "png", new File("printscreen.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        //Restore to default
        this.showPrintScreen = false;
        
        return bi;
	}
	
	/**
	 * Extracts the data that client sent to server about itself.
	 * @param data	SendableData-object data client sent.
	 */
	private void extractClientUserData(SendableData data) {
		this.client = new ClientUser(server.assignClientUniqueId());
		
		for(int i = 0; i < data.getCode().size(); i++) {
			switch(data.getCode().get(i)) {
			case 1:
				client.setComputername((String)data.getData().get(i));
				break;
			case 2:
				client.setUsername((String)data.getData().get(i));
				break;
			case 3:
				client.setIpaddress((String)data.getData().get(i));
				break;
			}
		}
		this.client.setMacaddress((String)data.getData().get(data.getData().size()-1));
		server.addUser(client);
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
			extractClientUserData(data);
			break;
		//Client sent requested printscreen
		case 1003:
			readPrintScreenFromClient(data);
			break;
		}
	}
}
