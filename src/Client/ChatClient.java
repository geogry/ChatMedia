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

	//�洢���������û���key���û���.value:��Ӧ��Chatter����
	private Hashtable hash = new Hashtable();
	//�Լ����û���
	private String myName = "chatter";
	//��������ַ
	private String serverAddress = null;
	//����ͻ��˵�Զ�̶���
	private Chatter chatter = null;
	//�������˵�Զ�̶���
	private ChatServer chatServer = null;
	
	private JTextArea displayBox = null;
	private JTextArea inputBox = null;
	private JComboBox usersBox = null;
	private JButton sendButton = null;
	private JButton recordButton = null;
	private JLabel statusLabel = null;
	
	private boolean hasSet = false;
		
	//���ӷ�����
	private ConnectionAction connectAction = new ConnectionAction();
	
	//�������ݿ�
	private ConnectDB connectDB = null;
	
	//��ȡ��ǰϵͳʱ��
	private Date dateTime = null;
	DateFormat dateFmt = null;
	
	//���û������û����ͷ�������ַ���Ի���
	private ConnectDlg dlg = new ConnectDlg(this);
	
	public ChatClient(){
		super("������");
		
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
		//�������ݿ����Ӷ���
		connectDB = new ConnectDB();
		connectDB.Connect();
		
		//����ʱ�����
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
		JMenuItem exit = new JMenuItem("�˳�");
		exit.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				exit();
			}
		});
		JMenu start = new JMenu("��ʼ");
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
		
		//�����ʾ������Ϣ�Ĵ����������ı���
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
		
		//�����Ϣ��������ʾ��ǩ
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.insets.top = 5;
		JLabel msgLabel = new JLabel("��Ϣ: ");
		chatPane.add(msgLabel, c);
		
		//�����Ϣ�����
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
		//��һ����ʱ��壬�����Ͱ�ť�������¼��������
		JPanel temp = new JPanel();
		temp.setPreferredSize(new Dimension(80, 100));
		temp.setMinimumSize(new Dimension(80, 100));
		temp.setLayout(gridbag);
		//��ӷ��Ͱ�ť
		c.weightx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		sendButton = new JButton("����");
		sendButton.setToolTipText("Ctrl+Enter");
		sendButton.setPreferredSize(new Dimension(80, 50));
		sendButton.setMinimumSize(new Dimension(80, 50));
		sendButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendMessage();
			}
		});
		temp.add(sendButton, c);
		
		//��������¼��ť
		c.weightx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		recordButton = new JButton("�����¼");
		recordButton.setToolTipText("�鿴�����¼");
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
		
		//��ӵ�JFrame��
		chatPane.add(temp, c);
		
		//���Ҫ������Ϣ���û���ѡ���
		c.weightx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = 1;
		JLabel sendToLabel = new JLabel("���͸���");
		chatPane.add(sendToLabel, c);
		usersBox = new JComboBox();
		usersBox.setBackground(Color.WHITE);
		usersBox.addItem("�����û�");
		chatPane.add(usersBox, c);
		
		//�������״̬��ʾ��ǩ
		JPanel statusPane = new JPanel(new GridLayout(1,1));
		statusLabel = new JLabel("δ����");
		statusPane.add(statusLabel);
		chatPane.add(statusPane, c);
		
		//��ӱ�ǩ
		JButton notepadButton = new JButton();
		notepadButton.setIcon(StaticValue.IMAGEICON.get(0));
		//notepadButton.setMaximumSize(new Dimension(150, 150));
		notepadButton.setToolTipText("��ǩ");
		notepadButton.setOpaque(true);
		notepadButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Notepad notepad = new Notepad(ChatClient.this);
			}
			
		});
		toolPane.add(notepadButton);
		
		//����Զ��ػ�
		JButton shutdownButton = new JButton();
		shutdownButton.setIcon(StaticValue.IMAGEICON.get(1));
		shutdownButton.setToolTipText("���ö�ʱ�ػ�");
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
		tabbedPane.add("����", chatPane);
		tabbedPane.add("����", musicPane);
		tabbedPane.add("������", toolPane);
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
				//���¼���������û�����displayBox������ʾ
				if(!hasEntered){
					display(name + "����������");
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
		display(name + "�뿪������");
	}
	
	public void receiveChat(String name, String message){
		display(name + " " + dateFmt.format(dateTime) + "\n" + message);
		connectDB.insert("'" + name + "'", "'" + message + "'", "'" + dateFmt.format(dateTime) + "'");
	}
	
	public void receiveWhisper(String name, String message){
		display(name + " ˽��  " + dateFmt.format(dateTime) + "\n" + message);
		connectDB.insert("'" + name + "'", "'" + message + "'", "'" + dateFmt.format(dateTime) + "'");
	}
	
	protected void addKeymapBindings(){
		Keymap keymap = JTextComponent.addKeymap("MyBindings", inputBox.getKeymap());
		Action action = null;
		KeyStroke key = null;
		//Ctrl+Enter������Ϣ
		action = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				sendMessage();
			}
		};
		key = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.CTRL_MASK);
		keymap.addActionForKeyStroke(key, action);
		//Enterʵ�ֻ���
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
			//����������꽹��Ϊ��㴦
			inputBox.setCaretPosition(0);
			display(myName + " " + dateFmt.format(dateTime) + "\n" +message);
			if(chatServer != null){
				//����Ϣ���͸������û�
				if("�����û�".equals(usersBox.getSelectedItem())){
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
				}else{//˽�ģ����͸���ѡ�û�
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
			//��Ϣ�ǿ�ʱ�Ŵ洢�����ݿ���
			connectDB.insert("'" + myName + "'", "'" + message + "'", "'" + dateFmt.format(dateTime) + "'");
		}
		inputBox.requestFocus();
	}
	
	public void serverStop(){
		display("������ֹͣ");
		
		chatServer = null;
		hash.clear();
		connectAction.setEnabled(true);
		statusLabel.setText("������ֹͣ,���ӶϿ�");
	}
	
	class ConnectionAction extends AbstractAction{
		
		public ConnectionAction(){
			super("����");
			putValue(Action.SHORT_DESCRIPTION, "���ӵ�������");
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
					statusLabel.setText(myName + " ������");
					this.setEnabled(false);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					statusLabel.setText("�������ӵ�������");
					return;
				} 
			}
		}
		
	}
}
