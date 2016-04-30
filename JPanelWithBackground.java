import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;

public class JPanelWithBackground extends JPanel{
	private Image backgroundImage;

	public JPanelWithBackground(String filename) throws IOException{
		backgroundImage = ImageIO.read(new File(filename));
	}

	public JPanelWithBackground(){
		backgroundImage = null;
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);

		g.drawImage(backgroundImage, 0, 0, this);
	}
}