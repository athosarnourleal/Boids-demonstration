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
	
	public int num = 100;
	public static Fish[] boid;
	public int vr = 30; // vision radious
	
	public static boolean click = false,rclick = false;
	public static int mx = 0,my = 0;
	
	public Slider[] sliders;
	public Button[] buttons;
	public Rectangle uiBox = new Rectangle(0,0,0,0);
	
	public double 
			kc = 0.5, 
			ka = 0.5,
			ks = 15,
			kb = 1;
	
	public double fbMax = 1000;
	
	public int max = 4;
	public static int placed = 0;
	public Qtree qt;
	public Vector showVector;
	
	public Rectangle Rmouse;
	public int[] mouseFound;
	public int frameRate = 0;
	
	public Game() {
		this.setPreferredSize(new Dimension(W,H));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		qt = new Qtree(0,0,W,H,max);
		boid = new Fish[num];
		for(int i = 0; i < num;i++) {
			boid[i] = new Fish();
			qt.insert(i);
		}
		
		Rmouse = new Rectangle(0,0,0,0);
		
		System.out.println("placed: "+placed);
		setUI();
		
		initFrame();
		thread.start();
	}
	
	public void setBoid() {
		qt = new Qtree(0,0,W,H,max);
		boid = new Fish[num];
		for(int i = 0; i < num;i++) {
			boid[i] = new Fish();
			qt.insert(i);
		}
	}
	
	public void setUI() {
		
		int sw = 400,sh = 30,sx = 10,space = 10;
		int pos = 0;
		sliders = new Slider[8];
		buttons = new Button[5];
		sliders[0] = new Slider(sx,space+(space+sh)*pos,sw,sh,0,1,"cohesion");
		pos++;
		sliders[1] = new Slider(sx,space+(space+sh)*pos,sw,sh,0,10,"allignment");
		pos++;
		sliders[2] = new Slider(sx,space+(space+sh)*pos,sw,sh,0,30,"separation");
		pos++;
		sliders[3] = new Slider(sx,space+(space+sh)*pos,sw,sh,0,10,"border");
		pos++;
		sliders[4] = new Slider(sx,space+(space+sh)*pos,sw,sh,0,15000,"border force max");
		pos++;
		sliders[5] = new Slider(sx,space+(space+sh)*pos,sw,sh,10,100,"vision radious");
		pos++;
		sliders[7] = new Slider(sx,space+(space+sh)*pos,sw,sh,10,500,"Query demonstration size");
		sliders[7].showInt = true;
		pos++;
		buttons[4] = new Button(sx,space+(space+sh)*pos,sh,"show QuadTree query demonstration",false);
		pos++;
		
		buttons[0] = new Button(sx,space+(space+sh)*pos,sh,"show fish",true);
		pos++;
		buttons[1] = new Button(sx,space+(space+sh)*pos,sh,"show quadtree",false);
		pos++;
		buttons[2] = new Button(	sx,space+(space+sh)*pos,sh,"fish move",true);
		pos++;
		sliders[6] = new Slider(sx,space+(space+sh)*pos,(int)(sw*2.5),sh,100,15000,"number");
		sliders[6].showInt = true;
		pos++;
		buttons[3] = new Button(sx*3,space+(space+sh)*pos,sh,"apply new boid number",false);
		pos++;

		uiBox.width = sw*3;
		uiBox.height = space+(space+sh)*pos;

		sliders[0].set(kc);
		sliders[1].set(ka);
		sliders[2].set(ks);
		sliders[3].set(kb);
		sliders[4].set(fbMax);
		sliders[5].set(vr);
		sliders[6].set(num);
		sliders[7].set(50);
		
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
		Rectangle scr = getBounds();
		int xoff = scr.width/2-W/2, yoff = scr.height/2-H/2;
		mx = e.getX()-xoff;
		my = e.getY()-yoff;
	}
	
	public void clickCheck() {
		Rectangle Rm = new Rectangle(mx,my,1,1);
		
		for(int i = 0;i < buttons.length;i++) {
			if (Rm.intersects(buttons[i].col)) {
				buttons[i].clicked();
				break;
			}
		}
		
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
		frame.setTitle("boids - "+frameRate);
		
		if (buttons[3].state == true) {
			num = (int)sliders[6].value();
			setBoid();
			buttons[3].state = false;	
			mouseFound = new int[0];
			return;
		}
		
		// atualizar quadtree
		qt.reset();
		for(int i = 0; i < num;i++) {
			qt.insert(i);
		}
		
		// arrumar demonstração //

		Rmouse.width = (int)sliders[7].value();
		Rmouse.height = (int)sliders[7].value();
		Rmouse.x = mx-Rmouse.width/2;
		Rmouse.y = my-Rmouse.height/2;
		
		mouseFound = qt.Querry(Rmouse);
		
		// atualizar variaveis
		for(int i = 0;i < sliders.length;i++) {
			sliders[i].tick();
		}

		kc = sliders[0].value();
		ka = sliders[1].value();
		ks = sliders[2].value();
		kb = sliders[3].value();
		fbMax = sliders[4].value();
		vr = (int) sliders[5].value();
		
		// rodar codigo
		if (buttons[2].state == true) {
			fishTick();
		}
	}
	
	public void fishTick() {// update fishes //
		double sr = Math.pow(vr,2); // vision radious squared
		
		int checks = 0;
		for(int i = 0;i < boid.length;i++) {// divide loading into chunks
			// for each fish
			Vector fa = new Vector(0,0); // alignment
			Vector fs = new Vector(0,0);// separation
			Vector fc = new Vector(0,0); // cohesion

			Vector avgPos = new Vector(0,0);
			int count = 0;
			int[] look = qt.Querry(boid[i].pos.x,boid[i].pos.y,vr);
			for(int j = 0; j < look.length;j++) {// check surroundings
				int id = look[j];
				checks++;
				if (distPow(boid[i].pos,boid[id].pos) > sr) {
					continue;// isnt in range
				}
				
				avgPos.add(boid[id].pos);
				
				if (i == id) {
					continue; // is the same
				}
				
				count++;
				fa.add(boid[id].vel);
				
				Vector v = new Vector(0,0);
				v.add(Vector.pointV(boid[id].pos,boid[i].pos));
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
			
			boid[i].move();
			
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
		}
		
//		System.out.println("checks: "+checks);
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
		
		// draw tree //
		
		if (buttons[1].state == true) {
			qt.render(g);
		}
		// draw querry demonstration //
		
		if (buttons[4].state == true) {
			g.setColor(Color.green);
			g.drawRect(Rmouse.x,Rmouse.y,Rmouse.width,Rmouse.height);
			int circleR = 5;
			for(int i = 0;i < mouseFound.length;i++) {
				Vector fishpos = boid[mouseFound[i]].pos;
				g.drawOval((int)(fishpos.x-circleR),(int)(fishpos.y-circleR),circleR*2,circleR*2);
			}
		}
		
		// draw fish //

		if (buttons[0].state == true) {
			if (sliders[5].clicked == true) {
				for(int i = 0; i < num;i++) {
					Vector p = boid[i].pos;
					g.setColor(Color.DARK_GRAY);
					g.fillOval((int)(p.x-vr), (int)(p.y-vr), (int)(vr*2), (int)(vr*2));
				}
			}
			
			int fishRadious = 3;
			double mult = 2	;
			for(int i = 0; i < num;i++) {
				Vector p = boid[i].pos;
				Vector v = boid[i].vel;
				
				g.setColor(Color.magenta);
				g.drawLine((int)(p.x), (int)(p.y), (int)(p.x+v.x*mult), (int)(p.y+v.y*mult));
				
				g.setColor(Color.red);
				g.fillRect((int)(p.x-fishRadious),(int)(p.y-fishRadious),fishRadious*2,fishRadious*2);
			}
		}
		
		// draw UI // 
		
		if (rclick == true) {
			g.setColor(Color.black);
			g.fillRect(uiBox.x,uiBox.y,uiBox.width,uiBox.height);
			g.setColor(Color.blue);
			g.drawRect(uiBox.x,uiBox.y,uiBox.width,uiBox.height);
			for(int i = 0; i < sliders.length;i++) {
				sliders[i].render(g);
			}
			for(int i = 0; i < buttons.length;i++) {
				buttons[i].render(g);
			}
		}
		
		g = bs.getDrawGraphics();
		Rectangle scr = getBounds();
		g.setColor(Color.black);
		g.fillRect(scr.x,scr.y,scr.width,scr.height);
		g.drawImage(screen,scr.width/2-W/2,scr.height/2-H/2,W,H,null);
		
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
				frameRate = frames;
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
