package server;

import java.awt.MouseInfo;
import java.io.IOException;
import java.util.Scanner;
import java.util.prefs.Preferences;

import helppackage.ClientUser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ContextMenuBuilder;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ServerMainController {
	
	@FXML private MenuItem 				start_stop_server;
	@FXML private Circle 				serverStatusCircle;
	@FXML private BorderPane			borderpane;
	
	private Server 						server = null;
	private Preferences 				preference;
	
	private ObservableList<ClientUser> 	userInfo;
	private TreeView<String> 			treeviewUsers;
	private TreeItem<String> 			rootNode;
	
	private ContextMenu 				contextMenu;
	private TreeItem<String>			rightClickedItem;
	
	public ServerMainController() {
		preference 		= Preferences.userRoot().node(Server.class.getName());
		userInfo 		= FXCollections.observableArrayList();
	}
	
	
	/**
	 * Function gets called when user press the 'Start server' or 'Stop server' menuItem
	 * and determines if the server should start or stop. 
	 */
	public void startStopServer() {
		if(start_stop_server.getText().equals("Start server")) {
			if(startServer()) {
				changeTextOnMenuItem(start_stop_server, "Stop server");
				changeColorOnServerStatus(Color.GREEN);
			}
		}
		else if(start_stop_server.getText().equals("Stop server")) {
			if(stopServer()) {
				changeTextOnMenuItem(start_stop_server, "Start server");
				changeColorOnServerStatus(Color.RED);
			}
		}
	}
	
	/**
	 * When user clicks 'Preference' a window with settings should appear
	 */
	public void showPreferenceWindow() {
		PreferenceServerController controller;
		
		FXMLLoader loader 	= openWindow("PreferenceServerScene.fxml", 600, 400);
		controller 			= (PreferenceServerController)loader.getController();
		
		controller.initTreeViewSettings();
	}
	
	/**
	 * When user clicks 'About' a window with settings should appear
	 */
	public void showAboutWindow() {
		openWindow("AboutServerScene.fxml", 200, 100);
	}
	
	/**
	 * Add a new user to the listview
	 * @param user	User to be added
	 */
	@SuppressWarnings("unchecked")
	public void addUserToTreeview(ClientUser user) {
		this.userInfo.add(user);
		
		TreeItem<String> newClient = new TreeItem<String>();

		switch(preference.get("showclientinfo", "username")) {
    	case "Username":
    		newClient.setValue((user.getUsername().getValue()));
    		break;
    	case "Computername":
    		newClient.setValue((user.getComputername().getValue()));
    		break;
    	case "IP address":
    		newClient.setValue((user.getIpaddress().getValue()));
    		break;
    	}
			
		TreeItem<String> id 			= new TreeItem<String>("id: " + 			user.getId());
		TreeItem<String> username 		= new TreeItem<String>("username: " + 		user.getUsername().getValue());
		TreeItem<String> computername 	= new TreeItem<String>("computername: " + 	user.getComputername().getValue());
		TreeItem<String> ipaddress 		= new TreeItem<String>("ipaddress: " + 		user.getIpaddress().getValue());
		
		newClient.getChildren().addAll(id, username, computername, ipaddress);
		
		rootNode.getChildren().add(newClient);
	}
	
	/**
	 * Opens a new window with help with a FXML file
	 * @param fxmlPath	FXML filename
	 * @param width		Width of window
	 * @param height	Height of window
	 */
	private FXMLLoader openWindow(String fxmlPath, double width, double height) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			Parent root = (Parent) loader.load();
			
			Stage stage = new Stage();
			stage.setScene(new Scene(root, width, height));
			stage.show();
			
			return loader;
		}
		catch(IOException e) {
			System.err.println("ServerMainController: Failed to open window (" + fxmlPath + ")");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Start up the server at given port
	 */
	private boolean startServer() {
		if(server == null) {
			this.server = new Server();
			setupTreeView();
			return server.startServer();
		}
		else {
			return server.startServer();
		}
	}
	
	/**
	 * Removes a client from the list
	 * @param id	Id of client
	 */
	private void removeUserFromListByID(int id) {
		for(ClientUser cu : userInfo) {
			if(cu.getId() == id) {
				Platform.runLater(new Runnable() {
					public void run() {
						userInfo.remove(cu);
						rightClickedItem.getParent().getChildren().remove(rightClickedItem);
					}
				});
			}
		}
	}
	
	/**
	 * Initialize the TreeView. Add root node and ContextMenu
	 */
	private void setupTreeView() {
		rootNode = new TreeItem<String>();
		
		treeviewUsers = new TreeView<String>(rootNode);
		
		contextMenu = new ContextMenu();
		MenuItem forceDisconnect = new MenuItem("Force disconnect");
		forceDisconnect.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent e) {
				//Extract the ID of client
				@SuppressWarnings("resource")
				Scanner in = new Scanner(rightClickedItem.getChildren().get(0).getValue()).useDelimiter("[^0-9]+");
				//Send ID to server and remove client from list
				int id = in.nextInt();
				if(server.forceDisconnectClient(id)) {
					removeUserFromListByID(id);
				}
			}
		});
		
		contextMenu.getItems().add(forceDisconnect);
		
		treeviewUsers.setCellFactory(tree -> {
			TreeCell<String> cell = new TreeCell<String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty) ;
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item);
                    }
                }
            };
            
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {
                	if(event.getButton() == MouseButton.SECONDARY) {
	                    TreeItem<String> treeItem 	= cell.getTreeItem();
	                    this.rightClickedItem 		= treeItem;
	                    contextMenu.show(treeviewUsers, MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY());
                	}
                }
            });
            return cell ;
		});
		
		treeviewUsers.setShowRoot(false);
		borderpane.setLeft(treeviewUsers);
	}
	
	/**
	 * Stop the server
	 */
	private boolean stopServer() {
		return server.stopServer();
	}
	
	/**
	 * Change the text on a MenuItem
	 * @param item		MenuItem that should be changed
	 * @param newText	The new text to the MenuItem
	 */
	private void changeTextOnMenuItem(MenuItem item, String newText) {
		item.setText(newText);
	}
	
	/**
	 * Change color on serverStatusCircle
	 * @param color	The new color
	 */
	private void changeColorOnServerStatus(Color color) {
		serverStatusCircle.setFill(color);
	}
	
	
}
