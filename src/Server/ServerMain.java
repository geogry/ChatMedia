package Server;

import images.StaticValue;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;


public class ServerMain extends JFrame implements ChatServerListener {

	private ChatServerImpl server = ChatServerImpl.getIntance();
	private JTextArea textArea = null;
	private JMenuBar menuBar = null;
	private JToolBar toolBar =null;
	private StarServerAction startAction = null;
	private StopServerAction stopAction = null;
	//public  String imagePath = System.getProperty("user.dir") + "/bin/images/";
	
	public ServerMain(){
		super("����-������");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation((width - 300)/2, (height - 700)/2);
		StaticValue.init();
		this.setIconImage(StaticValue.IMAGES.get(2));
		startAction = new StarServerAction();
		stopAction = new StopServerAction();
		setSize(300,500);
		layoutComponents();
	}
	
	@Override
	public void serverEvent(ChatServerEvent e) {
		// TODO Auto-generated method stub
		textArea.append(e.getMessage() + "\n");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerMain serverMain = new ServerMain();
		serverMain.show();
		//serverMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	/**
	 * ���÷������������
	 */
	private void layoutComponents(){
		setupMenu();
		setupToolBar();
		textArea = new JTextArea();
		textArea.setSize(300, 500);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				exit();
			}
		});
	}
	/**
	 * ���ò˵���
	 */
	private void setupMenu(){
		menuBar = new JMenuBar();
		JMenuItem startServer = new JMenuItem(startAction);
		JMenuItem stopServer = new JMenuItem(stopAction);
		JMenuItem exit = new JMenuItem("�뿪");
		
		exit.addActionListener(new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent arg) {
				// TODO Auto-generated method stub
				exit();
			}
		});
		
		JMenu server = new JMenu("������");
		server.add(startServer);
		server.add(stopServer);
		server.add(exit);
		menuBar.add(server);
		this.setJMenuBar(menuBar);
	}
	/**
	 * ���ù�����
	 */
	private void setupToolBar(){
		toolBar = new JToolBar();
		JButton button1 = new JButton();
		button1.setAction(startAction);
		toolBar.add(button1);
		JButton button2 = new JButton();
		button2.setAction(stopAction);
		toolBar.add(button2);
		
		getContentPane().add(toolBar, BorderLayout.NORTH);
	}
	
	private void exit(){
		try{
			server.stop();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}
	//�ڲ���ʵ����������������
	class StarServerAction extends AbstractAction{
		public StarServerAction(){
			super("����");
			try {
				putValue(Action.SMALL_ICON, new ImageIcon(StaticValue.IMAGES.get(0)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			putValue(Action.SHORT_DESCRIPTION, "����������");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control A"));
		}
		
		@Override
		public void actionPerformed(ActionEvent arg) {
			// TODO Auto-generated method stub
			//�˾�ز�����
			try {
				System.setProperty("java.rmi.server.hostname", InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try{
				RMISocketFactory.setSocketFactory(new SMRMISocket());
				LocateRegistry.createRegistry(8400);
				server.addListener(ServerMain.this);
				server.start();
				textArea.setText("������������\n");
				stopAction.setEnabled(true);
				this.setEnabled(false);
			}catch(Exception e){
				textArea.append("��������������\n");
				server.removeListener(ServerMain.this);
				e.printStackTrace();
				return;
			}
		}
	}
	
	class StopServerAction extends AbstractAction{
		public StopServerAction(){
			super("ֹͣ");
			try {
				putValue(Action.SMALL_ICON, new ImageIcon(StaticValue.IMAGES.get(1)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			putValue(Action.SHORT_DESCRIPTION, "ֹͣ������");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
			this.setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent arg) {
			// TODO Auto-generated method stub
			try{
				server.stop();
				textArea.append("��������ֹͣ\n");
				server.removeListener(ServerMain.this);
				startAction.setEnabled(true);
				this.setEnabled(false);
			}catch(Exception e){
				textArea.append("ֹͣ����������\n");
				e.printStackTrace();
				return;
			}
		}
	}
}


