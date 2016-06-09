package client;

import java.io.IOException;
import java.net.Socket;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Client {
	
	private int port;
	private Socket client;
	
	public Client(int port) {
		this.port = port;
	}
	
	/**
	 * Establish a connection to the server at given port from constructor
	 * @return	<b>True</b> if success, else <b>False</b>
	 */
	public boolean connectToServer() {
		try {
			client = new Socket("localhost", port);
			//Start a Thread that listen for incoming data
			new Thread(new IncomingData(client)).start();
			return true;
		}
		catch(IOException e) {
			System.err.println("Client: Couldn't connect to server");
			e.printStackTrace();
			showAlertError("Error", 
						   "Server may be offline. Try again in a moment", 
						   "Error information\n" + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Show an error dialog
	 * @param title			Title 
	 * @param headerText	Header
	 * @param contextText	Context
	 */
	public void showAlertError(String title, String headerText, String contextText) {
		Alert failedToConnect = new Alert(AlertType.ERROR);
		failedToConnect.setTitle(title);
		failedToConnect.setHeaderText(headerText);
		failedToConnect.setContentText(contextText);
		failedToConnect.show();
	}
	
	/**
	 * Show an warning dialog
	 * @param title			Title 
	 * @param headerText	Header
	 * @param contextText	Context
	 */
	public void showAlertWarning(String title, String headerText, String contextText) {
		Alert failedToConnect = new Alert(AlertType.WARNING);
		failedToConnect.setTitle(title);
		failedToConnect.setHeaderText(headerText);
		failedToConnect.setContentText(contextText);
		failedToConnect.show();
	}
	
	/**
	 * Disconnect from server
	 * @return	<b>True</b> if success, else <b>False</b>
	 */
	public boolean disconnectFromServer() {
		try {
			client.close();
			return true;
		}
		catch(IOException e) {
			System.err.println("Client: Couldn't disconnect from server");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Gets the status of the connection to server
	 * @return	<b>True</b> if client is connected, else <b>False</b>
	 */
	public boolean isClientConnected() {
		return !client.isClosed();
	}
	
	
}
