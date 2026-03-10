package core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Slider {
	public int x,y,w,h;
	public int wVal = 0;
	public double min = 0, max = 0;
	public boolean clicked = false;
	public String label = " ";

	public Slider(int x,int y,int w,int h,double min,double max) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		wVal = w/2;
		this.min = min;
		this.max = max;
	}
	public Slider(int x,int y,int w,int h,double min,double max,String label) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		wVal = w/2;
		this.min = min;
		this.max = max;
		this.label = label;
	}

	public Slider(int x,int y,int w,int h,double max) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		wVal = 0;
		min = 0;
		this.max = max;
	}
	
	public Rectangle getRect() {
		return new Rectangle(x,y,w,h);
	}
	
	public void tick() {
		if (clicked) {
			wVal = Game.mx-x;
			wVal = clamp(wVal,0,w);
			
			if (Game.click == false) {
				clicked = false;
			}
		}
	}

	public double value() {
		return wVal*(max-min)/w +min;
	}
	
	public void set(double val) {
		wVal = (int)((val-min)*w/(max-min));
		wVal = clamp(wVal,0,w);
	}
	
	public int clamp(int i,int min,int max) {
		if (i > max)
			return max;
		if (i < min)
			return min;
		return i;
	}
	
	public void render(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(x, y, w, h);
		if (!clicked) {
			g.setColor(Color.blue);
			g.fillRect(x, y, wVal, h);
		} else {
			g.setColor(Color.magenta);
			g.fillRect(x, y, wVal, h);

			g.setColor(Color.white);
			g.drawString(min+"",x, y+h);
			
			String ma = max+"";
			g.drawString(ma,x+w-ma.length()*6, y+h);
			
		}
		g.setColor(Color.green);
		g.drawString(label+":"+value(),x+w, y+h);
		
	}
	
}
