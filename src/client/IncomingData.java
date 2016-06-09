package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import helppackage.SendableData;

public class IncomingData implements Runnable {
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket client;
	private ClientMainController clientMainController;
	
	public IncomingData(Socket client) {
		this.client = client;
		clientMainController = Main.getClientMainController();
		setupStreams();
	}
	
	public void run() {
		while(true) {
			try {
				handle(in.readObject());
			}
			catch(IOException e) {
				//Lost connection to server
				break;
			} 
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Creates streams to communicate with server
	 */
	private void setupStreams() {
		try {
			in = new ObjectInputStream(client.getInputStream());
			out = new ObjectOutputStream(client.getOutputStream());
		}
		catch(IOException e) {
			System.err.println("IncomingData: Couldn't create stream");
			e.printStackTrace();
		}
	}
	
	private void sendToServer(SendableData data) {
		try {
			out.writeObject(data);
			out.flush();
		} catch (IOException e) {
			System.err.println("IncomingData.sendToServer(): Failed to send data to server");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Handle incoming data from server
	 * @param o	Incoming object
	 */
	private void handle(Object o) {
		SendableData data = (SendableData)o;
		
		switch(data.getMainCode()) {
		//Server send startup request
		case 1000:
			//Go through all send codes in the incoming data
			for(Integer code : data.getCode()) {
				handleSendcode(data, code);
			}
			data.setMainCode(1001);
			sendToServer(data);
			break;
		//Disconnect from server
		case 2000:
			if(clientMainController.disconnectFromServer()) {
				clientMainController.changeTextOnConnectDisconnectMenuItem("Connect to server");
				clientMainController.showForceDisconnectionWindow();
			}
			break;
		}
	}
	
	/**
	 * Go through all the code in SendableData.code and perform action for each one of them
	 * @param data	SendableData object that server sent
	 * @param code	Which code that should be executed
	 */
	private void handleSendcode(SendableData data, int code) {
		switch(code) {
		case 1002:
			data.addData(getComputerName());
			break;
		case 1004:
			data.addData(getUsername());
			break;
		case 1006:
			data.addData(getIPAdress());
			break;
		}
	}
	
	/**
	 * Get username of the user that is logged in
	 * @return	Name of user
	 */
	private String getUsername() {
		return System.getProperty("user.name");
	}
	
	/**
	 * Get name of the computer
	 * @return	Name of computer
	 */
	private String getComputerName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch(IOException e) {
			return "";
		}
	}
	
	/**
	 * Get the external IP-address
	 * @return	External IP-adress
	 */
	private String getIPAdress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		}
		catch(IOException e) {
			return "";
		}
	}
}
