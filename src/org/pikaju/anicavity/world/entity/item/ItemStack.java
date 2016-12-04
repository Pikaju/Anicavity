package org.pikaju.anicavity.world.entity.item;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.pikaju.anicavity.graphics.Bitmap;

public class ItemStack {

	private Item item;
	private int amount;
	
	public ItemStack(Item item, int amount) {
		this.item = item;
		this.amount = amount;
	}
	
	public ItemStack(Item item) {
		this(item, 1);
	}

	public ItemStack() {
		this(null, 0);
	}

	public void render(Bitmap screen, float x, float y, float width, float height) {
		item.render(screen, x, y, width, height);
		screen.draw("x" + amount, x + width, y + height - 3, 9, 1, 1, 1);
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public void readData(DataInput in) {
		try {
			item = Item.getItem(in.readShort());
			amount = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeData(DataOutput out) {
		try {
			out.writeShort(item.getId());
			out.writeInt(amount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return "ItemStack[item = " + item.getId() + ", amount = " + amount + "]";
	}
}