//Nick Zheng
//Java Final Project
//Brick Break - Work in progress

//imports
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;

import sun.audio.*;

public class BrickBreak extends JPanel implements KeyListener, ActionListener, Runnable{
	//sound stuff, not working, still experimenting
	static AudioPlayer musicPlayer = AudioPlayer.player;
	AudioStream backgroundMusic;
	AudioData musicData;
	static ContinuousAudioDataStream loop = null;

	//ball stuff
	int ballX = 400;
	int ballY = 600;
	float speed = 1;
	Rectangle Ball = new Rectangle(ballX, ballY, 25, 25);
	
	//paddle stuff
	int paddleX = 400;
	int paddleY = 670;
	Rectangle Paddle = new Rectangle(paddleX, paddleY, 80, 15);

	//brick stuff
	int brickX = 145;
	int brickY = 60;
	Rectangle[] Bricks = new Rectangle[24];
	
	//key stuff
	static boolean left = false;
	static boolean right = false;
	static boolean space = false;
	
	//collision stuff
	int xDirection = -1;
	int yDirection = -1;
	boolean dead = false;
	boolean bricksDone = false;
	int count = 0;
	String status;
	
	Thread t; 
	BrickBreak(){
		addKeyListener(this);
		setFocusable(true);
		t = new Thread(this);
		t.start();
	}
	
	static JFrame frame = new JFrame();
	
	public void music(){
		try{
			backgroundMusic = new AudioStream(new FileInputStream("menu.wav"));
			musicData = backgroundMusic.getData();
			loop = new ContinuousAudioDataStream(musicData); 
		}
		catch(IOException error){
			System.out.print("file not found");
		}
	}
	
