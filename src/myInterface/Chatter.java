package myInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Chatter extends Remote {

	//֪ͨ�û�����
	public void receiveEnter(String name, Chatter chatter, boolean hasEntered) throws RemoteException;
	
	//֪ͨ�û��뿪������
	public void receiveExit(String name) throws RemoteException;
	
	//�û�����
	public void receiveChat(String name, String message) throws RemoteException;
	
	//˽��
	public void receiveWhisper(String name, String message) throws RemoteException;
	
	//֪ͨ������ֹͣ
	public void serverStop() throws RemoteException;
}
