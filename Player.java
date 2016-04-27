import javax.swing.JOptionPane;

public class Player {
	// Tipi di giocatore
	public static final int CPU_EASY_X = 0;
	public static final int CPU_HARD_X = 1;
	public static final int CPU_EASY_Y = 2;
	public static final int CPU_HARD_Y = 3;
	public static final int MOUSE = 4;
	public static final int KEYBOARD = 5;
	
	private int type;
	public int position = 0;
	public int destination = 0;
	public int points = 0;
	
	public Player (int type) {
		if (type < 0 || type > 5) {
			type = CPU_EASY_X;
			JOptionPane.showMessageDialog (null, "Some errors in player definition");
		}
		this.type = type;
	}
	
	public int getType () {
		return type;
	}
}