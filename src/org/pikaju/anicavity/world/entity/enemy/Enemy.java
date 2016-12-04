package org.pikaju.anicavity.world.entity.enemy;

import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.entity.Mob;
import org.pikaju.anicavity.world.entity.PhysicsMob;
import org.pikaju.anicavity.world.entity.Player;

public class Enemy extends PhysicsMob {

	protected Mob target;
	
	public void damagePlayerOnContact(float amount) {
		for(Player player : world.getPlayers()) {
			if(intersects(player)) {
				float xd = player.x - x;
				float yd = player.y - y;
				float t = MathHelper.sqrt(xd * xd + yd * yd);
				player.damage(this, amount, xd / t * 0.1f, yd / t * 0.1f);
			}
		}
	}
}
