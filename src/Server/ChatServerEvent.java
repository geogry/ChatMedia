package Server;

import java.util.EventObject;

public class ChatServerEvent extends EventObject {

	String message = null;
	
	public ChatServerEvent(Object sre, String message) {
		super(sre);
		// TODO Auto-generated constructor stub
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
