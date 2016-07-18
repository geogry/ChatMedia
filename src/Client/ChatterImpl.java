package Client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import myInterface.Chatter;

public class ChatterImpl extends UnicastRemoteObject implements Chatter {
	
	ChatClient client = null;

	protected ChatterImpl(ChatClient client) throws RemoteException {
		// TODO Auto-generated constructor stub
		this.client = client;
	}

	@Override
	public void receiveEnter(String name, Chatter chatter, boolean hasEntered)
			throws RemoteException {
		// TODO Auto-generated method stub

		client.receiveEnter(name, chatter, hasEntered);
	}

	@Override
	public void receiveExit(String name) throws RemoteException {
		// TODO Auto-generated method stub

		client.receiveExit(name);
	}

	@Override
	public void receiveChat(String name, String message) throws RemoteException {
		// TODO Auto-generated method stub

		client.receiveChat(name, message);
	}

	@Override
	public void receiveWhisper(String name, String message)
			throws RemoteException {
		// TODO Auto-generated method stub

		client.receiveWhisper(name, message);
	}

	@Override
	public void serverStop() throws RemoteException {
		// TODO Auto-generated method stub

		client.serverStop();
	}

}

