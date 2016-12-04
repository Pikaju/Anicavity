package org.pikaju.anicavity.world.worldgen;

import java.util.Random;

import org.pikaju.anicavity.world.World;
import org.pikaju.anicavity.world.entity.terrain.boss.BossSpawner;
import org.pikaju.anicavity.world.tile.Tile;
import org.pikaju.anicavity.world.tile.TileData;

public class StructureGenerator {

	private static int BOSS_ROOM_LEVEL = WorldGenerator.STONE_LEVEL;

	public static void generateBossSpawners(World world, Random random) {
		for(int x = 0; x < world.getWidth(); x++) {
			for(int y = 0; y < world.getHeight(); y++) {
				if(random.nextInt(400) == 0) generateBossSpawner(x, y, world, random);
			}
		}
	}
	
	public static void generateBossSpawner(int x, int y, World world, Random random) {
		int radius = 3;
		for(int xx = x - radius; xx < x + radius; xx++) {
			for(int yy = y - radius; yy < y + radius; yy++) {
				TileData data = world.getTileData(xx, yy);
				if(!(data.getTile(0) == Tile.AIR && (data.getTile(1) == Tile.EARTH || data.getTile(1) == Tile.STONE) &&  y > WorldGenerator.SURFACE_LEVEL + 20)) {
					return;
				}
			}
		}
		
		if(world.isServer()) {
			BossSpawner spawner = new BossSpawner();
			spawner.x = x;
			spawner.y = y;
			world.addNewEntity(spawner);
		}
	}
	
	public static void generateBossRooms(World world, Random random) {
		for(int i = 0; i < 100; i++) {
			int x = random.nextInt(world.getWidth());
			int y = random.nextInt(world.getHeight() - BOSS_ROOM_LEVEL) + BOSS_ROOM_LEVEL;
			int width = 40 + random.nextInt(10);
			int height = 20 + random.nextInt(10);
			generateRoom(world, x, y, width, height, Tile.DARK_BRICK, Tile.DARK_BRICK);
			generateRoomDoor(world, x, y, width, height, 0, 14);
			generateRoomDoor(world, x, y, width, height, 1, 14);
			generateRoomDoor(world, x, y, width, height, 2, 14);
			generateRoomDoor(world, x, y, width, height, 3, 14);
		}
	}
	
	public static void generateRoom(World world, int x, int y, int width, int height, Tile tile, Tile background) {
		for(int xx = x; xx < x + width; xx++) {
			for(int yy = y; yy < y + height; yy++) {
				if(xx == x || yy == y || xx == x + width - 1 || yy == y + height - 1) {
					world.getTileData(xx, yy).setTile(tile, 0);
				} else {
					world.getTileData(xx, yy).setTile(Tile.AIR, 0);
				}
				if(background != null) world.getTileData(xx, yy).setTile(background, 1);
			}
		}
	}
	
	public static void generateRoomDoor(World world, int x, int y, int width, int height, int direction, int size) {
		for(int xx = x; xx < x + width; xx++) {
			for(int yy = y; yy < y + height; yy++) {
				if(xx == x && direction == 0 && yy > y + height / 2 - size / 2 && yy < y + height / 2 + size / 2) {
					world.getTileData(xx, yy).setTile(Tile.AIR, 0);
				}
				if(xx == x + width - 1 && direction == 1 && yy > y + height / 2 - size / 2 && yy < y + height / 2 + size / 2) {
					world.getTileData(xx, yy).setTile(Tile.AIR, 0);
				}
				if(yy == y && direction == 2 && xx > x + width / 2 - size / 2 && xx < x + width / 2 + size / 2) {
					world.getTileData(xx, yy).setTile(Tile.AIR, 0);
				}
				if(yy == y + height - 1 && direction == 3 && xx > x + width / 2 - size / 2 && xx < x + width / 2 + size / 2) {
					world.getTileData(xx, yy).setTile(Tile.AIR, 0);
				}
			}
		}
	}
}
