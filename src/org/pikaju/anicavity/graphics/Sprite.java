package org.pikaju.anicavity.graphics;

import org.pikaju.anicavity.io.ImageLoader;

public class Sprite {

	private int spriteWidth;
	private int spriteHeight;
	private int spriteAmountX;
	private int spriteAmountY;
	
	private Bitmap[] bitmaps;
	
	public Sprite(Bitmap bitmap, int sx, int sy) {
		spriteWidth = bitmap.width / sx;
		spriteHeight = bitmap.height / sy;
		spriteAmountX = sx;
		spriteAmountY = sy;
		createBitmaps(bitmap);
	}
	
	public Sprite(String filePath, int sx, int sy) {
		this(new Bitmap(ImageLoader.loadImage(filePath)), sx, sy);
	}
	
	private void createBitmaps(Bitmap bitmap) {
		bitmaps = new Bitmap[spriteAmountX * spriteAmountY];
		for(int y = 0; y < spriteAmountY; y++) {
			for(int x = 0; x < spriteAmountX; x++) {
				Bitmap b = new Bitmap(spriteWidth, spriteHeight);
				for(int xx = 0; xx < spriteWidth; xx++) {
					for(int yy = 0; yy < spriteHeight; yy++) {
						b.pixels[xx + yy * spriteWidth] = bitmap.pixels[(x * spriteWidth + xx) + (y * spriteHeight + yy) * bitmap.width];
					}
				}
				bitmaps[x + y * spriteAmountX] = b;
			}
		}
	}

	public Bitmap getBitmap(int index) {
		if(index < 0 || index >= bitmaps.length) return new Bitmap(1, 1);
		return bitmaps[index];
	}
	
	public String toString() {
		return "Sprite[spriteWidth = " + spriteWidth + ", spriteHeight = " + spriteHeight + ", spriteAmountX = " + spriteAmountX + ", + spriteAmountY = " + spriteAmountY + "]";
	}
}
