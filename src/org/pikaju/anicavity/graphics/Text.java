package org.pikaju.anicavity.graphics;

public class Text {
	
	public static final int LINE_DIST = 1;
	public static final int CHAR_DIST = 1;
	
	public static void draw(Bitmap bitmap, String text, int x, int y, int fontSize, float r, float g, float b) {
		char[] chars = text.toCharArray();
		
		int xo = x;
		int yo = y;
		
		for(char c : chars) {
			String s = c + "";
			
			if(s.equals(" ")) {
				xo += fontSize / 9.0f * (3.0f + CHAR_DIST);
				continue;
			}
			if(s.equals("\n")) {
				xo = x;
				yo += fontSize + (fontSize / 9.0f * LINE_DIST);
				continue;
			}
			
			int index = Font.getChar(s);
			if(index == -1) index = Font.getChar("?");
			int width = (int) (Font.getCharWidth(index) * (fontSize / 9.0f));
			
			bitmap.draw(Font.sprite.getBitmap(index), xo, yo, fontSize / 9.0f * 5.0f, fontSize, 0, r, g, b);
			xo += width + (fontSize / 9.0f * CHAR_DIST);
		}
	}
}
