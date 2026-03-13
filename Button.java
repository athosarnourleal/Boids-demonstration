package core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Button {
	public String label = "";
	public boolean state;
	public Rectangle col;
	public Button(int x,int y,int s,String la) {
		label = la;
		col = new Rectangle(x,y,s,s);
		state = false;
	}
	public Button(int x,int y,int s,String la,boolean sta) {
		label = la;
		col = new Rectangle(x,y,s,s);
		state = sta;
	}
	
	public void clicked() {
		if (state) {
			state = false;
		} else {
			state = true;
		}
	}
	
	public void render(Graphics g) {
		if (state == true) {
			g.setColor(Color.blue);
		} else {
			g.setColor(Color.DARK_GRAY);
		}
		g.fillRect(col.x,col.y,col.width,col.height);
		
		g.setColor(Color.green);
		g.drawString(label,col.x+col.width,col.y+col.height);
	}
}
