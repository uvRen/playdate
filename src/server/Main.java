package server;
	
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	
	private static ServerMainController controller = null;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader 	= new FXMLLoader(getClass().getResource("MainServerScene.fxml"));
			Parent root 		= (Parent) loader.load();
			Main.controller 	= (ServerMainController)loader.getController();
			Scene scene 		= new Scene(root, 600, 400);
			Preferences pref	= Preferences.userRoot().node(Server.class.getName());
			
			primaryStage.setTitle(pref.get("servername", "Server"));
			primaryStage.setScene(scene);
			primaryStage.show();
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Gets the controller that is connected to MainServerScene. 
	 * @return	Controller
	 */
	public static ServerMainController getServerMainController() {
		return controller;
	}
}
