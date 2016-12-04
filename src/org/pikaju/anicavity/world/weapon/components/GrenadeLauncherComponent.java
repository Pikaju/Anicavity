package org.pikaju.anicavity.world.weapon.components;

import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.entity.Entity;
import org.pikaju.anicavity.world.weapon.WeaponComponent;
import org.pikaju.anicavity.world.weapon.WeaponContainer;
import org.pikaju.anicavity.world.weapon.projectiles.ProjectileGrenade;

public class GrenadeLauncherComponent extends WeaponComponent {

	public GrenadeLauncherComponent(WeaponContainer container) {
		super(container);
	}
	
	public void update() {
		cooldown++;
	}
	
	public void shoot(Entity owner, float x, float y, float targetX, float targetY) {
		if(cooldown > 60) {
			ProjectileGrenade grenade = new ProjectileGrenade();
			grenade.x = x;
			grenade.y = y;
			float t = MathHelper.sqrt(targetX * targetX + targetY * targetY);
			grenade.setDX(targetX / t * 0.3f);
			grenade.setDY(targetY / t * 0.5f);
			grenade.owner = owner;
			container.world.addNewEntity(grenade);
			cooldown = 0;
		}
	}
}
