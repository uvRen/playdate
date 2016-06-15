package client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

import helppackage.SendableData;

public class IncomingData implements Runnable {
	
	private ObjectInputStream in;
	private Socket clientSocket;
	private Client client;
	private ClientMainController clientMainController;
	
	public IncomingData(Client client, Socket clientSocket) {
		this.clientSocket 		= clientSocket;
		this.client 			= client;
		clientMainController 	= Main.getClientMainController();
		setupStreams();
	}
	
	public void run() {
		while(true) {
			try {
				handle(in.readObject());
			}
			catch(SocketException e) {
				//Lost connection to server
				break;
			} 
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch(IOException e) {
				//Lost connection to server
				break;
			}
		}
	}
	
	/**
	 * Creates streams to communicate with server
	 */
	private void setupStreams() {
		try {
			in = new ObjectInputStream(clientSocket.getInputStream());
		}
		catch(IOException e) {
			System.err.println("IncomingData: Couldn't create stream");
			e.printStackTrace();
		}
	}
	
	/**
	 * Send data to server
	 * @param data	Data to be sent
	 */
	public void sendToServer(SendableData data) {
		client.sendToServer(data);
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
			handleStartupRequest(data);
			break;
		//Server request printscreen
		case 1002:
			sendPrintScreenToServer(data);
			break;
		//Server force client to disconnect
		case 2000:
			handleForceDisconnection();
			break;
		}
	}
	
	/**
	 * When server force client to disconnect from server client should disconnect
	 * and update all necessary text for example 'Disconnect from server' text should be
	 * changed to 'Connect to server'.
	 * It also displays a Dialog to announce that server has kicked the client
	 */
	private void handleForceDisconnection() {
		if(clientMainController.disconnectFromServer()) {
			clientMainController.changeTextOnConnectDisconnectMenuItem("Connect to server");
			clientMainController.showForceDisconnectionWindow();
		}
	}
	
	/**
	 * Collects all data that server requested when client connected. 
	 * Puts all data in a SendableData-object and the send it back to server
	 * @param data	SendableData object that server sent
	 * @param code	Which code that should be executed
	 */
	private void handleStartupRequest(SendableData data) {
		for(Integer code : data.getCode()) {
			switch(code) {
			case 1:
				data.addData(ExternalFunctionality.getComputerName());
				break;
			case 2:
				data.addData(ExternalFunctionality.getUsername());
				break;
			case 3:
				data.addData(ExternalFunctionality.getIPAdress());
				break;
			}
		}
		
		//Always add MAC-address to data, it is non optional
		data.addData(ExternalFunctionality.getMacAddress());
		
		data.setMainCode(data.getMainCode() + 1);
		sendToServer(data);
	}
	
	/**
	 * Take printscreen and send it to server
	 * @param data	SendableData object that server sent the printscreen request with
	 */
	private void sendPrintScreenToServer(SendableData data) {
		
		BufferedImage print = ExternalFunctionality.getPrintScreen();
		int height = print.getHeight();
		int width = print.getWidth();
		int[] pixels = new int[width * height];
		
		int[] temp = print.getRGB(0, 0, width, height, pixels, 0, width);
		
		data.getData().add(height);
		data.getData().add(width);
		data.getData().add(pixels);
		
		data.setMainCode(data.getMainCode() + 1);
		sendToServer(data);
		
	}
}
