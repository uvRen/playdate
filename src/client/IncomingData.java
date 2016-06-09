package client;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import javax.imageio.ImageIO;

import helppackage.SendableData;

public class IncomingData implements Runnable {
	
	private ObjectInputStream in;
	private Socket clientSocket;
	private Client client;
	private ClientMainController clientMainController;
	
	public IncomingData(Client client, Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.client = client;
		clientMainController = Main.getClientMainController();
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
				e.printStackTrace();
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
			//Go through all send codes in the incoming data
			for(Integer code : data.getCode()) {
				handleSendcode(data, code);
			}
			data.setMainCode(1001);
			sendToServer(data);
			break;
		//Server request printscreen
		case 1002:
			java.awt.Rectangle screen = new java.awt.Rectangle((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
															   (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight());

			try {
				BufferedImage capture = new Robot().createScreenCapture(screen);
				ImageIO.write(capture, "png", new File("test.png"));
			} 
			catch (AWTException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		case 1:
			data.addData(getComputerName());
			break;
		case 2:
			data.addData(getUsername());
			break;
		case 3:
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
