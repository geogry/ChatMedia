package images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class StaticValue {
	
	public static ArrayList<BufferedImage> IMAGES = new ArrayList<BufferedImage>();
	public static ArrayList<ImageIcon> IMAGEICON = new ArrayList<ImageIcon>();
	
	private static String imagePath = System.getProperty("user.dir") + "/bin/";
	
	public static void init(){
		try {
			IMAGES.add(0, ImageIO.read(new File(imagePath + "start.gif")));
			IMAGES.add(1, ImageIO.read(new File(imagePath + "stop.gif")));
			IMAGES.add(2, ImageIO.read(new File(imagePath + "title.gif")));
			
			IMAGEICON.add(0, new ImageIcon(imagePath + "notepad.gif"));
			IMAGEICON.add(1, new ImageIcon(imagePath + "shutdown.gif"));
			IMAGEICON.add(2, new ImageIcon(imagePath + "clock.gif"));
			//System.out.println("导入成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
