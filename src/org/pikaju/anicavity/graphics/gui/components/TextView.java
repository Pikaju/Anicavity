package org.pikaju.anicavity.graphics.gui.components;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.graphics.Font;
import org.pikaju.anicavity.graphics.gui.GUIComponent;

public class TextView extends GUIComponent {

	
	protected String text;
	private int fontSize;
	private float r;
	private float g;
	private float b;
	
	public TextView(String text, int x, int y, int fontSize, float r, float g, float b) {
		super(x, y, (int) (Font.getStringWidth(text) * fontSize / 9.0f), fontSize);
		this.text = text;
		this.fontSize = fontSize;
		setRGB(r, g, b);
	}
	
	public TextView(String text, int x, int y, int fontSize) {
		this(text, x, y, fontSize, 1, 1, 1);
	}
	
	public void update() {
	}
	
	public void render(Bitmap screen) {
		screen.draw(text, x, y, fontSize, r, g, b);
	}
	
	public void setRGB(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public static void draw(Bitmap screen, String text, int x, int y, int fontSize) {
		screen.draw(text, x, y, fontSize, 1, 1, 1);
	}
}
