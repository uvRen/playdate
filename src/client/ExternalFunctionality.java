package client;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;

public class ExternalFunctionality {
	/**
	 * Takes a printscreen of users screen
	 * @return	PrintScreen
	 */
	public static BufferedImage getPrintScreen() {
		BufferedImage capture 	= null;
		Dimension screenSize 	= Toolkit.getDefaultToolkit().getScreenSize();
		
		Rectangle screen = new Rectangle((int)screenSize.getWidth(), (int)screenSize.getHeight());

		try {
			capture = new Robot().createScreenCapture(screen);
		} 
		catch (AWTException e) {
			System.err.println("ExternalFunctionality: Failed to take printscreen");
			e.printStackTrace();
		}
		
		return capture;
	}
	
	/**
	 * Get username of the user that is logged in
	 * @return	Name of user
	 */
	public static String getUsername() {
		return System.getProperty("user.name");
	}
	
	/**
	 * Get name of the computer
	 * @return	Name of computer
	 */
	public static String getComputerName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch(IOException e) {
			return "";
		}
	}
	
	/**
	 * Get the external IP-address
	 * @return	External IP-address
	 */
	public static String getIPAdress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		}
		catch(IOException e) {
			return "";
		}
	}
}
