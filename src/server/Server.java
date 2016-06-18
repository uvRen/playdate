package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import client.ExternalFunctionality;
import helppackage.ClientUser;
import helppackage.SendCodes;
import javafx.application.Platform;

/**
 * A multi-threaded JavaServer that handles incoming connections and data.
 * The server has one thread that listen for incoming connection and one
 * thread for each connected client
 * @author Simon Berntsson
 *
 */
public class Server {
	
	private ServerSocket 			server;
	private ArrayList<ClientThread> clients;
	private Preferences 			preference;
	private int 					clientIdController;
	private static String			operatingSystem;
	
	public static SendCodes 		sendCodes;
	
	/**
	 * Server constructor
	 * @param port	Port that server should run on
	 */
	public Server() {
		clients 	= new ArrayList<ClientThread>();
		sendCodes 	= new SendCodes();
		preference 	= Preferences.userRoot().node(Server.class.getName());
		clientIdController = 0;
		operatingSystem = System.getProperty("os.name");
	}
	
	/**
	 * Check if server is running on Windows
	 * @return	<b>True</b> if Windows, else <b>False</b>
	 */
	public static boolean isWindows() {
		if(operatingSystem == null)
			operatingSystem = System.getProperty("os.name");
		return operatingSystem.startsWith("Windows");
	}
	
	/**
	 * Gets the default save path where the client data should be saved.
	 * Windows should be C:\Users\Username\playdate
	 * @return
	 */
	public static String getDeafultSaveLocation() {
		if(Server.isWindows())
			return "C:\\Users\\" + ExternalFunctionality.getUsername() + "\\playdate\\";
		else
			return "";
	}
	
	/**
	 * Start server on port that was given in constructor and start to listen for incoming connections
	 * @return	<b>True</b> if success, else <b>False</b>
	 */
	public boolean startServer() {
		try {
			//Start server on given port, default 9999
			server = new ServerSocket(preference.getInt("port", 9999));
			
			//Creates a folder where all userdata can be saved
			createFolderToContainClientData();
			
			//Start a Thread that listen for incoming connections
			new Thread(new ListenForIncomingConnections(this, server)).start();
			
			return true;
		}
		catch(IOException e) {
			System.err.println("Server.java: Server couldn't start");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Stop server
	 * @return	<b>True</b> if success, else <b>False</b>
	 */
	public boolean stopServer() {
		try {
			server.close();
			closeAllThread();
			return true;
		}
		catch(IOException e) {
			System.err.println("Server.java: Server couldn't stop");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Remove client from list
	 * @param id	Id of client
	 */
	public void removeClient(int id) {
		Main.getServerMainController().removeUserFromListByID(id);
	}
	
	/**
	 * When a client connects to the server the ClientThread object
	 * will be added to the servers ArrayList[ClientThread] clients
	 * @param client	ClientThread object for the client that connected
	 */
	public void addClient(ClientThread client) {
		this.clients.add(client);
	}
	
	/**
	 * Add a client to the list of clients
	 * @param user	ClientUser to be added
	 */
	public void addUser(ClientUser user) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Main.getServerMainController().addUserToTreeview(user);
			}
		});
	}
	
	/**
	 * Force a client to disconnect from server
	 * @param clientId	ID of client
	 * @return	<b>True</b> if success, else <b>False</b>
	 */
	public boolean forceDisconnectClient(int clientId) {
		//Find the Thread that the client is on
		for(ClientThread ct : clients) {
			//When found, force disconnection
			if(ct.getClient().getId() == clientId) {
				ct.forceDisconnect();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Request a printscreen from client
	 * @param clientId			Clients ID
	 * @param showPrintScreen	If the received printscreen should be shown and saved, or just saved.
	 */
	public void requestPrintScreenFromClient(int clientId, boolean showPrintScreen) {
		for(ClientThread ct : clients) {
			//When found, force disconnection
			if(ct.getClient().getId() == clientId) {
				ct.requestPrintScreen(showPrintScreen);
			}
		}
	}
	
	/**
	 * Gets the status of the server connection
	 * @return	<b>True</b> if server is online, else <b>False</b>
	 */
	public boolean isServerOnline() {
		return !server.isClosed();
	}
	
	/**
	 * Returns an unique id to be assign to a client
	 * @return	Unique number
	 */
	public int assignClientUniqueId() {
		return this.clientIdController++;
	}
	
	/**
	 * Creates a folder where all the data about the clients can be saved.
	 * If users hasn't select a own path it will be C:\Users\Username\playdate
	 */
	private void createFolderToContainClientData() {
		//User hasn't given a path where to save data, so a folder is created at C:/Users/User
		java.nio.file.Path path;
		if(preference.get("userdatalocation", "").equals("")) {
			path = Paths.get(getDeafultSaveLocation());
			if(Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
				System.out.println("Create folder at: " + getDeafultSaveLocation());
				new File(getDeafultSaveLocation()).mkdir();
			}
		}
		//Create a folder where the user wants it to be stored
		else {
			path = Paths.get(preference.get("userdatalocation", ""));
			if(Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
				new File(path.toString()).mkdir();
			}
		}
	}
	
	/**
	 * When server is shutting down all threads has to be terminated 
	 */
	private void closeAllThread() {
		for(ClientThread ct : clients) {
			try {
				ct.in.close();
				ct.out.close();
			}
			catch(IOException e) {
				System.err.println("Server failed to close ClientThread streams");
				e.printStackTrace();
			}
		}
	}
}

/**
 * A runnable class that listen for incoming connections
 * @author Simon Berntsson
 */
class ListenForIncomingConnections implements Runnable {
	
	private ServerSocket socketserver;
	private Server server;
	
	public ListenForIncomingConnections(Server server, ServerSocket socketserver) {
		this.socketserver 	= socketserver;
		this.server 		= server;
	}
	
	public void run() {
		while(true) {
			try {
				//When a client connect a ClientThread object is created and a Thread
				ClientThread client = new ClientThread(server, socketserver.accept());
				new Thread(client).start();
				
				server.addClient(client);
			}
			catch(IOException e) {
				//Server shutdown
				break;
			}
		}
	}
}
