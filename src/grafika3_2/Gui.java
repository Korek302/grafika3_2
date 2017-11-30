package grafika3_2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Gui
{
	JFrame frame;
	BufferedImage currImage;
	
	public static void main(String[] args)
	{
		Gui gui = new Gui();
		gui.GUI();
	}
	
	private void GUI()
	{
		frame = new JFrame();
		
		try
		{
			currImage = ImageIO.read(new File("res/cat2.png"));
		}
		catch (IOException e)
		{
			System.out.println("The image cannot be loaded");
		}
		
		ImagePanel panel = new ImagePanel(currImage);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Grafika3");
		frame.setSize(700, 700);
		frame.getContentPane().add(panel);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setVisible(true);
	}
}


