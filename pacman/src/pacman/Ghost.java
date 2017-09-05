package pacman;

import java.util.*;
import javax.imageio.*;
import java.awt.*;
import java.io.*;

public class Ghost extends Maze {
	/**
	 * All sprites for the 4 ghosts and 2 fireball ghosts
	 * Blinky - Red
	 * Inky - Blue
	 * Clyde - Orange
	 * Pinky - Pink
	 * Fireballs - are fireballs.
	 */
	private final Image blinky = ImageIO.read(new File("blinky.gif"));
	private final Image inky = ImageIO.read(new File("inky.gif"));
	private final Image clyde = ImageIO.read(new File("clyde.gif"));
	private final Image pinky = ImageIO.read(new File("pinky.gif"));
	private final Image fireballImage = ImageIO.read(new File("fireball.gif"));
	
	/**
	 * Name stores the name of each ghost
	 * Direction stores the current direction of movement for each ghost
	 * lastDirection is used to prevent ghosts from turning backwards
	 */
	private String name, direction, lastDirection;
	
	/**
	 * xPos and yPos store the current position of each ghost in the maze
	 */
	private int xPos, yPos;
	
	/**
	 * ways stores all possible directions that are free at an intersection
	 * We use an arraylist because we can add possible ways freely
	 */
	ArrayList<String> ways = new ArrayList<String>();
	
	/**
	 * Constructor for each ghost
	 * 
	 * @param x is the name of the ghost
	 * @param c is the starting position of the ghost
	 * @throws IOException
	 */
	public Ghost(String x, Coordinate c) throws IOException {
		name = x;
		xPos = c.getX();
		yPos = c.getY();
		lastDirection = "";
	}
	
	/**
	 * @return name of the ghost
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Method to return the sprite of a ghost
	 * 
	 * @param n is the name of the ghost who's image we want
	 * @return the corresponding image of the ghost
	 */
	public Image getImage(String n) {
		if(n.equals("b")) { // blinky is initialized as "b"
			return blinky;
		}
		if(n.equals("c")) { // clyde is initialized as "c"
			return clyde;
		}
		if(n.equals("i")) { // inky is initialized as "i"
			return inky;
		} 
		if(n.equals("p")) { // pinky is initialized as "p"
			return pinky;
		}
		return fireballImage; // if it's none of the 4 main ghosts, return fireball
		
	}
	
	/**
	 * @return the current x-position of the ghost
	 */
	public int getX() {
		return xPos;
	}
	
	/**
	 * @return the current y-position of the ghost
	 */
	public int getY() {
		return yPos;
	}
	
	/**
	 * Changes the current x-position of the ghost to be @param x
	 */
	public void setX(int x) {
		xPos = x;
	}
	
	/**
	 * Changes the current y-position of the ghost to be @param y
	 */
	public void setY(int y) {
		yPos = y;
	}
	
	/**
	 * @return the current x-position and y-position of the ghost as a Coordinate object
	 */
	public Coordinate pos() {
		return new Coordinate(xPos, yPos);
	}
	
	/**
	 * Method to determine which spaces in the grid around the ghost are not walls
	 * @return
	 */
	public ArrayList<String> isFree(){
		if (super.grid[xPos-1][yPos]!=0 && super.grid[xPos-1][yPos] != 4 && super.grid[xPos-1][yPos] != 3 && super.grid[xPos-1][yPos] != 8 && super.grid[xPos-1][yPos] != 7)
			ways.add("up");
		if (super.grid[xPos][yPos+1]!=0 && super.grid[xPos][yPos+1] != 4 && super.grid[xPos][yPos+1] != 3 && super.grid[xPos][yPos+1] != 8 && super.grid[xPos][yPos+1] != 7)
			ways.add("right");
		if (super.grid[xPos+1][yPos]!=0 && super.grid[xPos+1][yPos] != 4 && super.grid[xPos+1][yPos] != 3 && super.grid[xPos+1][yPos] != 8 && super.grid[xPos+1][yPos] != 7)
			ways.add("down");
		if (super.grid[xPos][yPos-1]!=0 && super.grid[xPos][yPos-1]!=4 && super.grid[xPos][yPos-1]!= 3  && super.grid[xPos][yPos-1]!=7 && super.grid[xPos][yPos-1]!=8)
			ways.add("left");
		
		
		return ways;
	}	
	
