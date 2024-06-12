package com.example.pong;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable{

	static final int SIRINA_IGRE = 1000;
	static final int VISINA_IGRE = (int)(SIRINA_IGRE * (0.5555));
	static final Dimension SCREEN_SIZE = new Dimension(SIRINA_IGRE,VISINA_IGRE);
	static final int PRECNIK_LOPTICE = 20;
	static final int SIRINA_REKETA = 25;
	static final int VISINA_REKETA = 100;
	Thread gameThread;
	Image image;
	Graphics graphics;
	Random random;
	Reket reket1;
	Reket reket2;
	Loptica loptica;
	Rezultat rezultat;
	
	GamePanel(){
		noviReket();
		novaLoptica();
		rezultat = new Rezultat(SIRINA_IGRE,VISINA_IGRE);
		this.setFocusable(true);
		this.addKeyListener(new AL());
		this.setPreferredSize(SCREEN_SIZE);
		
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void novaLoptica() {
		random = new Random();
		loptica = new Loptica((SIRINA_IGRE/2)-(PRECNIK_LOPTICE/2),random.nextInt(VISINA_IGRE-PRECNIK_LOPTICE),PRECNIK_LOPTICE,PRECNIK_LOPTICE);
	}
	public void noviReket() {
		reket1 = new Reket(0,(VISINA_IGRE/2)-(VISINA_IGRE/2),SIRINA_REKETA,VISINA_REKETA,1);
		reket2 = new Reket(SIRINA_IGRE-SIRINA_REKETA,(VISINA_IGRE/2)-(VISINA_REKETA/2),SIRINA_REKETA,VISINA_REKETA,2);
	}
	public void paint(Graphics g) {
		image = createImage(getWidth(),getHeight());
		graphics = image.getGraphics();
		draw(graphics);
		g.drawImage(image,0,0,this);
	}
	public void draw(Graphics g) {
		reket1.draw(g);
		reket2.draw(g);
		loptica.draw(g);
		rezultat.draw(g);
Toolkit.getDefaultToolkit().sync();

	}
	public void move() {
		reket1.move();
		reket2.move();
		loptica.move();
	}
	public void checkCollision() {
		
		//da loptica odskace o ivice (uglove)
		if(loptica.y <=0) {
			loptica.setYDirection(-loptica.yVelocity);
		}
		if(loptica.y >= VISINA_IGRE-PRECNIK_LOPTICE) {
			loptica.setYDirection(-loptica.yVelocity);
		}
		//za odskakanje loptica o rekete
		if(loptica.intersects(reket1)) {
			loptica.xVelocity = Math.abs(loptica.xVelocity);
			loptica.xVelocity++; //opcionalni deo, ubrzava lopticu pri udarcu o reket
			if(loptica.yVelocity>0)
				loptica.yVelocity++; //opcionalni deo, ubrzava lopticu
			else
				loptica.yVelocity--;
			loptica.setXDirection(loptica.xVelocity);
			loptica.setYDirection(loptica.yVelocity);
		}
		if(loptica.intersects(reket2)) {
			loptica.xVelocity = Math.abs(loptica.xVelocity);
			loptica.xVelocity++;
			if(loptica.yVelocity>0)
				loptica.yVelocity++; //oba dela ista kao prosli deo, samo za drugi reket
			else
				loptica.yVelocity--;
			loptica.setXDirection(-loptica.xVelocity);
			loptica.setYDirection(loptica.yVelocity);
		}
		//za zaustavljanje reketa na ivicama(da ne bi isli van sirine ekrana)
		if(reket1.y<=0)
			reket1.y=0;
		if(reket1.y >= (VISINA_IGRE-VISINA_REKETA))
			reket1.y = VISINA_IGRE-VISINA_REKETA;
		if(reket2.y<=0)
			reket2.y=0;
		if(reket2.y >= (VISINA_IGRE-VISINA_REKETA))
			reket2.y = VISINA_IGRE-VISINA_REKETA;
		//kada se da poen (gol?) rezultat se povecava za 1 i novi reketi se stvaraju
		if(loptica.x <=0) {
			rezultat.player2++;
			noviReket();
			novaLoptica();
			System.out.println("IGRAC 2: "+rezultat.player2);
		}
		if(loptica.x >= SIRINA_IGRE-PRECNIK_LOPTICE) {
			rezultat.player1++;
			noviReket();
			novaLoptica();
			System.out.println("IGRAC 1: "+rezultat.player1);
		}
	}
	public void run() {
		//petlja igre(da se igra ne ugasi cim se da poen, onda bi bila jako dosadna)
		long lastTime = System.nanoTime();
		double amountOfTicks =60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		while(true) {
			long now = System.nanoTime();
			delta += (now -lastTime)/ns;
			lastTime = now;
			if(delta >=1) {
				move();
				checkCollision();
				repaint();
				delta--;
			}
		}
	}
	public class AL extends KeyAdapter{
		public void keyPressed(KeyEvent e) {
			reket1.keyPressed(e);
			reket2.keyPressed(e);
		}
		public void keyReleased(KeyEvent e) {
			reket1.keyReleased(e);
			reket2.keyReleased(e);
		}
	}
}
