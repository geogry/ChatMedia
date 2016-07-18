package Server;

import myInterface.Chatter;

public class UserInfo {
	
	//�û���
	private String name;
	//Զ�̿ͻ��˶���
	private Chatter chatter;
	//�ͻ��˵�ַ
	private String IP;
	
	public UserInfo(String name, Chatter chatter, String IP){
		this.setName(name);
		this.setChatter(chatter);
		this.setIP(IP);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Chatter getChatter() {
		return chatter;
	}

	public void setChatter(Chatter chatter) {
		this.chatter = chatter;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}
}
