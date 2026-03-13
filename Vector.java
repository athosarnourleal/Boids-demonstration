package core;

import java.util.Random;

public class Vector {
	public double x,y;
	
	public Vector(double x,double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(double angle,double len, boolean nothing) {
		x = Math.cos(angle)*len;
		y = Math.sin(angle)*len;
	}
	
	public Vector(double mag) {
		Random r = new Random();
		x = r.nextDouble()*100;
		y = r.nextDouble()*100;
		setMag(mag);
	}

	public void mult(double n) {
		x = x*n;
		y = y*n;
	}

	public void div(double n) {
		x = x/n;
		y = y/n;
	}
	
	public void div(int n) {
		x = x/n;
		y = y/n;
	}
	
	public void add(Vector v2) {
		x += v2.x;
		y += v2.y;
	}
	
	public void addNo0(Vector v2) {
		if (x + v2.x == 0 && y+v2.y == 0) {
			v2.setMag(v2.mag()+0.5);
		}
		x += v2.x;
		y += v2.y;
	}
	
	public void sub(Vector v2) {
		x -= v2.x;
		y -= v2.y;
	}
	
	public double mag() {
		return Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
	}
	
	public double magPow() {
		// não faz sqrt() para aumentar desempenho
		return Math.pow(x,2)+Math.pow(y,2);
	}
	
	public void setMag(double mag2) {
		if (mag2 == 0 || (x == 0 && y == 0)) {
			x = 0;
			y = 0;
			return;
		}
		double set = mag2/mag();
		x = x * set;
		y = y * set;
	}
	
	public void setMagO(double mag2) {// set mag optimized
		if (mag2 == 0 || (x == 0 && y == 0)) {
			x = 0;
			y = 0;
			return;
		}
		double set = mag2/mag();
		x = x * set;
		y = y * set;
	}
	
	public static Vector pointV(Vector p1,Vector p2) {
		return new Vector(p2.x-p1.x, p2.y-p1.y);
	}

	public static double vectorMultiply(Vector v1, Vector v2) {
		return v1.x*v2.x + v1.y*v2.y;
	}
	
}
