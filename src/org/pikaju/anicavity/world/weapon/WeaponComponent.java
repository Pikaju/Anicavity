package org.pikaju.anicavity.world.weapon;

import org.pikaju.anicavity.graphics.Screen;
import org.pikaju.anicavity.world.entity.Entity;

public class WeaponComponent {

	public WeaponContainer container;
	public WeaponSlot slot;

	protected int cooldown;

	public WeaponComponent(WeaponContainer container) {
		this.container = container;
	}
	
	public void update() {
		
	}
	
	public void render(Screen screen, int xOff, int yOff) {
		
	}
	
	public void shoot(Entity owner, float x, float y, float targetX, float targetY) {
		
	}
}
