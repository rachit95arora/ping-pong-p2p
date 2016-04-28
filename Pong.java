import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Graphics;
import java.io.ByteArrayOutputStream;
import java.net.UnknownHostException;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

class Timestamp{
	public long timedLast;
}
class Receiver implements Runnable {
 
    /**
     * @param args the command line arguments
     */
    private Thread t; 
    private  String multiCastAddress;
    private int multiCastPort;
    private int bufferSize;;
    InetAddress group;
    int IDed;
    private int otherIDed;
    MulticastSocket s;
    DatagramPacket receivedPacket;
    int temp;
    Timestamp time;
    private SendClass other;
    //private String newer;
    //private String threadName;
    Receiver(String Addressmulti, int multiPORT, SendClass otherPlayer, int gameID, int otherID, Timestamp timest){
    	multiCastAddress = Addressmulti;
    	multiCastPort = multiPORT;
    	IDed = gameID;
    	otherIDed = otherID;
    	time = timest;
    	bufferSize =4096;
    	other = otherPlayer;
    }

    public void start ()
   {
      //System.out.println("Starting " +  threadName );
      if (t == null)
      {
         t = new Thread (this, "random");
         try{
         group = InetAddress.getByName(multiCastAddress);
	     s = new MulticastSocket(multiCastPort);
	     s.joinGroup(group);
         }
         catch (IOException aks){System.out.println("Na ho paya");}
         t.start ();
      }
   }

    public void run() {
        
        try{
	        
	 
	        //Receive data
	        while (true) {
	            //Wating for datagram to be received
	 
	            //Create buffer
	            byte[] buffer = new byte[bufferSize];
	            receivedPacket = new DatagramPacket(buffer, bufferSize, group, multiCastPort);
	            s.receive(receivedPacket);
	            //System.out.println("Datagram received!
	 
	            //Deserialize object
	            
	            try {
	            	
                    String tmp = new String(receivedPacket.getData(),0,receivedPacket.getLength());
	                int breaker = tmp.lastIndexOf(' ');
	                int firstBreak = tmp.indexOf(' ');
	                
	                temp = Integer.parseInt(tmp.substring(0,firstBreak));
	                if(temp ==otherIDed){
	            	//System.out.println("Receiving player "+Integer.toString(temp));
	            	time.timedLast = System.currentTimeMillis();
	            	//String strin = Integer.toString(gameID)+ " " + gameName + " " + Integer.toString(tester1)+"~"+Integer.toString(ball_x)+"~"+Integer.toString(ball_y)+"~"+Integer.toString(ball_x_speed)+"~"+Integer.toString(ball_y_speed);
	            	
	            	int ball0 = tmp.indexOf('~');
	            	int ball1 = 1 + ball0 + tmp.substring(ball0+1).indexOf('~');
	            	int ball2 = 1 + ball1 + tmp.substring(ball1+1).indexOf('~');
	            	int ball3 = 1 + ball2 + tmp.substring(ball2+1).indexOf('~');
	            	int ballLoad = 1+ ball3 + tmp.substring(ball3+1).indexOf('~');
	            	other.currentPlayer = Integer.parseInt(tmp.substring(breaker+1,ball0));
	            	other.ID = temp;
	            	other.ballx = Integer.parseInt(tmp.substring(ball0+1,ball1));
	            	other.bally = Integer.parseInt(tmp.substring(ball1+1,ball2));
	            	other.ballx_speed = Double.parseDouble(tmp.substring(ball2+1,ball3));
	            	other.bally_speed = Double.parseDouble(tmp.substring(ball3+1,ballLoad));
	            	other.loadingBall = Integer.parseInt(tmp.substring(ballLoad+1));
	            	other.name = tmp.substring(firstBreak+1,breaker);}
	            } catch (Exception e) {
	            	e.printStackTrace();
	                System.out.println("No object could be read from the received UDP datagram.");
	            }
	 
	        }
    	}
    	catch(Exception e1){System.out.println("Fuck!!");}	
	}
}

