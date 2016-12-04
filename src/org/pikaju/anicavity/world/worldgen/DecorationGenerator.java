package org.pikaju.anicavity.world.worldgen;

import java.util.Random;

import org.pikaju.anicavity.world.World;
import org.pikaju.anicavity.world.entity.terrain.decoration.Crystal;
import org.pikaju.anicavity.world.tile.Tile;
import org.pikaju.anicavity.world.tile.TileData;

public class DecorationGenerator {
	
	public static void generateDecoration(float x, float y, World world, Random random) {
		TileData data = world.getTileData((int) x, (int) y);
		if(data.getTile(0) == Tile.AIR && (data.getTile(1) == Tile.EARTH || data.getTile(1) == Tile.STONE) &&  y > WorldGenerator.SURFACE_LEVEL + 20) {
			if(world.isServer()) {
				Crystal crystal = new Crystal();
				crystal.x = x;
				crystal.y = y;
				world.addNewEntity(crystal);
			}
		}
	}

	public static void generateDecoration(World world, Random random) {
		for(int x = 0; x < world.getWidth(); x++) {
			for(int y = 0; y < world.getHeight(); y++) {
				if(random.nextInt(200) == 0) generateDecoration(random.nextInt(world.getWidth()), random.nextInt(world.getHeight()), world, random);
			}
		}
	}
}
