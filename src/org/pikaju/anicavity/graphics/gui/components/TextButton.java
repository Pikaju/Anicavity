package org.pikaju.anicavity.graphics.gui.components;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.io.Input;


public class TextButton extends TextView {

	private boolean hovering = false;
	private boolean lastClicked = false;
	private boolean wasClicked = false;
	
	private int border = 2;
	private int bo = 3;
	
	public boolean highlight = false;
	
	public TextButton(String text, int x, int y, int width, int height) {
		super(text, x, y, height / 9 * 9, 0.5f, 0.5f, 0.5f);
		this.width = width;
		this.height = height;
	}

	public void update() {
		if(Input.i.inRegion(x - border - bo, y - border - bo, width + bo * 2 + border * 2, height + bo * 2 + border * 2)) {
			hovering = true;
		} else {
			hovering = false;
		}
		if(!hovering) lastClicked = false;
		if(Input.i.isButtonDown(Input.BUTTON_LEFT)) lastClicked = true;
		else if(lastClicked) {
			wasClicked = true;
			lastClicked = false;
		}
	}
	
	public void render(Bitmap screen) {
		int color = highlight ? 0x20ff20 : 0x00a0ff;
		int dcolor = highlight ? 0x10a010 : 0x0080a0;
		screen.draw(hovering ? color : (highlight ? 0x004000 : 0x404040), x - border - bo, y - border - bo, width + border * 2 + bo * 2, height + border * 2 + bo * 2);
		screen.draw(hovering ? (lastClicked ? color : dcolor) : (highlight ? 0x002000 : 0x000000), x - bo, y - bo, width + bo * 2, height + bo * 2);
		float c = hovering ? 1 : 0.6f;
		setRGB(c, c, c);
		super.render(screen);
	}
	
	public boolean wasClicked() {
		boolean b = wasClicked;
		wasClicked = false;
		return b;
	}
}
