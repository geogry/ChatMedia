package Client;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ConnectDlg extends JDialog implements PropertyChangeListener {
	//服务器地址输入框
	private JTextField serverAddressField = null;
	//用户名输入框
	private JTextField userNameField = null;
	
	private JOptionPane optionPane = null;
	private String serverAddress = null;
	private String userName = null;
	private String hostIP = null;
	private int value = -1;
	
	public ConnectDlg(Frame frame){
		super(frame, "连接", true);
		serverAddressField = new JTextField(15);
		userNameField = new JTextField(20);
		
		Object[] array = {"服务器地址：", serverAddressField, "用户名:", userNameField};
		optionPane = new JOptionPane(array, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		setContentPane(optionPane);
		optionPane.addPropertyChangeListener(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				setVisible(false);
			}
		});
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		// TODO Auto-generated method stub
		String prop = e.getPropertyName();
		if(isVisible() && (e.getSource() == optionPane) && (JOptionPane.VALUE_PROPERTY.equals(prop) 
				|| JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))){
			if(optionPane.getValue() == JOptionPane.UNINITIALIZED_VALUE){
				return;
			}
			value = ((Integer) optionPane.getValue()).intValue();
			optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
		}
		
		if(value == JOptionPane.OK_OPTION){
			if("".equals(serverAddressField.getText()) || "".equals(userNameField.getText())){
				
				JOptionPane.showMessageDialog(this, "输入服务器用户名或服务器地址");
				
				if("".equals(serverAddressField.getText())){
					serverAddressField.requestFocus();
				}else if("".equals(userNameField.getText())){
					userNameField.requestFocus();
				}
			}else{
				setServerAddress(serverAddressField.getText());
				setUserName(userNameField.getText());
				setVisible(false);
			}
		}else{
			setVisible(false);
		}
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getValue() {
		return value;
	}

	public String getHostIP() {
		try {
			hostIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hostIP;
	}
}
