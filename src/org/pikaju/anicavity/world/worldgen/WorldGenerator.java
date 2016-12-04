package org.pikaju.anicavity.world.worldgen;

import java.util.Random;

import org.pikaju.anicavity.world.SpawnPoint;
import org.pikaju.anicavity.world.World;
import org.pikaju.anicavity.world.tile.Tile;
import org.pikaju.anicavity.world.tile.TileData;

public class WorldGenerator {

	public static int SURFACE_LEVEL = 8;
	public static int STONE_LEVEL = 40 + SURFACE_LEVEL;
	
	private World world;
	
	public WorldGenerator(World world) {
		this.world = world;
	}
	
	public void generateWorld(Random random) {
		world.setSize(64, 64);
		generateMountains(random);
		CaveGenerator.generateCaves(world, random);
		StructureGenerator.generateBossSpawners(world, random);
		DecorationGenerator.generateDecoration(world, random);
		spreadSunLight();
		createSpawnPoint(random);
	}

	private void createSpawnPoint(Random random) {
		while(world.getSpawnPointAmount() < world.getSpawnPoints().length) {
			int x = random.nextInt(world.getWidth());
			if(world.getHighestTile(x).getTile(0) == Tile.GRASS) {
				world.addSpawnPoint(new SpawnPoint(x, world.getHighestTileHeight(x)));
			}
		}
	}

	private void spreadSunLight() {
		for(int x = 0; x < world.getWidth(); x++) {
			float value = 1.0f;
			for(int y = 0; y < world.getHeight(); y++) {
				TileData data = world.getTileData(x, y);
				world.spreadStaticLight(x, y, value, 1.0f, 0.125f);
				if(data.isSolid()) break;
				if(data.isBackgroundSolid()) value -= 0.1f;
				if(value <= 0) break;
			}
		}
	}

	private void generateMountains(Random random) {
		float cy = SURFACE_LEVEL + random.nextInt(10) - 5;
		float dy = 0;
		for(int x = 0; x < world.getWidth(); x++) {
			dy += (SURFACE_LEVEL - cy) * 0.08f + random.nextFloat() * 1.5f;
			dy *= 0.9f;
			cy += dy;
			int earthDepth = random.nextInt(3) + STONE_LEVEL;
			for(int y = 0; y < world.getHeight(); y++) {
				TileData data = new TileData(Tile.STONE, Tile.STONE);
				if(y < cy) data = new TileData(Tile.AIR, Tile.AIR);
				if(y > cy && y < earthDepth) data = new TileData(Tile.EARTH, Tile.EARTH);
				if(y == (int) cy) data = new TileData(Tile.GRASS, Tile.EARTH);
				world.setTileData(x, y, data);
			}
		}
	}
}
