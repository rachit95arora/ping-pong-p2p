import java.io.Serializable;
public class SendClass implements Serializable
{
	public String name;
	public int loadingBall =0;
	public int forceUpdate = 0;
	public int paddleToggle = 0;
	public int ID;
	public int ballx = -420, bally = -420;
	public Double ballx_speed,bally_speed;
	public int currentPlayer = 200;
	}