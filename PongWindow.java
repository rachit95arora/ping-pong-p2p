/*  
 *  Copyright (C) 2010  Luca Wehrstedt
 *
 *  This file is released under the GPLv2
 *  Read the file 'COPYING' for more information
 */

import javax.swing.JFrame;
import javax.swing.Timer;

public class PongWindow extends JFrame {
	public PongWindow () {
		super ();
		
		setTitle ("Pong");
		setSize (640, 640);
		
		Pong content = new Pong (1, true, 4321, "Rachit");
		content.acceleration = false;
		getContentPane().add (content);
		
		addMouseListener (content);
		addKeyListener (content);
		
		Timer timer = new Timer (20, content);
		timer.start ();
	}
}
