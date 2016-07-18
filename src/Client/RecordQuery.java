package Client;

import java.awt.Dimension;
import java.awt.Insets;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class RecordQuery extends JFrame {
	
	//添加一个不可编辑的文本域
	private JTextArea displayBox = null;
	//连接数据库
	private ConnectDB connectDB = new ConnectDB();
	//或者数据库操作
	private ResultSet rs = null;
	private String chatterName = null;
	private String message = null;
	private String date = null;
	
	public RecordQuery(){
		super("聊天记录");
		
		displayBox = new JTextArea();
		displayBox.setSize(new Dimension(600,600));
		displayBox.setEditable(false);
		displayBox.setLineWrap(true);
		displayBox.setMargin(new Insets(5, 5, 5, 5));
		JScrollPane scrollPane = new JScrollPane(displayBox);
		this.getContentPane().add(scrollPane);
		
		this.setUndecorated(true);
		this.getRootPane().setWindowDecorationStyle(JRootPane.INFORMATION_DIALOG);
		this.setPreferredSize(new Dimension(600,600));
		this.pack();
		this.setVisible(true);
	}
	
	public void display(){
		connectDB.Connect();
		connectDB.query();
		this.rs = connectDB.getRs();
		try {
			while(this.rs.next()){
				chatterName = this.rs.getString(1);
				message = this.rs.getString(2);
				date = this.rs.getString(3);
				displayBox.append(chatterName + " " + date + "\n" + message + "\n");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connectDB.close();
	}
}
