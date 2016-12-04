package org.pikaju.anicavity.world.tile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Screen;
import org.pikaju.anicavity.util.MathHelper;

public class TileData {

	public static int LAYERS = 2;
	
	private Tile[] tiles;
	private float staticLight = 0.0f;
	
	public TileData() {
		tiles = new Tile[LAYERS];
		for(int i = 0; i < tiles.length; i++) {
			tiles[i] = Tile.getTile(0);
		}
	}
	
	public TileData(Tile tile, Tile bgtile) {
		tiles = new Tile[LAYERS];
		tiles[0] = tile;
		tiles[1] = bgtile;
	}
	
	public void render(Bitmap screen, int x, int y, int sx, int sy, float[] lightMap, int layer) {
		float[] lm = new float[lightMap.length];
		for(int i = 0; i < lm.length; i++) {
			lm[i] = lightMap[i] * 1.0f;
		}
		tiles[layer].render(screen, x, y, sx, sy, layer == 1, MathHelper.max(lm[0], staticLight), MathHelper.max(lm[1], staticLight), MathHelper.max(lm[2], staticLight));
	}
	
	public void render(Screen screen, int x, int y, int sx, int sy, int layer) {
		render(screen, x, y, sx, sy, new float[] { 1, 1, 1 }, layer);
	}
	
	public void setTile(Tile tile, int layer) {
		tiles[layer] = tile;
	}
	
	public Tile getTile(int layer) {
		return tiles[layer];
	}
	
	public void readData(DataInput in) {
		try {
			tiles = new Tile[LAYERS];
			for(int i = 0; i < tiles.length; i++) {
				tiles[i] = Tile.getTile(in.readInt());
			}
			staticLight = in.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeData(DataOutput out) {
		try {
			for(int i = 0; i < tiles.length; i++) {
				out.writeInt(tiles[i].getID());
			}
			out.writeFloat(staticLight);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		String tiles = "";
		for(int i = 0; i < this.tiles.length; i++) {
			tiles += ", tiles[" + i + "] = " + this.tiles[i];
		}
		return "TileData[layers = " + LAYERS + tiles + "]";
	}

	public boolean isSolid() {
		return tiles[0].isSolid();
	}

	public boolean isBackgroundSolid() {
		return tiles[1].isSolid();
	}
	
	public void setStaticLight(float staticLight) {
		this.staticLight = staticLight;
	}

	public float getStaticLight() {
		return staticLight;
	}
}