public class Pong extends JPanel implements ActionListener, MouseListener, KeyListener {
	
	private static final int RADIUS = 10; 
	private static final int START_SPEED = 20;
	private static final int ACCELERATION = 125;
	private int tester1;
	private int gameID;
	private String gameName;
	
	private static final int SPEED = 12;                    //changed values
	private static final int HEIGHT = 50;					//changed values
	private static final int WIDTH = 10;					//changed values
	private static final int TOLERANCE = 5;					//changed values
	private static final int PADDING = 3; 
	private SendClass otherPlayer1,otherPlayer2,otherPlayer3,otherPlayer4;
	private Timestamp stamp1,stamp2,stamp3,stamp4;
	private Player player1;
	private Player player2;
	private Player myPlayer;
	InetAddress group;
	private Player player3;
	private Player player4;
	private MulticastSocket s;
	private boolean new_game = true;
	private int loadingBall;
	private boolean presence = false;
	private String multiCastAddress = "228.6.7.8";
  	private int multiCastPort;
  	private int basePORT;
	private int ball_x;
	private int ball_y;
	private double ball_x_speed;
	private double ball_y_speed;
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private ObjectOutputStream oos;
	public boolean acceleration = false;
	private int ball_acceleration_count;
	private int syncBall;
	private boolean mouse_inside = false;
	private boolean key_up = false;
	private boolean key_down = false;
	private boolean key_left = false;
	private boolean key_right = false;
	
