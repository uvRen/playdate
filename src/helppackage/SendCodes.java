package helppackage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class SendCodes implements Serializable {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Tuple> tuple;
	
	/**
	 * Constructor read send codes from file
	 */
	public SendCodes() {
		tuple = new ArrayList<Tuple>();
		readSendCodes();
	}
	
	/**
	 * Add a new sendcode to the list
	 * @param name	Name of code
	 * @param code	Integer of code
	 */
	public void addSendCode(String name, int code) {
		tuple.add(new Tuple(name, code));
	}
	
	/**
	 * Get the sendcode of given option
	 * @param name	Name of option
	 * @return		SendCode of option
	 */
	public int getCode(String name) {
		for(Tuple t : tuple) {
			if(t.getName().equals(name))
				return t.getCode();
		}
		return -1;
	}
	
	/**
	 * Save all sendcodes to file
	 */
	public void saveSendCodes() {
		try {
			FileOutputStream out 	= new FileOutputStream("properties.data");
			ObjectOutputStream oos 	= new ObjectOutputStream(out);
			
			oos.writeObject(this.tuple);
			oos.flush();
			oos.close();
		}
		catch(IOException e) {
			System.err.println("SendCodes: Failed to write to file");
			e.printStackTrace();
		}
	}
	
	/**
	 * Read all sendcodes from file
	 */
	@SuppressWarnings("unchecked")
	public void readSendCodes() {
		try {
			FileInputStream in = new FileInputStream("properties.data");
			ObjectInputStream ois = new ObjectInputStream(in);
			
			this.tuple = (ArrayList<Tuple>)ois.readObject();
			ois.close();
		} 
		//File doesn't exists
		catch (FileNotFoundException e) {
			createSendCodeFile();
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * When user use the program for the first time a sendcode file need to be initialized. 
	 */
	private void createSendCodeFile() {
		tuple.clear();
		
		tuple.add(new Tuple("clientComputerName"	, 1002));
		tuple.add(new Tuple("clientUsername"		, 1004));
		tuple.add(new Tuple("clientIPAdress"		, 1006));
		
		saveSendCodes();
	}
	
}

class Tuple implements Serializable{
	
	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	private String 	name;
	private int		code;
	
	public Tuple(String name, int code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
}
