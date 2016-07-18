package Client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class AutoShutdown extends JDialog implements PropertyChangeListener {
	
	private JTextField inputField = null;
	private JOptionPane optionPane = null;
	private int value = -1;
	
	public AutoShutdown(JFrame frame){
		super(frame, "时间设定", true);
		
		inputField = new JTextField(10);
		Object[] array = {"时间(以分钟为单位):", inputField};
		
		optionPane = new JOptionPane(array, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		
		this.setContentPane(optionPane);
		optionPane.addPropertyChangeListener(this);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.pack();
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				setVisible(false);
			}
		});
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		// TODO Auto-generated method stub
		int totalSecond = 0;
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
			if("".equals(inputField.getText())){
				JOptionPane.showMessageDialog(this, "请输入关机时间");
				inputField.requestFocus();
			}else{
				totalSecond = Integer.parseInt(inputField.getText()) * 60;
				try {
					Runtime.getRuntime().exec("shutdown.exe -s -t " + totalSecond);
					JOptionPane.showMessageDialog(this, "设置成功，计算机将在" + inputField.getText() + "分钟后关机！");
					setVisible(false);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}else{
			setVisible(false);
		}
	}

	public int getValue() {
		return value;
	}
	
}
