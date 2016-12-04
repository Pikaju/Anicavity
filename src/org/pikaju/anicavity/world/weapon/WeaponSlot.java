package org.pikaju.anicavity.world.weapon;

import org.pikaju.anicavity.graphics.Screen;
import org.pikaju.anicavity.world.entity.Entity;

public class WeaponSlot {

	public int x;
	public int y;
	public WeaponComponent component;
	
	public void setWeaponComponent(WeaponComponent component) {
		this.component = component;
		this.component.slot = this;
	}

	public void update() {
		if(component != null) component.update();
	}

	public void render(Screen screen, int xOff, int yOff) {
		if(component != null) component.render(screen, xOff, yOff);
	}

	public void shoot(Entity owner, float x, float y, float targetX, float targetY) {
		if(component != null) component.shoot(owner, x, y, targetX, targetY);
	}
}
