package org.pikaju.anicavity.world.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class PhysicsMob extends Mob {

	protected static final float GRAVITY = 0.02f;
	
	private float dx;
	private float dy;
	protected boolean onGround = false;
	protected boolean intersectedWall = false;
	
	protected boolean knockedBack = false;
	protected boolean isStatic = false;
	protected float bouncyness = 0.0f;
	
	public void tick() {
		intersectedWall = false;
		onGround = false;
		dy += GRAVITY;
		if(move(0, dy)) {
			if(dy > 0) onGround = true;
			dy = 0;
			intersectedWall = true;
		}
		if(move(dx, 0)) {
			if(dy >= 0) {
				dx = -dx * bouncyness;
			}
			intersectedWall = true;
		}
		if(onGround) {
			if(knockedBack) {
				dx = 0;
			}
			knockedBack = false;
		}
	}

	public float getDX() {
		return dx;
	}

	public void setDX(float dx) {
		if(knockedBack) return;
		this.dx = dx;
	}

	public float getDY() {
		return dy;
	}

	public void setDY(float dy) {
		if(knockedBack) return;
		this.dy = dy;
	}
	
	public void damage(Entity source, float amount, float xDir, float yDir) {
		super.damage(source, amount, xDir, yDir);
		if(!isLocal()) return;
		if(!isStatic) {
			knockedBack = true;
			dx += xDir;
			dy += yDir;
		}
	}
	
	public void writeData(DataOutput out) {
		super.writeData(out);
		try {
			out.writeFloat(dx);
			out.writeFloat(dy);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readData(DataInput in) {
		super.readData(in);
		try {
			dx = in.readFloat();
			dy = in.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
