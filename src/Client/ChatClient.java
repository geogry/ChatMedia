package Client;

import images.StaticValue;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

import myInterface.ChatServer;
import myInterface.Chatter;

public class ChatClient extends JFrame {

	//存储已有聊天用户，key：用户名.value:对应的Chatter对象
	private Hashtable hash = new Hashtable();
	//自己的用户名
	private String myName = "chatter";
	//服务器地址
	private String serverAddress = null;
	//代表客户端的远程对象
	private Chatter chatter = null;
	//服务器端的远程对象
	private ChatServer chatServer = null;
	
	private JTextArea displayBox = null;
	private JTextArea inputBox = null;
	private JComboBox usersBox = null;
	private JButton sendButton = null;
	private JButton recordButton = null;
	private JLabel statusLabel = null;
	
	private boolean hasSet = false;
		
	//连接服务器
	private ConnectionAction connectAction = new ConnectionAction();
	
	//操作数据库
	private ConnectDB connectDB = null;
	
	//获取当前系统时间
	private Date dateTime = null;
	DateFormat dateFmt = null;
	
	//让用户输入用户名和服务器地址到对话框
	private ConnectDlg dlg = new ConnectDlg(this);
	
	public ChatClient(){
		super("聊天室");
		
		StaticValue.init();
		this.setIconImage(StaticValue.IMAGES.get(2));
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setLocation((width - 600) / 2, (height - 600) / 2);
		this.setResizable(false);
		
		layoutComponent();
		setupMenu();
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				exit();
			}
		});
		
		this.show();
		//创建数据库连接对象
		connectDB = new ConnectDB();
		connectDB.Connect();
		
		//创建时间对象
		dateTime = new Date();
		dateFmt = DateFormat.getDateTimeInstance();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ChatClient();
	}
	
	private void setupMenu(){
		JMenuBar menuBar = new JMenuBar();
		JMenuItem conn = new JMenuItem(connectAction);
		JMenuItem exit = new JMenuItem("退出");
		exit.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				exit();
			}
		});
		JMenu start = new JMenu("开始");
		start.add(conn);
		start.add(exit);
		menuBar.add(start);
		this.setJMenuBar(menuBar);
	}
	
	private void exit(){
		connectDB.close();
		destroy();
		System.exit(0);
	}

	public void layoutComponent(){
		setSize(600,600);
		
		JPanel chatPane = new JPanel();
		JPanel musicPane = new JPanel();
		JPanel toolPane = new JPanel();
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		chatPane.setLayout(gridbag);
		c.fill = GridBagConstraints.BOTH;
		
		//添加显示所有消息的带滚动条的文本域
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 6;
		c.weightx = 100;
		c.weighty = 100;
		c.insets.top = 5;
		displayBox = new JTextArea();
		displayBox.setLineWrap(true);
		displayBox.setMargin(new Insets(5, 5, 5, 5));
		displayBox.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(displayBox);
		chatPane.add(scrollPane, c);
		
		//添加消息输入框的提示标签
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.insets.top = 5;
		JLabel msgLabel = new JLabel("消息: ");
		chatPane.add(msgLabel, c);
		
		//添加消息输入框
		c.gridheight = 6;
		c.insets.top = 0;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.weightx = 100;
		inputBox = new JTextArea();
		addKeymapBindings();
		inputBox.setLineWrap(true);
		inputBox.setWrapStyleWord(true);
		JScrollPane inputScrollPane = new JScrollPane(inputBox);
		inputScrollPane.setPreferredSize(new Dimension(550, 550));
		inputScrollPane.setMinimumSize(new Dimension(10, 100));
		chatPane.add(inputScrollPane, c);
		
		c.weightx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		//用一个临时面板，将发送按钮和聊天记录管理起来
		JPanel temp = new JPanel();
		temp.setPreferredSize(new Dimension(80, 100));
		temp.setMinimumSize(new Dimension(80, 100));
		temp.setLayout(gridbag);
		//添加发送按钮
		c.weightx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		sendButton = new JButton("发送");
		sendButton.setToolTipText("Ctrl+Enter");
		sendButton.setPreferredSize(new Dimension(80, 50));
		sendButton.setMinimumSize(new Dimension(80, 50));
		sendButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendMessage();
			}
		});
		temp.add(sendButton, c);
		
		//添加聊天记录按钮
		c.weightx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		recordButton = new JButton("聊天记录");
		recordButton.setToolTipText("查看聊天记录");
		recordButton.setPreferredSize(new Dimension(80, 50));
		recordButton.setMinimumSize(new Dimension(80, 50));
		recordButton.setFont(new Font("Dialog", Font.BOLD, 10));
		recordButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				RecordQuery recordQuery = new RecordQuery();
				recordQuery.setLocationRelativeTo(ChatClient.this);
				recordQuery.show();
				recordQuery.display();
			}
			
		});
		temp.add(recordButton, c);
		
		//添加到JFrame中
		chatPane.add(temp, c);
		
		//添加要发送消息的用户的选择框
		c.weightx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		JLabel sendToLabel = new JLabel("发送给：");
		chatPane.add(sendToLabel, c);
		usersBox = new JComboBox();
		usersBox.setBackground(Color.WHITE);
		usersBox.addItem("所有用户");
		chatPane.add(usersBox, c);
		
		//添加连接状态显示标签
		JPanel statusPane = new JPanel(new GridLayout(1,1));
		statusLabel = new JLabel("未连接");
		statusPane.add(statusLabel);
		chatPane.add(statusPane, c);
		
		//添加便签
		JButton notepadButton = new JButton();
		notepadButton.setIcon(StaticValue.IMAGEICON.get(0));
		//notepadButton.setMaximumSize(new Dimension(150, 150));
		notepadButton.setToolTipText("便签");
		notepadButton.setOpaque(true);
		notepadButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Notepad notepad = new Notepad(ChatClient.this);
			}
			
		});
		toolPane.add(notepadButton);
		
		//添加自动关机
		JButton shutdownButton = new JButton();
		shutdownButton.setIcon(StaticValue.IMAGEICON.get(1));
		shutdownButton.setToolTipText("设置定时关机");
		//shutdownButton.setMaximumSize(new Dimension(150, 150));
		shutdownButton.setOpaque(false);
		final AutoShutdown autoShutdown = new AutoShutdown(this);
		autoShutdown.setVisible(false);
		autoShutdown.setLocationRelativeTo(this);
		shutdownButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(autoShutdown.getValue() != JOptionPane.OK_OPTION || !hasSet){
					autoShutdown.setVisible(true);
					hasSet = true;
				}else{
					try {
						Runtime.getRuntime().exec("shutdown.exe -a");
						hasSet = false;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		});
		toolPane.add(shutdownButton);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("聊天", chatPane);
		tabbedPane.add("音乐", musicPane);
		tabbedPane.add("工具箱", toolPane);
		this.getContentPane().add(tabbedPane);
		try {
			chatter = new ChatterImpl(this);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
	}
	
	public void destroy(){
		try {
			disconnect();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void connect() throws RemoteException, MalformedURLException, NotBoundException{
		chatServer = (ChatServer)Naming.lookup("//" + serverAddress + ":8400" + "/ChatServer");
		chatServer.login(myName, chatter, dlg.getHostIP());
	}
	
	protected void disconnect() throws RemoteException{
		if(chatServer != null)
			chatServer.logout(myName);
	}
	
	public void receiveEnter(String name, Chatter chatter, boolean hasEntered){
		if(name != null && chatter != null){
			hash.put(name, chatter);
			if(!name.equals(myName)){
				//对新加入的聊天用户，在displayBox给出提示
				if(!hasEntered){
					display(name + "进入聊天室");
				}
				usersBox.addItem(name);
			}
		}
	}
	
	public void receiveExit(String name){
		if(name != null && chatter != null)
			hash.remove(name);
		for(int i = 0; i< usersBox.getItemCount(); i++){
			if(name.equals((String)usersBox.getItemAt(i))){
				usersBox.remove(i);
				break;
			}
		}
		display(name + "离开聊天室");
	}
	
	public void receiveChat(String name, String message){
		display(name + " " + dateFmt.format(dateTime) + "\n" + message);
		connectDB.insert("'" + name + "'", "'" + message + "'", "'" + dateFmt.format(dateTime) + "'");
	}
	
	public void receiveWhisper(String name, String message){
		display(name + " 私聊  " + dateFmt.format(dateTime) + "\n" + message);
		connectDB.insert("'" + name + "'", "'" + message + "'", "'" + dateFmt.format(dateTime) + "'");
	}
	
	protected void addKeymapBindings(){
		Keymap keymap = JTextComponent.addKeymap("MyBindings", inputBox.getKeymap());
		Action action = null;
		KeyStroke key = null;
		//Ctrl+Enter发送消息
		action = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				sendMessage();
			}
		};
		key = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.CTRL_MASK);
		keymap.addActionForKeyStroke(key, action);
		//Enter实现换行
		action = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				inputBox.append("\n");
			}
		};
		key = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		keymap.addActionForKeyStroke(key, action);
		
		inputBox.setKeymap(keymap);
	}
	
	private void display(String s){
		if(!s.endsWith("\n")){
			displayBox.append(s + "\n");
		}else{
			displayBox.append(s);
		}
		
		int length = displayBox.getText().length() - 1;
		displayBox.select(length, length);
	}
	
	private void sendMessage(){
		String message = inputBox.getText();
		if(message != null && message.length() > 0){
			inputBox.setText(null);
			//设置输入框光标焦点为起点处
			inputBox.setCaretPosition(0);
			display(myName + " " + dateFmt.format(dateTime) + "\n" +message);
			if(chatServer != null){
				//将消息发送给所有用户
				if("所有用户".equals(usersBox.getSelectedItem())){
					try {
						chatServer.chat(myName, message);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{//私聊，发送给所选用户
					String destUserName = (String)usersBox.getSelectedItem();
					Chatter destChatter = (Chatter)hash.get(destUserName);
					try {
						destChatter.receiveWhisper(myName, message);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			//消息非空时才存储到数据库中
			connectDB.insert("'" + myName + "'", "'" + message + "'", "'" + dateFmt.format(dateTime) + "'");
		}
		inputBox.requestFocus();
	}
	
	public void serverStop(){
		display("服务器停止");
		
		chatServer = null;
		hash.clear();
		connectAction.setEnabled(true);
		statusLabel.setText("服务器停止,连接断开");
	}
	
	class ConnectionAction extends AbstractAction{
		
		public ConnectionAction(){
			super("连接");
			putValue(Action.SHORT_DESCRIPTION, "连接到服务器");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			dlg.pack();
			dlg.setLocationRelativeTo(ChatClient.this);
			dlg.setVisible(true);
			if(dlg.getValue() == JOptionPane.OK_OPTION){
				try {
					myName = dlg.getUserName();
					serverAddress = dlg.getServerAddress();
					connect();
					inputBox.setEditable(true);
					displayBox.setText("");
					statusLabel.setText(myName + " 已连接");
					this.setEnabled(false);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					statusLabel.setText("不能连接到服务器");
					return;
				} 
			}
		}
		
	}
}
