package myInterface;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote {
	
	//ע���µ������û�
		public void login(String name, Chatter chatter, String IP) throws RemoteException;
		
		//�û��˳�
		public void logout(String name) throws RemoteException;
		
		//�û����ô˺�������Ϣ���͸������û�
		public void chat(String name, String message) throws RemoteException, MalformedURLException, NotBoundException;
}