	/**
	 * Method that chooses a random direction from ways
	 * Sets direction to be a random direction from ways if ways is not empty
	 * @return the previous direction if it is in ways - makes ghost keep going forward
	 * otherwise @return the random direction
	 */
	public String chooseDirection(){
		int d = (int)(Math.random()*ways.size());
		if(ways.size() != 0) {
			direction = ways.get(d);
		}
		if(lastDirection != "" && (lastDirection.equals("up") && direction.equals("down")) || (lastDirection.equals("down") && direction.equals("up")) || 
				(lastDirection.equals("left") && direction.equals("right")) || (lastDirection.equals("right") && direction.equals("left"))) {
			if(ways.contains(lastDirection)) {
				direction = lastDirection;
			}
		} 
		return direction;
	}
	
	/**
	 * Method that calculates next position of the ghost
	 * Takes the next direction and swaps the next tile of that direction with a character
	 * Checks the name to determine which character to swap
	 */
	public void updateGhost(){
		String next;
		isFree();
		next = chooseDirection();
		lastDirection = next;
		if (next.equals("up")) {
			setX(xPos-1);
			int temp = super.grid[xPos][yPos];
			if (getName().equals("b")){
				super.grid[xPos][yPos] = 5;
			} else if (getName().equals("i")){
				super.grid[xPos][yPos] = 6;
			} else if (getName().equals("p")){
				super.grid[xPos][yPos] = 7;
			} else if(getName().equals("c")){
				super.grid[xPos][yPos] = 8;
			} else if(getName().equals("f1")) {
				super.grid[xPos][yPos] = 97;
			} else if(getName().equals("f2")) {
				super.grid[xPos][yPos] = 98;
			}
			super.grid[xPos+1][yPos] = temp;
		}
		if (next.equals("right")) {
			setY(yPos+1);
			int temp = super.grid[xPos][yPos];
			if (getName().equals("b")){
				super.grid[xPos][yPos] = 5;
			} else if (getName().equals("i")){
				super.grid[xPos][yPos] = 6;
			} else if (getName().equals("p")){
				super.grid[xPos][yPos] = 7;
			} else if(getName().equals("c")){
				super.grid[xPos][yPos] = 8;
			} else if(getName().equals("f1")) {
				super.grid[xPos][yPos] = 97;
			} else if(getName().equals("f2")) {
				super.grid[xPos][yPos] = 98;
			}
			super.grid[xPos][yPos-1] = temp;
		}
		if (next.equals("down")) {
			setX(xPos+1);
			int temp = super.grid[xPos][yPos];
			if (getName().equals("b")){
				super.grid[xPos][yPos] = 5;
			} else if (getName().equals("i")){
				super.grid[xPos][yPos] = 6;
			} else if (getName().equals("p")){
				super.grid[xPos][yPos] = 7;
			} else if(getName().equals("c")){
				super.grid[xPos][yPos] = 8;
			} else if(getName().equals("f1")) {
				super.grid[xPos][yPos] = 97;
			} else if(getName().equals("f2")) {
				super.grid[xPos][yPos] = 98;
			}
			super.grid[xPos-1][yPos] = temp;
		}
		if (next.equals("left")) {
			setY(yPos-1);
			int temp = super.grid[xPos][yPos];
			if (getName().equals("b")){
				super.grid[xPos][yPos] = 5;
			} else if (getName().equals("i")){
				super.grid[xPos][yPos] = 6;
			} else if (getName().equals("p")){
				super.grid[xPos][yPos] = 7;
			} else if(getName().equals("c")){
				super.grid[xPos][yPos] = 8;
			} else if(getName().equals("f1")) {
				super.grid[xPos][yPos] = 97;
			} else if(getName().equals("f2")) {
				super.grid[xPos][yPos] = 98;
			}
			super.grid[xPos][yPos+1] = temp;
		}
		ways.clear(); // clears ways for next run
	}
}
// mlg