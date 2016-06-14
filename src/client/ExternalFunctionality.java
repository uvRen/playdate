package client;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

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
	 * Get MAC-address of network device
	 * @return MAC-address 
	 */
	public static String getMacAddress() {
		try {
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			
			StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < mac.length; i++) {
	            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
	        }
	        System.out.println(sb.toString());
	        return sb.toString();
		} 
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
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
	 * @throws MalformedURLException 
	 */
	public static String getIPAdress() {
        BufferedReader in = null;
        try {
        	URL whatismyip = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
        } 
        catch (IOException e) {
			e.printStackTrace();
			return "";
		} 
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		/*
		try {
			return InetAddress.getLocalHost().getHostAddress();
		}
		catch(IOException e) {
			return "";
		}
		*/
	}
}
