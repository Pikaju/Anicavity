package org.pikaju.anicavity.world.light;

import org.pikaju.anicavity.world.World;
import org.pikaju.anicavity.world.entity.Entity;

public class Light {

	public float x;
	public float y;
	
	public float r;
	public float g;
	public float b;
	
	public float range = 6;
	
	private boolean wasMoved;
	
	public Entity entity;
	
	public Light(float x, float y, float r, float g, float b, float range) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.g = g;
		this.b = b;
		this.range = range;
	}
	
	public void move(float dx, float dy) {
		if(dx != 0 || dy != 0) wasMoved = true;
		x += dx;
		y += dy;
	}
	
	public void setPosition(float x, float y) {
		move(x - this.x, y - this.y);
	}
	
	public void setRange(float range) {
		this.range = range;
		wasMoved = true;
	}
	
	public boolean wasMoved() {
		boolean b = wasMoved;
		wasMoved = false;
		return b;
	}

	public void spread(World world) {
		world.spreadLightCircle(x, y, new float[] { r, g, b }, range, 0, true);
	}
}
