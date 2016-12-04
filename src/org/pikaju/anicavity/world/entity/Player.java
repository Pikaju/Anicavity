package org.pikaju.anicavity.world.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.connection.Connection;
import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Font;
import org.pikaju.anicavity.graphics.Screen;
import org.pikaju.anicavity.graphics.Sprite;
import org.pikaju.anicavity.io.Input;
import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.entity.item.Inventory;
import org.pikaju.anicavity.world.entity.item.Item;
import org.pikaju.anicavity.world.light.Light;
import org.pikaju.anicavity.world.tile.Tile;
import org.pikaju.anicavity.world.weapon.Weapon;
import org.pikaju.anicavity.world.weapon.components.GreenLaserComponent;
import org.pikaju.anicavity.world.weapon.components.GrenadeLauncherComponent;
import org.pikaju.anicavity.world.weapon.components.HookComponent;
import org.pikaju.anicavity.world.weapon.projectiles.ProjectileHook;

public class Player extends PhysicsMob {

	public static Sprite sprite = new Sprite("/mobs/human.png", 4, 4);
	public static Sprite humanHead = new Sprite("/mobs/humanHead.png", 1, 1);
	
	public Connection connection;
	public String username;
	public Light light;
	
	private boolean flying = false;
	
	private float walkTimer;
	private int direction = 0;
	private byte animation = 0;
	
	private Weapon weapon;
	public ProjectileHook hook;
	private float hookdist = 5;
	
	private boolean shouldSend = false;
	
	private int noSendTimer = 0;
	
	private Inventory inventory;
	
	public Player(String username, float x, float y) {
		this.x = x;
		this.y = y;
		this.username = username;
		width = 1;
		height = 2;
		setMaxHealth(100.0f);
		setHealth(getMaxHealth());
		weapon = new Weapon(3);
		weapon.setSlot(new GreenLaserComponent(weapon), 0);
		weapon.setSlot(new GrenadeLauncherComponent(weapon), 1);
		weapon.setSlot(new HookComponent(weapon), 2);
		inventory = new Inventory(this);
	}
	
	public void update() {
		if(light == null) {
			light = new Light(x, y, 1.0f, 0.5f, 0.2f, 6.0f);
			world.getLightEngine().addLight(light, this);
		}
		light.setPosition(x + width / 2.0f, y + height / 2.0f);
		if(isLocal()) {
			inventory.update();
			shouldSend = false;
			handleMovement();
			weapon.world = world;
			weapon.update();
			shoot();
			tick();
			handleDamage();
			setMaxHealth(inventory.rubies.getAmount() + 100);
			if(walkTimer > 0) shouldSend = true;
			noSendTimer++;
			if(noSendTimer > 10) {
				noSendTimer = 0;
				shouldSend = false;
			}
			if(knockedBack) releaseHook();
			if(shouldSend) sendUpdatePacket();
		} else {
			handleUpdatePackets();
		}
	}

	public void render(Bitmap screen, int scrollX, int scrollY) {
		float damageTimerFlash = getInvincibilityTimer() % 6 > 3 ? 4.0f : 1.0f;
		screen.draw(sprite.getBitmap(animation), (x - width / 2) * Tile.SIZE + scrollX, y * Tile.SIZE + scrollY, width * Tile.SIZE * 2, height * Tile.SIZE, 0, damageTimerFlash, damageTimerFlash, damageTimerFlash);
		renderHealthBar(screen, scrollX, scrollY, 0x0000a0);
		if(!isLocal()) {
			screen.draw(username, (x + width / 2) * Tile.SIZE + scrollX - Font.getStringWidth(username) / 2, y * Tile.SIZE + scrollY - 10, 9, 1, 1, 1);
		}
	}

	public void shoot() {
		if(hook != null && world.getEntityByID(hook.getId()) != hook) hook = null;
		int playerScreenPosX = (int) (-world.getScrollX() + (x + width / 2) * Tile.SIZE);
		int playerScreenPosY = (int) (-world.getScrollY() + (y + height / 2) * Tile.SIZE);
		float tx = world.getInput().getX() - playerScreenPosX;
		float ty = world.getInput().getY() - playerScreenPosY;
		if(world.getInput().isButtonDown(Input.BUTTON_LEFT)) {
			weapon.shoot(this, x + width / 2, y + height / 2, tx, ty, 0);
		}
		if(world.getInput().isButtonDown(Input.BUTTON_RIGHT)) {
			weapon.shoot(this, x + width / 2, y + height / 2, tx, ty, 1);
		}
		if(world.getInput().isKeyDown(Input.KEY_HOOK) && weapon.getSlotCooldown(2) > 20) {
			releaseHook();
			if(hook == null) {
				weapon.shoot(this, x + width / 2, y + height / 2, tx, ty, 2);
			}
		}
		
		if(hook != null) {
			hookdist += world.getInput().getDWheel() * 0.5f;
			if(hookdist < 0.2f) hookdist = 0.2f;
			if(hookdist > 10) hookdist = 10;
			float dist = distanceTo(hook);
			if(dist > 15) releaseHook();
			if(hook != null) {
				if(hook.isAttached()) {
					if(dist > hookdist) {
						move((hook.x - x) * 0.1f / hookdist, ((hook.y - 1.2f) - y) * 0.1f / hookdist);
					}
					if(y + height / 2 > hook.y + hook.height / 2 + hookdist - 1) setDY(GRAVITY);
				}
			}
		}
	}