	public void paint(Graphics g){
		//render the blocks/paddle/ball
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 750);
		g.setColor(Color.WHITE);
		g.fillOval(Ball.x, Ball.y, Ball.width, Ball.height);
		g.setColor(Color.RED);
		g.fill3DRect(Paddle.x, Paddle.y, Paddle.width, Paddle.height, true);
		g.setColor(Color.GREEN);
		g.drawRect(0, 0, 793, 692);
		for(int i=0; i<Bricks.length; i++){
			if(Bricks[i] != null){
				g.fill3DRect(Bricks[i].x, Bricks[i].y, Bricks[i].width, Bricks[i].height, true);
			}
		}
		if(bricksDone == true || dead == true){
			Font f = new Font("Times New Roman", Font.BOLD, 20);
			g.setFont(f);
			g.drawString(status, 340, 325);
			dead = false;
			bricksDone = false;
		}
	}
	
	//screen shake
	//taken off from http://www.rgagnon.com/javadetails/java-0622.html
	//modified length value
	public static class FrameUtils {

		private final static int VIBRATION_LENGTH = 2;
		private final static int VIBRATION_VELOCITY = 5;
		  
		private FrameUtils() { }
		  
		public static void vibrate(Frame frame) { 
			try { 
			final int originalX = frame.getLocationOnScreen().x; 
		    final int originalY = frame.getLocationOnScreen().y; 
		    for(int i = 0; i < VIBRATION_LENGTH; i++) { 
		    	Thread.sleep(10); 
		        frame.setLocation(originalX, originalY + VIBRATION_VELOCITY); 
		        Thread.sleep(10); 
		        frame.setLocation(originalX, originalY - VIBRATION_VELOCITY);
		        Thread.sleep(10); 
		        frame.setLocation(originalX + VIBRATION_VELOCITY, originalY);
		        Thread.sleep(10); 
		        frame.setLocation(originalX, originalY); 
		      	} 
		   } 
		   catch (Exception err) { 
			   err.printStackTrace(); 
		   } 
		}
	}
	
	public static void main(String[] args) {
		//alright, on to the good stuff
		//the settings
		BrickBreak myGame = new BrickBreak();	
		JButton button = new JButton("Reset");
		frame.setSize(800, 750);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(myGame);
		frame.add(button, BorderLayout.SOUTH);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		button.addActionListener(myGame);
		//music doesnt work
		musicPlayer.start(loop);
	}
	
	public void actionPerformed(ActionEvent e){
		String str = e.getActionCommand();
		if (str.equals("Reset")) {
			this.reset();
		}
	}
	
	//pressing left and right keys
	public void keyPressed(KeyEvent e){
		int keyCode = e.getKeyCode();
		if(keyCode == KeyEvent.VK_RIGHT){
			right = true;
		}
		if(keyCode == KeyEvent.VK_LEFT){
			left = true;	
		}
		if(keyCode == KeyEvent.VK_SPACE){
			space = true;
		}
	}
	
	public void keyReleased(KeyEvent e){
		int keyCode = e.getKeyCode();
		if(keyCode == KeyEvent.VK_LEFT){
			left = false;
		}
		if(keyCode == KeyEvent.VK_RIGHT){
			right = false;
		}	
		if(keyCode == KeyEvent.VK_SPACE){
			space = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void run() {
		// create bricks
		for(int i=0; i<Bricks.length; i++){
			Bricks[i] = new Rectangle(brickX, brickY, 80, 30);
			if(i == 5){
				brickX = 60;
				brickY = 95;
			}
			if(i == 11){
				brickX = 60;
				brickY = 130;
			}
			if(i == 17){
				brickX = 60;
				brickY = 165;
			}
			if(i == 23){
				brickX = 60;
				brickY = 185;
			}
			brickX += 85;
		}
		//update game
		while(dead == false && bricksDone == false){
			for(int i=0; i<Bricks.length; i++){
				if(Bricks[i] != null){
					//brick and ball collision
					if(Bricks[i].intersects(Ball)){
						Bricks[i] = null;
						//x collision doesnt work too well
						//xDirection = -xDirection;
						yDirection = -yDirection;
						count ++;
						speed += 0.2;
						FrameUtils.vibrate(frame);
					}
				}
			}
			if(space == true){
				this.reset();
			}
			//if player hits all the bricks, player wins
			if(count == Bricks.length){
				bricksDone = true;
				status = "You win!";
				repaint();
			}
			repaint();
			//this is how the ball moves
			Ball.x += xDirection * speed;
			Ball.y += yDirection * speed;
			
			if(left == true){
				Paddle.x -= 5;
				right = false;
			}
			if(right == true){
				Paddle.x += 5;
				left = false;
			}
			//Paddle/Wall collision
			//X axis
			if(Paddle.x <= 5){
				Paddle.x = 5;
			}
			//Paddle/Wall collision
			//Y axis
			if(Paddle.x >= 710){
				Paddle.x = 710;
			}
			
			//Paddle/Ball collision
			if(Ball.intersects(Paddle)){
				yDirection = -yDirection;
				FrameUtils.vibrate(frame);
			}
			
			//Ball/Wall collision
			//x axis
			if(Ball.x <= 0 || Ball.x + Ball.height >= 792){
				xDirection = -xDirection;
			}
			//y axis
			if(Ball.y <= 0){
				yDirection = -yDirection;
			}
			
			//if ball falls below 680, player loses
			if(Ball.y >= 690){
				dead = true;
				status = "You lose...";
				repaint();
			}
			
			try{
				Thread.sleep(8);
			} 
			catch (Exception ex){
			}
		}
	}
	
	//reset
	//reset button isnt working yet when user wins/loses
	public void reset(){
		
		//after you click reset, it will focus back on the game, not the button
	//	requestFocus(true);
		
		//Ball
		ballX = 400;
		ballY = 600;
		speed = 1;
		
		//Paddle
		paddleX = 400;
		paddleY = 670;
		
		//Bricks
		brickX = 145;
		brickY = 60;
		
		Ball = new Rectangle(ballX, ballY, 25, 25);
		Paddle = new Rectangle(paddleX, paddleY, 80, 15);
		Bricks = new Rectangle[24];
		
		xDirection = -1;
		yDirection = -1;
		dead = false;
		bricksDone = false;
		count = 0;
		status = null;
		
		for(int i=0; i<Bricks.length; i++){
			Bricks[i] = new Rectangle(brickX, brickY, 80, 30);
			if(i == 5){
				brickX = 60;
				brickY = 95;
			}
			if(i == 11){
				brickX = 60;
				brickY = 130;
			}
			if(i == 17){
				brickX = 60;
				brickY = 165;
			}
			if(i == 23){
				brickX = 60;
				brickY = 185;
			}
			brickX += 85;
		}
		repaint();
	}
}
