package core;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable,MouseListener,MouseMotionListener {
	
	private static final long serialVersionUID = 1L;
	public static int T = 16, W = 115*T,H = 60*T,Q = T*2;

	public boolean isRunning = false;
	
	public BufferedImage screen = new BufferedImage(W,H,BufferedImage.TYPE_4BYTE_ABGR_PRE);
	public JFrame frame;
	public Thread thread = new Thread(this);
	
	public int num = 1000;
	public Fish[] boid;
	public int vr = 30; // vision radious
	
	public static boolean click = false,rclick = false;
	public static int mx = 0,my = 0;
	
	public Slider[] sliders;
	
	public double 
			kc = 0.5, 
			ka = 0.5,
			ks = 15,
			kb = 1;
	
	public double fbMax = 5000;
	
	public Vector showVector;
	
	public Game() {
		this.setPreferredSize(new Dimension(W,H));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		boid = new Fish[num];
		for(int i = 0; i < num;i++) {
			boid[i] = new Fish();
		}
		
		setUI();
		
		initFrame();
		thread.start();
	}
	
	public void setUI() {
		
		int sw = 400,sh = 30,sx = 10,space = 10;
		sliders = new Slider[6];
		sliders[0] = new Slider(sx,space+(space+sh)*0,sw,sh,0,1,"cohesion");
		sliders[1] = new Slider(sx,space+(space+sh)*1,sw,sh,0,10,"allignment");
		sliders[2] = new Slider(sx,space+(space+sh)*2,sw,sh,5,20,"separation");
		sliders[3] = new Slider(sx,space+(space+sh)*3,sw,sh,0,10,"border");
		sliders[4] = new Slider(sx,space+(space+sh)*4,sw,sh,0,15000,"border force max");
		sliders[5] = new Slider(sx,space+(space+sh)*5,sw,sh,10,100,"vision radious");

		sliders[0].set(kc);
		sliders[1].set(ka);
		sliders[2].set(ks);
		sliders[3].set(kb);
		sliders[4].set(fbMax);
		sliders[5].set(vr);
		
	}
	
	public void initFrame() {
		frame = new JFrame("boids");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		requestFocus();
	}
	
	public void updateMouse(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
	}
	
	public void clickCheck() {
		Rectangle Rm = new Rectangle(mx,my,1,1);
		for(int i = 0;i < sliders.length;i++) {
			if (Rm.intersects(sliders[i].getRect())) {
				sliders[i].clicked = true;
				break;
			}
		}
	}
	
	public double distPow(Vector p1,Vector p2) {
		return Math.pow(p1.x-p2.x,2)+Math.pow(p1.y-p2.y,2);
	}
	
	public void tick() {
		
		for(int i = 0;i < sliders.length;i++) {
			sliders[i].tick();
		}

		kc = sliders[0].value();
		ka = sliders[1].value();
		ks = sliders[2].value();
		kb = sliders[3].value();
		fbMax = sliders[4].value();
		vr = (int) sliders[5].value();
		
		
		fishTick();
	}
	
	public void fishTick() {// update fishes //
		double sr = Math.pow(vr,2); // vision radious squared
		
		for(int i = 0;i < num;i++) {
			// for each fish
			Vector fa = new Vector(0,0); // alignment
			Vector fs = new Vector(0,0);// separation
			Vector fc = new Vector(0,0); // cohesion

			Vector avgPos = new Vector(0,0);

			int count = 0;
			for(int j = 0; j < num;j++) {// check surroundings	
				
				if (distPow(boid[i].pos,boid[j].pos) > sr) {
					continue;// isnt in range
				}
				
				avgPos.add(boid[j].pos);
				
				if (i == j) {
					continue; // is the same
				}
				
				count++;
				fa.add(boid[j].vel);
				
				Vector v = new Vector(0,0);
				v.add(Vector.pointV(boid[j].pos,boid[i].pos));
				if (v.magPow() != 0) {
					v.setMag(ks*Fish.velMag/v.magPow());
					
					fs.add(v);
				}
			}
			
			if (count > 0) {
				fa.mult(ka/count);

				avgPos.div(count+1);
				
				fc = Vector.pointV(boid[i].pos,avgPos);
				
				fc.mult(kc);
				fs.mult(ks);
				
				boid[i].force(fa);
				boid[i].force(fs);
				boid[i].force(fc);
			}
			
			boid[i].acc.setMag(Fish.velMag);
			
			Vector Fb[] = new Vector[4];// border Forces
			Fb[0] = Vector.pointV(new Vector(boid[i].pos.x,0),boid[i].pos);// left
			Fb[1] = Vector.pointV(new Vector(boid[i].pos.x,H),boid[i].pos);// right
			Fb[2] = Vector.pointV(new Vector(0,boid[i].pos.y),boid[i].pos);// up
			Fb[3] = Vector.pointV(new Vector(W,boid[i].pos.y),boid[i].pos);// down
			
			for(int j = 0; j < Fb.length;j++) {
				double d = Fb[j].mag();
				double fb = fbMax/Math.pow(d,2);
				Fb[j].setMag(fb+kb);
				
				boid[i].force(Fb[j]);
			}
			
			//wrap around
			
			if (boid[i].pos.x > W) {
				boid[i].pos.x -= W;
			}
			if (boid[i].pos.x < 0) {
				boid[i].pos.x += W;
			}
			
			if (boid[i].pos.y > H) {
				boid[i].pos.y -= H;
			}
			if (boid[i].pos.y < 0) {
				boid[i].pos.y += H;
			}
			
			boid[i].move();
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		//System.out.println("rendering");
		
		Graphics g = screen.getGraphics();
		
		g.setColor(Color.black);
		g.fillRect(0,0,W,H);

		// draw fish //

		if (sliders[5].clicked == true) {
			for(int i = 0; i < num;i++) {
				Vector p = boid[i].pos;
				g.setColor(Color.DARK_GRAY);
				g.fillOval((int)(p.x-vr), (int)(p.y-vr), (int)(vr*2), (int)(vr*2));
			}
		}
		
		int fishRadious = 2;
		for(int i = 0; i < num;i++) {
			Vector p = boid[i].pos;
			Vector v = boid[i].vel;
			
			g.setColor(Color.magenta);
			g.drawLine((int)(p.x), (int)(p.y), (int)(p.x+v.x), (int)(p.y+v.y));
			
			g.setColor(Color.red);
			g.fillRect((int)(p.x-fishRadious),(int)(p.y-fishRadious),fishRadious*2,fishRadious*2);
		}
		
		// draw Sliders // 
		
		if (rclick == true) {
			for(int i = 0; i < sliders.length;i++) {
				sliders[i].render(g);
			}
			
		}
		
		g = bs.getDrawGraphics();
		g.drawImage(screen,0,0,W,H,null);
		
		bs.show();
	}
	
	public static void main(String[] args) {
		new Game();
	}

	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 30.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		isRunning = true;
		
		while (isRunning) {
			long now = System.nanoTime();
			delta += (now-lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			if (System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: "+frames);
				frames = 0;
				timer += 1000;
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		updateMouse(e);
		
	}

	public void mouseMoved(MouseEvent e) {
		updateMouse(e);
		
	}

	public void mouseClicked(MouseEvent e) {
		updateMouse(e);
		
	}

	public void mousePressed(MouseEvent e) {
		updateMouse(e);
		
		if (e.getButton() == 3) {
			rclick = true;
		} else if (click == false) {
			click = true;
			clickCheck();
		}
	}

	public void mouseReleased(MouseEvent e) {
		updateMouse(e);
		if (e.getButton() == 3) {
			rclick = false;
		} else {
			click = false;
		}
	}

	public void mouseEntered(MouseEvent e) {
		updateMouse(e);
	}

	public void mouseExited(MouseEvent e) {
		updateMouse(e);
	}
	
}
