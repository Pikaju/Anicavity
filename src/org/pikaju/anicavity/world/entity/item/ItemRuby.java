package org.pikaju.anicavity.world.entity.item;

import org.pikaju.anicavity.graphics.Sprite;

public class ItemRuby extends Item {

	public ItemRuby() {
		bitmap = new Sprite("/items/ruby.png", 1, 1).getBitmap(0);
	}
}
