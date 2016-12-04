package org.pikaju.anicavity.world.worldgen;

import java.util.Random;

import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.World;
import org.pikaju.anicavity.world.tile.Tile;
import org.pikaju.anicavity.world.tile.TileData;

public class CaveGenerator {

	public static void generateCaves(World world, Random random) {
		for(int i = 0; i < 300; i++) {
			generateCave(world, random, random.nextInt(world.getWidth()), random.nextInt(world.getHeight()), random.nextInt(360), 0, WorldGenerator.SURFACE_LEVEL);
		}
	}
	
	private static void generateCave(World world, Random random, float x, float y, float angle, int length, int minHeight) {
		length++;
		if(y < minHeight) return;
		if(length > random.nextInt(40) + 10) return;
		TileData d = world.getTileData((int) x, (int) y);
		if(d.getTile(1) == Tile.AIR) return;
		float offset = 3.0f;
		x += MathHelper.cos(angle) * offset;
		y += MathHelper.sin(angle) * offset;
		angle += (random.nextFloat() - 0.5f) * 40.0f;
		generateSphere(world, (int) x, (int) y, random.nextInt(2) + 3, Tile.AIR, 0);
		generateCave(world, random, x, y, angle, length, minHeight);
	}
	
	private static void generateSphere(World world, int x, int y, int radius, Tile tile, int layer) {
		for(int xx = x - radius; xx < x + radius; xx++) {
			for(int yy = y - radius; yy < y + radius; yy++) {
				TileData d = world.getTileData(xx, yy);
				if(d.getTile(0) == Tile.AIR) continue;
				float xd = xx - x;
				float yd = yy - y;
				if(MathHelper.sqrt(xd * xd + yd * yd) < radius) {
					d.setTile(tile, layer);
				}
			}
		}
	}
	
	public static void generateSphere(World world, int x, int y, int radius, Tile tile, Tile target, int layer) {
		for(int xx = x - radius; xx < x + radius; xx++) {
			for(int yy = y - radius; yy < y + radius; yy++) {
				TileData d = world.getTileData(xx, yy);
				if(d.getTile(layer) != target) continue;
				float xd = xx - x;
				float yd = yy - y;
				if(MathHelper.sqrt(xd * xd + yd * yd) < radius) {
					d.setTile(tile, layer);
				}
			}
		}
	}
}
