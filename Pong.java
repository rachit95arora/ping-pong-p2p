import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.ByteArrayOutputStream;
import java.net.UnknownHostException;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.util.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.net.*;

import java.awt.event.MouseListener;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.awt.Image;

import javax.imageio.ImageIO;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Pong extends JPanel implements ActionListener, MouseListener, KeyListener {
	
	private static final int RADIUS = 10; 
	private static final int START_SPEED = 8;
	private static final int ACCELERATION = 125;
	private int tester1;
	private int gameID;
	int N;
	private String gameName;
	//Final variables for the speed, height of the paddle, width of the paddle, tolerance value, padding at edges
	private static final int SPEED = 6;                    //changed values
	private static final int HEIGHT = 50;					//changed values
	private static final int WIDTH = 10;					//changed values
	private static final int TOLERANCE = 5;					//changed values
	private static final int PADDING = 3; 
	private static final int PLAYER_WAIT = 1000;
	//SendClass objects for all the players that conatain attributes like the details of the balls and scores etc.
	private SendClass otherPlayer1,otherPlayer2,otherPlayer3,otherPlayer4;
	private Timestamp stamp1,stamp2,stamp3,stamp4;
	//Player objects for each of the players
	private Player player1;
	private Player player2;
	private Player myPlayer;
	InetAddress group;
	private Player player3;
	///For paddle size change
	private int expansions = 3;
	//For powerups as in size longevity, contract and speed up
	private int paddleToggle =  0;
	//The timer durations for monitoring the durations for powerup for paddle lengthening
	private long toggleTime = 0;
	private long currentTime;
	///For hitting and color change
	//The variable to account for the ny=umber of hit/slow usages left
	private int hits, slow;
	private int showTime = 30;
	//The arrays to store statuses of hit/stick for each of the balls and also show of stick/hit colorings and force updates
	private boolean hitting[] = new boolean[100];
	private boolean sticking[] = new boolean[100];
	private int showStick[] = new int[100];
	private int forceUpdate[] = new int[100];
	
	private Player player4;
	private MulticastSocket s;
	private boolean new_game = true;
	private int loadingBall[] = new int[100];
	private boolean presence = false;
	private String multiCastAddress;
  	private int multiCastPort;
  	private int basePORT;
  	/*
	The arrays in the current class to contain the positions/coordinates of the 
	balls in the game and the velocities, both components, of the balls in the
	game. Updated after all collisions.
  	*/
	private int ball_x[] = new int[100];
	private int ball_y[] = new int[100];
	private double ball_x_speed[] = new double[100];
	//The array for exit of a human player
	private int byeBye[] = new int[4];
	private double ball_y_speed[] = new double[100];
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private ObjectOutputStream oos;
	// The acceleration array is set to all false initially for each of the balls
	public boolean acceleration[] = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                                    false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                                    false,false,false,false};
    //The count array for acceleration stores the duration specific numbers for the accelerated motion of each ball
    //Arrays also for storing ball-level details for each ball such as the syncing, angle and rotation
	private int ball_acceleration_count[] = new int[100];
	private int syncBall[] = new int[100];
	private boolean mouse_inside = false;
	private int ball_angle[] = new int[100];
    private double ball_rotation[] = new double[100];
	//Key Handles
	private boolean key_up = false;
	private boolean key_down = false;
	private boolean key_left = false;
	private boolean key_right = false;
	private boolean key_space = false;
	private boolean key_A = false;
	private boolean key_D = false;

	//FOR POWERUPS
	private int powerupCountDown = 0;
	private static final int POWERUP_TIME = 500;
	private  Powerup myPowerUp; 
	private int speedCount = 0;
	private int lengthCount = 0;
	private int paddleSpeedToggle  = 0;
	private int paddleSpeed;
	//The powerups for shrink and flick speed use images generated randomly
	//Details stored in these variables
	BufferedImage slate;
    TexturePaint slatetp;
    Image background;
   Image powerup1;
   Image powerup2;
	
	// Constructor
	public Pong (int ID, boolean keyboard, int recdPort, String nameMe, String THE_ADDRESS, int ballnos) {
		super ();
		//Assignment to local variables for all the details received from the players as parameters to this method
		N=ballnos;
		gameName = nameMe;
		basePORT = recdPort;
		multiCastAddress = THE_ADDRESS;
		loadImages();
		setBackground (new Color (70, 80, 70));
		gameID = ID;
		byeBye[0]=byeBye[1]=byeBye[2]=byeBye[3]=0;
		speedCount = 0;
		lengthCount =0;
		multiCastPort = recdPort+gameID;
		//Setting up the initial configs of the game by initializing the objects to declared instances
		// and assigning other details like paddle lengths, speeds etc for all the players
		player1 = new Player (Player.CPU_HARD_X);
		player2 = new Player (Player.CPU_HARD_X);
		player3 = new Player (Player.CPU_HARD_Y);
		player4 = new Player (Player.CPU_HARD_Y);
		player1.paddleLength = player2.paddleLength = player3.paddleLength = player4.paddleLength = HEIGHT;
		player1.paddleSpeed = player2.paddleSpeed = player3.paddleSpeed = player4.paddleSpeed = SPEED;
		player1.position = player2.position = player3.position = player4.position = 200;
		otherPlayer1 = new SendClass();
		otherPlayer2 = new SendClass();
		otherPlayer3 = new SendClass();
		otherPlayer4 = new SendClass();
		stamp1 = new Timestamp();
		stamp2 = new Timestamp();
		stamp3 = new Timestamp();
		stamp4 = new Timestamp();
		stamp1.timedLast = stamp2.timedLast = stamp3.timedLast = stamp4.timedLast = System.currentTimeMillis();
		///Instantiate my powerup
		myPowerUp = new Powerup();
		//Initialize Hitting
		showStick = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                       0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                       0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                       0,0,0,0};
        //The start out config for the hits and slows, Capacity id four at max and the status is false for all players in the beginning
        //for both the actions and all balls
		hits = slow = 4;
		hitting = new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                              false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                              false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                              false,false,false,false};
		sticking = new boolean[]{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                              false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                              false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
                              false,false,false,false};
		//Using the keyboard/mouse preferences to set up the player controls
        //KEYBOARD
		if(keyboard){
			if(gameID ==1 ) {
				player1 = new Player(Player.KEYBOARD);
				myPlayer = player1;
			}
			else if(gameID ==2 ) {
				player2 = new Player(Player.KEYBOARD);
				myPlayer = player2;
			}
			else if(gameID == 3 ) {
				player3 = new Player(Player.KEYBOARD);
				myPlayer = player3;
			}
			else if(gameID == 4 ) {
				player4 = new Player(Player.KEYBOARD);
				myPlayer = player4;
			}
		}
		//MOUSE
		else{
			if(gameID ==1 ) {
				player1 = new Player(Player.MOUSE);
				myPlayer = player1;
			}
			else if(gameID ==2 ) {
				player2 = new Player(Player.MOUSE);
				myPlayer = player2;
			}
			else if(gameID == 3 ) {
				player3 = new Player(Player.MOUSE);
				myPlayer = player3;
			}
			else if(gameID == 4 ) {
				player4 = new Player(Player.MOUSE);
				myPlayer = player4;
			}
		}
	}

	// Compute destination of the ball: X coordinate
	private void computeDestinationX (Player player) {
		int base;
		//Conditions for identifying the direction of the balls x direction speed and predicting the aim by interpolation
		// X * (DY/DX)
		if ((int)ball_x_speed[0] > 0)
			player.destination = ball_y[0] + (getWidth() - PADDING - WIDTH - RADIUS - ball_x[0]) * (int)(ball_y_speed[0]) / (int)(ball_x_speed[0]);
		else if((int)ball_x_speed[0] < 0)
			player.destination = ball_y[0] - (ball_x[0] - PADDING - WIDTH - RADIUS) * (int)(ball_y_speed[0]) / (int)(ball_x_speed[0]);
		else if (ball_x_speed[0]>0)
			player.destination = ball_y[0] + (getWidth() - PADDING - WIDTH - RADIUS - ball_x[0]) * (int)(ball_y_speed[0]);
		else if(ball_x_speed[0]<= 0)
			player.destination = ball_y[0] - (ball_x[0] - PADDING - WIDTH - RADIUS) * (int)(ball_y_speed[0]) * -1;


		//The other cases like when the computation yields destination samlller than radius
		// or violating the board edge length constraint
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
	//Compute destination of the ball: Y coordinate
	private void computeDestinationY (Player player) {
		int base;
		//Conditions for identifying the direction of the balls x direction speed and predicting the aim by interpolation
		// X * (DY/DX)
		if ((int)ball_y_speed[0] > 0)
			player.destination = ball_x[0] + (getHeight() - PADDING - WIDTH - RADIUS - ball_y[0]) * (int)(ball_x_speed[0]) /(int)(ball_y_speed[0]);
		else if((int)ball_y_speed[0] < 0)
			player.destination = ball_x[0] - (ball_y[0] - PADDING - WIDTH - RADIUS) * (int)(ball_x_speed[0]) /(int)(ball_y_speed[0]);
		else if (ball_y_speed[0] > 0)
			player.destination = ball_x[0] + (getHeight() - PADDING - WIDTH - RADIUS - ball_y[0]) * (int)(ball_x_speed[0]);
		else if(ball_y_speed[0] <= 0)
			player.destination = ball_x[0] - (ball_y[0] - PADDING - WIDTH - RADIUS) * (int)(ball_x_speed[0]) * -1;
		
		//The other cases like when the computation yields destination samlller than radius
		// or violating the board edge length constraint
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
			
			if (distance > player.paddleSpeed)
				distance = player.paddleSpeed;
			
			player.position += direction * distance;
			//The boundary conditions
			//A restraint is applied to the case the paddles tend to clash in the corners
			if (player.position - player.paddleLength < 0+(PADDING+WIDTH))
				player.position = player.paddleLength+(PADDING+WIDTH);
			if (player.position + player.paddleLength > getHeight()-(PADDING+WIDTH))
				player.position = getHeight() - player.paddleLength-(PADDING+WIDTH);
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
		else if (player.getType() == Player.KEYBOARD && (gameID ==1 || gameID ==2)) {
			//According to the key pressed, the paddle is moved likewise in the keyboard case
			//For the vertical moving paddles
			if (key_up && !key_down) {
				movePlayer (player, player.position - player.paddleSpeed);
			}
			else if (key_down && !key_up) {
				movePlayer (player, player.position + player.paddleSpeed);
			}
		}
		//For the horizontal moving paddles
		else if (player.getType() == Player.KEYBOARD && (gameID ==3 || gameID ==4)) {
			if (key_left && !key_right) {
				movePlayer (player, player.position - player.paddleSpeed);
			}
			else if (key_right && !key_left) {
				movePlayer (player, player.position + player.paddleSpeed);
			}
		}
		// CPU HARD
		else if (player.getType() == Player.AGENT_X || player.getType() == Player.CPU_HARD_X || player.getType()== Player.AGENT_Y || player.getType() == Player.CPU_HARD_Y) {
			movePlayer (player, player.destination);
		}
		// CPU EASY
		else if (player.getType() == Player.CPU_EASY_X || player.getType() == Player.CPU_EASY_Y) {
			movePlayer (player, ball_y[0]);
		}
	}
	/*


	*/
	public void startReceivers()
	{
		Receiver receiveThread1 = new Receiver(multiCastAddress,basePORT + 1, otherPlayer1, gameID , 1 , stamp1);
		Receiver receiveThread2 = new Receiver(multiCastAddress,basePORT + 2, otherPlayer2, gameID , 2 , stamp2);
		Receiver receiveThread3 = new Receiver(multiCastAddress,basePORT + 3, otherPlayer3, gameID , 3 , stamp3);
		Receiver receiveThread4 = new Receiver(multiCastAddress,basePORT + 4, otherPlayer4, gameID , 4 , stamp4);
		if(gameID == 1){
			receiveThread2.start();
			receiveThread3.start();
			receiveThread4.start();
		}
		else if(gameID == 2){
			receiveThread1.start();
			receiveThread3.start();
			receiveThread4.start();
		}
		else if(gameID == 3){
			receiveThread1.start();
			receiveThread2.start();
			receiveThread4.start();
		}
		else if(gameID == 4){
			receiveThread1.start();
			receiveThread2.start();
			receiveThread3.start();
		}
	}
	/*
	For the j'th ball. Done for each ball in the game.

	*/
	private boolean syncFromOthers(int j)
		{
			long timered = System.currentTimeMillis();
			if(otherPlayer1.loadingBall[j]>50 && otherPlayer1.ballx[j] != -420 && (timered - stamp1.timedLast<100))
			{
				player1.points = otherPlayer1.score1;
				player2.points = otherPlayer1.score2;
				player3.points = otherPlayer1.score3;
				player4.points = otherPlayer1.score4;
			}
			else if(otherPlayer2.loadingBall[j]>50 && otherPlayer2.ballx[j] != -420 && (timered - stamp2.timedLast<100))
			{
				player1.points = otherPlayer2.score1;
				player2.points = otherPlayer2.score2;
				player3.points = otherPlayer2.score3;
				player4.points = otherPlayer2.score4;
			}
			else if(otherPlayer3.loadingBall[j]>50 && otherPlayer3.ballx[j] != -420 && (timered - stamp3.timedLast<100))
			{
				player1.points = otherPlayer3.score1;
				player2.points = otherPlayer3.score2;
				player3.points = otherPlayer3.score3;
				player4.points = otherPlayer3.score4;
			}
			else if(otherPlayer4.loadingBall[j]>50 && otherPlayer4.ballx[j] != -420 && (timered - stamp4.timedLast<100))
			{
				player1.points = otherPlayer4.score1;
				player2.points = otherPlayer4.score2;
				player3.points = otherPlayer4.score3;
				player4.points = otherPlayer4.score4;
			}
			myPlayer.points = 0;
			int sumx = 0,sumy = 0;
	 		int count =0;
	 		Double suvx = 0.0;
	 		
	 		Double suvy = 0.0;
	 		if(otherPlayer1.loadingBall[j]>50 && otherPlayer1.ballx[j] != -420 && (timered - stamp1.timedLast<100)){
	 			sumx += otherPlayer1.ballx[j];
	 			sumy += otherPlayer1.bally[j];
	 			suvx +=otherPlayer1.ballx_speed[j];
	 			suvy +=otherPlayer1.bally_speed[j];
	 			count++;
	 		}
	 		if(otherPlayer2.loadingBall[j]>50 && otherPlayer2.ballx[j] != -420 && (timered - stamp2.timedLast<100)){
	 			sumx += otherPlayer2.ballx[j];
	 			sumy += otherPlayer2.bally[j];
	 			suvx +=otherPlayer2.ballx_speed[j];
	 			suvy +=otherPlayer2.bally_speed[j];
	 			count++;
	 		}
	 		if(otherPlayer3.loadingBall[j]>50 && otherPlayer3.ballx[j] != -420 && (timered - stamp3.timedLast<100)){
	 			sumx += otherPlayer3.ballx[j];
	 			sumy += otherPlayer3.bally[j];
	 			suvx +=otherPlayer3.ballx_speed[j];
	 			suvy +=otherPlayer3.bally_speed[j];
	 			count++;
	 		if(otherPlayer4.loadingBall[j]>50 && otherPlayer4.ballx[j] != -420 && (timered - stamp4.timedLast<100)){
	 			sumx += otherPlayer4.ballx[j];
	 			sumy += otherPlayer4.bally[j];
	 			suvx +=otherPlayer4.ballx_speed[j];
	 			suvy +=otherPlayer4.bally_speed[j];
	 			count++;
	 		}
	 		}
	 		if(count > 0){
	 			ball_x[j] = sumx/count ;
	 			ball_y[j] = sumy/count ;
	 			ball_x_speed[j] = suvx / ((double)count);
	 			ball_y_speed[j] = suvy / ((double)count);
	 			return true;
	 		}
	 		else
	 		{
	 			if(otherPlayer1.loadingBall[j]>loadingBall[j] && otherPlayer1.ballx[j] != -420 && (timered - stamp1.timedLast<100)){
		 			sumx += otherPlayer1.ballx[j];
		 			sumy += otherPlayer1.bally[j];
		 			suvx +=otherPlayer1.ballx_speed[j];
		 			suvy +=otherPlayer1.bally_speed[j];
		 			count++;
		 		}
		 		if(otherPlayer2.loadingBall[j]>loadingBall[j] && otherPlayer2.ballx[j] != -420 && (timered - stamp2.timedLast<100)){
		 			sumx += otherPlayer2.ballx[j];
		 			sumy += otherPlayer2.bally[j];
		 			suvx +=otherPlayer2.ballx_speed[j];
		 			suvy +=otherPlayer2.bally_speed[j];
		 			count++;
		 		}
		 		if(otherPlayer3.loadingBall[j]>loadingBall[j] && otherPlayer3.ballx[j] != -420 && (timered - stamp3.timedLast<100)){
		 			sumx += otherPlayer3.ballx[j];
		 			sumy += otherPlayer3.bally[j];
		 			suvx +=otherPlayer3.ballx_speed[j];
		 			suvy +=otherPlayer3.bally_speed[j];
		 			count++;
		 		}
		 		if(otherPlayer4.loadingBall[j]>loadingBall[j] && otherPlayer4.ballx[j] != -420 && (timered - stamp4.timedLast<100)){
		 			sumx += otherPlayer4.ballx[j];
		 			sumy += otherPlayer4.bally[j];
		 			suvx +=otherPlayer4.ballx_speed[j];
		 			suvy +=otherPlayer4.bally_speed[j];
		 			count++;
		 		}
		 		if(sumx > 0){
	 			ball_x[j] = sumx/count ;
	 			ball_y[j] = sumy/count ;
	 			ball_x_speed[j] = suvx / ((double)count);
	 			ball_y_speed[j] = suvy / ((double)count);
	 			return true;
	 		}
	 		}
	 		return false;
		}

		private boolean sameSign(double a, double b){
			if(Math.signum(a)*Math.signum(b)>=0.0)return true;
			return false;
		}
		/*
		For the j'th ball. Done for each ball in the game.



		*/
		private boolean syncFromAll(int j)
		{
			int sumx = 0,sumy = 0;
	 		int count =0;
	 		Double suvx = 0.0;
	 		long timered = System.currentTimeMillis();
	 		Double suvy = 0.0;
	 		if(otherPlayer1.loadingBall[j]>50 && otherPlayer1.ballx[j] != -420 && sameSign(otherPlayer1.ballx_speed[j],ball_x_speed[j]) && sameSign(otherPlayer1.bally_speed[j],ball_y_speed[j]) && (timered - stamp1.timedLast<100))
	 		{
	 			sumx += otherPlayer1.ballx[j];
	 			sumy += otherPlayer1.bally[j];
	 			suvx +=otherPlayer1.ballx_speed[j];
	 			suvy +=otherPlayer1.bally_speed[j];
	 			count++;
	 		}
	 		if(otherPlayer2.loadingBall[j]>50 && otherPlayer2.ballx[j] != -420  && sameSign(otherPlayer2.ballx_speed[j],ball_x_speed[j]) && sameSign(otherPlayer2.bally_speed[j],ball_y_speed[j]) && (timered - stamp2.timedLast<100)){
	 			sumx += otherPlayer2.ballx[j];
	 			sumy += otherPlayer2.bally[j];
	 			suvx +=otherPlayer2.ballx_speed[j];
	 			suvy +=otherPlayer2.bally_speed[j];
	 			count++;
	 		}
	 		if(otherPlayer3.loadingBall[j]>50 && otherPlayer3.ballx[j] != -420  && sameSign(otherPlayer3.ballx_speed[j],ball_x_speed[j]) && sameSign(otherPlayer3.bally_speed[j],ball_y_speed[j]) && (timered - stamp3.timedLast<100)){
	 			sumx += otherPlayer3.ballx[j];
	 			sumy += otherPlayer3.bally[j];
	 			suvx +=otherPlayer3.ballx_speed[j];
	 			suvy +=otherPlayer3.bally_speed[j];
	 			count++;
	 		}
	 		if(otherPlayer4.loadingBall[j]>50 && otherPlayer4.ballx[j] != -420 && sameSign(otherPlayer4.ballx_speed[j],ball_x_speed[j]) && sameSign(otherPlayer4.bally_speed[j],ball_y_speed[j]) && (timered - stamp4.timedLast<100)){
	 			sumx += otherPlayer4.ballx[j];
	 			sumy += otherPlayer4.bally[j];
	 			suvx +=otherPlayer4.ballx_speed[j];
	 			suvy +=otherPlayer4.bally_speed[j];
	 			count++;
	 		}
	 		if(sumx > 0){
	 			count++;
	 			ball_x[j] = (sumx +ball_x[j])/count ;
	 			ball_y[j] = (sumy +ball_y[j])/count ;
	 			ball_x_speed[j] = (suvx + ball_x_speed[j]) / ((double)count);
	 			ball_y_speed[j] =  (suvy + ball_y_speed[j]) / ((double)count);
	 			return true;
	 		}
	 		return false;
		}
		//The power up function for the powerups grabbed by the moving paddle
	private void handlePowerUp()
	{
		//System.out.println(powerupCountDown);
		//System.out.println(myPowerUp.countdown);
		if(myPowerUp.countdown==0 && powerupCountDown < POWERUP_TIME){
			powerupCountDown++;
		}
		//The image is to be positioned depending on the player's paddle orientation
		//and for a limited duration. This part handles that. Also the exact position varies with
		//a certain randomness across the paddle domain
		else if(powerupCountDown == POWERUP_TIME){
			powerupCountDown = 0;
			int tentative = (int) Math.round(Math.random())+1;
			myPowerUp.type = tentative;
			myPowerUp.countdown = 1000;
			myPowerUp.transition = 0;
			if(gameID==1 || gameID ==2)
			{
				myPowerUp.position = (int)(0.1*getHeight()) + (int)(Math.random() * 0.8 * getHeight());	
			}
			else if(gameID==3 || gameID ==4)
			{
				myPowerUp.position = (int)(0.1*getWidth()) + (int)(Math.random() * 0.8 * getWidth());	
			}
			myPowerUp.ID = gameID;
			myPowerUp.flick = false;
			myPowerUp.visible = true;
		}
		//Handle Active powerup
		if(myPowerUp.countdown> 0){
			myPowerUp.countdown--;
			//The flick value is assigned a boolean value according to the transition value
			if(myPowerUp.transition < myPowerUp.TRANSIT)
			{
				myPowerUp.flick = false;
				myPowerUp.transition++;
			}
			else if(myPowerUp.transition < 2 * myPowerUp.TRANSIT)
			{
				myPowerUp.flick = true;
				myPowerUp.transition++;
			}
			else
			{
				myPowerUp.transition = 0;
			}
		}
		//Everything is zeroed down to resume normal game play
		else if(myPowerUp.countdown == 0){
			myPowerUp.type = 0;
			myPowerUp.transition = 0;
			myPowerUp.position = 0;
			myPowerUp.visible = false;
		}
	}

	// Draw
	public void paintComponent (Graphics g) {
		Toolkit.getDefaultToolkit().sync();
		super.paintComponent (g);
		
		
		if (new_game) {
			startReceivers();
			//All the ball parameters for all the balls are initialized/ setup for a new game
			//Suvh as angle , loading ball value, force updates and sync ball
			ball_angle = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                0,0,0,0};  

			try{

			slatetp = new TexturePaint(slate, new Rectangle(0, 0, 500, 500));    

			Thread.sleep(1000);}
			catch(Exception e){System.out.println("Threads not invoked successfully!");}
			//Start receiving data
			loadingBall = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                0,0,0,0};
			forceUpdate = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                0,0,0,0};
			paddleSpeedToggle = 0;
			paddleSpeed = SPEED;
			syncBall = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                0,0,0,0};
			try{
		        group = InetAddress.getByName(multiCastAddress);
		        oos = new ObjectOutputStream(baos);
		        s = new MulticastSocket(multiCastPort);
		        s.joinGroup(group);
	 		}
	 		catch(IOException abc){System.out.println("Shit man");}
	 		//Setting up initial conditions of the ball using network or auto...
	 		for(int p=0;p<N;p++)
	 		{
	 		if(syncFromOthers(p));
	 		else{
	 			/*
	 			The balls in the case of a new game are arranged uniformly in a circle like the
	 			vertices of an N sided polygon.
	 			*/
				ball_x[p] = getWidth () / 2 + (int)(100*Math.cos (2*p*Math.PI/N));
				ball_y[p] = getHeight () / 2 + (int)(100*Math.sin (2*p*Math.PI/N));
				
				/*
	 			The balls in the case of a new game are arranged uniformly in a circle like the
	 			vertices of an N sided polygon. But they will start moving randomly according to a phase
	 			variable that dictates their x and y components of speed.
	 			*/
				double phase = (Math.round(Math.random()*4) * (Math.PI/2)) +(Math.random () * Math.PI / 4) + Math.PI / 8;
				ball_x_speed[p] = (int)(Math.cos (phase) * START_SPEED);
				ball_y_speed[p] = (int)(Math.sin (phase) * START_SPEED);
				
				ball_acceleration_count[p] = 0;
				ball_rotation[p] = 0.0;
			}
			}
			//////////////////////////////////////////////////////////////
			
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
			//After the configurations are settled in the new game, the game ceases to be new
			new_game = false;
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//Handle PowerUps
		handlePowerUp();

		//Paddle Length Setup && showColor for hits
		currentTime = System.currentTimeMillis();
		if(currentTime - toggleTime <20000 && paddleToggle!=2){
			paddleToggle = 1;
		}
		else if(paddleToggle!=2) paddleToggle = 0;

		for(int j=0;j<N;j++)
		{
			//The color changes on hit and stick have to be transient
			//Here we check if the time has expired and downgrade the true value
			//for the change to false
		if(showStick[j] < showTime && (hitting[j] || sticking[j])){showStick[j]++;}
		else if(hitting[j] || sticking[j] ){
			showStick[j] = 0;
			hitting[j] = sticking[j] = false;
		}
		if(lengthCount>0)
		{
			lengthCount--;
			paddleToggle = 2;
		}
		else if(lengthCount == 0){
			paddleToggle = 0;
			lengthCount = -1;
		}
		}
		//System.out.println(paddleToggle);
		/*
		There are two similiar powerups in the game
		1: The lengthening powerup which doubles the paddle length on space tap
		2: The power up on paddle grabbing the image which shrinks it to half
		Conditionally, the required one is done here.
		*/
		if(otherPlayer1.paddleToggle ==1){player1.paddleLength = 2 * HEIGHT; }
		else if(otherPlayer1.paddleToggle ==2){player1.paddleLength = HEIGHT /2; }
		else player1.paddleLength = HEIGHT;
		if(otherPlayer2.paddleToggle ==1)player2.paddleLength = 2 * HEIGHT;
		else if(otherPlayer2.paddleToggle ==2)player2.paddleLength = HEIGHT/2;
		else player2.paddleLength = HEIGHT;
		if(otherPlayer3.paddleToggle ==1)player3.paddleLength = 2 * HEIGHT;
		else if(otherPlayer3.paddleToggle ==2)player3.paddleLength =  HEIGHT/2;
		else player3.paddleLength = HEIGHT;
		if(otherPlayer4.paddleToggle ==1)player4.paddleLength = 2 * HEIGHT;
		else if(otherPlayer4.paddleToggle ==2)player4.paddleLength = HEIGHT/2;
		else player4.paddleLength = HEIGHT;
		
		//The mypowerup coonfigs for the paddle toggle
		if(!myPowerUp.visible ||  myPowerUp.type !=2)
		{
			if(paddleToggle ==1)myPlayer.paddleLength = 2 * HEIGHT;
			else if(paddleToggle ==2)myPlayer.paddleLength = HEIGHT/2;
			else myPlayer.paddleLength = HEIGHT;
		}
		else
		{
			//The mypower trigger details warrant the following changes to the
			//concerned attributes depending on whether the image fell
			//under the range of paddle
			if(Math.abs(myPowerUp.position - myPlayer.position) < myPlayer.paddleLength){

				myPlayer.paddleLength = HEIGHT/2;
				paddleToggle = 2;
				lengthCount = 1000;
				myPowerUp.visible = false;
				myPowerUp.countdown = 0;
				myPowerUp.type = 0;
				myPowerUp.transition = 0;
				myPowerUp.position = 0;	
			}
		}
		if(myPowerUp.visible && myPowerUp.type ==1)
		{
			if(Math.abs(myPowerUp.position - myPlayer.position )< myPlayer.paddleLength){
				myPowerUp.visible = false;
				speedCount = 1000;
				myPowerUp.countdown = 0;
				myPowerUp.type = 0;
				myPowerUp.transition = 0;
				myPowerUp.position = 0;
			}
		}
		//The active power up doubles the speed for the player that hit it
		if(otherPlayer1.paddleSpeedToggle ==1){player1.paddleSpeed = 2 * SPEED; }
		else player1.paddleSpeed = SPEED;
		if(otherPlayer2.paddleSpeedToggle ==1)player2.paddleSpeed = 2 * SPEED;
		else player2.paddleSpeed = SPEED;
		if(otherPlayer3.paddleSpeedToggle ==1)player3.paddleSpeed = 2 * SPEED;
		else player3.paddleSpeed = SPEED;
		if(otherPlayer4.paddleSpeedToggle ==1)player4.paddleSpeed = 2 * SPEED;
		else player4.paddleSpeed = SPEED;
		if(speedCount > 0 )
		{
			speedCount--;
			paddleSpeed = 2*SPEED;
			paddleSpeedToggle = 1;
		}
		else if(speedCount ==0) {
			paddleSpeed = SPEED;
			paddleSpeedToggle = 0;
		}

		myPlayer.paddleSpeed = paddleSpeed;

		//SCORING RESET FOR NEW PLAYER
		if(otherPlayer1.loadingBall[0]>0 && otherPlayer1.loadingBall[0]<50) player1.points = 0;
		if(otherPlayer2.loadingBall[0]>0 && otherPlayer2.loadingBall[0]<50) player2.points = 0;
		if(otherPlayer3.loadingBall[0]>0 && otherPlayer3.loadingBall[0]<50) player3.points = 0;
		if(otherPlayer4.loadingBall[0]>0 && otherPlayer4.loadingBall[0]<50) player4.points = 0;
		//Loading Ball State
		for(int h=0;h<N;h++)
		{
		if(loadingBall[h] < 50){
			loadingBall[h]++;
			syncFromOthers(h);
		}
		else if(loadingBall[h] <700){
			loadingBall[h]++;
		}
		}
		//Sync Ball with live players
		//System.out.println(forceUpdate);
		for(int k=0;k<N;k++)
		{
		if(forceUpdate[k] == 0 && loadingBall[k] >60)
		{
			//System.out.println("Yay");
			if(otherPlayer1.forceUpdate[k] + otherPlayer2.forceUpdate[k] + otherPlayer3.forceUpdate[k] + otherPlayer4.forceUpdate[k] > 0){
				SendClass point = new SendClass();
			
				if(otherPlayer1.forceUpdate[k] > otherPlayer2.forceUpdate[k] && otherPlayer1.forceUpdate[k] > otherPlayer3.forceUpdate[k] && otherPlayer1.forceUpdate[k] > otherPlayer4.forceUpdate[k])
				{
					point = otherPlayer1;
				}
				else if(otherPlayer2.forceUpdate[k] > otherPlayer1.forceUpdate[k] && otherPlayer2.forceUpdate[k] > otherPlayer3.forceUpdate[k] && otherPlayer2.forceUpdate[k] > otherPlayer4.forceUpdate[k])
				{
					point = otherPlayer2;
				}
				else if(otherPlayer3.forceUpdate[k] > otherPlayer1.forceUpdate[k] && otherPlayer3.forceUpdate[k] > otherPlayer2.forceUpdate[k] && otherPlayer3.forceUpdate[k] > otherPlayer4.forceUpdate[k])
				{
					point = otherPlayer3;
				}
				else if(otherPlayer4.forceUpdate[k] > otherPlayer1.forceUpdate[k] && otherPlayer4.forceUpdate[k] > otherPlayer2.forceUpdate[k] && otherPlayer4.forceUpdate[k] > otherPlayer3.forceUpdate[k])
				{
					point = otherPlayer4;
				}
				//The updates are put to assignment	
				ball_x[k] = point.ballx[k];
				ball_y[k] = point.bally[k];
				ball_x_speed[k] = point.ballx_speed[k];
				ball_y_speed[k] = point.bally_speed[k];
			}
			else{
				//synchronize function is run for each and every ball thereafter in the same loop
				//if the updates sum was not positive
				synchronize(k);
			}
		}
		else  if (forceUpdate[k]>0){forceUpdate[k]--;}
		}
		updatePlayer1();
		updatePlayer2();
		updatePlayer3();
		updatePlayer4();

		initiatePaddleExpansions();
		//The ball details for all the balls in the game are updated by this looped call
		for(int k=0;k<N;k++)
		updateBall(k);
		sendPlayer();

		PrintUI(g);

	}

	private void loadImages() {                                         //FOR TEXTURE
		try{
			File file = new File("wood_texture_004.png");
			slate = ImageIO.read(file);
			//The images are gotten from the loaded files
			background = Toolkit.getDefaultToolkit().createImage("backnew.jpg");
			powerup1 = Toolkit.getDefaultToolkit().createImage("powerup1.png");
			powerup2 = Toolkit.getDefaultToolkit().createImage("powerup2.png");
		}
		catch (IOException ex) {
            System.out.println("problem");
        }
	}

	private void PrintUI(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;
		g.drawImage(background,0,0,null);
		//The below lines create the square blocks at the corners for impeding the paddles from coalescing there
		g.setColor (Color.RED);
        g.fill3DRect (0, 0, PADDING+WIDTH, PADDING+WIDTH,true);
        g.fill3DRect (0, getHeight()-PADDING-WIDTH, PADDING+WIDTH, PADDING+WIDTH, true);
        g.fill3DRect (getWidth()-PADDING-WIDTH,0,PADDING+WIDTH, PADDING+WIDTH, true);
        g.fill3DRect (getWidth()-PADDING-WIDTH, getHeight()-PADDING-WIDTH, PADDING+WIDTH, PADDING+WIDTH, true);
		g.setPaint (slatetp);
		//System.out.println(player1.paddleLength);
		//The slated texture is used to draw the paddles at each of the edges
		g.fillRect (PADDING, player1.position - player1.paddleLength, WIDTH, player1.paddleLength * 2);
		g.fillRect (getWidth() - PADDING - WIDTH, player2.position - player2.paddleLength, WIDTH, player2.paddleLength * 2);
		g.fillRect (player3.position - player3.paddleLength, PADDING, player3.paddleLength*2, WIDTH);
		g.fillRect (player4.position - player4.paddleLength, getHeight() - PADDING - WIDTH, player4.paddleLength*2, WIDTH);
		g.setColor(Color.BLACK);
		//Thin narrow black lines are drawn to demarcate board and play area boundaries for the ball
		g.fillRect(0,0,PADDING,getHeight());
		g.fillRect(getWidth()-PADDING,0,PADDING,getHeight());
		g.fillRect(0,0,getWidth(),PADDING);
		g.fillRect(0,getHeight()-PADDING,getWidth(),PADDING);
		g.fillRect(PADDING+WIDTH,0,PADDING/2,getHeight());
		g.fillRect(getWidth()-3*PADDING/2-WIDTH,0,PADDING/2,getHeight());
		g.fillRect(0,PADDING+WIDTH,getWidth(),PADDING/2);
		g.fillRect(0,getHeight()-3*PADDING/2-WIDTH,getWidth(),PADDING/2);
		//DRAWING THE TEXT AREAS WITH THE CRUCIAL GAME INFO
		if(gameID ==1) g.drawString ("YOU : " + player1.points+" ", getWidth() / 10, getHeight()/2);
		else if(otherPlayer1.ballx[0] != -420)g.drawString (otherPlayer1.name + " : " + player1.points+" ", getWidth() / 10, getHeight()/2);
		else g.drawString ("Left AI Player", getWidth() / 10, getHeight()/2);
		
		if(gameID ==3) g.drawString ("YOU : " + player3.points+" ",  getWidth() /2 - 100 , 30);
		else if(otherPlayer3.ballx[0] != -420)g.drawString (otherPlayer3.name + " : " + player3.points+" ", getWidth() / 2 - 100 , 30);
		else g.drawString ("Top AI Player", getWidth() /2 - 50 , 30);
		
		if(gameID ==2) g.drawString ("YOU : " + player2.points+" ", 9 * getWidth() / 10 -100, getHeight()/2 );
		else if(otherPlayer2.ballx[0] != -420)g.drawString (otherPlayer2.name + " : " + player2.points+" ", 9 * getWidth() / 10 -100, getHeight()/2);
		else g.drawString ("Right AI Player", 9 * getWidth() / 10 -100, getHeight()/2);
		
		if(gameID ==4) g.drawString ("YOU : " + player4.points+" ",  getWidth() / 2 - 100  , getHeight() - 30);
		else if(otherPlayer4.ballx[0] != -420)g.drawString (otherPlayer4.name + " : " + player4.points+" ",  getWidth() / 2 - 100  , getHeight() - 30);
		else g.drawString ("Bottom AI Player",  getWidth() / 2 - 50  , getHeight() - 30);

		//Player Joins
		if(otherPlayer1.loadingBall[0]<100 &&  otherPlayer1.loadingBall[0]>0 && otherPlayer1.name!=null)
		{
				g.drawString (otherPlayer1.name + " has joined the game.", getWidth() / 2 - 50, getHeight()/2- 50);
		}
		if(otherPlayer2.loadingBall[0]<100 && otherPlayer2.loadingBall[0]>0 && otherPlayer2.name!=null)
		{
				g.drawString (otherPlayer2.name + " has joined the game.", getWidth() / 2 - 50, getHeight()/2 );
		}
		if(otherPlayer3.loadingBall[0]<100 && otherPlayer3.loadingBall[0]>0 && otherPlayer3.name!=null)
		{
				g.drawString (otherPlayer3.name + " has joined the game.", getWidth() / 2 - 50, getHeight()/2 +50 );
		}
		if(otherPlayer4.loadingBall[0]<100 && otherPlayer4.loadingBall[0]>0 && otherPlayer4.name!=null)
		{
				g.drawString (otherPlayer4.name + " has joined the game.", getWidth() / 2 - 50, getHeight()/2 +100);
		}
		//The draw UI parts for the images to be grabbed for the power ups
		if(myPowerUp.visible)
		{
			if(myPowerUp.type == 1 && !myPowerUp.flick)
			{
				if(gameID == 1){	g.drawImage(powerup1,0,myPowerUp.position-10,null);}
				else if(gameID == 2){	g.drawImage(powerup1,getWidth()-20,myPowerUp.position-10,null);}
				else if(gameID == 3){	g.drawImage(powerup1,myPowerUp.position-10,0,null);}
				else {	g.drawImage(powerup1,myPowerUp.position-10,getHeight()-20,null);}
			}
			else if(myPowerUp.type == 2 && !myPowerUp.flick)
			{
				if(gameID == 1){	g.drawImage(powerup2,0,myPowerUp.position-10,null);}
				else if(gameID == 2){	g.drawImage(powerup2,getWidth()-20,myPowerUp.position-10,null);}
				else if(gameID == 3){	g.drawImage(powerup2,myPowerUp.position-10,0,null);}
				else {	g.drawImage(powerup2,myPowerUp.position-10,getHeight()-20,null);}
			}
		}
		//Player Leaves
		if(byeBye[0]>1 && otherPlayer1.name != null)
		{
			byeBye[0]--;	
			g.drawString (otherPlayer1.name + " has left the game." , getWidth() / 2 - 100, getHeight()/2- 50);
		}
		if(byeBye[1]>1 && otherPlayer2.name != null)
		{
			byeBye[1]--;	
			g.drawString (otherPlayer2.name + " has left the game." , getWidth() / 2 - 100, getHeight()/2 );
		}
		if(byeBye[2]>1 && otherPlayer3.name != null)
		{
			byeBye[2]--;	
			g.drawString (otherPlayer3.name + " has left the game." , getWidth() / 2 - 100, getHeight()/2 +50 );
		}
		if(byeBye[3]>1 && otherPlayer4.name != null)
		{
			byeBye[3]--;	
			g.drawString (otherPlayer4.name + " has left the game." , getWidth() / 2 - 100, getHeight()/2 +100);
		}
		//INFO ABOUT THE LEFT USAGES FOR HITS AND SLOWS
		g.drawString("Hits : "+Integer.toString(hits)+ " Damps : "+Integer.toString(slow)+ " Grow : "+Integer.toString(expansions), getWidth()/2 - 70, 70);
		for(int r=0;r<N;r++)
		{
			//The color changes for hit/sticks and the semicircular balls
			//Essentially the entire rendering of balls
		if(hitting[r]){
			g.setColor(Color.GREEN);
			g.fillOval (ball_x[r] - RADIUS, ball_y[r] - RADIUS, RADIUS*2, RADIUS*2);
			for(int s=0;s<N;s++)
			{
				if(r!=s){
			g.setColor(Color.WHITE);
			g.fillArc (ball_x[s] - RADIUS, ball_y[s] - RADIUS, RADIUS*2, RADIUS*2,ball_angle[s],180);
			g.setColor(Color.RED);
			g.fillArc (ball_x[s] - RADIUS, ball_y[s] - RADIUS, RADIUS*2, RADIUS*2,ball_angle[s],-180);}
			}
		}
		else if(sticking[r]){
			g.setColor(Color.RED);
			g.fillOval (ball_x[r] - RADIUS, ball_y[r] - RADIUS, RADIUS*2, RADIUS*2);
			for(int s=0;s<N;s++)
			{
				if(r!=s){
			g.setColor(Color.WHITE);
			g.fillArc (ball_x[s] - RADIUS, ball_y[s] - RADIUS, RADIUS*2, RADIUS*2,ball_angle[s],180);
			g.setColor(Color.RED);
			g.fillArc (ball_x[s] - RADIUS, ball_y[s] - RADIUS, RADIUS*2, RADIUS*2,ball_angle[s],-180);}
			}
		}
		}
		//If any of the balls are in the hit/ stick/ any status
		boolean oner=false;
		for(int t=0;t<N;t++)
			oner=oner||hitting[t]||sticking[t];
		boolean onerhit=false;
		for(int t=0;t<N;t++)
			onerhit=onerhit||hitting[t];
		boolean onerstick=false;
		for(int t=0;t<N;t++)
			onerstick=onerstick||sticking[t];

		if(oner){
			//GREEN for hit and RED for stick
			if(onerhit)
		    {g.setColor(new Color(128,255,0));}
		    else if(onerstick)
		    {g.setColor(new Color(255,0,0));}
			//The rectangles are also colored according for the transient duration
			if(gameID == 1)
			{
				g.fillRect (PADDING, player1.position - player1.paddleLength, WIDTH, player1.paddleLength * 2);
			}
			else if(gameID ==2)
			{
				g.fillRect (getWidth() - PADDING - WIDTH, player2.position - player2.paddleLength, WIDTH, player2.paddleLength * 2);
			}
			else if(gameID ==3){
				g.fillRect (player3.position - player3.paddleLength, PADDING, player3.paddleLength*2, WIDTH);
			}
			else if(gameID == 4)
			{
				g.fillRect (player4.position - player4.paddleLength, getHeight() - PADDING - WIDTH, player4.paddleLength*2, WIDTH);
			}
		}
		else{
			//The half red half white balls
			for(int y=0;y<N;y++)
			{
			g.setColor(Color.WHITE);
			g.fillArc (ball_x[y] - RADIUS, ball_y[y] - RADIUS, RADIUS*2, RADIUS*2,ball_angle[y],180);
			g.setColor(Color.RED);
			g.fillArc (ball_x[y] - RADIUS, ball_y[y] - RADIUS, RADIUS*2, RADIUS*2,ball_angle[y],-180);
			}
		}  ////THE GENERAL BALL GRAPHICS SETTINGS GO HERE ..............
	}

