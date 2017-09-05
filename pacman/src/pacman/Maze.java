package pacman;

import java.util.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.imageio.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Maze extends JPanel implements KeyListener, MouseListener {
	/**
	 *  The jFrame.
	 */
	private static JFrame jf;
	
	/**
	 * pUp/pDown/pLeft/pRight stores if a direction is possible for pacman to go to.
	 * up/down/left/right stores pacman's current direction of movement.
	 * beginning stores if the game has just started.
	 * memes stores if xXx_MLG_n0sc0p3_i11umina7i_xXx mode is activated.
	 */	
	private boolean pUp, pDown, pLeft, pRight, up, down, left, right, beginning;
	
	/**
	 *  Booleans to track when game ends by death or by winning.
	 */
	private static boolean finished, win, memes;
	
	/**
	 *  Time delay for blinking.
	 */
	private static long blinkTime; 
	
	/**
	 * We store the maze as a matrix that reads from a text file.
	 */
	public int[][] grid = new int[23][19];
	
	/**
	 *  Array that stores which key is being pressed and determines direction by setting corresponding element to true.
	 */
	private boolean[] keys = {false, false, false, false}; 
	
	/**
	 * A delay for repainting to have synced movements for characters.
	 * Greatly simplifies testing positions.
	 */
	private static final long delay = 167; 
	
	/**
	 * dots stores how many dots there are in total.
	 * eatenDots stores how many dots have been eaten - we use this to determine if the game is finished.
	 * lastDirection stores the previous direction pacman was facing as an int - we use this to draw him facing the right way.
	 */
	protected int dots, eatenDots, score, lastDirection; 
	
	/**
	 * Images for the apache and illuminati
	 */
	protected final Image apache = ImageIO.read(new File("apache.png"));
	protected final Image meme = ImageIO.read(new File("illuminati.png"));
	// private AudioInputStream istream; <- what is this?
	/**
	 * Coordinate objects:
	 * jump1 and jump2 are the teleporters from the left to the right
	 * blinky, clyde, inky, pinky, pac, fireball1, and fireball2 store the position of their respective characters
	 */
	protected Coordinate jump1, jump2, blinky, clyde, inky, pinky, pac, fireball1, fireball2;
	
	/**
	 *  Ghosts
	 */
	private Ghost blinkyGhost, clydeGhost, inkyGhost, pinkyGhost, fireballGhost1, fireballGhost2;
	/** 
	 * Generates a random color for the maze walls each time the game is started.
	 */
	private Color random = new Color((int) (Math.random()*256), (int) (Math.random()*256), (int) (Math.random()*256));
	
	public Maze() throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader("maze.txt")); //scans self-made 23x19 maze of 0-9
		for(int i = 0; i < grid.length; i++) {
			String[] line = bf.readLine().split(""); //splits lines of the maze into individual string numbers
			for(int j = 0; j < grid[i].length; j++) {
				if(line[j].equals("a")) {
					grid[i][j] = 97; //ascii value of a
				} else if(line[j].equals("b")) {
					grid[i][j] = 98;
				} else {
					grid[i][j] = Integer.parseInt(line[j]); //parses string ints to int ints lol.	
				}
			}

		}
		setSize(570,690); // each tile is 30 pixels by 30 pixels
		setVisible(true);
		dots = eatenDots = score = lastDirection = 0; // initializes variables
		up = down = left = right = pLeft = pRight = pUp = pDown = false;
		beginning = true;
	}
	
	@Override
	/**
	 * Draws everything onto the JPanel.
	 * 
	 * From the text file:
	 * 0 = wall
	 * 1 = dot
	 * 2 = black space
	 * 3 = apache
	 * 4 = teleport
	 * 5 = blinky
	 * 6 = inky
	 * 7 = pinky
	 * 8 = clyde
	 * 9 = pacman
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int fontSize = 32;
		/**
		 * Calculates score as the # of dots eaten x10 and draws it on the screen.
		 */
		score = eatenDots*10;
		g2.setFont(new Font("Courier", Font.PLAIN, fontSize));
		g2.setColor(Color.BLACK); 
		g2.drawString(String.valueOf(score), 10, 55);
		/**
		 * "meme it up" button that triggers the illuminati.
		 */
		g.setColor(Color.red);
		g.fillRect(400, 20, 75, 30);
		g.setColor(Color.BLACK);
		g2.setFont(new Font("Courier", Font.PLAIN, 9));
		g2.drawString("meme it up", 410, 35);
		/**
		 * Draws the maze and pacman.
		 */
		drawMaze(g);
		drawPacman(g);
		/**
		 * Attempts to draw ghosts.
		 * If the game has just started, ghosts are initialized
		 * and beginning is set to false.
		 */
		try {
			if(beginning) {
				initializeGhosts();
				beginning = false;
			}
			drawGhost(g, blinkyGhost);
			drawGhost(g, clydeGhost);
			drawGhost(g, inkyGhost);
			drawGhost(g, pinkyGhost);
			drawGhost(g, fireballGhost1);
			drawGhost(g, fireballGhost2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/**
		 * If you die, you get a "Game Over!"
		 * Else if you win, you get a "You Won!"
		 * The program stops either way.
		 */
		if(finished) {
			g.setColor(Color.BLACK);
			g2.setFont(new Font("Courier", Font.PLAIN, fontSize));
			g2.drawString("GAME OVER!", 190, 320);
		} else if(win) {
			g.setColor(Color.BLACK);
			g2.setFont(new Font("Courier", Font.PLAIN, fontSize));
			g2.drawString("You WON!", 190, 320);
			finished = true;
		}
	}
	
	/**
	 * Method that creates all ghost objects with a name and a Coordinate
	 * 
	 * @throws IOException
	 */
	public void initializeGhosts() throws IOException {
		/**
		 * Method that create all ghosts with name and Coordinate position.
		 */
		blinkyGhost = new Ghost("b", blinky);
		clydeGhost = new Ghost("c", clyde);
		inkyGhost = new Ghost("i",inky);
		pinkyGhost = new Ghost("p", pinky);
		fireballGhost1 = new Ghost("f1", fireball1);
		fireballGhost2 = new Ghost("f2", fireball2);
	}
	
	/**
	 * The method that creates the maze and stores the coordinates of characters.
	 */
	public void drawMaze(Graphics g) {
		for(int i = 0; i < grid.length; i++) { 
			for(int j = 0; j < grid[i].length;j++) {
				if(grid[i][j] == 0) { // Creates walls
					g.setColor(random);
					g.fillRect(j*30, i*30, 30, 30);
				} else if(grid[i][j] == 1) { // Creates dots
					if(System.nanoTime() >= blinkTime+69696) {
						blinkTime = System.nanoTime();
					} else if(System.nanoTime() >= blinkTime+334) {
						if(memes) {
							g.drawImage(meme, j*30, i*30, j*30+30, i*30+30, 0, 0, 768, 768, null);
						} else {
							g.setColor(Color.ORANGE);
							g.fillOval(j*30+8,i*30+8,10,10);
						}
					}
					++dots;
				} else if(grid[i][j] == 3) {
					g.drawImage(apache, j*30, i*30, j*30+30, i*30+30, 0, 0, 1000, 666, null); //auto scales image to the position from original size of 64x64
					
					
				} else if(grid[i][j] == 4) {
					jump1 = new Coordinate(i, 0);
					if(jump1 != null) jump2 = new Coordinate(i, j);
				} else if(grid[i][j] == 9) {
					pac = new Coordinate(i, j);
				} else if(grid[i][j] == 5) {
					blinky = new Coordinate (i, j);
				} else if(grid[i][j] == 6) {
					inky = new Coordinate(i, j);
				} else if(grid[i][j] == 7) {
					pinky = new Coordinate(i, j);
				} else if(grid[i][j] == 8) {
					clyde = new Coordinate(i, j);
				} else if(grid[i][j] == 97) {
					fireball1 = new Coordinate(i,j);
				} else if(grid[i][j] == 98) {
					fireball2 = new Coordinate(i,j);
				}
			}
		}
		
	}
	
	/**
	 * Method that draws pacman to face a certain direction after moving.
	 */
	public void drawPacman(Graphics g) {
		 g.setColor(Color.yellow);
		 pacmanCheckDirection(pac);
		 update();
		 if(right || lastDirection == 0) { //draws for the direction and if stationary, draws in the direction it was last moved
			 g.fillArc(pac.getY()*30, pac.getX()*30,30,30,30,300);
			 lastDirection = 0;
		 } 
		 if(left|| lastDirection == 1) {
			 g.fillArc(pac.getY()*30, pac.getX()*30,30,30,210,300);
			 lastDirection = 1;
		 }
		 if (down|| lastDirection == 2) {
			 g.fillArc(pac.getY()*30, pac.getX()*30,30,30,300,300);
			 lastDirection = 2;
		 }
		 if(up || lastDirection == 3) {
			 g.fillArc(pac.getY()*30, pac.getX()*30,30,30,120,300);
			 lastDirection = 3;
		 }
		
	}
	
	/**
	 * Method that draws a ghost by checking the name and using the corresponding sprite.
	 * Also checks if a ghost and pacman occupy the same tile - if they do, it's game over.
	 * 
	 * @param gh is the ghost that is drawn.
	 */
	public void drawGhost(Graphics g, Ghost gh) {
		gh.updateGhost();
		if(gh.getName().equals("f1") || gh.getName().equals("f2")) {
			g.drawImage(gh.getImage(gh.getName()),gh.getY()*30, gh.getX()*30, gh.getY()*30+30, gh.getX()*30+30, 0, 0, 160,160, null);
		} else {
			g.drawImage(gh.getImage(gh.getName()),gh.getY()*30, gh.getX()*30, gh.getY()*30+30, gh.getX()*30+30, 0, 0, 25,25, null);
		}
		if(Math.abs(gh.getX()*30-pac.getX()*30) < 30 && Math.abs(gh.getY()*30-pac.getY()*30) < 30) {
			finished = true;
		}
		
	}
	
	/**
	 * Method that interprets key presses and checks the adjacent tiles around pacman
	 * to find which tiles are empty, and thus are possible to move to.
	 * 
	 * @param c is the current Coordinate for pacman's position.
	 */
	public void pacmanCheckDirection(Coordinate c) { //x is ROW y is COLUMN (or x-axis)
		int x = c.getX();
		int y = c.getY();
		if(grid[x][y+1] != 0) {
			pRight = true;
		} else{
			pRight = false;
		}
		if(grid[x][y-1] != 0) {
			pLeft = true;
		} else {
			pLeft = false;
		}
		if(grid[x-1][y] != 0) {
			pUp = true;
		} else {
			pUp = false;
		}
		if(grid[x+1][y] != 0) {
			pDown = true;
		}  else {
			pDown = false;
		}
		
	}
	
	@Override
	/**
	 * Method that changes the keys array based on which keys are being pressed.
	 */
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			keys[0] = true;
		} else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			keys[1] = true;
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			keys[2] = true;
		} else if(e.getKeyCode() == KeyEvent.VK_UP) {
			keys[3] = true;
		}
		
	}
	
	/**
	 * Method that updates pacman's position.
	 * Tests for if a key is being pressed and if a direction is possible
	 * If it is, update pacman's position to be 1 tile in that direction.
	 * Also tests if pacman's next tile is a dot, blank tile, or teleport and acts accordingly.
	 */
	public void update() {
		if(keys[0]) {
			if(pLeft) { //checks if possible, add helper method to check if possible
				pac.setY(pac.getY()-1);
				left = true;
				//if pos = 1, add dot number and swap with blank pos 2
				if(grid[pac.getX()][pac.getY()] == 1) { //movement on dotted tile
					eatenDots++;
					grid[pac.getX()][pac.getY()] = 9;
					grid[pac.getX()][pac.getY()+1] = 2;
				} else if(grid[pac.getX()][pac.getY()] == 2) { //movement on blank tile
					grid[pac.getX()][pac.getY()] = 9;
					grid[pac.getX()][pac.getY()+1] = 2;
				} else if(grid[pac.getX()][pac.getY()] == 4) { //teleport
					if(pac.getY() == jump1.getY()) {
						pac.setY(jump2.getY()-1);
						grid[pac.getX()][pac.getY()] = 9;
					}
				} else if(grid[pac.getX()][pac.getY()] == 3) {
					grid[pac.getX()][pac.getY()+1] = 2;
				}
			} else {
				left = false;
			}
		} else if(keys[1]) {
			if(pRight) {
				pac.setY(pac.getY()+1);
				right = true;
				if(grid[pac.getX()][pac.getY()] == 1) {
					eatenDots++;
					grid[pac.getX()][pac.getY()] = 9;
					grid[pac.getX()][pac.getY()-1] = 2;
				} else if(grid[pac.getX()][pac.getY()] == 2) {
					grid[pac.getX()][pac.getY()] = 9;
					grid[pac.getX()][pac.getY()-1] = 2;
				}
				else if(grid[pac.getX()][pac.getY()] == 4) { //teleport
					if(pac.getY() == jump2.getY()) {
						pac.setY(jump1.getY()+1);
						grid[pac.getX()][pac.getY()] = 9;
					}
				} else if(grid[pac.getX()][pac.getY()] == 3) {
					grid[pac.getX()][pac.getY()-1] = 2;
				}
			} else {
				right = false;
			}
		} else if(keys[2]) {
			if(pDown) { 
				pac.setX(pac.getX()+1);
				down = true;
				//if pos = 1, add dot number and swap with blank pos 2
				if(grid[pac.getX()][pac.getY()] == 1) {
					eatenDots++;
					grid[pac.getX()][pac.getY()] = 9;
					grid[pac.getX()-1][pac.getY()] = 2;
				} else if(grid[pac.getX()][pac.getY()] == 2) {
					grid[pac.getX()][pac.getY()] = 9;
					grid[pac.getX()-1][pac.getY()] = 2;
				} else if(grid[pac.getX()][pac.getY()] == 3) {
					
					grid[pac.getX()-1][pac.getY()] = 2;
				}
			} else {
				down = false;
			}
			
		} else if(keys[3]) {
			if(pUp) { //checks if possible, add helper method to check if possible
				pac.setX(pac.getX()-1);
				up = true;
				//if pos = 1, add dot number and swap with blank pos 2
				if(grid[pac.getX()][pac.getY()] == 1) {
					eatenDots++;
					grid[pac.getX()][pac.getY()] = 9;
					grid[pac.getX()+1][pac.getY()] = 2;
				} else if(grid[pac.getX()][pac.getY()] == 2) {
					grid[pac.getX()][pac.getY()] = 9;
					grid[pac.getX()+1][pac.getY()] = 2;
				} else if(grid[pac.getX()][pac.getY()] == 3) {
					
					grid[pac.getX()+1][pac.getY()] = 2;
				}
			} else {
				up = false;
			}
		}
	}
	
	
	
	/**
	 * These methods resets the key pressed direction to be false and the direction 
	 * pacman is going to all be false so that pacman doesn't move. 
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		keys[0] =keys[1] = keys[2] = keys[3] = left = right = up = down = false; 
	}
	
	/**
	 * We don't use key typing for anything, so we leave the method blank.
	 */
	@Override
	public void keyTyped(KeyEvent e) {}

	/**
	 * The main class that initializes the JFrame, sets it's settings, plays music, and repaints while not finished.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		Maze m = new Maze();
		jf = new JFrame(); //jframe initialization
		jf.setSize(570, 740);
		jf.setVisible(true);
		jf.add(m);
		m.setBackground(Color.BLACK);
		jf.setResizable(false);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setFocusable(true);
		jf.addKeyListener(m);
		jf.addMouseListener(m);
		
		finished = false;
		playMusic();
		while(!finished) {
			try {
				Thread.sleep(delay);
				blinkTime = System.nanoTime(); 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			  jf.repaint();
			   //m.setBackground(new Color((int) (Math.random()*256), (int) (Math.random()*256), (int) (Math.random()*256))); //MEMES LOL
			if(m.eatenDots == 145) {
				win = true;
			}
		}
		if(finished) {
			jf.repaint();
			
		}
		
	}
	
	/**
	 * Method that plays the music.
	 * It reads an .wav file named "music" and makes a clip from the entire file
	 * then it loops it endlessly until the game is finished
	 */
	public static void playMusic() {
	    try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("music.wav").getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	        if(!finished) {
	        	clip.loop(Clip.LOOP_CONTINUOUSLY);
	        } else {
	        	clip.close();
	        }
	    } catch(Exception ex) {
	        System.out.println("Ya got MEMED");
	        ex.printStackTrace();
	    }
	}
	/**
	 * Method for mouseListener that is used for the memes button
	 * Checks if the mouse is clicked in a certain box
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getXOnScreen() >= 400 && e.getXOnScreen() <= 475 && e.getYOnScreen() >= 45 && e.getYOnScreen() <= 75) {
			if(!memes) {
				memes = true;
			} else {
				memes = false;
			}
		}
	}
	
	/**
	 * We don't use the mouse for anything else, so the other mouseListener methods are empty
	 */
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}
// 1337