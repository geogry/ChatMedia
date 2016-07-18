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
	private final static String[] STATEMSG = {"����������", "������ֹͣ"};
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
		//�������ݴ������˿�Ϊ8400����ַΪ��ǰ������ַ��������Ϊserver
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
		//��ȡ����IP
		String hostIP = null;
		try {
			hostIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//�ѷ��Է��͸���ǰ�������е������û�
		Iterator itr = chatters.iterator();
		while(itr.hasNext()){
			UserInfo u = (UserInfo)itr.next();
			if(!name.equals(u.getName())){
				u.getChatter().receiveChat(name, message);
			}
		}
		//ÿ������ʹ����������������ͬ�Ķ˿ں͵�ַ
		Naming.rebind("//" + hostIP + ":" + 8400 + "/" + BINDNAME, server);
	}

	public void login(String name, Chatter chatter, String IP) throws RemoteException {
		// TODO Auto-generated method stub
		if(name != null && chatter != null){
			//�����µ��û�UserInfo
			UserInfo u = new UserInfo(name, chatter, IP);
			//֪ͨ���������û��¼��ķ���
			notifyListener(u.getName() + " ���������� (��ַ��" + u.getIP() + ")");
			//�������������е��û��������û�
			Iterator itr = chatters.iterator();
			while(itr.hasNext()){
				//ȡ��chatters�е�����Ԫ�����δ������û�
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
		//��chatters���ҵ���Ϊname���û����Ƴ�
		synchronized(chatters){
			for(int i = 0; i < chatters.size(); i++){
				UserInfo u = (UserInfo)chatters.get(i);
				if(u.getName().equals(name)){
					notifyListener(name + " �뿪������");
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
		
		//֪ͨ�������������û�����Ϊname���û��˳�������
		while(itr.hasNext()){
			UserInfo u = (UserInfo)itr.next();
			u.getChatter().receiveExit(name);
		}
	}

	//���һ��������
	public void addListener(ChatServerListener listener){
		listeners.add(listener);
	}
	
	//�Ƴ�һ��������
	public void removeListener(ChatServerListener listener){
		listeners.remove(listener);
	}
	
	//֪ͨ���м������¼�����
	public void notifyListener(String message){
		Iterator itr = listeners.iterator();
		ChatServerEvent e = new ChatServerEvent(this, message);
		while(itr.hasNext()){
			((ChatServerListener)itr.next()).serverEvent(e);
		}
	}
}