//	To periodically match ball position over players
	private void synchronize(int k)
	{
		if(syncBall[k] < 5)         /// Sync time is 10*refresh time
		{
			syncBall[k]++;
		}
		else{
			syncBall[k] = 0;
			syncFromAll(k);
		}
	}
	//RESET the player configs
	private void resetPlayer(SendClass a){
		for(int j=0;j<N;j++)
		{
		a.ballx[j] = -420;
		a.bally[j] = -420;
		
		a.loadingBall[j] =0;
		a.forceUpdate[j] = 0;
		}
		a.paddleToggle = 0;
		if(a.name!=null && a.ballx[0] == -420 && byeBye[a.ID-1] ==0){byeBye[a.ID - 1] = 100;}
		
	}

	//PLAYER 1 update function
	//computation and movement of paddles and aim
	public void updatePlayer1(){
		if(gameID == 1)
		{
			computePosition (player1);
		}
		else
		{
			if((System.currentTimeMillis() - stamp1.timedLast) < PLAYER_WAIT)
			{
				byeBye[0]=0;
				player1.position = otherPlayer1.currentPlayer;
			}
			else if(ball_x_speed[0] < 0) {
				computePosition (player1);
				resetPlayer(otherPlayer1);}
		
			else {resetPlayer(otherPlayer1);}
		}
		// // Calculate the position of player one
		// if(player1.getType()!=Player.AGENT_Y && player1.getType()!=Player.AGENT_X){
		// if (player1.getType() == Player.MOUSE || player1.getType() == Player.KEYBOARD || ball_x_speed < 0)
			
		// }
	}
	//PLAYER 2 update function
	//computation and movement of paddles and aim
	public void updatePlayer2(){

		if(gameID == 2)
		{
			computePosition (player2);
		}
		else
		{
			if((System.currentTimeMillis() - stamp2.timedLast) < PLAYER_WAIT)
			{
				byeBye[1]=0;
				player2.position = otherPlayer2.currentPlayer;
			}
			else if(ball_x_speed[0] > 0)
			{	resetPlayer(otherPlayer2);
				computePosition (player2);
			}
			else{resetPlayer(otherPlayer2);}
		}	
	}
	//PLAYER 3 update function
	//computation and movement of paddles and aim
	public void updatePlayer3(){
		if(gameID == 3)
		{
			computePosition (player3);
		}
		else
		{
			if((System.currentTimeMillis() - stamp3.timedLast) < PLAYER_WAIT)
			{
				byeBye[2]=0;
				player3.position = otherPlayer3.currentPlayer;
			}
			else if(ball_y_speed[0] < 0)
			{
				computePosition (player3);
				resetPlayer(otherPlayer3);
			}
			else
			{
				resetPlayer(otherPlayer3);
			}
		}
	}
	//PLAYER 4 update function
	//computation and movement of paddles and aim
	public void updatePlayer4(){
		if(gameID == 4)
		{
			computePosition (player4);
		}
		else
		{
			if((System.currentTimeMillis() - stamp4.timedLast) < PLAYER_WAIT)
			{
				byeBye[3]=0;
				player4.position = otherPlayer4.currentPlayer;
			}
			else if(ball_y_speed[0] > 0){
				computePosition (player4);
				resetPlayer(otherPlayer4);
			}
			else
			{
				resetPlayer(otherPlayer4);
			}
		}
	}
	/*
	The function to check what hit/stick key is pressed and accordingly set the ball status of index f to true/false
	*/
	public void initiateForceHit(boolean horizontal, int f)
	{	
		if(horizontal)
		{
			if(key_A && !key_D && hits>0 && loadingBall[f]>60 ){
			hits--;
			ball_x_speed[f]*=1.5;
			//System.out.println("Hit");
			hitting[f] = true; forceUpdate[f] = 20;
			}
			else if(key_D && !key_A && slow>0){
				slow--;
				ball_x_speed[f]*=0.67;
				//System.out.println("Slow");
				sticking[f] = true; forceUpdate[f] = 20;				
			}
		}
		else
		{
			if(key_A && !key_D && hits>0 && loadingBall[f]>60){
			hits--;
			ball_y_speed[f]*=1.5;
			System.out.println("Hit");
			hitting[f] = true; forceUpdate[f] = 20;
			}
			else if(key_D && !key_A && slow>0){
				slow--;
				ball_y_speed[f]*=0.67;
				System.out.println("Slow");
				sticking[f] = true; forceUpdate[f] = 20;				
			}
			System.out.println(hitting[f]);
			System.out.println(sticking[f]);
		}
	}
	/*
	The function for paddle expansion
	Checks space pressed and sets up the expansion durtion
	Then toggles the length to double
	*/
	public void initiatePaddleExpansions()
	{
		if(key_space && expansions > 0)
		{
			if(paddleToggle == 0)
			{
				expansions--;
				toggleTime = System.currentTimeMillis();
			}
		}
	}
	public void updateBall(int g){
		// Routine update of the coordinate vis a vis speed
		if(loadingBall[g] > 50){
		ball_x[g] += ball_x_speed[g];
		ball_y[g] += ball_y_speed[g];

		if (ball_y_speed[g] < 0) // Hack to fix double-to-int conversion
			ball_y[g] ++;
		}
		//Rotation is added by spin
		ball_angle[g] += ball_rotation[g];
		// Acceleration handled here
		double ball__x_speed[] = new double[100];
		double ball__y_speed[] = new double[100];
		if (false) {
			ball_acceleration_count[0]++;
			if (ball_acceleration_count[0] == ACCELERATION) {
				ball__x_speed[0] = ball__x_speed[0] + (int)ball__x_speed[0] / Math.hypot ((int)ball__x_speed[0], (int)ball__y_speed[0]) * 2;
				ball__y_speed[0] = ball__y_speed[0] + (int)ball__y_speed[0] / Math.hypot ((int)ball__x_speed[0], (int)ball__y_speed[0]) * 2;
				ball_acceleration_count[0] = 0;
			}
		}
		/*
		Collision module for inter ball collision
		Checks for each pair
		*/
		for(int x=0;x<N;x++)
		{
		    for(int y=x+1;y<N;y++)
		    {
		if (Math.sqrt( (ball_x[x] - ball_x[y])*(ball_x[x] - ball_x[y])+(ball_y[x] - ball_y[y])*(ball_y[x] - ball_y[y])) <= 2.0*RADIUS )
        {
            double sep = Math.sqrt( (ball_x[x] - ball_x[y])*(ball_x[x] - ball_x[y])+(ball_y[x] - ball_y[y])*(ball_y[x] - ball_y[y]));
            //Computes distance. Checks if impinging occurs. Finds the collision angle sines/cosines. Calculates increments to position
            //To bring out of the clash area. Updates speed in accordance with the laws of physics.
            double sin = (ball_y[x] - ball_y[y])/(2.0 * RADIUS);
            double cos = (ball_x[x] - ball_x[y])/(2.0 * RADIUS);
            double incre_x = (ball_x[x] - ball_x[y])*(2.0 * RADIUS)/(1.0 * sep);
            double incre_y = (ball_y[x] - ball_y[y])*(2.0 * RADIUS)/(1.0 * sep);
            ball_x[x] = ball_x[y] + (int)incre_x;
            ball_y[x] = ball_y[y] + (int)incre_y;
            double ball_along_speed = ball_x_speed[x]*cos + ball_y_speed[x]*sin;
            double ball_perp_speed = ball_y_speed[x]*cos - ball_x_speed[x]*sin;
            double ballo_along_speed = ball_x_speed[y]*cos + ball_y_speed[x]*sin;
            double ballo_perp_speed = ball_y_speed[x]*cos - ball_x_speed[y]*sin;
            double inter = ballo_along_speed;
            //System.out.println("sin: "+sin+"\n"+"cos: "+cos+"\n"+"ball:: "+ball_along_speed+","+ball_perp_speed+"\n"+"ballo:: "+ballo_along_speed+","+ballo_perp_speed);
            ballo_along_speed = ball_along_speed;
            ball_along_speed = inter;
            /*ball_x_speed[x] = ball_along_speed*cos - ball_perp_speed*sin;
            ball_y_speed[x] = ball_perp_speed*cos + ball_along_speed*sin;
            ball_x_speed[y] = ballo_along_speed*cos - ballo_perp_speed*sin;
            ball_y_speed[y] = ballo_perp_speed*cos + ballo_along_speed*sin;*/
            double a = ball_x_speed[x];
            double b = ball_y_speed[x];
            ball_x_speed[x] = ball_x_speed[y];
            ball_y_speed[x] = ball_y_speed[y];
            ball_x_speed[y] = a;
            ball_y_speed[y] = b;
            //System.out.println("Now!\n"+"ball:: "+ball_along_speed+","+ball_perp_speed+"\n"+"ballo:: "+ballo_along_speed+","+ballo_perp_speed);
                if (player1.getType() == Player.CPU_HARD_X)
                    computeDestinationX (player1);    
                if (player2.getType() == Player.CPU_HARD_X)
                    computeDestinationX (player2);
                if (player3.getType() == Player.CPU_HARD_Y)
                    computeDestinationY (player3);
                if (player4.getType() == Player.CPU_HARD_Y)
                    computeDestinationY (player4);
        }}}
		// Border-collision LEFT
		if (ball_x[g] <= PADDING + WIDTH + RADIUS) {
			int collision_point = ball_y[g] + (int)(ball_y_speed[g] / ball_x_speed[g] * (PADDING + WIDTH + RADIUS - ball_x[g]));
			if (collision_point > player1.position - player1.paddleLength - TOLERANCE && 
			    collision_point < player1.position + player1.paddleLength + TOLERANCE) {
				if(loadingBall[g]>50){
					ball_x[g] = 2 * (PADDING + WIDTH + RADIUS) - ball_x[g];
					ball_x_speed[g] = Math.abs (ball_x_speed[g]);
					if(ball_y_speed[g]*ball_rotation[g] > 0.0)
						ball_rotation[g] -= ball_y_speed[g];
					else
						ball_rotation[g] += ball_y_speed[g];
					ball_y_speed[g] -= Math.sin ((double)(player1.position - ball_y[g]) / player1.paddleLength * Math.PI / 4)
					                * Math.hypot (ball_x_speed[g], ball_y_speed[g]) * 0.01;
				}
				if(gameID ==1)
				{
					
					initiateForceHit(false, g);
				}
				if(g==0){
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
				}
			}
			else {
				player1.points --;
				if(loadingBall[g]>50){
					ball_x_speed[g] = Math.abs (ball_x_speed[g]);
					if(ball_y_speed[g]*ball_rotation[g] > 0.0)
						ball_rotation[g] -= ball_y_speed[g];
					else
						ball_rotation[g] += ball_y_speed[g];
				}
				if(g==0){
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);             //To reflect the ball appropriately
				}
			}
		}
		
		// Border Collision RIGHT
		if (ball_x[g] >= getWidth() - PADDING - WIDTH - RADIUS) {
			int collision_point = ball_y[g] - (int)(ball_y_speed[g] / ball_x_speed[g] * (ball_x[g] - getWidth() + PADDING + WIDTH + RADIUS));
			if (collision_point > player2.position - player2.paddleLength - TOLERANCE && 
			    collision_point < player2.position + player2.paddleLength + TOLERANCE) {
				if(loadingBall[g] >50){
					ball_x[g] = 2 * (getWidth() - PADDING - WIDTH - RADIUS ) - ball_x[g];
					ball_x_speed[g] = -1 * Math.abs (ball_x_speed[g]);
					if(ball_y_speed[g]*ball_rotation[g] > 0.0)
						ball_rotation[g] -= ball_y_speed[g];
					else
						ball_rotation[g] += ball_y_speed[g];
					ball_y_speed[g] -= Math.sin ((double)(player2.position - ball_y[g]) / player2.paddleLength * Math.PI / 4)           //some sort of spin here
					                * Math.hypot (ball_x_speed[g], ball_y_speed[g]) * 0.01;
				}

				if(gameID ==2)
				{
					
					initiateForceHit(false, g);
				}
				if(g==0){
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);}
			}
			else {
				player2.points --;
				if(loadingBall[g]>50){
					ball_x_speed[g] = -1 * Math.abs (ball_x_speed[g]);
					ball_x_speed[g] = -1 * Math.abs (ball_x_speed[g]);
					if(ball_y_speed[g]*ball_rotation[g] > 0.0)
						ball_rotation[g] -= ball_y_speed[g];
					else
						ball_rotation[g] += ball_y_speed[g];
				}
				if(g==0){
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);	}          //To reflect the ball appropriately
			}
		}
		
		//Border collision top
		if (ball_y[g] <= PADDING + WIDTH + RADIUS) {
			int collision_point = ball_x[g] + (int)(ball_x_speed[g] / ball_y_speed[g] * (PADDING + WIDTH + RADIUS - ball_y[g]));
			if (collision_point > player3.position - player3.paddleLength - TOLERANCE && 
			    collision_point < player3.position + player3.paddleLength + TOLERANCE) {
				if(loadingBall[g] >50){
					ball_y[g] = 2 * (PADDING + WIDTH + RADIUS) - ball_y[g];
					ball_y_speed[g] = Math.abs (ball_y_speed[g]);
					if(ball_x_speed[g]*ball_rotation[g] > 0.0)
						ball_rotation[g] -= ball_x_speed[g];
					else
						ball_rotation[g] += ball_x_speed[g];
					ball_x_speed[g] -= Math.sin ((double)(player3.position - ball_x[g]) / player3.paddleLength * Math.PI / 4)
					                * Math.hypot (ball_x_speed[g], ball_y_speed[g]) * 0.01;
				}

				if(gameID ==3)
				{
					
					initiateForceHit(true, g);
				}
				if(g==0){
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);}
			}
			else {
				player3.points --;
				if(loadingBall[g] >50)
				{
					ball_y_speed[g] = Math.abs (ball_y_speed[g]);
					if(ball_x_speed[g]*ball_rotation[g] > 0.0)
						ball_rotation[g] -= ball_x_speed[g];
					else
						ball_rotation[g] += ball_x_speed[g];             //To reflect the ball appropriately
				}
				if(g==0){
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);     }     //To reflect the ball appropriately
			}
		}
		
		// Border-collision bottom
		if (ball_y[g] >= getHeight() - PADDING - WIDTH - RADIUS) {
			int collision_point = ball_x[g] - (int)(ball_x_speed[g] / ball_y_speed[g] * (ball_y[g] - getHeight() + PADDING + WIDTH + RADIUS));
			if (collision_point > player4.position - player3.paddleLength - TOLERANCE && 
			    collision_point < player4.position + player3.paddleLength + TOLERANCE) {
				if(loadingBall[g]>50)
				{
					ball_y[g] = 2 * (getHeight() - PADDING - WIDTH - RADIUS ) - ball_y[g];
					ball_y_speed[g] = -1 * Math.abs (ball_y_speed[g]);
					if(ball_x_speed[g]*ball_rotation[g] > 0.0)
						ball_rotation[g] -= ball_x_speed[g];
					else
						ball_rotation[g] += ball_x_speed[g];
					ball_x_speed[g] -= Math.sin ((double)(player4.position - ball_x[g]) / player3.paddleLength * Math.PI / 4)
					                * Math.hypot (ball_x_speed[g], ball_y_speed[g]) * 0.01;
				}

				if(gameID ==4)
				{
					
					initiateForceHit(true, g);
				}
				if(g==0){
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);}
			}
			else {
				player4.points --;
				if(loadingBall[g] >50){
					ball_y_speed[g] = -1 * Math.abs (ball_y_speed[g]);
					if(ball_x_speed[g]*ball_rotation[g] > 0.0)
						ball_rotation[g] -= ball_x_speed[g];
					else
						ball_rotation[g] += ball_x_speed[g];          //To reflect the ball appropriately
				}
				if(g==0){
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
				}
			}
		}

		// Border-collision TOP with radius more gap
		if (ball_y[g] <= RADIUS) {
			if(loadingBall[g]>50){
				ball_y_speed[g] = Math.abs (ball_y_speed[g]);
				ball_y[g] = 2 * RADIUS - ball_y[g];	
			}
			
		}
		
		// Border-collision BOTTOM with radius more gap
		if (ball_y[g] >= getHeight() - RADIUS) {
			if(loadingBall[g] >50)
			{
				ball_y_speed[g] = -1 * Math.abs (ball_y_speed[g]);
				ball_y[g] = 2 * (getHeight() - RADIUS) - ball_y[g];	
			}
		}
	}

	public void sendPlayer(){
		//Integer.toString(ball_x)+"~"+Integer.toString(ball_y)+"~"+Double.toString(ball_x_speed)+"~"+Double.toString(ball_y_speed)+"~"+Integer.toString(loadingBall)+"~"+Integer.toString(forceUpdate)
		tester1 = myPlayer.position;
		String strin = Integer.toString(gameID)+ " " + gameName + " " + Integer.toString(tester1)+"~"+Integer.toString(N);
		for(int y=0;y<N;y++)
		{
			strin=strin+"~"+Integer.toString(ball_x[y])+"~"+Integer.toString(ball_y[y])+"~"+Double.toString(ball_x_speed[y])+"~"+Double.toString(ball_y_speed[y])+"~"+Integer.toString(loadingBall[y])+"~"+Integer.toString(forceUpdate[y]);
		}
		strin=strin+"!"+Integer.toString(paddleToggle)+"~"+Integer.toString(paddleSpeedToggle)+"~"+Integer.toString(player1.points)+"~"+Integer.toString(player2.points)+"~"+Integer.toString(player3.points)+"~"+Integer.toString(player4.points)+"`"+Long.toString(System.currentTimeMillis());
		/*
		The part above makes up the appropriate string for transfering across classes. With the format:
		ID Name tester~N~x~y~vx~vy~...and other tilde separated attributes
		*/
		//Address
		try
		{
	 		DatagramPacket dp = new DatagramPacket(strin.getBytes(), strin.length(),group, multiCastPort);
	        //Send data
	        s.send(dp);
      	}
      	catch(IOException e){System.out.println("Kushagra");}
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
	//Setting up booleans if any key involved is pressed
	public void keyPressed (KeyEvent e) {
//		System.out.println ("Pressed "+e.getKeyCode()+"   "+KeyEvent.VK_UP+" "+KeyEvent.VK_DOWN);
		if (e.getKeyCode() == KeyEvent.VK_UP)
			key_up = true;
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			key_down = true;
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
			key_left = true;
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			key_right = true;
		else if (e.getKeyCode() == KeyEvent.VK_A)
			key_A = true;
		else if (e.getKeyCode() == KeyEvent.VK_D)
			key_D = true;
		else if (e.getKeyCode() == KeyEvent.VK_SPACE)
			key_space = true;
	}
	
	// Key released
	//Setting up booleans if any key involved is released
	public void keyReleased (KeyEvent e) {
//		System.out.println ("Released "+e.getKeyCode());
		if (e.getKeyCode() == KeyEvent.VK_UP)
			key_up = false;
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			key_down = false;
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
			key_left = false;
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			key_right = false;
		else if (e.getKeyCode() == KeyEvent.VK_A)
			key_A = false;
		else if (e.getKeyCode() == KeyEvent.VK_D)
			key_D = false;
		else if (e.getKeyCode() == KeyEvent.VK_SPACE)
			key_space = false;
	}
	
	// Key released
	public void keyTyped (KeyEvent e) {}
}
