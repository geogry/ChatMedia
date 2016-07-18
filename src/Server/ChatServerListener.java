package Server;

import java.util.EventListener;

public interface ChatServerListener extends EventListener {

	public void serverEvent(ChatServerEvent e);
}
