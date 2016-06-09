package helppackage;

import java.io.Serializable;
import java.util.ArrayList;

public class SendableData implements Serializable {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	private int 				mainCode;
	private ArrayList<Integer> 	code;
	private ArrayList<Object> 	data;
	
	public SendableData() {
		code = new ArrayList<Integer>();
		data = new ArrayList<Object>();
	}

	public ArrayList<Integer> getCode() {
		return code;
	}
	
	public void setMainCode(int code) {
		this.mainCode = code;
	}
	
	public int getMainCode() {
		return this.mainCode;
	}
	
	/**
	 * Add a code to the list
	 * @param code	Code to add
	 */
	public void addCode(int code) {
		this.code.add(code);
	}

	public ArrayList<Object> getData() {
		return data;
	}
	
	/**
	 * Add an object to the list
	 * @param data	Object to be added
	 */
	public void addData(Object data) {
		this.data.add(data);
	}
	
}
