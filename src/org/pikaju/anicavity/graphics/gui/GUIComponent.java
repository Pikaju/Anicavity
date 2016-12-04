package org.pikaju.anicavity.graphics.gui;

import org.pikaju.anicavity.graphics.Bitmap;

public class GUIComponent {

	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	public GUIComponent(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void update() {
		
	}
	
	public void render(Bitmap screen) {
		
	}
}
