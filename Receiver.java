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

public class Receiver implements Runnable {
 
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
	            	time.timedLast = System.currentTimeMillis();
	            	////String strin = Integer.toString(gameID)+ " " + gameName + " " + Integer.toString(tester1)+"~"+Integer.toString(ball_x)+"~"+Integer.toString(ball_y)+"~"+Double.toString(ball_x_speed)+"~"+Double.toString(ball_y_speed)+"~"+Integer.toString(loadingBall)+"~"Integer.toString(forceUpdate)+"!"+Integer.toString(paddleToggle)+"`"+Long.toString(System.currentTimeMillis());
	            	int timered = tmp.indexOf('`');
	            	
	            	if(true)
	            	{
	            		int ballmin = tmp.indexOf('~');
	            		int ballpass = tmp.indexOf('~');
	            		int ball0 = 1 + ballmin + tmp.substring(ballmin+1).indexOf('~');
	            		int N = Integer.parseInt(tmp.substring(ballmin+1,ball0));
	            		int ballz[][] = new int[100][6];
	            		for(int j=0;j<N;j++)
	            		{
	            			ballz[j][0] = 1 + ballpass + tmp.substring(ballpass+1).indexOf('~');
	            			ballz[j][1] = 1 + ballz[j][0] + tmp.substring(ballz[j][0]+1).indexOf('~');
		            		ballz[j][2] = 1 + ballz[j][1] + tmp.substring(ballz[j][1]+1).indexOf('~');
		            		ballz[j][3] = 1 + ballz[j][2] + tmp.substring(ballz[j][2]+1).indexOf('~');
			            	ballz[j][4] = 1 + ballz[j][3] + tmp.substring(ballz[j][3]+1).indexOf('~');
			            	ballz[j][5] = 1 + ballz[j][4] + tmp.substring(ballz[j][4]+1).indexOf('~');
			            	ballpass = ballz[j][5];
	            		}
		            	
		            	int paddleToggle = 1 + ballpass + tmp.substring(ballpass + 1).indexOf('!');
		            	int score1 = 1 + paddleToggle + tmp.substring(paddleToggle + 1).indexOf('~');
		            	int score2 = 1 + score1 + tmp.substring(score1 + 1).indexOf('~');
		            	int score3 = 1 + score2 + tmp.substring(score2 + 1).indexOf('~');
		            	int score4 = 1 + score3 + tmp.substring(score3 + 1).indexOf('~');
		            	other.currentPlayer = Integer.parseInt(tmp.substring(breaker+1,ballmin));
		            	other.ID = temp;
		            	for(int j=0;j<N;j++)
		            	{
		            		other.ballx[j] = Integer.parseInt(tmp.substring(ballz[j][0]+1,ballz[j][1]));
			            	other.bally[j] = Integer.parseInt(tmp.substring(ballz[j][1]+1,ballz[j][2]));
			            	other.ballx_speed[j] = Double.parseDouble(tmp.substring(ballz[j][2]+1,ballz[j][3]));
			            	other.bally_speed[j] = Double.parseDouble(tmp.substring(ballz[j][3]+1,ballz[j][4]));
		    	        	other.loadingBall[j] = Integer.parseInt(tmp.substring(ballz[j][4]+1, ballz[j][5]));
		    	        	if(j==N-1)
		    	        		other.forceUpdate[j] = Integer.parseInt(tmp.substring(ballz[j][5]+1, paddleToggle));
		    	        	else
		        	    		other.forceUpdate[j] = Integer.parseInt(tmp.substring(ballz[j][5]+1, ballz[j+1][0]));
		            	}
		            	
		            	other.paddleToggle = Integer.parseInt(tmp.substring(paddleToggle+1,score1));
		            	other.score1 = Integer.parseInt(tmp.substring(score1+1,score2));
		            	other.score2 = Integer.parseInt(tmp.substring(score2+1,score3));
		            	other.score3 = Integer.parseInt(tmp.substring(score3+1,score4));
		            	other.score4 = Integer.parseInt(tmp.substring(score4+1,timered));
		            	other.name = tmp.substring(firstBreak+1,breaker);
	            	}
	              }
	            } catch (Exception e) {
	            	e.printStackTrace();
	                System.out.println("No object could be read from the received UDP datagram.");
	            }
	 
	        }
    	}
    	catch(Exception e1){System.out.println("Handle this exception");}	
	}
}