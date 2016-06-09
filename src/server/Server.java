package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.prefs.Preferences;

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
	}
	
	/**
	 * Start server on port that was given in constructor and start to listen for incoming connections
	 * @return	<b>True</b> if success, else <b>False</b>
	 */
	public boolean startServer() {
		try {
			//Start server on given port, default 9999
			server = new ServerSocket(preference.getInt("port", 9999));
			
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
			return true;
		}
		catch(IOException e) {
			System.err.println("Server.java: Server couldn't stop");
			e.printStackTrace();
			return false;
		}
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
}

/**
 * A runnable class that listen for incoming connections
 * @author Simon Berntsson
 */
class ListenForIncomingConnections implements Runnable {
	
	private ServerSocket socketserver;
	private Server server;
	
	public ListenForIncomingConnections(Server server, ServerSocket socketserver) {
		this.socketserver = socketserver;
		this.server = server;
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
