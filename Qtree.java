package core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Qtree {
	
	public Rectangle col;//colision
	public Qtree ne,nw,se,sw;// filhos
	public boolean div = false;
	public int max;
	public int fishID[], fishNum = 0;
	public Qtree(int x,int y,int w,int h,int max) {
		col = new Rectangle(x,y,w,h);
		fishID = new int[max];
		this.max = max;
	}
	
	public void divide() {
		// set children
		ne = new Qtree(col.x            ,col.y             ,col.width/2,col.height/2,max);
		nw = new Qtree(col.x+col.width/2,col.y             ,col.width/2,col.height/2,max);
		se = new Qtree(col.x            ,col.y+col.height/2,col.width/2,col.height/2,max);
		sw = new Qtree(col.x+col.width/2,col.y+col.height/2,col.width/2,col.height/2,max);
		
		div = true;
	}
	
	public void reset() {
		div = false;
		ne = null;
		nw = null;
		se = null;
		sw = null;
		fishID = new int[max];
		fishNum = 0;
	}
	
	public int[] Querry(double x,double y,double r) {
		return Querry(new Rectangle((int)(x-r),(int)(y-r),(int)(r*2),(int)(r*2)));
	}
	
	public int[] Querry(Rectangle range) {
		int[] found = new int[0];
		if (range.intersects(col) == false) {
			return found;//vazio
		}
		
		found = concat(found,fishID);
		if (div == true) {
			found = concat(found,ne.Querry(range));
			found = concat(found,nw.Querry(range));
			found = concat(found,se.Querry(range));
			found = concat(found,sw.Querry(range));
		}
		return found;
	}
	
	public int[] push(int v,int[] arr) {
		int[] narr = new int[arr.length+1];
		for(int i = 0; i < arr.length;i++) {
			narr[i] = arr[i];
		}
		narr[arr.length] = v;
		return narr;
	}
	public int[] concat(int[] arr0,int[] arr1) {
		int[] narr = new int[arr0.length+arr1.length];
		int count = 0;
		for(int i = 0; i < arr0.length;i++) {
			narr[count] = arr0[i];
			count++;
		}
		for(int i = 0; i < arr1.length;i++) {
			narr[count] = arr1[i];
			count++;
		}
		return narr;
	}
	
	public boolean insert(int fish) {
		Vector fishpos = Game.boid[fish].pos;
		if (!isInside(fishpos)) {
			return false;
		}
		
		if (fishNum < max && div == false) {
			// tem espaço
			fishID[fishNum] = fish;// add ID of fish to the stack
			fishNum++;
			Game.placed++;
			return true;
		} else {
			// overflow
			if (div == false) {
				divide();
			}
			if (ne.insert(fish) == true) {
				return true; // insert was successfull
			}
			if (nw.insert(fish) == true) {
				return true; // insert was successfull
			}
			if (se.insert(fish) == true) {
				return true; // insert was successfull
			}
			if (sw.insert(fish) == true) {
				return true; // insert was successfull
			}
			
		}
		return false;// esse return em teoria nunca será executado(?)
	}
	
	public boolean isInside(Vector v) {
		if (v.x >= col.x && v.x <= col.x+col.width &&
				v.y >= col.y && v.y <= col.y+col.height) {
			return true;
		}
		return false;
	}
	
	public void render(Graphics g) {
		g.setColor(Color.DARK_GRAY);
		g.drawRect(col.x,col.y,col.width,col.height);
		if (div == true) {
			ne.render(g);
			nw.render(g);
			se.render(g);
			sw.render(g);
		}
	}
}
