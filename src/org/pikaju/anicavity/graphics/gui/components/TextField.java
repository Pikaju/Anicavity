package org.pikaju.anicavity.graphics.gui.components;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.io.Input;

public class TextField extends TextView {

	private boolean selected;
	private Bitmap bitmap;
	
	private int border = 2;
	private int bo = 3;
	
	private int xx;
	private int yy;
	
	public TextField(String text, int x, int y, int width, int height) {
		super(text, 0, 0, height);
		this.xx = x;
		this.yy = y;
		this.width = width;
		this.height = height;
		bitmap = new Bitmap(width, height);
	}

	public void update() {
		if(Input.i.isButtonDown(Input.BUTTON_LEFT)) {
			selected = Input.i.inRegion(xx - border - bo, yy - border - bo, width + bo * 2 + border * 2, height + bo * 2 + border * 2);
		}
		if(selected) {
			char c = Input.i.getKeyChar();
			if(c != 0) {
				if(c == 'v' && Input.i.isControlDown()) {
					try {
						text += (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if(c == '\b') {
					if(text.length() > 0) {
						text = text.substring(0, text.length() - 1);
					}
				} else if(c != '\n') {
					text += c + "";
				}
			}
		}
	}
	
	public void render(Bitmap screen) {
		bitmap.clear();
		screen.draw(selected ? 0xffffff : 0x505050, xx - border - bo, yy - border - bo, width + border * 2 + bo * 2, height + border * 2 + bo * 2);
		screen.draw(0x000000, xx - bo, yy - bo, width + bo * 2, height + bo * 2);
		float c = selected ? 1 : 0.5f;
		setRGB(c, c, c);
		super.render(bitmap);
		screen.draw(bitmap, xx, yy, width, height, 0, 1, 1, 1);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
