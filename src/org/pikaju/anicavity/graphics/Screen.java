package org.pikaju.anicavity.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Screen extends Bitmap {

	public static int WIDTH = 0;
	public static int HEIGHT = 0;
	
	private BufferedImage image;
	
	public Screen(int width, int height) {
		super(width, height);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		WIDTH = width;
		HEIGHT = height;
	}
	
	public void render(Graphics2D g, int width, int height) {
		g.drawImage(image, 0, 0, width, height, null);
		clear();
	}
}