package helppackage;

import javafx.beans.property.SimpleStringProperty;

public class ClientUser {
	private int id;
	private SimpleStringProperty macaddress;
	private SimpleStringProperty username;
	private SimpleStringProperty computername;
	private SimpleStringProperty externalipaddress;
	private SimpleStringProperty localipaddress;
	
	public ClientUser(int id) {
		this.id = id;
		this.username 			= new SimpleStringProperty("");
		this.computername 		= new SimpleStringProperty("");
		this.externalipaddress 	= new SimpleStringProperty("");
		this.macaddress 		= new SimpleStringProperty("");
		this.localipaddress 	= new SimpleStringProperty("");
	}
	
	public ClientUser() {
		this.username 			= new SimpleStringProperty("");
		this.computername 		= new SimpleStringProperty("");
		this.externalipaddress 	= new SimpleStringProperty("");
		this.macaddress 		= new SimpleStringProperty("");
		this.localipaddress 	= new SimpleStringProperty("");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public SimpleStringProperty getMacaddress() {
		return macaddress;
	}

	public void setMacaddress(String macaddress) {
		this.macaddress = new SimpleStringProperty(macaddress);
	}

	public SimpleStringProperty getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = new SimpleStringProperty(username);
	}

	public SimpleStringProperty getComputername() {
		return computername;
	}

	public void setComputername(String computername) {
		this.computername = new SimpleStringProperty(computername);
	}

	public SimpleStringProperty getExternalIPaddress() {
		return externalipaddress;
	}

	public void setExternalIPaddress(String ipaddress) {
		this.externalipaddress = new SimpleStringProperty(ipaddress);
	}
	
	public SimpleStringProperty getLocalIPaddress() {
		return localipaddress;
	}

	public void setLocalIPaddress(String localipaddress) {
		this.localipaddress = new SimpleStringProperty(localipaddress);
	}
}
