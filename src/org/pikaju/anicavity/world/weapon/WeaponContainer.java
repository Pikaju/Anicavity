package org.pikaju.anicavity.world.weapon;

import org.pikaju.anicavity.graphics.Screen;
import org.pikaju.anicavity.world.World;
import org.pikaju.anicavity.world.entity.Entity;

public class WeaponContainer extends WeaponComponent {

	public WeaponSlot[] slots;
	public World world;
	
	public WeaponContainer(WeaponContainer container, int slotAmount) {
		super(container);
		slots = new WeaponSlot[slotAmount];
		for(int i = 0; i < slots.length; i++) {
			slots[i] = new WeaponSlot();
		}
	}
	
	public WeaponContainer(int slotAmount) {
		this(null, slotAmount);
	}
	
	public void update() {
		super.update();
		for(int i = 0; i < slots.length; i++) {
			slots[i].update();
		}
	}
	
	public void render(Screen screen, int xOff, int yOff) {
		super.render(screen, xOff, yOff);
		for(int i = 0; i < slots.length; i++) {
			slots[i].render(screen, xOff, yOff);
		}
	}
	
	public void setSlot(WeaponComponent component, int slot) {
		if(slots.length <= slot) return;
		slots[slot].component = component;
	}
	
	public void setSlotPosition(int slot, int x, int y) {
		slots[slot].x = x;
		slots[slot].y = y;
	}
	
	public void shoot(Entity owner, float x, float y, float targetX, float targetY) {
		for(int i = 0; i < slots.length; i++) {
			shoot(owner, x, y, targetX, targetY, i);
		}
	}
	
	public void shoot(Entity owner, float x, float y, float targetX, float targetY, int slot) {
		slots[slot].shoot(owner, x, y, targetX, targetY);
	}
	
	public int getSlotCooldown(int slot) {
		return slots[slot].component.cooldown;
	}

	public void setSlotCooldown(int slot, int cooldown) {
		slots[slot].component.cooldown = cooldown;
	}
}
