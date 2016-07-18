package Server;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.rmi.PortableRemoteObject;

import myInterface.ChatServer;
import myInterface.Chatter;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServer{

	static ChatServerImpl server = null;
	private final static String BINDNAME = "ChatServer";
	private final static String[] STATEMSG = {"服务器启动", "服务器停止"};
	private List chatters = new ArrayList();
	private List listeners = new ArrayList();

	protected ChatServerImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static ChatServerImpl getIntance(){
		try{
			if(server == null)
				server = new ChatServerImpl();
		}catch(RemoteException e){
			e.printStackTrace();
			return null;
		}
		return server;
	}

	public void start() throws RemoteException, MalformedURLException{
		String hostIP = null;
		try {
			hostIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//设置数据传输服务端口为8400，地址为当前主机地址，服务器为server
		Naming.rebind("//" + hostIP + ":" + 8400 + "/" + BINDNAME, server);
		notifyListener(STATEMSG[0]);
	}
	
	public void stop() throws RemoteException, NotBoundException, MalformedURLException, UnknownHostException{
		notifyListener("");
		Iterator itr = chatters.iterator();
		while(itr.hasNext()){
			UserInfo u = (UserInfo)itr.next();
			u.getChatter().serverStop();
		}
	}
	
	public void chat(String name, String message) throws RemoteException, MalformedURLException, NotBoundException {
		// TODO Auto-generated method stub
		//获取本机IP
		String hostIP = null;
		try {
			hostIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//把发言发送给当前聊天室中的所有用户
		Iterator itr = chatters.iterator();
		while(itr.hasNext()){
			UserInfo u = (UserInfo)itr.next();
			if(!name.equals(u.getName())){
				u.getChatter().receiveChat(name, message);
			}
		}
		//每次聊天使用与启动服务器相同的端口和地址
		Naming.rebind("//" + hostIP + ":" + 8400 + "/" + BINDNAME, server);
	}

	public void login(String name, Chatter chatter, String IP) throws RemoteException {
		// TODO Auto-generated method stub
		if(name != null && chatter != null){
			//创建新的用户UserInfo
			UserInfo u = new UserInfo(name, chatter, IP);
			//通知所有在线用户事件的发生
			notifyListener(u.getName() + " 进入聊天室 (地址：" + u.getIP() + ")");
			//把聊天室里已有的用户传给此用户
			Iterator itr = chatters.iterator();
			while(itr.hasNext()){
				//取得chatters中的所有元素依次传给该用户
				UserInfo u2 = (UserInfo)itr.next();
				u2.getChatter().receiveEnter(name, chatter, false);
				chatter.receiveEnter(u2.getName(), u2.getChatter(), true);
			}
			chatters.add(u);
		}
	}

	public void logout(String name) throws RemoteException {
		// TODO Auto-generated method stub
		if(name == null){
			System.out.println("null name on logout: cannot remove chatter!");
			return;
		}
		
		UserInfo u_gone = null;
		Iterator itr = null;
		//从chatters中找到名为name的用户并移除
		synchronized(chatters){
			for(int i = 0; i < chatters.size(); i++){
				UserInfo u = (UserInfo)chatters.get(i);
				if(u.getName().equals(name)){
					notifyListener(name + " 离开聊天室");
					u_gone = u;
					chatters.remove(i);
					itr = chatters.iterator();
					break;
				}
			}
		}
		
		if(u_gone == null || itr == null){
			System.out.println("no user by name of " + name + " found: not removing chatter");
			return;
		}
		
		//通知其他所有在线用户，名为name的用户退出了聊天
		while(itr.hasNext()){
			UserInfo u = (UserInfo)itr.next();
			u.getChatter().receiveExit(name);
		}
	}

	//添加一个监听者
	public void addListener(ChatServerListener listener){
		listeners.add(listener);
	}
	
	//移除一个监听者
	public void removeListener(ChatServerListener listener){
		listeners.remove(listener);
	}
	
	//通知所有监听者事件发生
	public void notifyListener(String message){
		Iterator itr = listeners.iterator();
		ChatServerEvent e = new ChatServerEvent(this, message);
		while(itr.hasNext()){
			((ChatServerListener)itr.next()).serverEvent(e);
		}
	}
}
