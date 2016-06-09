package server;

import java.util.prefs.Preferences;

import javax.swing.plaf.OptionPaneUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class PreferenceServerController {
	
	@FXML private BorderPane 	borderpane;
	@FXML private HBox 			contentContainer;
	@FXML private GridPane 		optionContainer;
	@FXML private Label 		header;
	
	private Preferences 		preference;
	private TreeItem<String>	currentSelection = null;
	private Button				saveButton;
	
	private TextField serverNameTextField,
					  serverPortTextField,
					  serverConnectionsTextField;
	
	private CheckBox clientComputerNameCB,
					 clientUsernameCB,
					 clientIPAdressCB;
	
	private ComboBox<String> comboBoxShowClientInfo;
	
	public PreferenceServerController() {
		preference = Preferences.userRoot().node(Server.class.getName());
		
		this.saveButton = new Button("Save");
		saveButton.setOnMouseClicked(e -> 
		{
			saveOptions();
		});
	}
	 
	
	/**
	 * Initialize the TreeView containing all options and by default show all
	 * options for the group 'General'
	 */
	@SuppressWarnings("unchecked")
	public void initTreeViewSettings() {
		TreeItem<String> root 		= new TreeItem<String>("DummyNode");
		TreeItem<String> general 	= new TreeItem<String>("General");
		TreeItem<String> client 	= new TreeItem<String>("Client");
		
		root.getChildren().addAll(general, client);
		
		//Set 'currentSelection' default to 'General'
		currentSelection = general;
		
		TreeView<String> tree = new TreeView<String>(root);
		tree.setShowRoot(false);
		
		//EventHandler for when user clicks an item in the TreeView
		tree.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				handleMouseClickListView(tree.getSelectionModel().getSelectedItem());
			}
		});
		
		//Remove the TreeView that is added from SceneBuilder and replace it with a new one
		contentContainer.getChildren().remove(0);
		contentContainer.getChildren().add(0, tree);
		
		createOptionsForGeneral();
	}
	
	/**
	 * Add all options connected to 'General' to the GridPane
	 */
	private void createOptionsForGeneral() {
		//If the is something in the GridPane, clear it first
		optionContainer.getChildren().clear();
		
		header.setText("General");
		
		Label serverNameLabel = new Label("Servername");
		GridPane.setConstraints(serverNameLabel, 0, 0);
		
		serverNameTextField = new TextField();
		serverNameTextField.setText(preference.get("servername", "server1"));
		GridPane.setConstraints(serverNameTextField, 1, 0);
		
		Label serverPortLabel = new Label("Port");
		GridPane.setConstraints(serverPortLabel, 0, 1);
		
		serverPortTextField = new TextField();
		serverPortTextField.setText(Integer.toString(preference.getInt("port", 9999)));
		GridPane.setConstraints(serverPortTextField, 1, 1);
		
		Label serverConnectionsLabel = new Label("Connections");
		GridPane.setConstraints(serverConnectionsLabel, 0, 2);
		
		serverConnectionsTextField = new TextField();
		serverConnectionsTextField.setText(preference.get("connections", "-1"));
		GridPane.setConstraints(serverConnectionsTextField, 1, 2);
		
		Label connectionExplain = new Label("(-1 is infinity)");
		GridPane.setConstraints(connectionExplain, 2, 2, 3, 1);
		
		Label comboExplain = new Label("What data should be shown about a client");
		GridPane.setConstraints(comboExplain, 0, 3, 4, 1);
		
		ObservableList<String> choice = FXCollections.observableArrayList("Username", "Computername", "IP address");
		comboBoxShowClientInfo = new ComboBox<String>(choice);
		comboBoxShowClientInfo.getSelectionModel().select(preference.get("showclientinfo", "IP address"));
		GridPane.setConstraints(comboBoxShowClientInfo, 0, 4, 2, 1);

		GridPane.setConstraints(this.saveButton, 0, 14);
		
		optionContainer.getChildren().addAll(serverNameLabel, 
											 serverNameTextField,
											 saveButton,
											 serverPortLabel,
											 serverPortTextField,
											 serverConnectionsLabel,
											 serverConnectionsTextField,
											 connectionExplain, 
											 comboExplain,
											 comboBoxShowClientInfo);
	}
	
	/**
	 * Add all options connected to 'Client' to the GridPane
	 */
	private void createOptionsForClient() {
		//If the is something in the GridPane, clear it first
		optionContainer.getChildren().clear();
		
		header.setText("Client");
		
		clientComputerNameCB = new CheckBox("Computer name");
		clientComputerNameCB.setSelected(preference.getBoolean("clientComputerName", false));
		GridPane.setConstraints(clientComputerNameCB, 0, 1, 2, 1);
		
		clientUsernameCB = new CheckBox("User name");
		clientUsernameCB.setSelected(preference.getBoolean("clientUsername", false));
		GridPane.setConstraints(clientUsernameCB, 0, 2, 2, 1);
		
		clientIPAdressCB = new CheckBox("IP");
		clientIPAdressCB.setSelected(preference.getBoolean("clientIPAdress", false));
		GridPane.setConstraints(clientIPAdressCB, 0, 3, 2, 1);
		
		GridPane.setConstraints(this.saveButton, 0, 14);
		
		optionContainer.getChildren().addAll(clientComputerNameCB,
											 clientUsernameCB,
											 clientIPAdressCB,
											 saveButton);
	}
	/**
	 * Save all options that is collected from the 'Preference' window
	 */
	private void saveOptions() {
		switch(currentSelection.getValue()) {
		case "General":
			preference.put("servername", 		serverNameTextField.getText());
			preference.putInt("port", 			Integer.parseInt(serverPortTextField.getText()));
			preference.putInt("connections",	Integer.parseInt(serverConnectionsTextField.getText()));
			preference.put("showclientinfo", 	comboBoxShowClientInfo.getSelectionModel().getSelectedItem());
			break;
		case "Client":
			preference.putBoolean("clientComputerName", clientComputerNameCB.isSelected());
			preference.putBoolean("clientUsername", 	clientUsernameCB.isSelected());
			preference.putBoolean("clientIPAdress", 	clientIPAdressCB.isSelected());
			break;
		}
		
	}
	
	/**
	 * When user click an option in the TreeView the window should update and show 
	 * setting for that category
	 * @param itemClicked	Item that was clicked
	 */
	private void handleMouseClickListView(TreeItem<String> itemClicked) {
		//If user clicked same option, return
		if(itemClicked == currentSelection) return;
		
		currentSelection = itemClicked;
		
		switch(itemClicked.getValue()) {
		case "General":
			createOptionsForGeneral();
			break;
		case "Client":
			createOptionsForClient();
			break;
		}
		
	}
}
