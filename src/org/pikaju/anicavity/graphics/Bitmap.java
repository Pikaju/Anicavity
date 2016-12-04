package org.pikaju.anicavity.graphics;

import java.awt.image.BufferedImage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.util.MathHelper;

public class Bitmap {

	public int[] pixels;
	public int width;
	public int height;
	
	public Bitmap(int width, int height) {
		pixels = new int[width * height];
		this.width = width;
		this.height = height;
	}
	
	public Bitmap(int width, int height, int color) {
		pixels = new int[width * height];
		this.width = width;
		this.height = height;
		clear(color);
	}
	
	public Bitmap(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();
		pixels = new int[width * height];
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				pixels[x + y * width] = image.getRGB(x, y);
			}
		}
	}
	
	public Bitmap() {
		width = 0;
		height = 0;
	}
	
	public void draw(Bitmap bitmap, float xOff, float yOff, float width, float height, int flip) {
		for(int x = 0; x < width; x++) {
			int xx = (int) (x + xOff);
			if(flip == 1 || flip == 3) xx = (int) (xOff - x - 1 + width);
			if(xx < 0) continue;
			if(xx >= this.width) break;
			
			for(int y = 0; y < height; y++) {
				int yy = (int) (y + yOff);
				if(flip == 2 || flip == 3) yy = (int) (yOff - y - 1 + height);
				if(yy < 0) continue;
				if(yy >= this.height) break;
				
				int interpolation = (int) (x * (bitmap.width / width)) + (int) (y * (bitmap.height / height)) * bitmap.width;
				int color = bitmap.pixels[interpolation];
				if((color & 0xffffff) != 0xff00ff) {
					pixels[xx + yy * this.width] = color;
				}
			}
		}
	}
	
	public void draw(Bitmap bitmap, float xOff, float yOff, float width, float height, int flip, float red, float green, float blue) {
		width = (int) width;
		height = (int) height;
		for(int x = 0; x < (int) width; x++) {
			int xx = (int) (x + xOff);
			if(flip == 1 || flip == 3) xx = (int) (xOff - x - 1 + width);
			if(xx < 0) continue;
			if(xx >= this.width) break;
			
			for(int y = 0; y < height; y++) {
				int yy = (int) (y + yOff);
				if(flip == 2 || flip == 3) yy = (int) (yOff - y - 1 + height);
				if(yy < 0) continue;
				if(yy >= this.height) break;
				
				int interpolation = (int) (x * (bitmap.width / width)) + (int) (y * (bitmap.height / height)) * bitmap.width;
				int color = bitmap.pixels[interpolation];
				if((color & 0xffffff) != 0xff00ff) {
					int r = (int) (((color >> 16) & 0xff) * red); if(r > 255) r = 255;
					int g = (int) (((color >> 8) & 0xff) * green); if(g > 255) g = 255;
					int b = (int) (((color >> 0) & 0xff) * blue); if(b > 255) b = 255;
					color = (r << 16) | (g << 8) | b;
					
					pixels[xx + yy * this.width] = color;
				}
			}
		}
	}
	
	public void draw(Bitmap bitmap, float xOff, float yOff, float width, float height, int flip, float[] colors) {
		draw(bitmap, xOff, yOff, width, height, flip, colors[0], colors[1], colors[2]);
	}
	
	public void draw(int color, float xOff, float yOff, float width, float height) {
		for(int x = 0; x < width; x++) {
			int xx = (int) (x + xOff);
			if(xx < 0) continue;
			if(xx >= this.width) break;
			
			for(int y = 0; y < height; y++) {
				int yy = (int) (y + yOff);
				if(yy < 0) continue;
				if(yy >= this.height) break;
				
				pixels[xx + yy * this.width] = color;
			}
		}
	}
	
	public void draw(int color, float xOff, float yOff, float width, float height, float red, float green, float blue) {
		int r = (int) (((color >> 16) & 0xff) * red); if(r > 255) r = 255;
		int g = (int) (((color >> 8) & 0xff) * green); if(g > 255) g = 255;
		int b = (int) (((color >> 0) & 0xff) * blue); if(b > 255) b = 255;
		color = (r << 16) | (g << 8) | b;
		
		for(int x = 0; x < width; x++) {
			int xx = (int) (x + xOff);
			if(xx < 0) continue;
			if(xx >= this.width) break;
			
			for(int y = 0; y < height; y++) {
				int yy = (int) (y + yOff);
				if(yy < 0) continue;
				if(yy >= this.height) break;
				
				pixels[xx + yy * this.width] = color;
			}
		}
	}
	
	public void draw(String text, float xOff, float yOff, int fontSize, float r, float g, float b) {
		Text.draw(this, text, (int) xOff, (int) yOff, fontSize, r, g, b);
	}
	
	public void draw(int color, float xOff, float yOff, float width, float height, float[] colors) {
		draw(color, xOff, yOff, width, height, colors[0], colors[1], colors[2]);
	}
	
	public void draw(Bitmap bitmap, float xOff, float yOff, float width, float height, int flip, float red, float green, float blue, float rotation) {
		float sin = MathHelper.sin(rotation);
		float cos = MathHelper.cos(rotation);
		for(int x = 0; x < width; x++) {
			int xx = (int) (x + xOff);
			if(flip == 1 || flip == 3) xx = (int) (xOff - x - 1 + width);
			
			for(int y = 0; y < height; y++) {
				int yy = (int) (y + yOff);
				if(flip == 2 || flip == 3) yy = (int) (yOff - y - 1 + height);
				
				int x0 = (int) (((xx - xOff) * cos) + ((yy - yOff) * sin) + xOff + width / 2 * -cos + width / 2);
				int y0 = (int) (((xx - xOff) * sin) - ((yy - yOff) * cos) + yOff + height / 2 * -sin + height / 2);
				
				if(x0 < 0 || y0 < 0 || x0 >= this.width || y0 >= this.height) continue;
				
				int interpolation = (int) (x * (bitmap.width / width)) + (int) (y * (bitmap.height / height)) * bitmap.width;
				int color = bitmap.pixels[interpolation];
				if((color & 0xffffff) != 0xff00ff) {
					int r = (int) (((color >> 16) & 0xff) * red); if(r > 255) r = 255;
					int g = (int) (((color >> 8) & 0xff) * green); if(g > 255) g = 255;
					int b = (int) (((color >> 0) & 0xff) * blue); if(b > 255) b = 255;
					color = (r << 16) | (g << 8) | b;
					
					pixels[x0 + y0 * this.width] = color;
				}
			}
		}
	}
	
	public void draw(Bitmap bitmap, float xOff, float yOff, float width, float height, int flip, float[] colors, float rotation) {
		draw(bitmap, xOff, yOff, width, height, flip, colors[0], colors[1], colors[2], rotation);
	}
	
	public void clear() {
		clear(0x000000);
	}
	
	public void clear(int color) {
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = color;
		}
	}
	
	public void writeData(DataOutput out) {
		try {
			out.writeInt(width);
			out.writeInt(height);
			for(int i = 0; i < pixels.length; i++) {
				out.writeInt(pixels[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readData(DataInput in) {
		try {
			width = in.readInt();
			height = in.readInt();
			pixels = new int[width * height];
			for(int i = 0; i < pixels.length; i++) {
				pixels[i] = in.readInt();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof Bitmap)) return false;
		Bitmap b = (Bitmap) obj;
		if(b.width != width) return false;
		if(b.height != height) return false;
		for(int i = 0; i < pixels.length; i++) {
			if(b.pixels[i] != pixels[i]) return false;
		}
		return true;
	}
	
	public Object clone() {
		Bitmap bitmap = new Bitmap(width, height);
		for(int i = 0; i < pixels.length; i++) {
			bitmap.pixels[i] = pixels[i];
		}
		return bitmap;
	}
	
	public String toString() {
		return "Bitmap[width = " + width + ", height = " + height + "]";
	}
	
	public int hashCode() {
		int hash = 0;
		hash = width | (height << 16);
		for(int i = 0; i < pixels.length; i++) {
			hash += (pixels[i] << i);
		}
		return hash;
	}
}
