/*  
 *  Copyright (C) 2010  Luca Wehrstedt
 *
 *  This file is released under the GPLv2
 *  Read the file 'COPYING' for more information
 */

import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Graphics;

import java.awt.event.MouseListener;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Pong extends JPanel implements ActionListener, MouseListener, KeyListener {
	// Proprietà della palla
	private static final int RADIUS = 10; // Raggio
	private static final int START_SPEED = 9; // Velocità iniziale
	private static final int ACCELERATION = 125; // Ogni quanti frame aumenta di 1 pixel la velocità 

	// Proprietà dei carrelli
	private static final int SPEED = 12; // Velocità dei carrelli
	private static final int HEIGHT = 50; // SEMI-altezza del carrello
	private static final int WIDTH = 20;
	private static final int TOLERANCE = 5;
	private static final int PADDING = 10;
	
	private Player player1;
	private Player player2;
	private Player player3;
	private Player player4;
	
	private boolean new_game = true;
	
	private int ball_x;
	private int ball_y;
	private double ball_x_speed;
	private double ball_y_speed;
	
	public boolean acceleration = false;
	private int ball_acceleration_count;
	
	private boolean mouse_inside = false;
	private boolean key_up = false;
	private boolean key_down = false;
	
	// Constructor
	public Pong (int p1_type, int p2_type, int p3_type, int p4_type) {
		super ();
		setBackground (new Color (70, 80, 70));
		
		player1 = new Player (p1_type);
		player2 = new Player (p2_type);
		player3 = new Player (p3_type);
		player4 = new Player (p4_type);
	}
	
	// Compute destination of the ball
	private void computeDestinationX (Player player) {
		int base;
		if (ball_x_speed > 0)
			player.destination = ball_y + (getWidth() - PADDING - WIDTH - RADIUS - ball_x) * (int)(ball_y_speed) / (int)(ball_x_speed);
		else
			player.destination = ball_y - (ball_x - PADDING - WIDTH - RADIUS) * (int)(ball_y_speed) / (int)(ball_x_speed);
		
		if (player.destination <= RADIUS)
			player.destination = 2 * PADDING - player.destination;
		
		if (player.destination > getHeight() - 10) {
			player.destination -= RADIUS;
			if ((player.destination / (getHeight() - 2 * RADIUS)) % 2 == 0)
				player.destination = player.destination % (getHeight () - 2 * RADIUS);
			else
				player.destination = getHeight() - 2 * RADIUS - player.destination % (getHeight () - 2 * RADIUS);
			player.destination += RADIUS;
		}
	}

	private void computeDestinationY (Player player) {
		int base;
		if (ball_y_speed > 0)
			player.destination = ball_x + (getHeight() - PADDING - WIDTH - RADIUS - ball_y) * (int)(ball_x_speed) / (int)(ball_y_speed);
		else
			player.destination = ball_x - (ball_y - PADDING - WIDTH - RADIUS) * (int)(ball_x_speed) / (int)(ball_y_speed);
		
		if (player.destination <= RADIUS)
			player.destination = 2 * PADDING - player.destination;
		
		if (player.destination > getWidth() - 10) {
			player.destination -= RADIUS;
			if ((player.destination / (getWidth() - 2 * RADIUS)) % 2 == 0)
				player.destination = player.destination % (getWidth () - 2 * RADIUS);
			else
				player.destination = getWidth() - 2 * RADIUS - player.destination % (getWidth () - 2 * RADIUS);
			player.destination += RADIUS;
		}
	}
	
	// Set new position of the player
	private void movePlayer (Player player, int destination) {
		int distance = Math.abs (player.position - destination);
		
		if (distance != 0) {
			int direction = - (player.position - destination) / distance;
			
			if (distance > SPEED)
				distance = SPEED;
			
			player.position += direction * distance;
			
			if (player.position - HEIGHT < 0)
				player.position = HEIGHT;
			if (player.position + HEIGHT > getHeight())
				player.position = getHeight() - HEIGHT;
		}
	}
	
	// Compute player position
	private void computePosition (Player player) {
		// MOUSE
		if (player.getType() == Player.MOUSE) {
			if (mouse_inside) {
				int cursor = getMousePosition().y;
				movePlayer (player, cursor);
			}
		}
		// KEYBOARD
		else if (player.getType() == Player.KEYBOARD) {
			if (key_up && !key_down) {
				movePlayer (player, player.position - SPEED);
			}
			else if (key_down && !key_up) {
				movePlayer (player, player.position + SPEED);
			}
		}
		// CPU HARD
		else if (player.getType() == Player.CPU_HARD_X || player.getType() == Player.CPU_HARD_Y) {
			movePlayer (player, player.destination);
		}
		// CPU EASY
		else if (player.getType() == Player.CPU_EASY_X || player.getType() == Player.CPU_EASY_Y) {
			movePlayer (player, ball_y);
		}
	}
	
	// Draw
	public void paintComponent (Graphics g) {
		Toolkit.getDefaultToolkit().sync();
		super.paintComponent (g);
		System.out.println(player2.position);
		
		// Prepara il campo di gioco
		if (new_game) {
			ball_x = getWidth () / 2;
			ball_y = getHeight () / 2;
			
			double phase = Math.random () * Math.PI / 2 - Math.PI / 4;
			ball_x_speed = (int)(Math.cos (phase) * START_SPEED);
			ball_y_speed = (int)(Math.sin (phase) * START_SPEED);
			
			ball_acceleration_count = 0;
			
			if (player1.getType() == Player.CPU_HARD_X || player1.getType() == Player.CPU_EASY_X) {
				player1.position = getHeight () / 2;
				computeDestinationX (player1);
			}
			if (player2.getType() == Player.CPU_HARD_X || player2.getType() == Player.CPU_EASY_X) {
				player2.position = getHeight () / 2;
				computeDestinationX (player2);
			}
			if (player3.getType() == Player.CPU_HARD_Y || player3.getType() == Player.CPU_EASY_Y) {
				player3.position = getHeight () / 2;
				computeDestinationY (player3);
			}
			if (player4.getType() == Player.CPU_HARD_Y || player4.getType() == Player.CPU_EASY_Y) {
				player4.position = getHeight () / 2;
				computeDestinationY (player4);
			}
			
			new_game = false;
		}
		
		// Calcola la posizione del primo giocatore
		if (player1.getType() == Player.MOUSE || player1.getType() == Player.KEYBOARD || ball_x_speed < 0)
			computePosition (player1);
		
		// Calcola la posizione del secondo giocatore
		if (player2.getType() == Player.MOUSE || player2.getType() == Player.KEYBOARD || ball_x_speed > 0)
			computePosition (player2);

		if (player3.getType() == Player.MOUSE || player3.getType() == Player.KEYBOARD || ball_y_speed < 0)
			computePosition (player3);
		
		// Calcola la posizione del secondo giocatore
		if (player4.getType() == Player.MOUSE || player4.getType() == Player.KEYBOARD || ball_y_speed > 0)
			computePosition (player4);
		
		// Calcola la posizione della pallina
		ball_x += ball_x_speed;
		ball_y += ball_y_speed;
		if (ball_y_speed < 0) // Hack to fix double-to-int conversion
			ball_y ++;
		
		// Acceleration handled here
		/*if (acceleration) {
			ball_acceleration_count ++;
			if (ball_acceleration_count == ACCELERATION) {
				ball_x_speed = ball_x_speed + (int)ball_x_speed / Math.hypot ((int)ball_x_speed, (int)ball_y_speed) * 2;
				ball_y_speed = ball_y_speed + (int)ball_y_speed / Math.hypot ((int)ball_x_speed, (int)ball_y_speed) * 2;
				ball_acceleration_count = 0;
			}
		}*/
		
		// Border-collision LEFT
		if (ball_x <= PADDING + WIDTH + RADIUS) {
			int collision_point = ball_y + (int)(ball_y_speed / ball_x_speed * (PADDING + WIDTH + RADIUS - ball_x));
			if (collision_point > player1.position - HEIGHT - TOLERANCE && 
			    collision_point < player1.position + HEIGHT + TOLERANCE) {
				ball_x = 2 * (PADDING + WIDTH + RADIUS) - ball_x;
				ball_x_speed = Math.abs (ball_x_speed);
				ball_y_speed -= Math.sin ((double)(player1.position - ball_y) / HEIGHT * Math.PI / 4)
				                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
			}
			else {
				player1.points --;
				ball_x_speed = Math.abs (ball_x_speed);             //To reflect the ball appropriately
			}
		}
		
		// Border-collision RIGHT
		if (ball_x >= getWidth() - PADDING - WIDTH - RADIUS) {
			int collision_point = ball_y - (int)(ball_y_speed / ball_x_speed * (ball_x - getWidth() + PADDING + WIDTH + RADIUS));
			if (collision_point > player2.position - HEIGHT - TOLERANCE && 
			    collision_point < player2.position + HEIGHT + TOLERANCE) {
				ball_x = 2 * (getWidth() - PADDING - WIDTH - RADIUS ) - ball_x;
				ball_x_speed = -1 * Math.abs (ball_x_speed);
				ball_y_speed -= Math.sin ((double)(player2.position - ball_y) / HEIGHT * Math.PI / 4)           //some sort of spin here
				                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
			}
			else {
				player2.points --;
				ball_x_speed = -1 * Math.abs (ball_x_speed);          //To reflect the ball appropriately
			}
		}
		

		if (ball_y <= PADDING + WIDTH + RADIUS) {
			int collision_point = ball_x + (int)(ball_x_speed / ball_y_speed * (PADDING + WIDTH + RADIUS - ball_y));
			if (collision_point > player3.position - HEIGHT - TOLERANCE && 
			    collision_point < player3.position + HEIGHT + TOLERANCE) {
				ball_y = 2 * (PADDING + WIDTH + RADIUS) - ball_y;
				ball_y_speed = Math.abs (ball_y_speed);
				ball_x_speed -= Math.sin ((double)(player3.position - ball_x) / HEIGHT * Math.PI / 4)
				                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
			}
			else {
				player3.points --;
				ball_y_speed = Math.abs (ball_y_speed);             //To reflect the ball appropriately
			}
		}
		
		// Border-collision RIGHT
		if (ball_y >= getHeight() - PADDING - WIDTH - RADIUS) {
			int collision_point = ball_x - (int)(ball_x_speed / ball_y_speed * (ball_y - getHeight() + PADDING + WIDTH + RADIUS));
			if (collision_point > player4.position - HEIGHT - TOLERANCE && 
			    collision_point < player4.position + HEIGHT + TOLERANCE) {
				ball_y = 2 * (getHeight() - PADDING - WIDTH - RADIUS ) - ball_y;
				ball_y_speed = -1 * Math.abs (ball_y_speed);
				ball_x_speed -= Math.sin ((double)(player4.position - ball_x) / HEIGHT * Math.PI / 4)
				                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
			}
			else {
				player4.points --;
				ball_y_speed = -1 * Math.abs (ball_y_speed);          //To reflect the ball appropriately
			}
		}
		// Border-collision TOP
		if (ball_y <= RADIUS) {
			ball_y_speed = Math.abs (ball_y_speed);
			ball_y = 2 * RADIUS - ball_y;
		}
		
		// Border-collision BOTTOM
		if (ball_y >= getHeight() - RADIUS) {
			ball_y_speed = -1 * Math.abs (ball_y_speed);
			ball_y = 2 * (getHeight() - RADIUS) - ball_y;
		}
		
		// Disegna i carrelli
		g.setColor (Color.WHITE);
		g.fillRect (PADDING, player1.position - HEIGHT, WIDTH, HEIGHT * 2);
		g.fillRect (getWidth() - PADDING - WIDTH, player2.position - HEIGHT, WIDTH, HEIGHT * 2);
		g.fillRect (player3.position - HEIGHT, PADDING, HEIGHT*2, WIDTH);
		g.fillRect (player4.position - HEIGHT, getHeight() - PADDING - WIDTH, HEIGHT*2, WIDTH);
		// Disegna la palla
		g.fillOval (ball_x - RADIUS, ball_y - RADIUS, RADIUS*2, RADIUS*2);
		
		// Disegna i punti
		g.drawString (player1.points+" ", getWidth() / 2 - 20, 20);
		g.drawString (player2.points+" ", getWidth() / 2 + 20, 20);
	}
	
	// New frame
	public void actionPerformed (ActionEvent e) {
		repaint ();
	}
	
	// Mouse inside
	public void mouseEntered (MouseEvent e) {
		mouse_inside = true;
	}
	
	// Mouse outside
	public void mouseExited (MouseEvent e) {
		mouse_inside = false;
	}
	
	// Mouse pressed
	public void mousePressed (MouseEvent e) {}
	
	// Mouse released
	public void mouseReleased (MouseEvent e) {}
		
	// Mouse clicked
	public void mouseClicked (MouseEvent e) {}
	
	// Key pressed
	public void keyPressed (KeyEvent e) {
//		System.out.println ("Pressed "+e.getKeyCode()+"   "+KeyEvent.VK_UP+" "+KeyEvent.VK_DOWN);
		if (e.getKeyCode() == KeyEvent.VK_UP)
			key_up = true;
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			key_down = true;
	}
	
	// Key released
	public void keyReleased (KeyEvent e) {
//		System.out.println ("Released "+e.getKeyCode());
		if (e.getKeyCode() == KeyEvent.VK_UP)
			key_up = false;
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			key_down = false;
	}
	
	// Key released
	public void keyTyped (KeyEvent e) {}
}
