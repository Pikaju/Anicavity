package org.pikaju.anicavity.world.worldgen;

import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.World;
import org.pikaju.anicavity.world.entity.Mob;
import org.pikaju.anicavity.world.entity.Player;
import org.pikaju.anicavity.world.entity.enemy.Flower;
import org.pikaju.anicavity.world.entity.enemy.Slime;
import org.pikaju.anicavity.world.tile.Tile;
import org.pikaju.anicavity.world.tile.TileData;

public class EnemyGenerator {

	private static final int RED_SLIME_LEVEL = WorldGenerator.STONE_LEVEL + 20;
	
	public static void generateEnemy(float x, float y, World world) {
		if(y < 0) return;
		if(!world.isServer()) return;
		for(int xx = (int) x; xx < x + 1.0f; xx++) {
			for(int yy = (int) y; yy < y + 1.0f; yy++) {
				TileData data = world.getTileData(xx, yy);
				if(data.getStaticLight() > 0.0f) return;
				if(data.getTile(0) != Tile.AIR)	return;
			}
		}
		int type = MathHelper.getRandom().nextInt(20);
		
		if(type < 15) {
			Slime slime = new Slime();
			slime.x = x;
			slime.y = y;
			if(y > RED_SLIME_LEVEL) slime.color = 0xff5050;
			world.addNewEntity(slime);
		}
		if(type >= 15) {
			Flower flower = new Flower();
			flower.x = (int) x;
			flower.y = (int) y;
			world.addNewEntity(flower);;
		}
	}
	
	public static void generateEnemies(World world) {
		Player player = world.getRandomPlayer();
		if(player == null) return;
		if(world.getEnemies().length > getDifficultyAt((int) player.y)) return;
		float xdir = MathHelper.getRandom().nextFloat() - 0.5f;
		float ydir = MathHelper.getRandom().nextFloat() - 0.5f;
		float t = MathHelper.sqrt(xdir * xdir + ydir * ydir);
		xdir = xdir / t * (Mob.UPDATE_RANGE + 10.0f);
		ydir = ydir / t * (Mob.UPDATE_RANGE + 10.0f);;
		generateEnemy(player.x + xdir, player.y + ydir, world);
	}
	
	public static int getDifficultyAt(int height) {
		return height / 20 + 1;
	}
}
