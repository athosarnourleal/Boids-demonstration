package core;

import java.util.Random;

public class Fish {
	
	public Vector pos,vel,acc;
	public static double velMag = 5;
	public Vector show;
	
	public Fish() {
		Random r = new Random();
		pos = new Vector(r.nextGaussian()*Game.W, r.nextGaussian()*Game.H);
		vel = new Vector(velMag);
		acc = new Vector(0,0);
	}
	
	public void force(Vector f) {
		acc.add(f);
	}
	
	public void move() {
		
		if (acc.magPow() != 0) {
			acc.setMag(velMag/2);
			vel.add(acc);
		}
		
		if (Math.abs(vel.mag()-velMag) > 0.5) {
			vel.setMag(velMag);
		}
		
		pos.add(vel);
		acc.setMag(0);
		
		vel.setMag(velMag);
	}
	
}
