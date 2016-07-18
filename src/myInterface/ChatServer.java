package myInterface;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote {
	
	//注册新的聊天用户
		public void login(String name, Chatter chatter, String IP) throws RemoteException;
		
		//用户退出
		public void logout(String name) throws RemoteException;
		
		//用户调用此函数把消息发送给所有用户
		public void chat(String name, String message) throws RemoteException, MalformedURLException, NotBoundException;
}
