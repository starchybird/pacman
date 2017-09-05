package pacman;

/**
 * Coordinate object stores current position of a character
 */
public class Coordinate {
	
	/**
	 * Current x-position and y-position of a character
	 */
	private int x, y;
	
	/**
	 * Constructor for Coordinate objects
	 * 
	 * x-position becomes @param r
	 * y-position becomes @param c
	 */
	public Coordinate(int r, int c) {
		x = r;
		y = c;
	}
	
	/**
	 * @return current x-position
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * @return current y-position
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Method to change current x-position
	 * x-position becomes @param a
	 */
	public void setX(int a) {
		x = a;
	}
	
	/**
	 * Method to change current y-position
	 * y-position becomes @param a
	 */
	public void setY(int a) {
		y = a;
	}
}
