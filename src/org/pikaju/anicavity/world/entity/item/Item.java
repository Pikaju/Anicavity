package org.pikaju.anicavity.world.entity.item;

import org.pikaju.anicavity.graphics.Bitmap;

public class Item {

	protected Bitmap bitmap;
	private short id;
	
	public static ItemRuby RUBY = new ItemRuby();
	
	private static Item[] itemMap;
	
	static {
		itemMap = new Item[] {
			RUBY,
		};
		for(short i = 0; i < itemMap.length; i++) itemMap[i].setId(i);
	}
	public static Item getItem(short id) {
		return itemMap[id];
	}
	
	public void render(Bitmap screen, float x, float y, float width, float height) {
		render(screen, x, y, width, height, 1, 1, 1);
	}
	
	public void render(Bitmap screen, float x, float y, float width, float height, float r, float g, float b) {
		screen.draw(bitmap, x, y, width, height, 0, r, g, b);
	}
	
	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}
}
