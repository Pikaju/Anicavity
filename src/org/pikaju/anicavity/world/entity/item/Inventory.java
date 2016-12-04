package org.pikaju.anicavity.world.entity.item;

import org.pikaju.anicavity.graphics.Bitmap;
import org.pikaju.anicavity.util.MathHelper;
import org.pikaju.anicavity.world.entity.Player;

public class Inventory {

	public Player owner;
	public ItemStack rubies;
	
	private float pickupAnim = 1;
	
	public Inventory(Player owner) {
		this.owner = owner;
		rubies = new ItemStack(Item.RUBY, 0);
	}
	
	public void update() {
		if(pickupAnim < 1) {
			pickupAnim += 0.1f;
		}
	}
	
	public void render(Bitmap screen) {
		float d = MathHelper.sin(pickupAnim * 180);
		float s = 4;
		if(pickupAnim >= 1) d = 0;
		rubies.render(screen, 6 - d * s, 6 - d * s, 16 + d * s * 2, 16 + d * s * 2);
	}

	public void addItem(ItemStack itemStack) {
		if(itemStack.getItem() == Item.RUBY) {
			rubies.setAmount(rubies.getAmount() + itemStack.getAmount());
			owner.damage(null, -itemStack.getAmount() * 15, 0, 0);
			pickupAnim = 0;
		}
	}
}
