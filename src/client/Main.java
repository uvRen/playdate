package client;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static ClientMainController controller = null;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainClientScene.fxml"));
			Parent root = (Parent) loader.load();
			Main.controller = (ClientMainController)loader.getController();
			
			Scene scene = new Scene(root, 600, 400);
			
			primaryStage.setScene(scene);
			primaryStage.show();
		}
		catch(IOException e) {
			
		}
	}
	
	public static void main(String args[]) {
		launch(args);
	}
	
	/**
	 * Gets the controller that is connected to MainClientScene. 
	 * @return	Controller
	 */
	public static ClientMainController getClientMainController() {
		return controller;
	}
}