	// Constructor
	public Pong (int ID, boolean keyboard, int recdPort, String nameMe) {
		super ();
		gameName = nameMe;
		basePORT = recdPort;
		
		setBackground (new Color (70, 80, 70));
		gameID = ID;
		multiCastPort = recdPort+gameID;
		player1 = new Player (Player.CPU_HARD_X);
		player2 = new Player (Player.CPU_HARD_X);
		player3 = new Player (Player.CPU_HARD_Y);
		player4 = new Player (Player.CPU_HARD_Y);
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

	// Compute destination of the ball
	private void computeDestinationX (Player player) {
		int base;
		if (ball_x_speed > 0)
			player.destination = ball_y + (getWidth() - PADDING - WIDTH - RADIUS - ball_x) * (int)((ball_y_speed) / (ball_x_speed));
		else
			player.destination = ball_y - (ball_x - PADDING - WIDTH - RADIUS) * (int)((ball_y_speed) / (ball_x_speed));
		
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
			player.destination = ball_x + (getHeight() - PADDING - WIDTH - RADIUS - ball_y) * (int)((ball_x_speed) /(ball_y_speed));
		else
			player.destination = ball_x - (ball_y - PADDING - WIDTH - RADIUS) * (int)((ball_x_speed) /(ball_y_speed));
		
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
		else if (player.getType() == Player.AGENT_X || player.getType() == Player.CPU_HARD_X || player.getType()== Player.AGENT_Y || player.getType() == Player.CPU_HARD_Y) {
			movePlayer (player, player.destination);
		}
		// CPU EASY
		else if (player.getType() == Player.CPU_EASY_X || player.getType() == Player.CPU_EASY_Y) {
			movePlayer (player, ball_y);
		}
	}
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

	private boolean syncFromOthers()
		{
			int sumx = 0,sumy = 0;
	 		int count =0;
	 		Double suvx = 0.0;
	 		Double suvy = 0.0;
	 		if(otherPlayer1.loadingBall>50 && otherPlayer1.ballx != -420){
	 			sumx += otherPlayer1.ballx;
	 			sumy += otherPlayer1.bally;
	 			suvx +=otherPlayer1.ballx_speed;
	 			suvy +=otherPlayer1.bally_speed;
	 			count++;
	 		}
	 		if(otherPlayer2.loadingBall>50 && otherPlayer2.ballx != -420){
	 			sumx += otherPlayer2.ballx;
	 			sumy += otherPlayer2.bally;
	 			suvx +=otherPlayer2.ballx_speed;
	 			suvy +=otherPlayer2.bally_speed;
	 			count++;
	 		}
	 		if(otherPlayer3.loadingBall>50 && otherPlayer3.ballx != -420){
	 			sumx += otherPlayer3.ballx;
	 			sumy += otherPlayer3.bally;
	 			suvx +=otherPlayer3.ballx_speed;
	 			suvy +=otherPlayer3.bally_speed;
	 			count++;
	 		}
	 		if(otherPlayer4.loadingBall>50 && otherPlayer4.ballx != -420){
	 			sumx += otherPlayer4.ballx;
	 			sumy += otherPlayer4.bally;
	 			suvx +=otherPlayer4.ballx_speed;
	 			suvy +=otherPlayer4.bally_speed;
	 			count++;
	 		}
	 		if(count > 0){
	 			ball_x = sumx/count ;
	 			ball_y = sumy/count ;
	 			ball_x_speed = suvx / ((double)count);
	 			ball_y_speed = suvy / ((double)count);
	 			return true;
	 		}
	 		else
	 		{
	 			if(otherPlayer1.loadingBall>loadingBall && otherPlayer1.ballx != -420){
		 			sumx += otherPlayer1.ballx;
		 			sumy += otherPlayer1.bally;
		 			suvx +=otherPlayer1.ballx_speed;
		 			suvy +=otherPlayer1.bally_speed;
		 			count++;
		 		}
		 		if(otherPlayer2.loadingBall>loadingBall && otherPlayer2.ballx != -420){
		 			sumx += otherPlayer2.ballx;
		 			sumy += otherPlayer2.bally;
		 			suvx +=otherPlayer2.ballx_speed;
		 			suvy +=otherPlayer2.bally_speed;
		 			count++;
		 		}
		 		if(otherPlayer3.loadingBall>loadingBall && otherPlayer3.ballx != -420){
		 			sumx += otherPlayer3.ballx;
		 			sumy += otherPlayer3.bally;
		 			suvx +=otherPlayer3.ballx_speed;
		 			suvy +=otherPlayer3.bally_speed;
		 			count++;
		 		}
		 		if(otherPlayer4.loadingBall>loadingBall && otherPlayer4.ballx != -420){
		 			sumx += otherPlayer4.ballx;
		 			sumy += otherPlayer4.bally;
		 			suvx +=otherPlayer4.ballx_speed;
		 			suvy +=otherPlayer4.bally_speed;
		 			count++;
		 		}
		 		if(sumx > 0){
	 			ball_x = sumx/count ;
	 			ball_y = sumy/count ;
	 			ball_x_speed = suvx / ((double)count);
	 			ball_y_speed = suvy / ((double)count);
	 			return true;
	 		}
	 		}
	 		return false;
		}

		private boolean sameSign(double a, double b){
			if(Math.signum(a)*Math.signum(b)>=0.0)return true;
			return false;
		}
		private boolean syncFromAll()
		{
			int sumx = 0,sumy = 0;
	 		int count =0;
	 		Double suvx = 0.0;
	 		Double suvy = 0.0;
	 		if(otherPlayer1.loadingBall>50 && otherPlayer1.ballx != -420 && sameSign(otherPlayer1.ballx_speed,ball_x_speed) && sameSign(otherPlayer1.bally_speed,ball_y_speed))
	 		{
	 			sumx += otherPlayer1.ballx;
	 			sumy += otherPlayer1.bally;
	 			suvx +=otherPlayer1.ballx_speed;
	 			suvy +=otherPlayer1.bally_speed;
	 			count++;
	 		}
	 		if(otherPlayer2.loadingBall>50 && otherPlayer2.ballx != -420  && sameSign(otherPlayer2.ballx_speed,ball_x_speed) && sameSign(otherPlayer2.bally_speed,ball_y_speed)){
	 			sumx += otherPlayer2.ballx;
	 			sumy += otherPlayer2.bally;
	 			suvx +=otherPlayer2.ballx_speed;
	 			suvy +=otherPlayer2.bally_speed;
	 			count++;
	 		}
	 		if(otherPlayer3.loadingBall>50 && otherPlayer3.ballx != -420  && sameSign(otherPlayer3.ballx_speed,ball_x_speed) && sameSign(otherPlayer3.bally_speed,ball_y_speed)){
	 			sumx += otherPlayer3.ballx;
	 			sumy += otherPlayer3.bally;
	 			suvx +=otherPlayer3.ballx_speed;
	 			suvy +=otherPlayer3.bally_speed;
	 			count++;
	 		}
	 		if(otherPlayer4.loadingBall>50 && otherPlayer4.ballx != -420 && sameSign(otherPlayer4.ballx_speed,ball_x_speed) && sameSign(otherPlayer4.bally_speed,ball_y_speed)){
	 			sumx += otherPlayer4.ballx;
	 			sumy += otherPlayer4.bally;
	 			suvx +=otherPlayer4.ballx_speed;
	 			suvy +=otherPlayer4.bally_speed;
	 			count++;
	 		}
	 		if(sumx > 0){
	 			count++;
	 			ball_x = (sumx +ball_x)/count ;
	 			ball_y = (sumy +ball_y)/count ;
	 			ball_x_speed = (suvx + ball_x_speed) / ((double)count);
	 			ball_y_speed =  (suvy + ball_y_speed) / ((double)count);
	 			return true;
	 		}
	 		return false;
		}
	
	// Draw
	public void paintComponent (Graphics g) {
		Toolkit.getDefaultToolkit().sync();
		super.paintComponent (g);
		
		
		if (new_game) {
			startReceivers();
			try{
			Thread.sleep(1000);}
			catch(Exception e){System.out.println("Threads not invoked successfully!");}
			//Start receiving data
			loadingBall = 0;
			syncBall = 0;
			try{
		        group = InetAddress.getByName(multiCastAddress);
		        oos = new ObjectOutputStream(baos);
		        s = new MulticastSocket(multiCastPort);
		        s.joinGroup(group);
	 		}
	 		catch(IOException abc){System.out.println("Shit man");}
	 		//Setting up initial conditions of the ball using network or auto...
	 		if(syncFromOthers());
	 		else{
				ball_x = getWidth () / 2;
				ball_y = getHeight () / 2;
				
				double phase = (Math.round(Math.random()*4) * (Math.PI/2)) +(Math.random () * Math.PI / 4) + Math.PI / 8;
				ball_x_speed = (int)(Math.cos (phase) * START_SPEED);
				ball_y_speed = (int)(Math.sin (phase) * START_SPEED);
				
				ball_acceleration_count = 0;
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
			
			new_game = false;
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		//Loading Ball State
		if(loadingBall < 50){
			loadingBall++;
			syncFromOthers();
		}
		else if(loadingBall <100){
			loadingBall++;
		}
		//Sync Ball with live players
		if(loadingBall>70){
			synchronize();
		}
		updatePlayer1();
		updatePlayer2();
		updatePlayer3();
		updatePlayer4();


		updateBall();
		sendPlayer();

        g.setColor (Color.WHITE);
		g.fillRect (PADDING, player1.position - HEIGHT, WIDTH, HEIGHT * 2);
		g.fillRect (getWidth() - PADDING - WIDTH, player2.position - HEIGHT, WIDTH, HEIGHT * 2);
		g.fillRect (player3.position - HEIGHT, PADDING, HEIGHT*2, WIDTH);
		g.fillRect (player4.position - HEIGHT, getHeight() - PADDING - WIDTH, HEIGHT*2, WIDTH);
		g.fillOval (ball_x - RADIUS, ball_y - RADIUS, RADIUS*2, RADIUS*2);
		g.drawString (player1.points+" ", getWidth() / 2 - 20, 20);
		g.drawString (player2.points+" ", getWidth() / 2 + 20, 20);
	}

//	To periodically match ball position over players
	private void synchronize()
	{
		if(syncBall < 5)         /// Sync time is 10*refresh time
		{
			syncBall++;
		}
		else{
			syncBall = 0;
			syncFromAll();
		}
	}

	private void resetPlayer(SendClass a){
		a.ballx = -420;
		a.bally = -420;
		a.loadingBall =0;
	}


	public void updatePlayer1(){
		if(gameID == 1)
		{
			computePosition (player1);
		}
		else
		{
			if((System.currentTimeMillis() - stamp1.timedLast) < 5000)
			{
				player1.position = otherPlayer1.currentPlayer;
			}
			else if(ball_x_speed < 0) {
				computePosition (player1);
				resetPlayer(otherPlayer1);}
		
			else {resetPlayer(otherPlayer1);}
		}
		// // Calculate the position of player one
		// if(player1.getType()!=Player.AGENT_Y && player1.getType()!=Player.AGENT_X){
		// if (player1.getType() == Player.MOUSE || player1.getType() == Player.KEYBOARD || ball_x_speed < 0)
			
		// }
	}
	
	public void updatePlayer2(){

		if(gameID == 2)
		{
			computePosition (player2);
		}
		else
		{
			if((System.currentTimeMillis() - stamp2.timedLast) < 5000)
			{
				player2.position = otherPlayer2.currentPlayer;
			}
			else if(ball_x_speed > 0)
			{	resetPlayer(otherPlayer2);
				computePosition (player2);
			}
			else{resetPlayer(otherPlayer2);}
		}	
	}

	public void updatePlayer3(){
		if(gameID == 3)
		{
			computePosition (player3);
		}
		else
		{
			if((System.currentTimeMillis() - stamp3.timedLast) < 5000)
			{
				player3.position = otherPlayer3.currentPlayer;
			}
			else if(ball_y_speed < 0)
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

	public void updatePlayer4(){
		if(gameID == 4)
		{
			computePosition (player4);
		}
		else
		{
			if((System.currentTimeMillis() - stamp4.timedLast) < 5000)
			{
				player4.position = otherPlayer4.currentPlayer;
			}
			else if(ball_y_speed > 0){
				computePosition (player4);
				resetPlayer(otherPlayer4);
			}
			else
			{
				resetPlayer(otherPlayer4);
			}
		}
	}

	public void updateBall(){
		// Calcola la posizione della pallina
		if(loadingBall > 50){
		ball_x += ball_x_speed;
		ball_y += ball_y_speed;

		if (ball_y_speed < 0) // Hack to fix double-to-int conversion
			ball_y ++;
		}
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
				if(loadingBall>50){
					ball_x = 2 * (PADDING + WIDTH + RADIUS) - ball_x;
					ball_x_speed = Math.abs (ball_x_speed);
					ball_y_speed -= Math.sin ((double)(player1.position - ball_y) / HEIGHT * Math.PI / 4)
					                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;
				}
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
			}
			else {
				player1.points --;
				if(loadingBall>50){
					ball_x_speed = Math.abs (ball_x_speed);
				}
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);             //To reflect the ball appropriately
			}
		}
		
		// Border Collision RIGHT
		if (ball_x >= getWidth() - PADDING - WIDTH - RADIUS) {
			int collision_point = ball_y - (int)(ball_y_speed / ball_x_speed * (ball_x - getWidth() + PADDING + WIDTH + RADIUS));
			if (collision_point > player2.position - HEIGHT - TOLERANCE && 
			    collision_point < player2.position + HEIGHT + TOLERANCE) {
				if(loadingBall >50){
					ball_x = 2 * (getWidth() - PADDING - WIDTH - RADIUS ) - ball_x;
					ball_x_speed = -1 * Math.abs (ball_x_speed);
					ball_y_speed -= Math.sin ((double)(player2.position - ball_y) / HEIGHT * Math.PI / 4)           //some sort of spin here
					                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;
				}
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
			}
			else {
				player2.points --;
				if(loadingBall>50){
					ball_x_speed = -1 * Math.abs (ball_x_speed);
					ball_x_speed = -1 * Math.abs (ball_x_speed);
				}
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);	          //To reflect the ball appropriately
			}
		}
		

		if (ball_y <= PADDING + WIDTH + RADIUS) {
			int collision_point = ball_x + (int)(ball_x_speed / ball_y_speed * (PADDING + WIDTH + RADIUS - ball_y));
			if (collision_point > player3.position - HEIGHT - TOLERANCE && 
			    collision_point < player3.position + HEIGHT + TOLERANCE) {
				if(loadingBall >50){
					ball_y = 2 * (PADDING + WIDTH + RADIUS) - ball_y;
					ball_y_speed = Math.abs (ball_y_speed);
					ball_x_speed -= Math.sin ((double)(player3.position - ball_x) / HEIGHT * Math.PI / 4)
					                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;
				}
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
			}
			else {
				player3.points --;
				if(loadingBall >50)
				{
					ball_y_speed = Math.abs (ball_y_speed);             //To reflect the ball appropriately
				}
				if (player4.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player4);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);          //To reflect the ball appropriately
			}
		}
		
		// Border-collision RIGHT
		if (ball_y >= getHeight() - PADDING - WIDTH - RADIUS) {
			int collision_point = ball_x - (int)(ball_x_speed / ball_y_speed * (ball_y - getHeight() + PADDING + WIDTH + RADIUS));
			if (collision_point > player4.position - HEIGHT - TOLERANCE && 
			    collision_point < player4.position + HEIGHT + TOLERANCE) {
				if(loadingBall>50)
				{
					ball_y = 2 * (getHeight() - PADDING - WIDTH - RADIUS ) - ball_y;
					ball_y_speed = -1 * Math.abs (ball_y_speed);
					ball_x_speed -= Math.sin ((double)(player4.position - ball_x) / HEIGHT * Math.PI / 4)
					                * Math.hypot (ball_x_speed, ball_y_speed) * 0.01;
				}
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
			}
			else {
				player4.points --;
				if(loadingBall >50){
					ball_y_speed = -1 * Math.abs (ball_y_speed);          //To reflect the ball appropriately
				}
				if (player3.getType() == Player.CPU_HARD_Y)
					computeDestinationY (player3);
				if (player2.getType() == Player.CPU_HARD_X)
					computeDestinationX (player2);
				if (player1.getType() == Player.CPU_HARD_X)
					computeDestinationX (player1);
			}
		}

		// Border-collision TOP
		if (ball_y <= RADIUS) {
			if(loadingBall>50){
				ball_y_speed = Math.abs (ball_y_speed);
				ball_y = 2 * RADIUS - ball_y;	
			}
			
		}
		
		// Border-collision BOTTOM
		if (ball_y >= getHeight() - RADIUS) {
			if(loadingBall >50)
			{
				ball_y_speed = -1 * Math.abs (ball_y_speed);
				ball_y = 2 * (getHeight() - RADIUS) - ball_y;	
			}
		}
	}

	public void sendPlayer(){

		tester1 = myPlayer.position;
		String strin = Integer.toString(gameID)+ " " + gameName + " " + Integer.toString(tester1)+"~"+Integer.toString(ball_x)+"~"+Integer.toString(ball_y)+"~"+Double.toString(ball_x_speed)+"~"+Double.toString(ball_y_speed)+"~"+Integer.toString(loadingBall);

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
	}
	
	// Key released
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
	}
	
	// Key released
	public void keyTyped (KeyEvent e) {}
}
