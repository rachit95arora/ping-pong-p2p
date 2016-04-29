public class Powerup
{
	public int type = 0;  // 1 for flash, 2 for downsize.
	public int countdown =0;
	public static int TRANSIT = 10;
	public int transition =0;
	public int position = 0;
	public int ID = 0;
	public boolean flick = false;
	public boolean visible = false;
	Powerup(){
		//Plain Constructor.
	}
	Powerup(int T, int count, int pos, int IDe, boolean vis)
	{
		type = T;
		countdown = 1000;
		position = pos;
		ID = IDe;
		visible = vis;
	}
}