	public boolean isLocal() {
		if(username == null || world.username == null) return false;
		return world.username.equals(username);
	}
	
	public void writeData(DataOutput out) {
		super.writeData(out);
		try {
			out.writeByte(animation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readData(DataInput in) {
		super.readData(in);
		try {
			animation = in.readByte();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleMovement() {
		setDX(0);
		if(world.getInput().isKeyDown(Input.KEY_RIGHT)) setDX(getDX() + 1);
		if(world.getInput().isKeyDown(Input.KEY_LEFT)) setDX(getDX() - 1);
		if(flying) {
			setDY(0);
			if(world.getInput().isKeyDown(Input.KEY_UP)) setDY(getDY() - 1);
			if(world.getInput().isKeyDown(Input.KEY_DOWN)) setDY(getDY() + 1);
			setDY(getDY() * 0.2f);
			setDY(getDY() - GRAVITY);
		} else {
			if(world.getInput().isKeyDown(Input.KEY_UP) && onGround) setDY(-0.5f);
		}
		if(getDX() > 0) direction = 0;
		if(getDX() < 0) direction = 1;
		if(getDX() != 0) {
			walkTimer += 0.1f;
		} else {
			walkTimer = 0.9f;
		}
		animation = (byte) (((int) walkTimer % 4) * 4 + direction);
		
		setDX(getDX() * 0.2f);
		
		if(getDX() != 0 || getDY() != 0) shouldSend = true;
	}
	
	public void damage(Entity source, float amount, float xDir, float yDir) {
		xDir = 0;
		yDir = 0;
		super.damage(source, amount, xDir, yDir);
	}
	
	public boolean isDamagable() {
		return true;
	}
	
	public void renderOtherPlayers(Bitmap screen) {
		if(!isLocal()) return;
		for(Player p : world.getPlayers()) {
			if(p == this) continue;
			if(distanceTo(p) < 22) continue;
			float xDir = p.x - x;
			float yDir = p.y - y;
			float t = MathHelper.sqrt(xDir * xDir + yDir * yDir);
			xDir /= t;
			yDir /= t;
			xDir *= Screen.WIDTH / 2 * (2.0f / 2.3f);
			yDir *= Screen.HEIGHT / 2 * (2.0f / 2.3f);
			xDir += Screen.WIDTH / 2;
			yDir += Screen.HEIGHT / 2;
			int size = 8;
			screen.draw(humanHead.getBitmap(0), xDir - size, yDir - size, size * 2, size * 2, 0, 1, 1, 1);
			String name = p.username;
			screen.draw(name, xDir - Font.getStringWidth(name) / 2 + size / 4, yDir - 10 - size, 9, 1, 1, 1);
		}
	}
	
	public void renderScreenHealthBar(Bitmap screen) {
		if(isLocal()) return;
		int x1 = 100;
		int y1 = 10;
		int x0 = Screen.WIDTH / 2 - x1 / 2;
		int y0 = 10;
		
		screen.draw(0xffffff, x0, y0, x1, y1);
		screen.draw(0xa0a0a0, x0 + 1, y0 + 1, x1 - 2, y1 - 2);
		screen.draw(0x4000a0, x0 + 1, y0 + 1, (x1 - 2) * (getHealth() / getMaxHealth()), y1 - 2);
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	private void releaseHook() {
		if(hook != null) {
			weapon.setSlotCooldown(2, 0);
			world.removeEntity(hook);
			hook = null;
			hookdist = 5;
		}
	}
	
	public void onKill() {
		releaseHook();
		int r = (int) (inventory.rubies.getAmount() / 3.0f);
		inventory.rubies.setAmount(inventory.rubies.getAmount() - r);
		world.dropItem(Item.RUBY, r, x + width / 2, y + height / 2);
		world.getSpawnPoint().resetMob(this);
		setHealth(getMaxHealth());
	}
	
	public int getInvincibilityTime() {
		return 60;
	}
}
