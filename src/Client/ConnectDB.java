package Client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectDB {
	private String DBPath = null;
	private String url = null;
	private Connection conn = null;
	private Statement sql = null;
	private ResultSet rs = null;
	
	public ConnectDB(){
		DBPath = System.getProperty("user.dir") + "/bin/ChatRecord.mdb";
		url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + DBPath;
	}
	//连接到数据库
	public void Connect(){
		try {
			//创建连接
			conn = DriverManager.getConnection(url, "", "");
			sql = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("连接数据库失败！");
		}
	}
	
	//保存聊天记录
	public void insert(String name, String message, String date){
		try {
			sql.executeUpdate("INSERT INTO ChatRecord VALUES(" + name + "," + message + "," + date + ")");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//查询聊天记录
	public void query(){
		try {
			rs = sql.executeQuery("SELECT * FROM ChatRecord");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//断开连接
	public void close(){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResultSet getRs() {
		return rs;
	}
	
}
