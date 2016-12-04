package org.pikaju.anicavity.io;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLoader {

	public static BufferedImage loadImage(String filePath) {
		try {
			return ImageIO.read(ImageLoader.class.getResource(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
