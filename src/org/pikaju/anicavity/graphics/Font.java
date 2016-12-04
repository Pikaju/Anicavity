package org.pikaju.anicavity.graphics;

public class Font {

	public static Sprite sprite = new Sprite("/font.png", 8, 16);
	
	public static String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.,?!:;/\\-_+*|=()$%  0123456789<>";
	public static String width = "555555555555555555555555555555545514415555545355555411511133455314225555535555555533";
	
	public static int getChar(String c) {
		return chars.indexOf(c);
	}
	
	public static int getCharWidth(int c) {
		return Integer.parseInt(width.substring(c, c + 1));
	}
	
	public static int getStringWidth(String s) {
		int v = 0;
		int highest = -1;
		for(char c : s.toCharArray()) {
			if(c == ' ') {
				v += 3 + Text.CHAR_DIST;
				continue;
			}
			if(c == '\n') {
				if(v > highest) {
					highest = v;
				}
				v = 0;
				continue;
			}
			v += getCharWidth(getChar(c + "")) + Text.CHAR_DIST;
		}
		return highest > v ? highest : v;
	}
}
