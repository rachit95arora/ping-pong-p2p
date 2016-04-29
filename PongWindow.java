
import javax.swing.JFrame;
import javax.swing.Timer;
class Ported{
	public int PORT;
}
public class PongWindow extends JFrame {
	public PongWindow () {
		super ();
		
		setTitle ("Pong");
		setSize (640, 640);
		String address  = "228.6.7.8";
		Ported mainPort = new Ported();
		mainPort.PORT = 4321;
		
		int gameIDtoJoin = seekNetwork(mainPort,address);
		Pong content = new Pong (gameIDtoJoin, true, mainPort.PORT, "Akshit",address);
		for(int k=0;k<100;k++)
		content.acceleration[k] = false;
		getContentPane().add (content);
		setLocationRelativeTo(null);
		addMouseListener (content);
		addKeyListener (content);
		
		Timer timer = new Timer (20, content);
		timer.start ();
	}

	public int seekNetwork(Ported basePORT, String multiCastAddress)
	{
		SendClass otherPlayer1 = new SendClass();
		SendClass otherPlayer2 = new SendClass();
		SendClass otherPlayer3 = new SendClass();
		SendClass otherPlayer4 = new SendClass();
		Timestamp stamp1 = new Timestamp();
		Timestamp stamp2 = new Timestamp();
		Timestamp stamp3 = new Timestamp();
		Timestamp stamp4 = new Timestamp();
		Receiver receiveThread1 = new Receiver(multiCastAddress,basePORT.PORT + 1, otherPlayer1, 0 , 1 , stamp1);
		Receiver receiveThread2 = new Receiver(multiCastAddress,basePORT.PORT + 2, otherPlayer2, 0 , 2 , stamp2);
		Receiver receiveThread3 = new Receiver(multiCastAddress,basePORT.PORT + 3, otherPlayer3, 0 , 3 , stamp3);
		Receiver receiveThread4 = new Receiver(multiCastAddress,basePORT.PORT + 4, otherPlayer4, 0 , 4 , stamp4);
		receiveThread1.start();
		receiveThread2.start();
		receiveThread3.start();
		receiveThread4.start();
		try{Thread.sleep(1000);}
			catch(Exception e){System.out.println("Threads not sleeping successfully!");}
		if(otherPlayer1.ballx[0] == -420){
			return 1;
		}
		else if(otherPlayer2.ballx[0] == -420){
			return 2;
		}
		else if(otherPlayer3.ballx[0] == -420){
			return 3;
		}			
		else if(otherPlayer4.ballx[0] == -420){
			return 4;
		}
		else{
			basePORT.PORT+=5;
			seekNetwork(basePORT,multiCastAddress);
		}
		basePORT.PORT = basePORT.PORT + 30;
		return 1;
	}
}