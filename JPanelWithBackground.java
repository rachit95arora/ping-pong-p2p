import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;

public class JPanelWithBackground extends JPanel{
	private Image backgroundImage;
	//Constructor to read in the image file that is to be set as background for the JPanel
	public JPanelWithBackground(String filename) throws IOException{
		backgroundImage = ImageIO.read(new File(filename));
	}
	//An overloaded constructor for the case where no background image is needed
	public JPanelWithBackground(){
		backgroundImage = null;
	}
	//Method to render the JPanel on screen with the specified image/null
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		g.drawImage(backgroundImage, 0, 0, this);
	}
}