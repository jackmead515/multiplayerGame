package com.jack.entities;

public class Ammo {

	public int amount;
	public int total;
	
	public Projectile type;

	public Ammo(Projectile projectile, int i) {
		type = projectile;
		total = i;
		amount = i;
	}

}
