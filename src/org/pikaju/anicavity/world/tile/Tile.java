package org.pikaju.anicavity.world.tile;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Sprite;

public class Tile {
	
	public static final int SIZE = 16;
	
	protected static Sprite sprite = new Sprite("/tiles/tiles.png", 16, 16);
	
	private static Tile[] tileMap;
	
	public static TileAir AIR = new TileAir();
	public static TileStone STONE = new TileStone();
	public static TileEarth EARTH = new TileEarth();
	public static TileGrass GRASS = new TileGrass();
	public static TileDarkBrick DARK_BRICK = new TileDarkBrick();
	
	static {
		tileMap = new Tile[] {
			AIR,
			STONE,
			EARTH,
			GRASS,
			DARK_BRICK,
		};
		for(short i = 0; i < tileMap.length; i++) tileMap[i].setID(i);
	}	
	public static Tile getTile(int id) {
		return tileMap[id];
	}

	private short id = -1;
	private boolean solid = true;
	private Bitmap bitmap;
	
	public void render(Bitmap screen, int x, int y, int sx, int sy, boolean background, float r, float g, float b) {
		if(bitmap == null) return;
		float rcol = background ? 0.2f : 1;
		float gcol = background ? 0.2f : 1;
		float bcol = background ? 0.2f : 1;
		screen.draw(bitmap, x * SIZE + sx, y * SIZE + sy, SIZE, SIZE, 0, rcol * r, gcol * g, bcol * b);
	}
	
	public Tile setID(short id) {
		this.id = id;
		return this;
	}
	
	public short getID() {
		return id;
	}

	public boolean isSolid() {
		return solid;
	}

	public Tile setSolid(boolean solid) {
		this.solid = solid;
		return this;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public Tile setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		return this;
	}
	
	public String toString() {
		return "Tile[id = " + id + ", solid = " + isSolid() + ", bitmap = " + bitmap + "]";
	}
}
