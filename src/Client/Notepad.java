package Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Notepad extends JFrame {
	
	JTextArea textArea = null;
	JFrame tmp = null;
	
	public Notepad(JFrame frame){
		super("±„«©");
		tmp = frame;
		//com.sun.awt.AWTUtilities.setWindowOpacity(this, 0.6f);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setBackground(Color.green);
		textArea.setSize(200, 250);
		textArea.setFont(new Font("Œ¢»Ì—≈∫⁄", Font.BOLD, 20));
		JScrollPane scrollPane = new JScrollPane(textArea);
		
		this.setUndecorated(true);
		this.setLocationRelativeTo(frame);
		this.getRootPane().setWindowDecorationStyle(JRootPane.INFORMATION_DIALOG);
		this.setContentPane(scrollPane);
		this.setPreferredSize(new Dimension(200, 250));
		this.setResizable(false);
		this.setVisible(true);
		this.pack();
	}
	
}
