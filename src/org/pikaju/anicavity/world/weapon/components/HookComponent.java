package org.pikaju.anicavity.world.weapon.components;

import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.entity.Entity;
import org.pikaju.anicavity.world.entity.Player;
import org.pikaju.anicavity.world.weapon.WeaponComponent;
import org.pikaju.anicavity.world.weapon.WeaponContainer;
import org.pikaju.anicavity.world.weapon.projectiles.ProjectileHook;

public class HookComponent extends WeaponComponent {

	public HookComponent(WeaponContainer container) {
		super(container);
	}
	
	public void update() {
		cooldown++;
	}
	
	public void shoot(Entity owner, float x, float y, float targetX, float targetY) {
		if(cooldown > 30) {
			ProjectileHook hook = new ProjectileHook();
			((Player) owner).hook = hook;
			hook.owner = owner;
			hook.x = x - hook.width / 2.0f;
			hook.y = y - hook.height / 2.0f;
			float t = MathHelper.sqrt(targetX * targetX + targetY * targetY);
			hook.setDX(targetX / t * 0.5f);
			hook.setDY(targetY / t * 0.7f);
			container.world.addNewEntity(hook);
			cooldown = 0;
		}
	}
}
